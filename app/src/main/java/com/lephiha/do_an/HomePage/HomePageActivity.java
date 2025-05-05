package com.lephiha.do_an.HomePage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lephiha.do_an.AppointmentPage.AppointmentFragment;
import com.lephiha.do_an.Container.NotificationReadAll;
import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.LoginPage.LoginActivity;
import com.lephiha.do_an.NotificationPage.NotificationFragment;
import com.lephiha.do_an.R;
import com.lephiha.do_an.SettingPage.SettingsFragment;
import com.lephiha.do_an.chatbotAI.ChatActivity;
import com.lephiha.do_an.chatbotAI.ChatHeadService;
import com.lephiha.do_an.configAPI.HTTPRequest;
import com.lephiha.do_an.configAPI.HTTPService;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomePageActivity extends AppCompatActivity {
    private final String TAG = "HomePage Activity";
    private Dialog dialog;
    private GlobaleVariable globaleVariable;

    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private String fragmentTag;

    private SharedPreferences sharedPreferences;
    private static final int REQUEST_CODE_OVERLAY_PERMISSION = 1001;

    // weak activity + setter
    public static WeakReference<HomePageActivity> weakActivity;
    public static HomePageActivity getInstance() {
        return weakActivity.get();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        weakActivity = new WeakReference<>(HomePageActivity.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION);
        } else {
            startChatHeadService();
        }

        // enable homefragment by default
        fragment = new HomeFragment();
        fragmentTag = "homeFragment";
        enableFragment(fragment, fragmentTag);
        sendBroadcast(new Intent(ChatHeadService.ACTION_HOME_FRAGMENT_SHOWN)); // Hiện bong bóng khi khởi tạo

        setupVariable();
        setupEvent();
        setNumberOnNotificationIcon();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                startChatHeadService();
            } else {
                dialog.announce();
                dialog.show(R.string.attention, "Bạn cần cấp quyền hiển thị trên các ứng dụng khác để sử dụng bong bóng chat.", R.drawable.ic_info);
                dialog.btnOK.setOnClickListener(view -> dialog.close());
            }
        }
    }

    private void startChatHeadService() {
        Intent intent = new Intent(this, ChatHeadService.class);
        startService(intent);
    }

    private void stopChatHeadService() {
        Intent intent = new Intent(this, ChatHeadService.class);
        stopService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setNumberOnNotificationIcon();
        Tooltip.setLocale(this, sharedPreferences);
        // Kiểm tra fragment hiện tại khi resume
        if ("homeFragment".equals(fragmentTag)) {
            sendBroadcast(new Intent(ChatHeadService.ACTION_HOME_FRAGMENT_SHOWN));
        } else {
            sendBroadcast(new Intent(ChatHeadService.ACTION_HOME_FRAGMENT_HIDDEN));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Ẩn bong bóng khi activity không còn ở foreground
        sendBroadcast(new Intent(ChatHeadService.ACTION_HOME_FRAGMENT_HIDDEN));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopChatHeadService(); // Dừng service khi activity bị hủy
    }

    private void setupVariable() {
        globaleVariable = (GlobaleVariable) this.getApplication();
        dialog = new Dialog(this);

        sharedPreferences = this.getApplication().getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);

        bottomNavigationView = findViewById(R.id.bottomNavigationMenu);
    }

    private void setupEvent() {
        bottomNavigationView.setOnItemReselectedListener(item -> {
            int shortcut = item.getItemId();

            if (shortcut == R.id.shortcutHome) {
                fragment = new HomeFragment();
                fragmentTag = "homeFragment";
                sendBroadcast(new Intent(ChatHeadService.ACTION_HOME_FRAGMENT_SHOWN));
            } else if (shortcut == R.id.shortcutNotification) {
                setNumberOnNotificationIcon();
                fragment = new NotificationFragment();
                fragmentTag = "notificationFragment";
                sendBroadcast(new Intent(ChatHeadService.ACTION_HOME_FRAGMENT_HIDDEN));
            } else if (shortcut == R.id.shortcutAppointment) {
                fragment = new AppointmentFragment();
                fragmentTag = "appointmentFragment";
                sendBroadcast(new Intent(ChatHeadService.ACTION_HOME_FRAGMENT_HIDDEN));
            } else if (shortcut == R.id.shortcutChatAi) {
                Intent intent = new Intent(HomePageActivity.this, ChatActivity.class);
                startActivity(intent);
                return;
            } else if (shortcut == R.id.shortcutPersonality) {
                fragment = new SettingsFragment();
                fragmentTag = "settingsFragment";
                sendBroadcast(new Intent(ChatHeadService.ACTION_HOME_FRAGMENT_HIDDEN));
            }

            enableFragment(fragment, fragmentTag);
        });
    }

    public void enableFragment(Fragment fragment, String fragmentTag) {
        this.fragmentTag = fragmentTag;

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Map<String, String> headers = ((GlobaleVariable)getApplication()).getHeaders();
        String accessToken = headers.get("Authorization");
        String contentType = headers.get("Content-Type");

        Bundle bundle = new Bundle();
        bundle.putString("accessToken", accessToken);
        bundle.putString("contentType", contentType);
        fragment.setArguments(bundle);

        transaction.replace(R.id.frameLayout, fragment, fragmentTag);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        dialog.confirm();
        dialog.show(getString(R.string.attention),
                getString(R.string.are_you_sure_about_that), R.drawable.ic_info);
        dialog.btnOK.setOnClickListener(view -> {
            super.onBackPressed();
            finish();
        });
        dialog.btnCancel.setOnClickListener(view -> dialog.close());
    }

    public void setNumberOnNotificationIcon() {
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        Map<String, String> header = globaleVariable.getHeaders();

        Call<NotificationReadAll> container = api.notificationReadAll(header);

        container.enqueue(new Callback<NotificationReadAll>() {
            @Override
            public void onResponse(@NonNull Call<NotificationReadAll> call, @NonNull Response<NotificationReadAll> response) {
                if (response.isSuccessful()) {
                    NotificationReadAll content = response.body();
                    assert content != null;

                    int quantityUnread = content.getQuantityUnread();
                    bottomNavigationView
                            .getOrCreateBadge(R.id.shortcutNotification)
                            .setNumber(quantityUnread);
                }
                if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        System.out.println(jObjError);
                    } catch (Exception e) {
                        System.out.println(TAG);
                        System.out.println("Exception: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationReadAll> call, @NonNull Throwable t) {
                System.out.println(TAG);
                System.out.println("setNumberOnNotificationIcon - error: " + t.getMessage());
            }
        });
    }

    public void exit() {
        SharedPreferences sharedPreferences = this.getApplication()
                .getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);

        sharedPreferences.edit().putString("accessToken", null).apply();
        sharedPreferences.edit().putInt("darkMode", 1).apply();
        sharedPreferences.edit().putString("language", getString(R.string.vietnamese)).apply();

        System.out.println(TAG);
        System.out.println("access token: " + sharedPreferences.getString("accessToken", null));

        Intent intent = new Intent(this, LoginActivity.class);
        finish();
        startActivity(intent);
    }
}