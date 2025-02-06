package com.lephiha.do_an.Container;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lephiha.do_an.Model.Service;

import java.util.List;

public class ServiceReadAll {
    @SerializedName("result")
    @Expose
    private int result;

    @SerializedName("quantity")
    @Expose
    private int quantity;

    @SerializedName("msg")
    @Expose
    private String msg;


    @SerializedName("data")
    @Expose
    private List<Service> data;

    public int getResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    public List<Service> getData() {
        return data;
    }

    public int getQuantity() {
        return quantity;
    }
}
