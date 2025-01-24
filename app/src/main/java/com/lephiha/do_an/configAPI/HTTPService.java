package com.lephiha.do_an.configAPI;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
public class HTTPService {
    private static final String APP_PATH = Constant.APP_PATH();
    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        Gson gson = new GsonBuilder().setLenient().create();

        retrofit = new Retrofit.Builder().baseUrl(APP_PATH)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit;
    }

    //homePage sử dụng

    public static final String OPEN_WEATHER_MAP = Constant.OPEN_WEATHER_MAP_PATH();
    public static Retrofit getOpenWeatherMapInstance()
    {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(OPEN_WEATHER_MAP)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit;
    }
}
