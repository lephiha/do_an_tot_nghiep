package com.lephiha.do_an.configAPI;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class  Constant {

//    public static String UPLOAD_URI() {
//        return "http://192.168.56.1:8080/Do_an_tot_nghiep_lph/api/assets/uploads/";
//    }

    public static String UPLOAD_URI() {
        return "https://profound-platypus-exactly.ngrok-free.app/Do_an_tot_nghiep_lph/api/assets/uploads/";
    }

//    public static String APP_PATH()
//    {
//        return "http://192.168.56.1:8080/Do_an_tot_nghiep_lph/";
//    }
    public static String APP_PATH()
    {
        return "https://profound-platypus-exactly.ngrok-free.app/Do_an_tot_nghiep_lph/";
    }

    public static String APP_PATH_EMULATOR()
    {
        return "http://10.0.2.2:8080/Do_an_tot_nghiep_lph/";
    }

    public static String VIDEO_PATH()
    {
        return "https://www.youtube.com/watch?v=abPmZCZZrFA";
    }
    //define app name
    public static String APP_NAME()
    {
        return "LeeHa Medical";
    }

    public static String accessToken;
    public static void setAccessToken(String accessToken)
    {
        Constant.accessToken = accessToken;
    }
    public static String getAccessToken()
    {
        return Constant.accessToken;
    }

    /**
     * đây là API key từ https://openweathermap.org/
     * tui dùng API này để lấy nhiệt độ của Hà Nội
     * https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}
     * lat là kinh độ của thành phố
     * lon là vĩ độ của thành phố
     * API Key là key bên dưới
     */
    public static String OPEN_WEATHER_MAP_API_KEY() {
        return "9a17a80007022feb2bed09db047e14f9";
    }



    public static String OPEN_WEATHER_MAP_PATH()
    {
        return "https://api.openweathermap.org/data/2.5/weather/";
    }

    private static final Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    private static Retrofit retrofit = null;
    private static Retrofit retrofit2 = null;
    private static Retrofit retrofitFastAPI = null;

    private static String BASE_URL ="http://192.168.56.1:8080/Do_an_tot_nghiep_lph/";
    //    private static String BASE_URL ="https://edoc.cloudkma.fun/";
    private static String IMAGE_URL="https://anh.moe/";
    private static String FASTAPI_URL = "http://10.0.2.2:8000/";

    public static ApiService getService(){
        if(retrofit==null)
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

        return retrofit.create(ApiService.class);
    }

    public static ApiService getImage(){
        if(retrofit2==null)
            retrofit2 = new Retrofit.Builder()
                    .baseUrl(IMAGE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();

        return retrofit2.create(ApiService.class);
    }

    public static ApiService getFastAPIService() {
        if (retrofitFastAPI == null) {
            retrofitFastAPI = new Retrofit.Builder()
                    .baseUrl(FASTAPI_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofitFastAPI.create(ApiService.class);
    }
}
