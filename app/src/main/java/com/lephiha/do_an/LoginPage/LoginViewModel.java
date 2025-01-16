package com.lephiha.do_an.LoginPage;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lephiha.do_an.Container.Login;
import com.lephiha.do_an.configAPI.HTTPRequest;
import com.lephiha.do_an.configAPI.HTTPService;

import retrofit2.Retrofit;

public class LoginViewModel extends ViewModel {
    private String TAG = "LoginViewModel";
    private MutableLiveData<Login> loginWithPhoneResponse;
    private MutableLiveData<Boolean> animation;

    //getter
    public MutableLiveData<Login> getLoginWithPhoneResponse() {
        if (loginWithPhoneResponse == null) {
            loginWithPhoneResponse = new MutableLiveData<>();
        }
        return loginWithPhoneResponse;
    }

    public MutableLiveData<Boolean> getAnimation() {
        if (animation == null) {
            animation = new MutableLiveData<>();
        }
        return animation;
    }

        // Function
    public void loginWithPhone (String phone, String password) {
        animation.setValue(true);

        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);
    }
}
