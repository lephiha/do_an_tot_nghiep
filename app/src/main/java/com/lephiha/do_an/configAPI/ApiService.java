package com.lephiha.do_an.configAPI;

import com.lephiha.do_an.Model.CallDoctor;
import com.lephiha.do_an.Model.ChatRequest;
import com.lephiha.do_an.Model.ChatResponse;
import com.lephiha.do_an.Model.Patient;
import com.lephiha.do_an.Model.ReturnData;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @GET("api/app/video_call/getcalldoctor.php")
    Call<List<CallDoctor>> getCallDoctor(@Query("pid") int pid);

    @FormUrlEncoded
    @POST("api/app/video_call/vnpay_create_payment.php")
    Call<ReturnData> createPayment(@Field("amount") int amount,
                                   @Field("order_id") long orderId,
                                   @Field("order_info") String orderInfo,
                                   @Field("doctor_id") int doctorId);

    @GET("api/app/video_call/getPatient.php")
    Call<Patient> getPatientById(@HeaderMap Map<String, String> headers, @Query("pid") int id);

    @POST("chat")
    Call<ChatResponse> getAnswer(@Body ChatRequest request);


}
