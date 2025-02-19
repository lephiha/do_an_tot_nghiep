package com.lephiha.do_an.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.lephiha.do_an.Container.BookingCreate;
import com.lephiha.do_an.Container.BookingReadAll;
import com.lephiha.do_an.Container.BookingReadByID;
import com.lephiha.do_an.configAPI.HTTPRequest;
import com.lephiha.do_an.configAPI.HTTPService;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BookingRepository {

    private final String TAG = "Booking Repository";

    private MutableLiveData<Boolean> animation = new MutableLiveData<>();
    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }

    //create
    private MutableLiveData<BookingCreate> bookingCreate = new MutableLiveData<>();
    public MutableLiveData<BookingCreate> getBookingCreate() {
        return bookingCreate;
    }
    public MutableLiveData<BookingCreate> create (Map<String , String> header, Map<String, String> body) {
        //1
        animation.setValue(true);

        //2
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //3
        String doctorId = body.get("doctorId");
        String serviceId = body.get("serviceId");
        String bookingName = body.get("bookingName");
        String bookingPhone = body.get("bookingPhone");
        String name = body.get("name");
        String gender = body.get("gender");
        String address = body.get("address");
        String reason = body.get("reason");
        String birthday = body.get("birthday");
        String appointmentTime = body.get("appointmentTime");
        String appointmentDate = body.get("appointmentDate");

        Call<BookingCreate> container = api.bookingCreate(header, doctorId, serviceId, bookingName,
                bookingPhone, name, gender, address, reason, birthday, appointmentTime, appointmentDate);

        //4
        container.enqueue(new Callback<BookingCreate>() {
            @Override
            public void onResponse(@NonNull Call<BookingCreate> call,@NonNull Response<BookingCreate> response) {
                if (response.isSuccessful()) {
                    BookingCreate content = response.body();
                    assert content != null;

                    bookingCreate.postValue(content);
                    animation.setValue(false);
//                    System.out.println(TAG);
//                    System.out.println(content.getResult());
//                    System.out.println(content.getMsg());
                }
                if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        System.out.println(jObjError);
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    bookingCreate.postValue(null);
                    animation.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookingCreate> call,@NonNull Throwable t) {
                System.out.println("Booking Repository - Create - error: " + t.getMessage());
                //readAllResponse.setValue(null);
                animation.postValue(false);
            }
        });
        return bookingCreate;
    }

    //Read by id
    private MutableLiveData<BookingReadByID> bookingReadById = new MutableLiveData<>();
    public MutableLiveData<BookingReadByID> readByID (Map<String, String> header, String bookingId) {
        //1
        animation.setValue(true);

        //2
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //3
        Call<BookingReadByID> container = api.bookingReadByID(header, bookingId);

        //4
        container.enqueue(new Callback<BookingReadByID>() {
            @Override
            public void onResponse(@NonNull Call<BookingReadByID> call,@NonNull Response<BookingReadByID> response) {
                if (response.isSuccessful()) {
                    BookingReadByID content = response.body();
                    assert content != null;
                    bookingReadById.postValue(content);
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
                    bookingReadById.postValue(null);
                    animation.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookingReadByID> call,@NonNull Throwable t) {
                System.out.println("Booking Repository - Read By ID - error: " + t.getMessage());
                //readAllResponse.setValue(null);
                animation.postValue(false);
            }
        });
        return bookingReadById;
    }

    /*  Read All */
    private MutableLiveData<BookingReadAll> bookingReadAll = new MutableLiveData<>();
    public MutableLiveData<BookingReadAll> readAll (Map<String, String> header, Map<String, String> parameters) {
        //1
        animation.setValue(true);

        //2
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //3
        Call<BookingReadAll> container = api.bookingReadAll(header, parameters);

        //4

        container.enqueue(new Callback<BookingReadAll>() {
            @Override
            public void onResponse(@NonNull Call<BookingReadAll> call,@NonNull Response<BookingReadAll> response) {
                if (response.isSuccessful()) {
                    BookingReadAll content = response.body();
                    assert content != null;
                    bookingReadAll.postValue(content);
                    animation.setValue(false);
                    System.out.println(TAG);
                    System.out.println("result: " + content.getResult());
                    System.out.println("msg: " + content.getMsg());
                }

                if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        System.out.println(TAG);
                        System.out.println(jObjError);
                    }
                    catch (Exception e) {
                        System.out.println(TAG);
                        System.out.println(e);
                    }
                    animation.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookingReadAll> call,@NonNull Throwable t) {
                System.out.println(TAG);
                System.out.println("Booking Repository - Read All - error: " + t.getMessage());
                System.out.println(t);
                animation.postValue(false);
            }
        });
        return bookingReadAll;
    }
}
