package com.lephiha.do_an;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.Notification;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.HomePage.HomePageActivity;
import com.lephiha.do_an.Model.User;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MAIN ACTIVITY";
    private SharedPreferences sharedPreferences;
    private GlobaleVariable globaleVariable;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Khai báo SharedPreferences và biến toàn cục
        globaleVariable = (GlobaleVariable) this.getApplication();
        sharedPreferences = this.getApplication().getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);
        MainViewModal viewModal = new ViewModelProvider(this).get(MainViewModal.class);
        dialog = new Dialog(this);

        // Cấu hình Notification cho Android 8+
        Notification notification = new Notification(this);
        notification.createChannel();

        // 2. Kiểm tra kết nối Internet
        if (!isInternetAvailable()) {
            dialog.announce();
            dialog.show(R.string.attention, getString(R.string.check_your_internet_connection), R.drawable.ic_info);
            dialog.btnOK.setOnClickListener(view -> {
                dialog.close();
                finish();
            });
            return;
        }

        // 3. Kiểm tra chế độ Dark Mode
        int darkModeValue = sharedPreferences.getInt("darkMode", 1);
        AppCompatDelegate.setDefaultNightMode(darkModeValue);

        // 4. Kiểm tra Access Token
        String accessToken = sharedPreferences.getString("accessToken", null);
        System.out.println(TAG + " - AccessToken: " + accessToken);

        if (accessToken != null) {
            globaleVariable.setAccessToken(accessToken);
            Map<String, String> headers = globaleVariable.getHeaders();

            // Gửi yêu cầu lấy thông tin người dùng
            viewModal.readPersonalInformation(headers);

            viewModal.getResponse().observe(this, response -> {
                try {
                    if (response != null && response.getResult() == 1) {
                        // Lưu thông tin user vào biến toàn cục
                        User user = response.getData();
                        globaleVariable.setAuthUser(user);

                        // Chuyển đến màn hình chính (HomePage)
                        Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Nếu token không hợp lệ, chuyển về màn hình chọn đăng nhập
                        System.out.println(TAG + " - Token không hợp lệ, yêu cầu đăng nhập lại");
                        sharedPreferences.edit().putString("accessToken", null).apply();
                        navigateToChooseLogin();
                    }
                } catch (Exception e) {
                    System.out.println(TAG + " - Exception: " + e.getMessage());
                    dialog.announce();
                    dialog.show(R.string.attention, getString(R.string.check_your_internet_connection), R.drawable.ic_info);
                    dialog.btnOK.setOnClickListener(view -> {
                        dialog.close();
                        finish();
                    });
                }
            });
        } else {
            // Nếu không có token, sau 1 giây vào màn hình chọn đăng nhập
            new Handler(Looper.getMainLooper()).postDelayed(this::navigateToChooseLogin, 1000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tooltip.setLocale(this, sharedPreferences);
    }

    // Chuyển sang màn hình chọn đăng nhập
    private void navigateToChooseLogin() {
        Intent intent = new Intent(MainActivity.this, com.lephiha.do_an.ChooseLoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Kiểm tra kết nối Internet
    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
