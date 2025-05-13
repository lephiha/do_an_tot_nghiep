package com.lephiha.do_an.DoctorPage.HomePageDoctor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lephiha.do_an.AppointmentPage.AppointmentFragment;
import com.lephiha.do_an.CallVideo.stringee.activity.CallActivity;
import com.lephiha.do_an.CallVideo.stringee.common.Constant2;
import com.lephiha.do_an.CallVideo.stringee.common.PermissionsUtils;
import com.lephiha.do_an.CallVideo.stringee.common.Utils;
import com.lephiha.do_an.CallVideo.stringee.manager.ClientManager;
import com.lephiha.do_an.ChooseLoginActivity;
import com.lephiha.do_an.Container.NotificationReadAll;
import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.NotificationPage.NotificationFragment;
import com.lephiha.do_an.R;
import com.lephiha.do_an.SettingPage.SettingsFragment;
import com.lephiha.do_an.configAPI.HTTPRequest;
import com.lephiha.do_an.configAPI.HTTPService;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeDoctorActivity extends AppCompatActivity {

    private final String TAG = "HomeDoctor Activity";

    private Dialog dialog;
    private GlobaleVariable globaleVariable;
    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private String fragmentTag;
    private SharedPreferences sharedPreferences;
    private ClientManager clientManager;

    public static WeakReference<HomeDoctorActivity> weakActivity;

    public static HomeDoctorActivity getInstance() {
        return weakActivity.get();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_doctor);
        weakActivity = new WeakReference<>(HomeDoctorActivity.this);

        // Enable HomeDoctorFragment by default
        fragment = new HomeDoctorFragment();
        fragmentTag = "HomeDoctorFragment";
        enableFragment(fragment, fragmentTag);

        setupVariable();
        setupEvent();
        setNumberOnNotificationIcon();
        setupVideoCall();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setNumberOnNotificationIcon();
        Tooltip.setLocale(this, sharedPreferences);
    }

    private void setupVariable() {
        globaleVariable = (GlobaleVariable) this.getApplication();
        dialog = new Dialog(this);
        sharedPreferences = this.getApplication()
                .getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);
        bottomNavigationView = findViewById(R.id.bottomNavigationMenu);
        clientManager = ClientManager.getInstance(this); // Khởi tạo clientManager
        if (clientManager == null) {
            System.out.println(TAG + ": Khởi tạo ClientManager thất bại");
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void setupEvent() {
        // Sự kiện cho BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int shortcut = item.getItemId();
            if (shortcut == R.id.shortcutHome) {
                fragment = new HomeDoctorFragment();
                fragmentTag = "homeDoctorFragment";
            } else if (shortcut == R.id.shortcutNotification) {
                setNumberOnNotificationIcon();
                fragment = new NotificationFragment();
                fragmentTag = "notificationFragment";
            } else if (shortcut == R.id.shortcutAppointment) {
                fragment = new AppointmentFragment();
                fragmentTag = "appointmentFragment";
            } else if (shortcut == R.id.shortcutPersonality) {
                fragment = new SettingsFragment();
                fragmentTag = "settingsFragment";
            }
            enableFragment(fragment, fragmentTag);
            return true;
        });

        // Sự kiện cho nút gọi video
        findViewById(R.id.videoCallBtn).setOnClickListener(v -> {
            String callId = "recipient_call_id"; // Thay bằng ID người nhận thực tế
            if (callId.isEmpty()) {
                Toast.makeText(this, "Vui lòng cung cấp ID người nhận", Toast.LENGTH_SHORT).show();
                return;
            }
            makeCall(true, true, callId);
        });
    }

    private void enableFragment(Fragment fragment, String fragmentTag) {
        this.fragmentTag = fragmentTag;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
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
                        System.out.println(TAG + ": Exception: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationReadAll> call, @NonNull Throwable t) {
                System.out.println(TAG + ": setNumberOnNotificationIcon - error: " + t.getMessage());
            }
        });
    }

    public void exit() {
        SharedPreferences sharedPreferences = this.getApplication()
                .getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);
        sharedPreferences.edit().putString("accessToken", null).apply();
        sharedPreferences.edit().putInt("darkMode", 1).apply();
        sharedPreferences.edit().putString("language", getString(R.string.vietnamese)).apply();
        System.out.println(TAG + ": access token: " + sharedPreferences.getString("accessToken", null));
        Intent intent = new Intent(this, ChooseLoginActivity.class);
        finish();
        startActivity(intent);
    }

    private void setupVideoCall() {
        if (clientManager == null) {
            System.out.println(TAG + ": Không thể thiết lập gọi video, clientManager là null");
            Toast.makeText(this, "Lỗi khởi tạo gọi video", Toast.LENGTH_SHORT).show();
            return;
        }
        initAndConnectStringee();
        requestPermission();
    }

    private void requestPermission() {
        if (!PermissionsUtils.getInstance().checkSelfPermission(this)) {
            PermissionsUtils.getInstance().requestPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isGranted = PermissionsUtils.getInstance().verifyPermissions(grantResults);
        if (requestCode == PermissionsUtils.REQUEST_PERMISSION) {
            if (clientManager != null) {
                clientManager.isPermissionGranted = isGranted;
            }
            if (!isGranted && PermissionsUtils.getInstance().shouldRequestPermissionRationale(this)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.app_name);
                builder.setMessage("Cần cấp quyền để thực hiện cuộc gọi");
                builder.setPositiveButton("OK", (dialogInterface, id) -> dialogInterface.cancel());
                builder.setNegativeButton("Cài đặt", (dialogInterface, id) -> {
                    dialogInterface.cancel();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                });
                builder.create().show();
            }
        }
    }

    public void initAndConnectStringee() {
        if (clientManager == null) {
            System.out.println(TAG + ": Không thể kết nối Stringee, clientManager là null");
            return;
        }
        clientManager.connect();
    }

    public void makeCall(boolean isStringeeCall, boolean isVideoCall, String callId) {
        if (clientManager == null) {
            System.out.println(TAG + ": Không thể thực hiện cuộc gọi, clientManager là null");
            Toast.makeText(this, "Lỗi hệ thống gọi video", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Utils.isStringEmpty(callId) || !clientManager.getStringeeClient().isConnected()) {
            Toast.makeText(this, "Không thể gọi, kiểm tra kết nối hoặc ID người nhận", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!clientManager.isPermissionGranted) {
            PermissionsUtils.getInstance().requestPermissions(this);
            return;
        }
        Intent intent = new Intent(this, CallActivity.class);
        intent.putExtra(Constant2.PARAM_TO, callId);
        intent.putExtra(Constant2.PARAM_IS_VIDEO_CALL, isVideoCall);
        intent.putExtra(Constant2.PARAM_IS_INCOMING_CALL, false);
        intent.putExtra(Constant2.PARAM_IS_STRINGEE_CALL, isStringeeCall);
        startActivity(intent);
    }
}