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
import com.lephiha.do_an.Model.Queue;
import com.lephiha.do_an.R;
import com.lephiha.do_an.configAPI.Constant;
import com.lephiha.do_an.configAPI.HTTPRequest;
import com.lephiha.do_an.configAPI.HTTPService;

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

public class AppointmentService extends IntentService{

    private static final  String TAG = "Appointment service";
    private PowerManager.WakeLock wakeLock;
    private GlobaleVariable globaleVariable;
    private MediaPlayer mediaPlayer;

    private String recordId; // id of appointment that user is watting for examining
    private String recordType; //is key to create message for BOOKING or APPOINTMENT
    private String position; //position n queue that if current position => it's user turn
    private  String doctorId; //id of doctor that user wait for
    private String doctorName;

    private boolean isNotify = false;

    public AppointmentService() {
        super("NotificationService");
        setIntentRedelivery(true); //if systems kills service, it will be created again
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate() {
        super.onCreate();
        globaleVariable = (GlobaleVariable) this.getApplication();
        Log.d(TAG, "onCreate");

        //phone screen can turn off but cpu keep running to complete task
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "appointment service - WakeLock");
        wakeLock.acquire(2*60*1000L /*2 phut*/); //turn off lock after 2'
        Log.d(TAG, "onCreate - wakelock acquire");

        //show notification app running
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, Constant.APP_NAME())
                    .setContentTitle(Constant.APP_NAME())
                    .setContentText(getString(R.string.umbreall_health_is_running_in_background))
                    .setSmallIcon(R.drawable.ic_leeha)
                    .build();

            startForeground(10, notification);
        }
    }

    //intent sent from Appointment recyclerView
    //this run first whenever the service is called

    boolean running = canServiceRun();

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent");
        assert intent != null;
        doctorId = intent.getStringExtra("doctorId");
        doctorName = intent.getStringExtra("doctorName");
        position = intent.getStringExtra("recordId");
        recordType = intent.getStringExtra("recordType");

        int interval = 1000*45; //run function every 45s
        System.out.println("First create - is running: " + running);

        while (running) {
            //get request every 30s
            running = canServiceRun(); // chay canServiceRun de xem no co the chay hay k
            System.out.println("is running: " + running);
            if (!running) {
                stopSelf();
                System.out.println("intent service is killed!");
                return;
            }
            getAppointmentQueue();
            SystemClock.sleep(interval);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        wakeLock.release();
        if(mediaPlayer != null) {
            mediaPlayer.release();
        }
        Log.d(TAG, "onDestroy - wakeLock release");
    }

    public void getAppointmentQueue() {
        //1 - prepare header va parameters
        Map<String, String> headers = globaleVariable.getHeaders();
        Map<String , String> parameters = new HashMap<>();
        parameters.put("doctor_id", doctorId);
        parameters.put("date", Tooltip.getToday());
        parameters.put("order[column]", "position" );
        parameters.put("order[dir]", "asc");
        parameters.put("length", "3");
        parameters.put("status", "processing");

        //2- prepare api
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //3
        Call<AppointmentQueue> container = api.appointmentQueue(headers, parameters);

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
                if(response.errorBody() != null)
                {
                    try
                    {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        System.out.println( jObjError );
                    }
                    catch (Exception e) {
                        System.out.println( e.getMessage() );
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AppointmentQueue> call, @NonNull Throwable t) {
                System.out.println("Appointment-page Service - doSomething - error: " + t.getMessage());
            }
        });
    }

    /** this function returns boolean flag that this service can be run or not?
     * YES, isNotify == false || 7 <= hour <= 18
     *      * NO, if the device have gone off notification and sound || current time <= 7 || current time > 18
     */
    public boolean canServiceRun()
    {
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
        Calendar now = Calendar.getInstance(timeZone);
        int hour = now.get(Calendar.HOUR_OF_DAY);

        if(isNotify)
        {
            System.out.println("can not run by isNotify");
            return false;
        }
        if(  hour < 7 || hour > 18)
        {
            System.out.println("can not run by working hour");
            return false;
        }
        return true;
    }

    //check next patient and show notification
    //loop through list of next 3 patients. if 1 patient of lists is user's position, device'll show notif

    private void checkNextPatientAndShowNotif(List<Queue> list) {
        String text = this.getString(R.string.it_is_your_turn);
        String bigText = globaleVariable.getAuthUser().getName()+ " ơi!" +
                "Hãy chuẩn bị, sắp đến lượt khám của bạn với " + doctorName + " rồi";

        for(Queue element : list) {
            int positionInQueue = element.getPosition();
            if (positionInQueue == Integer.parseInt(position)) {
                isNotify = true;
                showMessageInDevice(text, bigText);
                creatNotificationInserver(bigText);
                super.onDestroy();
                return;
            }
        }
    }

    //show message (text is the short text, bigtext full)

    public void showMessageInDevice(String text, String bigText) {
        //tao nd cho notif
        com.lephiha.do_an.Helper.Notification notification = new com.lephiha.do_an.Helper.Notification(this);
        String title = this.getString(R.string.app_name);
        notification.setup(title, text, bigText);
        notification.show();

        //phat am thanh

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound_3);
        int duration = 1000*10; //run 10s
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

    //create notif in server (help users to watch notif again)
    private void creatNotificationInserver(String message) {
        //1- prepare header + parameters
        Map<String, String> headers = globaleVariable.getHeaders();

        //2- api
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //3
        Call<NotificationCreate> container = api.notificationCreate(headers, message, recordId, recordType);

        //4
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
