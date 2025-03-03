package com.lephiha.do_an.AppointmentPage;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.lephiha.do_an.Container.AppointmentQueue;
import com.lephiha.do_an.Container.NotificationCreate;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.R;
import com.lephiha.do_an.configAPI.Constant;
import com.lephiha.do_an.configAPI.HTTPRequest;
import com.lephiha.do_an.configAPI.HTTPService;
import com.lephiha.do_an.Model.Queue;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AppointmentService extends IntentService {

    public static final String TAG = "AppointmentService";

    private PowerManager.WakeLock wakeLock;
    private GlobaleVariable globaleVariable;
    private MediaPlayer mediaPlayer;

    private String recordId; //id of record that user is waiting for examining
    private String recordType; //type is key to create message for booking/appointment

    private String positon;
    private String doctorId;
    private String doctorName;
    private boolean isNotify = false;

    public AppointmentService() {
        super("AppointmentService");
        setIntentRedelivery(true); //if system kill service, it will restart service

    }

    @SuppressLint({"InvalidWakeLockTag", "ForegroundServiceType"})
    @Override
    public void onCreate() {
        super.onCreate();
        globaleVariable = (GlobaleVariable) getApplication();
        Log.d(TAG, "onCreate: ");

        //phone screen can turn off but CPU keeps running to complete task
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.acquire(2*60*1000L); //2'
        Log.d(TAG, "onCreate: wakelock acquired");

        //show notif that app is running

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, Constant.APP_NAME())
                    .setContentTitle(Constant.APP_NAME())
                    .setContentText(getString(R.string.leeha_medical_is_running_in_background))
                    .setSmallIcon(R.drawable.ic_leeha).build();

            startForeground(10, notification);
        }
    }

    //this function is called when service is started

    boolean running = canServiceRun();

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent: ");
        assert intent != null;
        doctorId = intent.getStringExtra("doctorId");
        doctorName = intent.getStringExtra("doctorName");
        positon = intent.getStringExtra("position");
        recordId = intent.getStringExtra("recordId");
        recordType = intent.getStringExtra("recordType");

        int interva = 1000*45; //chay moi 45s
        System.out.println("First create is running: "+running);

        while (running) {
            //get request every 30s
            running = canServiceRun(); // run canServiceRun() to know it can be continue or not
            System.out.println("is running: "+running);
            if (!running) {
                stopSelf();
                System.out.println("stop service");
                return;
            }
            getAppointmentQueue();
            SystemClock.sleep(interva);

        }
    }

    //this function return boolen flag that service can run or not
    //yes, isNotify = false || 7 <= hour <= 18
    //no, if device have gone off notif and sound || current time <= 7 || current time > 18

    public boolean canServiceRun() {
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
        Calendar calendar = Calendar.getInstance(timeZone);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (isNotify) {
            System.out.println("can not run by isNotify");
            return false;

        }
        if (hour < 7 || hour > 18) {
            System.out.println("can not run by time");
            return false;
        }

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        wakeLock.release();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        Log.d(TAG, "onDestroy: wakelock released");
    }

    public void getAppointmentQueue() {
        //1 - prepare header & parameter
        Map<String, String> header = globaleVariable.getHeaders();
        Map<String, String> parameter = new HashMap<>();
        parameter.put("doctor_id", doctorId);
        parameter.put("date", Tooltip.getToday());
        parameter.put("order[column]", "position");
        parameter.put("order[dir]", "asc");
        parameter.put("limit", "3");
        parameter.put("status", "processing");

        //2- prepare api
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //3
        Call<AppointmentQueue> container = api.appointmentQueue(header, parameter);

        //4
        container.enqueue(new Callback<AppointmentQueue>() {
            @Override
            public void onResponse(@NonNull Call<AppointmentQueue> call,@NonNull Response<AppointmentQueue> response) {
                if (response.isSuccessful()) {
                    AppointmentQueue content = response.body();
                    assert content != null;
                    List<Queue> list = content.getData();
                    checkNextPatientAndShowNotif(list);
                }
                if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        System.out.println(jObjError);
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AppointmentQueue> call, @NonNull Throwable t) {
                System.out.println("Appointment-page Service - doSomething - error: " + t.getMessage());
            }
        });
    }

    private void checkNextPatientAndShowNotif(List<Queue> list) {
        String text = this.getString(R.string.it_is_your_turn);
        String bigText = globaleVariable.getAuthUser().getName() + " ơi! " +
                "Hãy chuẩn bị, sắp đến lượt khám của bạn với " + doctorName + " rồi đấy!";

        for (Queue element: list) {
            int positionInQueue = element.getPosition();
            if (positionInQueue == Integer.parseInt(positon)) {
                isNotify = true;
                ShowMessageInDevice(text, bigText);
                createNotificationInServer(bigText);
                super.onDestroy();
                return;
            }
        }
    }

    //text is short text, bigText is full text
    public void ShowMessageInDevice(String text, String bigText) {
        //tao noi dung cho Notification
        com.lephiha.do_an.Helper.Notification notification = new com.lephiha.do_an.Helper.Notification(this);
        String title = this.getString(R.string.app_name);
        notification.setup(title, text, bigText);
        notification.show();

        //phat am thanh
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound_3);
        int duration = 1000*10; // media player will play sound in 10s
        int interval = 1000; //count down 1s

        CountDownTimer timer = new CountDownTimer(duration, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                mediaPlayer.start();

            }

            @Override
            public void onFinish() {
                mediaPlayer.stop();

            }
        };
        timer.start();
    }

    //create notification in server(help user to watch notif again)

    private void createNotificationInServer(String message) {
        //1
        Map<String, String> headers = globaleVariable.getHeaders();

        //2
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //3
        Call<NotificationCreate> container = api.notificationCreate(headers, message, recordId, recordType);

        //4 - send request to server
        container.enqueue(new Callback<NotificationCreate>() {
            @Override
            public void onResponse(@NonNull Call<NotificationCreate> call,@NonNull Response<NotificationCreate> response) {
                if (response.isSuccessful()) {
                    NotificationCreate content = response.body();
                    assert content != null;
                    System.out.println(TAG);
                    System.out.println(content.getMsg());
                }
                if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        System.out.println(jObjError);
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationCreate> call, @NonNull Throwable t) {
                System.out.println("Appointment-page Service - create notification in server - error: " + t.getMessage());
            }
        });
    }
}
