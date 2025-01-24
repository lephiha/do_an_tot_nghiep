package com.lephiha.do_an.configAPI;


import com.lephiha.do_an.Container.Login;

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

}
