package com.lephiha.do_an.Repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.lephiha.do_an.Container.SpecialityReadAll;
import com.lephiha.do_an.Container.SpecialityReadByID;
import com.lephiha.do_an.configAPI.HTTPRequest;
import com.lephiha.do_an.configAPI.HTTPService;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SpecialRespository {
    private final String TAG = "SpecialityRespository";

    //animation
    private final MutableLiveData<Boolean> animation = new MutableLiveData<>();

    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }

    /** read all ***/

    //getter
    private final MutableLiveData<SpecialityReadAll> readAllResponsse = new MutableLiveData<>();
    //function
    public MutableLiveData<SpecialityReadAll> readAll(Map<String, String> headers, Map<String, String> parameters) {
        //1
        animation.setValue(true);

        //2
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //3
        Call<SpecialityReadAll> container = api.specialityReadAll(headers, parameters);

        //4
        container.enqueue(new Callback<SpecialityReadAll>() {
            @Override
            public void onResponse(@NonNull Call<SpecialityReadAll> call, @NonNull Response<SpecialityReadAll> response) {
                if (response.isSuccessful()) {
                    SpecialityReadAll content = response.body();
                    assert content != null;
                    readAllResponsse.setValue(content);
                    animation.setValue(false);
                    System.out.println(TAG);
                    System.out.println("quantity: " + content.getQuantity());
                }

                if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        System.out.println(jObjError);
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    readAllResponsse.setValue(null);
                    animation.setValue(false);
                }
            }

            @Override
            public void onFailure(@Nullable Call<SpecialityReadAll> call,@NonNull Throwable t) {
                System.out.println("Speciality Repository - Read All - error: " + t.getMessage());
                readAllResponsse.setValue(null);
                animation.setValue(false);

            }
        });
        return readAllResponsse;
    }

    /** read by id **/

    //getter
    private final MutableLiveData<SpecialityReadByID> readByID = new MutableLiveData<>();
    //function

    public MutableLiveData<SpecialityReadByID> readByID (Map<String, String> headers, String specialityId) {
        //1
        animation.setValue(true);
        //2
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //3
        Call<SpecialityReadByID> container = api.specialityReadById(headers, specialityId);

        //4
        container.enqueue(new Callback<SpecialityReadByID>() {
            @Override
            public void onResponse(@NonNull Call<SpecialityReadByID> call,@NonNull Response<SpecialityReadByID> response) {
                if (response.isSuccessful()) {
                    SpecialityReadByID content = response.body();
                    assert content != null;
                    readByID.setValue(content);
                    animation.setValue(false);

                    System.out.println(TAG);
                    System.out.println("result: " + content.getResult());
                   //System.out.println("quantity: " + content.getQuantity());
                }
                if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        System.out.println( jObjError );

                    }catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    readByID.setValue(null);
                    animation.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<SpecialityReadByID> call,@NonNull Throwable t) {
                System.out.println("Speciality Repository - Read By ID - error: " + t.getMessage());
                readByID.setValue(null);
                animation.setValue(false);

            }
        });
        return  readByID;
    }
}
