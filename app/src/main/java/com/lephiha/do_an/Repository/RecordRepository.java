package com.lephiha.do_an.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.lephiha.do_an.Container.RecordReadByID;
import com.lephiha.do_an.configAPI.HTTPRequest;
import com.lephiha.do_an.configAPI.HTTPService;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RecordRepository {

    private final String TAG = "Record Repository";

    //animation
    private MutableLiveData<Boolean> animation = new MutableLiveData<>();
    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }

    //read by id
    private MutableLiveData<RecordReadByID> readByIDResponse = new MutableLiveData<>();
    public MutableLiveData<RecordReadByID> readByID(Map<String, String> header, String appoinmentId) {
        //1
        animation.setValue(true);

        //2
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //3
        Call<RecordReadByID> container = api.recordReadById(header, appoinmentId);

        //4
        container.enqueue(new Callback<RecordReadByID>() {
            @Override
            public void onResponse(@NonNull Call<RecordReadByID> call,@NonNull Response<RecordReadByID> response) {
                if (response.isSuccessful()) {
                    RecordReadByID content = response.body();
                    assert content != null;
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
                    readByIDResponse.setValue(null);
                    animation.setValue(false);
                }

            }

            @Override
            public void onFailure(@NonNull Call<RecordReadByID> call,@NonNull Throwable t) {
                    System.out.println("Record Repository - Read By ID - error: "+ t.getMessage());
                    animation.setValue(false);
            }
        });
        return readByIDResponse;
    }
}
