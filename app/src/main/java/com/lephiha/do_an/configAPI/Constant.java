package com.lephiha.do_an.configAPI;

public class  Constant {

    public static String UPLOAD_URI() {
        return "http://192.168.56.1/Do_an_tot_nghiep_lph/api/assets/uploads/";
    }

    public static String APP_PATH()
    {
        return "http://192.168.56.1/Do_an_tot_nghiep_lph/";
    }

    public static String APP_PATH_EMULATOR()
    {
        return "http://10.0.2.2/Do_an_tot_nghiep_lph/";
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
        return "4167c6d6038647807b56abd84b7d6626";
    }

    public static String OPEN_WEATHER_MAP_API_KEY_2() {
        return "fc07e74110a9bcbc166c7887e51ec2db";
    }

    public static String OPEN_WEATHER_MAP_PATH() {
        // Hà Nội có tọa độ: lat = 21.0285, lon = 105.8542
        return "https://api.openweathermap.org/data/2.5/weather?lat=21.0285&lon=105.8542&appid=" + OPEN_WEATHER_MAP_API_KEY();
    }
}
