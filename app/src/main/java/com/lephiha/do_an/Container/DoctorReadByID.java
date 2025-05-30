package com.lephiha.do_an.Container;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lephiha.do_an.Model.Doctor;

public class DoctorReadByID {

    @SerializedName("result")
    @Expose
    private int result;

    @SerializedName("msg")
    @Expose
    private String msg;

    @SerializedName("data")
    @Expose
    private Doctor data;

    public int getResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    public Doctor getData() {
        return data;
    }
}
