package com.lephiha.do_an.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Main {
    @SerializedName("temp")
    @Expose
    private float temp;

    @SerializedName("feels_like")
    @Expose
    private float feelsLike;

    @SerializedName("temp_min")
    @Expose
    private float tempMin;

    @SerializedName("pressure")
    @Expose
    private float pressure;

    @SerializedName("humidity")
    @Expose
    private float humidity;

    public float getTemp() {
        return temp;
    }

    public float getFeelsLike() {
        return feelsLike;
    }

    public float getTempMin() {
        return tempMin;
    }

    public float getPressure() {
        return pressure;
    }

    public float getHumidity() {
        return humidity;
    }
}
