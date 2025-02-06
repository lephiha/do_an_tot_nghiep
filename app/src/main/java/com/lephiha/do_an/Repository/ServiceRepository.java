package com.lephiha.do_an.Repository;

import android.support.annotation.NonNull;

import androidx.lifecycle.MutableLiveData;

import com.lephiha.do_an.Container.ServiceReadAll;
import com.lephiha.do_an.Container.ServiceReadByID;
import com.lephiha.do_an.configAPI.HTTPRequest;
import com.lephiha.do_an.configAPI.HTTPService;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ServiceRepository {

    private final String TAG = "ServiceRepository";

    //animation
    private MutableLiveData<Boolean> animation = new MutableLiveData<>();

    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }

    /** read all **/
    //getter
    private MutableLiveData<ServiceReadAll> readAllResponse = new MutableLiveData<>();

    //function

    public MutableLiveData<ServiceReadAll> readAll(Map<String, String> headers, Map<String, String> parameters) {

        //step 1
        animation.setValue(true);

        //step 2
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //step 3
        Call<ServiceReadAll> container = api.serviceReadAll(headers, parameters);

        //step 4

        container.enqueue(new Callback<ServiceReadAll>() {
            @Override
            public void onResponse(@NonNull Call<ServiceReadAll> call, @NonNull Response<ServiceReadAll> response) {
                if (response.isSuccessful()) {
                    ServiceReadAll content = response.body();
                    assert content != null;

                    readAllResponse.setValue(content);
                    animation.setValue(false);
                }
                if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        System.out.println(jObjError);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    readAllResponse.setValue(null);
                    animation.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServiceReadAll> call, @NonNull Throwable t) {
                System.out.println("Service Repository - Read All - error: " + t.getMessage());
                //readAllResponse.setValue(null);
                animation.setValue(false);
            }
        });
        return readAllResponse;
    }

    /** read by id **/
    //getter
    private final MutableLiveData<ServiceReadByID> readByID = new MutableLiveData<>();

    public MutableLiveData<ServiceReadByID> readByID (Map<String, String> headers, String serviceId) {
        //step 1
        animation.setValue(true);

        //step 2
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //step 3
        Call<ServiceReadByID> container = api.serviceReadByID(headers, serviceId);

        //step 4
        container.enqueue(new Callback<ServiceReadByID>() {
            @Override
            public void onResponse(@NonNull Call<ServiceReadByID> call, @NonNull Response<ServiceReadByID> response) {
                if (response.isSuccessful()) {
                    ServiceReadByID content = response.body();
                    assert content != null;

                    readByID.setValue(content);
                    animation.setValue(false);
                }
                if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        System.out.println(jObjError);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    readByID.setValue(null);
                    animation.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServiceReadByID> call, @NonNull Throwable t) {
                System.out.println("Service Repository - Read By ID - error: " + t.getMessage());
                //readByID.setValue(null);
                animation.setValue(false);
            }
        });
        return readByID;
    }
}
