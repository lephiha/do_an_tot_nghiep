package com.lephiha.do_an.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.lephiha.do_an.Container.NotificationReadAll;
import com.lephiha.do_an.configAPI.HTTPRequest;
import com.lephiha.do_an.configAPI.HTTPService;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NotificationRepository {

    private final String TAG = "NotificationRepository";

    //animation
    private MutableLiveData<Boolean> animation = new MutableLiveData<>();
    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }

    //read all

    private final MutableLiveData<NotificationReadAll> readAllResponse = new MutableLiveData<>();

    public MutableLiveData<NotificationReadAll> getReadAllResponse() {
        return readAllResponse;
    }

    //function
    public MutableLiveData<NotificationReadAll> readAll(Map<String, String> header) {
        //1
        animation.setValue(true);

        //2
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //3
        Call<NotificationReadAll> container = api.notificationReadAll(header);

        //4
        container.enqueue(new Callback<NotificationReadAll>() {
            @Override
            public void onResponse(@NonNull Call<NotificationReadAll> call,@NonNull Response<NotificationReadAll> response) {

                if (response.isSuccessful()) {
                    NotificationReadAll content = response.body();
                    assert content != null;
                    readAllResponse.setValue(content);

                    animation.setValue(false);
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
            public void onFailure(@NonNull Call<NotificationReadAll> call,@NonNull Throwable t) {
                System.out.println("Notification Repository - Read All - error: " + t.getMessage());
            }

        });
        return readAllResponse;
    }
}
