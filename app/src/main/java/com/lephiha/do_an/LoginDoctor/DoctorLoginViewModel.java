package com.lephiha.do_an.LoginDoctor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lephiha.do_an.Container.Login;
import com.lephiha.do_an.configAPI.HTTPRequest;
import com.lephiha.do_an.configAPI.HTTPService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DoctorLoginViewModel extends ViewModel {

    private MutableLiveData<Login> loginResponse = new MutableLiveData<>();

    public LiveData<Login> getLoginResponse() {
        return loginResponse;
    }

    public LiveData<Login> login(String email, String password, String type) {
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        Call<Login> container = api.login(email, password, type);

        container.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()) {
                    loginResponse.setValue(response.body());
                }
                else {
                    loginResponse.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                loginResponse.setValue(null);
            }
        });
        return loginResponse;
    }
}
