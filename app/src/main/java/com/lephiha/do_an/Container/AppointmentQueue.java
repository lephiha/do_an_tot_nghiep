package com.lephiha.do_an.Container;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lephiha.do_an.Model.Queue;

import java.util.List;


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

    public List<Queue> getData() {
        return data;
    }
}
