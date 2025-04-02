package com.lephiha.do_an.configAPI;

import com.lephiha.do_an.Model.CallDoctor;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("api/doctors")
    Call<List<CallDoctor>> getCallDoctor(@Query("pid") int pid);
}
