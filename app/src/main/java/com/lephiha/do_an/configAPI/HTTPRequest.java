package com.lephiha.do_an.configAPI;


import com.lephiha.do_an.Container.DoctorReadAll;
import com.lephiha.do_an.Container.DoctorReadByID;
import com.lephiha.do_an.Container.Login;
import com.lephiha.do_an.Container.NotificationReadAll;
import com.lephiha.do_an.Container.PatientProfile;
import com.lephiha.do_an.Container.PatientProfileChangeAvatar;
import com.lephiha.do_an.Container.PatientProfileChangePersonalInformation;
import com.lephiha.do_an.Container.ServiceReadAll;
import com.lephiha.do_an.Container.ServiceReadByID;
import com.lephiha.do_an.Container.SpecialityReadAll;
import com.lephiha.do_an.Container.SpecialityReadByID;
import com.lephiha.do_an.Container.Weather;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
public interface HTTPRequest {
    //Login with phone
    @FormUrlEncoded
    @POST("api/login")
    Call<Login> login(@Field("phone") String phone, @Field("password") String password, @Field("type") String type);

    //Login with google
    @FormUrlEncoded
    @POST("api/login/google")
    Call<Login> loginWithGoogle(@Field("email") String email, @Field("password") String password, @Field("type") String type);

    //PATIENT PROFILE - GET - READ PERSONAL INFORMATION
    @GET("api/patient/profile")
    Call<PatientProfile> readPersonalInformation(@HeaderMap Map<String, String> headers);

    @FormUrlEncoded
    @POST("api/patient/profile")
    Call<PatientProfileChangePersonalInformation> changePersonalInformation(@HeaderMap Map<String, String> header ,
                                                                            @Field("action") String action,
                                                                            @Field("name") String name,
                                                                            @Field("gender") String gender,
                                                                            @Field("birthday") String birthday,
                                                                            @Field("address") String address);

    @FormUrlEncoded
    @POST("api/patient/profile")
    Call<PatientProfileChangeAvatar> changeAvatar(@Header("Authorization") String accessToken,
                                                  @Header("Type") String type,
                                                  @Part MultipartBody.Part file,
                                                  @Part("action") RequestBody action);

    //Speciality

    @GET("api/specialities")
    Call<SpecialityReadAll> specialityReadAll(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> parameters);

    @GET("api/specialities/{id}")
    Call<SpecialityReadByID> specialityReadById(@HeaderMap Map<String, String> headers, @Path("id") String id);

    //Doctor

    @GET("api/doctors")
    Call<DoctorReadAll> doctorReadAll (@HeaderMap Map<String, String> headers,@QueryMap Map<String, String> parameters);

    @GET("api/doctors/{id}")
    Call<DoctorReadByID> doctorReadByID (@HeaderMap Map<String, String> headers, @Path("id") String id);

    //Notification
    @GET("api/patient/notifications")
    Call<NotificationReadAll> notificationReadAll(@HeaderMap Map<String, String> header);



    //Weather - opent weather map.org
    @GET("https://api.openweathermap.org/data/2.5/weather")
    Call<Weather> getCurrentWeather(@QueryMap Map<String, String> parameters);


    //service
    @GET("api/services")
    Call<ServiceReadAll> serviceReadAll(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> parameters);

    @GET("api/services/{id}")
    Call<ServiceReadByID> serviceReadByID(@HeaderMap Map<String, String> headers, @Path("id") String id);
}
