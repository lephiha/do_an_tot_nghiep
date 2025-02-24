package com.lephiha.do_an.Container;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Queue;

public class AppointmentQueue {

    @SerializedName("result")
    @Expose
    private int result;

    @SerializedName("msg")
    @Expose
    private String msg;

    @SerializedName("quantity")
    @Expose
    private int quantity;

    @SerializedName("data")
    @Expose
    private List<Queue> data;

    public int getResult () {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    public int getQuantity() {
        return quantity;
    }

    public List<com.lephiha.do_an.Model.Queue> getData() {
        return data;
    }
}
