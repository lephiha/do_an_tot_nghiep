package com.lephiha.do_an.configAPI;


import com.lephiha.do_an.Container.AppointmentQueue;
import com.lephiha.do_an.Container.AppointmentReadAll;
import com.lephiha.do_an.Container.AppointmentReadByID;
import com.lephiha.do_an.Container.BookingCancel;
import com.lephiha.do_an.Container.BookingCreate;
import com.lephiha.do_an.Container.BookingPhotoDelete;
import com.lephiha.do_an.Container.BookingPhotoReadAll;
import com.lephiha.do_an.Container.BookingPhotoUpload;
import com.lephiha.do_an.Container.BookingReadAll;
import com.lephiha.do_an.Container.BookingReadByID;
import com.lephiha.do_an.Container.DoctorReadAll;
import com.lephiha.do_an.Container.DoctorReadByID;
import com.lephiha.do_an.Container.Login;
import com.lephiha.do_an.Container.NotificationCreate;
import com.lephiha.do_an.Container.NotificationMarkAllAsRead;
import com.lephiha.do_an.Container.NotificationMarkAsRead;
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

    @POST("api/patient/notifications/mark-as-read/{id}")
    Call<NotificationMarkAsRead> notificationMarkAsRead(@HeaderMap Map<String, String> header, @Path("id") String notificationId);


    @POST("api/patient/notifications")
    Call<NotificationMarkAllAsRead> notificationMarkAllAsRead(@HeaderMap Map <String, String> header);

    @FormUrlEncoded
    @PUT("api/patient/notifications")
    Call<NotificationCreate> notificationCreate(@HeaderMap Map <String, String> header,
                                                @Field("message") String message,
                                                @Field("record_id") String recordId,
                                                @Field("record_type") String recordType);



    //Weather - opent weather map.org
    @GET("https://api.openweathermap.org/data/2.5/weather")
    Call<Weather> getCurrentWeather(@QueryMap Map<String, String> parameters);


    //service
    @GET("api/services")
    Call<ServiceReadAll> serviceReadAll(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> parameters);

    @GET("api/services/{id}")
    Call<ServiceReadByID> serviceReadByID(@HeaderMap Map<String, String> headers, @Path("id") String id);

    //Booking
    @FormUrlEncoded
    @POST("api/patient/booking")
    Call<BookingCreate> bookingCreate(@HeaderMap Map<String, String> headers,
                                      @Field("doctor_id") String doctorId,
                                      @Field("service_id") String serviceId,
                                      @Field("booking_name") String bookingName,
                                      @Field("booking_phone") String bookingPhone,
                                      @Field("name") String name,
                                      @Field("gender") String gender,
                                      @Field("address") String address,
                                      @Field("reason") String reason,
                                      @Field("birthday") String birthday,
                                      @Field("appointment_time") String appointmentTime,
                                      @Field("appointment_date") String appointmentDate);

    @GET("api/patient/booking")
    Call<BookingReadAll> bookingReadAll(@HeaderMap Map<String, String> header, @HeaderMap Map<String, String> parameters);

    @GET("api/patient/booking/{id}")
    Call<BookingReadByID> bookingReadByID(@HeaderMap Map <String, String> header, @Path("id") String bookingId);

    @DELETE("api/patient/booking/{id}")
    Call<BookingCancel> bookingCancel(@HeaderMap Map <String, String> header, @Path("id") String bookingId);

    //Booking photo
    @GET("api/booking/photos/{id}")
    Call<BookingPhotoReadAll> bookingPhotoReadAll(@HeaderMap Map<String, String> headers, @Path("id") String id);

    @Multipart
    @POST("api/booking/upload-photo")
    Call<BookingPhotoUpload> bookingPhotoUpload(@Header("Authorization") String accessToken,
                                                @Header("Type") String type,
                                                @Part("booking_id") RequestBody bookingId,
                                                @Part MultipartBody.Part file);

    @DELETE("api/booking/photo/{id}")
    Call<BookingPhotoDelete> bookingPhotoDelete(@HeaderMap Map<String, String> header, @Path("id") int id);

    //appointment
    @GET("api/patient/appointments")
    Call<AppointmentReadAll> appointmentReadAll(@HeaderMap Map <String, String> header, @QueryMap Map<String, String> parameters);

    @GET("api/patient/appointments/{id}")
    Call<AppointmentReadByID> appointmentReadByID(@HeaderMap Map <String, String> header, @Path("id") String appointmentId);

    //appointment queue
    @GET("api/appointment-queue")
    Call<AppointmentQueue> appointmentQueue(@HeaderMap Map <String, String> header, @QueryMap Map<String, String> parameters);






}
