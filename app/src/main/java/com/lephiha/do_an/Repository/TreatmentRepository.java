package com.lephiha.do_an.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.lephiha.do_an.Container.TreatmentReadAll;
import com.lephiha.do_an.Container.TreatmentReadByID;
import com.lephiha.do_an.configAPI.HTTPRequest;
import com.lephiha.do_an.configAPI.HTTPService;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TreatmentRepository {

    private final String TAG = "TreatmentRepository";

    //animation

    private final MutableLiveData<Boolean> animation = new MutableLiveData<>();

    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }

    //read all

    //getter
    private final MutableLiveData<TreatmentReadAll> readAllResponse = new MutableLiveData<>();
    //function

    public MutableLiveData<TreatmentReadAll> readAll (Map<String, String> headers, String appointmentId) {
        //1
        animation.setValue(true);

        //2
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //3
        Call<TreatmentReadAll> container = api.treatmentReadAll(headers, appointmentId);

        //4
        container.enqueue(new Callback<TreatmentReadAll>() {
            @Override
            public void onResponse(@NonNull Call<TreatmentReadAll> call,@NonNull Response<TreatmentReadAll> response) {
                if (response.isSuccessful()) {
                    TreatmentReadAll content = response.body();
                    assert content != null;
                    readAllResponse.setValue(content);
                    animation.setValue(false);
                    //System.out.println(TAG);
                }
                if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        System.out.println(jObjError);
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    readAllResponse.setValue(null);
                    animation.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TreatmentReadAll> call, @NonNull Throwable t) {
                System.out.println("Treatment Repository - Read All - error: " + t.getMessage());
                readAllResponse.setValue(null);
                animation.setValue(false);
            }
        });

        return readAllResponse;
    }

    //read by id

    //getter
    private final MutableLiveData<TreatmentReadByID> readByIDResponse = new MutableLiveData<>();
    //function
    public MutableLiveData<TreatmentReadByID> readByID (Map<String, String> headers, String treatmentId) {
        //1
        animation.setValue(true);

        //2
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //3
        Call<TreatmentReadByID> container = api.treatmentReadByID(headers, treatmentId);

        //4
        container.enqueue(new Callback<TreatmentReadByID>() {
            @Override
            public void onResponse(@NonNull Call<TreatmentReadByID> call,@NonNull Response<TreatmentReadByID> response) {
                if (response.isSuccessful()) {
                    TreatmentReadByID content = response.body();
                    assert content != null;
                    readByIDResponse.setValue(content);
                    animation.setValue(false);
                    //System.out.println(TAG);
                }
                if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        System.out.println(jObjError);
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    readByIDResponse.setValue(null);
                    animation.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TreatmentReadByID> call, @NonNull Throwable t) {
                System.out.println("Treatment Repository - Read By ID - error: " + t.getMessage());
                readByIDResponse.setValue(null);
                animation.setValue(false);
            }
        });
        return readByIDResponse;
    }
}
