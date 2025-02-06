package com.lephiha.do_an;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.Notification;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.HomePage.HomePageActivity;
import com.lephiha.do_an.LoginPage.LoginActivity;
import com.lephiha.do_an.Model.User;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MAIN ACTIVITY";
    private SharedPreferences sharedPreferences;
    private GlobaleVariable globaleVariable;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //1- declare sharedpre & globalvari
        globaleVariable = (GlobaleVariable)this.getApplication();
        sharedPreferences = this.getApplication().getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);
        MainViewModal viewModal = new ViewModelProvider(this).get(MainViewModal.class);
        dialog = new Dialog(this);

        //If we wanna use notification on Android 8 or higher, this function must be run
        Notification notification = new Notification(this);
        notification.createChannel();

        //2- check internet
        boolean isConnected = isInternetAvailable();
        if( !isConnected )
        {
            dialog.announce();
            dialog.show(R.string.attention, getString(R.string.check_your_internet_connection), R.drawable.ic_info);
            dialog.btnOK.setOnClickListener(view->{
                dialog.close();
                finish();
            });
            return;
        }

        //3 check dark mode
        int value = sharedPreferences.getInt("darkMode", 1);
        AppCompatDelegate.setDefaultNightMode(value);

        //4 access token null ?
        String accessToken = sharedPreferences.getString("accessToken", null);
        System.out.println(TAG);
        System.out.println(accessToken);

        if (accessToken != null) {
            //global variable chỉ hđ trong phiên lamf việc nên phải gán lại accessToken
            globaleVariable.setAccessToken(accessToken);

            //cài đặt header với yêu cầu đọc thông tin cá nhân bn
            Map<String, String> headers = globaleVariable.getHeaders();

            //gử ycau đọc thông tin bnhan
            viewModal.readPersonalInformation(headers);

            //lang nghe phản hồi

            viewModal.getResponse().observe(this, response -> {
                try {
                    int result = response.getResult();

                    if (result == 1) { //luu thong tin vao hompage
                        //cap nhat thong tin user

                        User user = response.getData();
                        globaleVariable.setAuthUser(user);

                        //chuyen sang homepage
                        Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    if (result == 0) { //thông báo và cho login lại
                        System.out.println(TAG);
                        System.out.println("result: "+ result);
                        System.out.println("msg: "+ response.getMsg());
                        sharedPreferences.edit().putString("accessToken", null).apply();

                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }
                catch (Exception e) {
                    /*Neu truy van lau qua ma khong nhan duoc phan hoi thi cung dong ung dung*/
                    System.out.println(TAG + "- exception: " + e.getMessage());
                    dialog.announce();
                    dialog.show(R.string.attention, getString(R.string.check_your_internet_connection), R.drawable.ic_info);
                    dialog.btnOK.setOnClickListener(view->{
                        dialog.close();
                        finish();
                    });
                }
            });
        }
        else  {
            //delay 1s truoc khi vaof home

            Handler handler = new Handler(Looper.myLooper());
            handler.postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }, 1000);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Tooltip.setLocale(this, sharedPreferences);
    }

    public boolean isInternetAvailable() {
        boolean connected;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        return connected;
    }
}