package com.lephiha.do_an.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.lephiha.do_an.Container.DoctorReadAll;
import com.lephiha.do_an.Container.DoctorReadByID;
import com.lephiha.do_an.configAPI.HTTPRequest;
import com.lephiha.do_an.configAPI.HTTPService;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DoctorRepository {

    private final String TAG = "DoctorRepository";

    //animation
    private final MutableLiveData<Boolean> animation = new MutableLiveData<>();

    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }

    /** read all **/

    //getter
    private final MutableLiveData<DoctorReadAll> readAllResponse = new MutableLiveData<>();
    private MutableLiveData<DoctorReadAll> getReadAllResponse() {
        return readAllResponse;
    }

    //function

    public MutableLiveData<DoctorReadAll> readAll(Map<String, String> header, Map<String, String> parameters) {
        //1
        animation.setValue(true);

        //2
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //3
        Call<DoctorReadAll> container = api.doctorReadAll(header, parameters);

        //4

        container.enqueue(new Callback<DoctorReadAll>() {
            @Override
            public void onResponse(@NonNull Call<DoctorReadAll> call,@NonNull Response<DoctorReadAll> response) {
                if (response.isSuccessful()) {
                    DoctorReadAll content = response.body();
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
                    readAllResponse.setValue(null);
                    animation.setValue(false);
                }

            }

            @Override
            public void onFailure(@NonNull Call<DoctorReadAll> call,@NonNull Throwable t) {
                System.out.println("Doctor Repository - Read All - error: " + t.getMessage());
                //readAllResponse.setValue(null);
                animation.setValue(false);
            }
        });
        return  readAllResponse;
    }

    /** read by id **/
    //getter
    private MutableLiveData<DoctorReadByID> readByIdResponse = new MutableLiveData<>();
    public MutableLiveData<DoctorReadByID> getReadByIdResponse() {
        return readByIdResponse;
    }
    public MutableLiveData<DoctorReadByID> readById (Map<String, String> header, String doctorId) {
        //1
        animation.setValue(true);
        //2
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //3
        Call<DoctorReadByID> container = api.doctorReadByID(header, doctorId);

        //4
        container.enqueue(new Callback<DoctorReadByID>() {
            @Override
            public void onResponse(@NonNull Call<DoctorReadByID> call,@NonNull Response<DoctorReadByID> response) {
                if (response.isSuccessful()) {
                    DoctorReadByID content = response.body();
                    assert content != null;
                    readByIdResponse.setValue(content);
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
                    readByIdResponse.setValue(null);
                    animation.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<DoctorReadByID> call,@NonNull Throwable t) {
                System.out.println("Doctor Repository - Read By ID - error: " + t.getMessage());
                //readAllResponse.setValue(null);
                animation.setValue(false);
            }
        });
        return readByIdResponse;
    }
}
