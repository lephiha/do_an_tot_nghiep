package com.lephiha.do_an.DoctorPage.HomePageDoctor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

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
    ClientManager clientManager;

    public static WeakReference<HomeDoctorActivity> weakActivity;
    public static HomeDoctorActivity getInstance() {
        return weakActivity.get();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_doctor);
        weakActivity = new WeakReference<>(HomeDoctorActivity.this);


        //enable homefragment by default
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
    private void setupVariable()
    {
        globaleVariable = (GlobaleVariable) this.getApplication();
        dialog = new Dialog(this);

        sharedPreferences = this.getApplication()
                .getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);
        bottomNavigationView = findViewById(R.id.bottomNavigationMenu);
    }

    @SuppressLint("NonConstantResourceId")
    private void setupEvent(){
        /*set up event when users click on item in bottom navigation view*/
        bottomNavigationView.setOnItemSelectedListener(item -> {
            /*When ever users click on any icon, we updates the number of unread notifications*/


            int shortcut = item.getItemId();
            if (shortcut == R.id.shortcutHome) {
                // setNumberOnNotif
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
        dialog.btnOK.setOnClickListener(view->{
            super.onBackPressed();
            finish();
        });
        dialog.btnCancel.setOnClickListener(view-> dialog.close());
    }

    public void setNumberOnNotificationIcon()
    {
        /*Step 1 - setup Retrofit*/
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        /*Step 2 - prepare header*/
        Map<String, String> header = globaleVariable.getHeaders();

        /*Step 3*/
        Call<NotificationReadAll> container = api.notificationReadAll(header);

        /*Step 4*/
        container.enqueue(new Callback<NotificationReadAll>() {
            @Override
            public void onResponse(@NonNull Call<NotificationReadAll> call, @NonNull Response<NotificationReadAll> response) {
                /*if successful, update the number of unread notification*/
                if(response.isSuccessful())
                {
                    NotificationReadAll content = response.body();
                    assert content != null;
                    /*update the number of unread notification*/
                    int quantityUnread = content.getQuantityUnread();
                    bottomNavigationView
                            .getOrCreateBadge(R.id.shortcutNotification)
                            .setNumber(quantityUnread);
                }
                /*if fail, show exception*/
                if(response.errorBody() != null)
                {
                    System.out.println(response);
                    try
                    {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        System.out.println( jObjError );
                    }
                    catch (Exception e) {
                        System.out.println(TAG);
                        System.out.println("Exception: " + e.getMessage() );
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
    public void exit()
    {
        SharedPreferences sharedPreferences = this.getApplication()
                .getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);

        sharedPreferences.edit().putString("accessToken", null).apply();
        sharedPreferences.edit().putInt("darkMode", 1).apply();// 1 is off, 2 is on
        sharedPreferences.edit().putString("language", getString(R.string.vietnamese)).apply();


        System.out.println(TAG);
        System.out.println("access token: " + sharedPreferences.getString("accessToken", null) );

        Intent intent = new Intent(this, ChooseLoginActivity.class);
        finish();
        startActivity(intent);
    }

    private void setupVideoCall() {
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
            clientManager.isPermissionGranted = isGranted;
            if (!isGranted) {
                if (PermissionsUtils.getInstance().shouldRequestPermissionRationale(this)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.app_name);
                    builder.setMessage("Permissions must be granted for the call");
                    builder.setPositiveButton("Ok", (dialogInterface, id) -> dialogInterface.cancel());
                    builder.setNegativeButton("Settings", (dialogInterface, id) -> {
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
    }

    public void initAndConnectStringee() {
        clientManager.connect();
    }

    public void makeCall(boolean isStringeeCall, boolean isVideoCall, String callId) {
        if (Utils.isStringEmpty(callId) || !clientManager.getStringeeClient().isConnected()) {
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
