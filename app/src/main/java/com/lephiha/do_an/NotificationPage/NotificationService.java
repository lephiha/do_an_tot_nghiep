package com.lephiha.do_an.NotificationPage;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.lephiha.do_an.Container.DoctorReadAll;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.R;
import com.lephiha.do_an.configAPI.Constant;
import com.lephiha.do_an.configAPI.HTTPRequest;
import com.lephiha.do_an.configAPI.HTTPService;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NotificationService extends IntentService {

    public static final String TAG = "NotificationService";

    private PowerManager.WakeLock wakeLock;
    private GlobaleVariable globaleVariable;
    private MediaPlayer mediaPlayer;

    public NotificationService() {
        super("NotificationService");
        setIntentRedelivery(true); // if system kills this service, it will be created again

    }

    @SuppressLint({"InvalidWakeLockTag", "ForegroundServiceType"})
    @Override
    public void onCreate() {
        super.onCreate();
        globaleVariable = (GlobaleVariable) this.getApplication();
        Log.d(TAG, "onCreate: ");

        //phone screen can turn off but CPU keep running to complete task
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "NotificationService - WakeLock");
        wakeLock.acquire(2*60*1000L ); // turn off wake lock after 2 minutes
        Log.d(TAG, "onCreate - wakeLock acquire: ");

        //show notif that appcation is running
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, Constant.APP_NAME())
                    .setContentTitle(Constant.APP_NAME())
                    .setContentText(getString(R.string.leeha_medical_is_running_in_background))
                    .setSmallIcon(R.drawable.ic_leeha)
                    .build();

            startForeground(1, notification);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent: ");
        //String input = intent.getStringExtra("input");

        int interval = 1000*60*5; //5'
        boolean isWorkingHour = isWorkingHour();

        while (isWorkingHour) {
            //print time now on console & notification
            TimeZone timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
            Calendar calendar = Calendar.getInstance(timeZone);
            int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
            int nowMinute = calendar.get(Calendar.MINUTE);
            String nowValue = "It's " + nowHour + ":" + nowMinute;
            System .out.println(nowValue);
            showMessage(nowValue, nowValue);

            //get request evey 5'
            isWorkingHour = isWorkingHour(); //run function is WorkingHour to know it can be continue or not
            doSomeThing();
            SystemClock.sleep(interval);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        wakeLock.release();
        mediaPlayer.stop();
        mediaPlayer.release();
        Log.d(TAG, "onDestroy - wakeLock release: ");
    }

    //flag to check service is running or not

    public boolean isWorkingHour() {
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
        Calendar now = Calendar.getInstance(timeZone);
        int hour = now.get(Calendar.HOUR_OF_DAY);

        if (hour < 7 || hour > 22) {
            return false;
        }
        return true;
    }

    public void doSomeThing() {
        Map<String, String> headers = globaleVariable.getHeaders();
        Map<String, String> parameters = new HashMap<>();

        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //3
        Call<DoctorReadAll> container = api.doctorReadAll(headers, parameters);

        //4
        container.enqueue(new Callback<DoctorReadAll>() {
            @Override
            public void onResponse(@NonNull Call<DoctorReadAll> call,@NonNull Response<DoctorReadAll> response) {
                if (response.isSuccessful()) {
                    DoctorReadAll content = response.body();
                    assert content != null;

                    System.out.println("result: " + content.getResult());
                    System.out.println("quantity " + content.getQuantity());
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
            public void onFailure(@NonNull Call<DoctorReadAll> call, @NonNull Throwable t) {
                System.out.println("Doctor Repository - Read All - error: " + t.getMessage());
            }
        });
    }

    public void showMessage(String text, String bigText) {
        //tao noi dung cho Notification
        com.lephiha.do_an.Helper.Notification notification = new com.lephiha.do_an.Helper.Notification(this);
        String title = this.getString(R.string.app_name);

        notification.setup(title, text, bigText);
        notification.show();

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound_3);
    }
}
