package com.lephiha.do_an.HomePage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

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

    //weak activity + setter
    public static WeakReference<HomePageActivity> weakActivity;
    public static HomePageActivity getInstance() {
        return weakActivity.get();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        weakActivity = new WeakReference<>(HomePageActivity.this);

        //enable homefragment by default
        fragment = new HomeFragment();
        fragmentTag = "homeFragment";
        enableFragment(fragment, fragmentTag);

        //run necessary function
        setupVariable();
        setupEvent();
        setNumberOnNotificationIcon();
    }

    //whenever this activity opens, update the number of unread notification
    @Override
    protected void onResume() {
        super.onResume();
        setNumberOnNotificationIcon();
        Tooltip.setLocale(this, sharedPreferences);
    }

    private void setupVariable() {
        globaleVariable = (GlobaleVariable) this.getApplication();
        dialog = new Dialog(this);

        sharedPreferences = this.getApplication().getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);

        bottomNavigationView = findViewById(R.id.bottomNavigationMenu);
    }

    private void setupEvent() {
        //click item in bottom nav view

        bottomNavigationView.setOnItemReselectedListener(item -> {
            //click bat ky button thi update unread notif

            int shortcut = item.getItemId();

            if (shortcut == R.id.shortcutHome) {
                // setNumberOnNotif
                fragment = new HomeFragment();
                fragmentTag = "homeFragment";
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

        });
    }

    //activate a fragmment right away

    public void enableFragment(Fragment fragment, String fragmentTag) {
        //1- update lai fragmentTag de obBackpress
        this.fragmentTag = fragmentTag;

        //2
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        //3
        Map<String, String> headers = ((GlobaleVariable)getApplication()).getHeaders();
        String accessToken = headers.get("Authorization");
        String contentType = headers.get("Content-Type");

        //4
        Bundle bundle = new Bundle();
        bundle.putString("accessToken", accessToken);
        bundle.putString("contentType", contentType);
        fragment.setArguments(bundle);

        //5
        transaction.replace(R.id.frameLayout, fragment, fragmentTag);
        transaction.commit();

    }

    //nut quay lai
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

    //set nut notif icon
    public void setNumberOnNotificationIcon() {
        //1- set Retrofit
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //2- prepare header
        Map<String, String > header = globaleVariable.getHeaders();

        //3
        Call<NotificationReadAll> container = api.notificationReadAll(header);

        //4
        container.enqueue(new Callback<NotificationReadAll>() {
            @Override
            public void onResponse(@NonNull Call<NotificationReadAll> call, @NonNull Response<NotificationReadAll> response) {
                //neu thanh con thi update number unread notif
                if (response.isSuccessful()) {
                    NotificationReadAll content = response.body();
                    assert content != null;

                    //update unread notif
                    int quantityUnread = content.getQuantityUnread();
                    bottomNavigationView
                            .getOrCreateBadge(R.id.shortcutNotification)
                            .setNumber(quantityUnread);
                }
                //neu fail thi show exception
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

    /**
     * exit app
     * call o setting RecyclerView dong 64
     */

    public void exit()
    {
        SharedPreferences sharedPreferences = this.getApplication()
                .getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);

        sharedPreferences.edit().putString("accessToken", null).apply();
        sharedPreferences.edit().putInt("darkMode", 1).apply();// 1 is off, 2 is on
        sharedPreferences.edit().putString("language", getString(R.string.vietnamese)).apply();


        System.out.println(TAG);
        System.out.println("access token: " + sharedPreferences.getString("accessToken", null) );

        Intent intent = new Intent(this, LoginActivity.class);
        finish();
        startActivity(intent);
    }
}
