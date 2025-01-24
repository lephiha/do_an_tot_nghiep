package com.lephiha.do_an.Helper;

import android.app.Application;
import android.app.VoiceInteractor;

import com.google.firebase.FirebaseApp;
import com.lephiha.do_an.Model.Option;
import com.lephiha.do_an.Model.User;
import com.lephiha.do_an.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobaleVariable extends Application {
    private String accessToken;
    private User AuthUser;

    private final String SHARED_PREFERENCE_KEY = "doantotnghiep";
    private String contentType = "application/x-www-form-urlencoded";

    private Map<String, String> headers;
    @Override
    public void onCreate() {
        super.onCreate();

        // Khởi tạo Firebase
        FirebaseApp.initializeApp(this);
    }

    public Map<String, String> getHeaders() {

        this.headers = new HashMap<>();
        this.headers.put("Content-Type", contentType );
        this.headers.put("Authorization", accessToken);
        this.headers.put("type", "patient");

        return headers;
    }
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public User getAuthUser() {
        return AuthUser;
    }

    public void setAuthUser(User authUser) {
        AuthUser = authUser;
    }

    public String getSharedReferenceKey() {
        return SHARED_PREFERENCE_KEY;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public List<Option> getFilterOption() {
        List<Option> list = new ArrayList<>();

        Option option1 = new Option();
        option1.setIcon(R.drawable.ic_service);
        option1.setName(getString(R.string.service));

        Option option2 = new Option();
        option2.setIcon(R.drawable.ic_speciality);
        option2.setName(getString(R.string.speciality));

        Option option3 = new Option();
        option3.setIcon(R.drawable.ic_doctor);
        option3.setName(getString(R.string.doctor));

        list.add(option1);
        list.add(option2);
        list.add(option3);

        return list;
    }
}
