package com.lephiha.do_an.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.lephiha.do_an.Container.BookingPhotoReadAll;
import com.lephiha.do_an.configAPI.HTTPRequest;
import com.lephiha.do_an.configAPI.HTTPService;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BookingPhotoRepository {

    private final String TAG = "Booking Photo Repository";

    private final MutableLiveData<Boolean> animation = new MutableLiveData<>();
    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }


    //create
    private final MutableLiveData<BookingPhotoReadAll> readAllReponse = new MutableLiveData<>();
    public MutableLiveData<BookingPhotoReadAll> readAll(Map<String, String> header, String bookingId) {
        //1
        animation.setValue(true);

        //2
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //3
        Call<BookingPhotoReadAll> container = api.bookingPhotoReadAll(header, bookingId);

        //4
        container.enqueue(new Callback<BookingPhotoReadAll>() {
            @Override
            public void onResponse(@NonNull Call<BookingPhotoReadAll> call,@NonNull Response<BookingPhotoReadAll> response) {
                if (response.isSuccessful()) {
                    BookingPhotoReadAll content = response.body();
                    assert content != null;
                    readAllReponse.postValue(content);
                    animation.postValue(false);
                }

                if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        System.out.println(jObjError);
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    animation.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookingPhotoReadAll> call,@NonNull Throwable t) {
                System.out.println(TAG);
                System.out.println("Booking Photo Repository - Read All - error: " + t.getMessage());
                animation.postValue(false);
            }
        });
        return readAllReponse;
    }
}
