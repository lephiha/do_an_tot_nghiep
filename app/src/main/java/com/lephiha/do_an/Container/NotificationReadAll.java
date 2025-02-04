package com.lephiha.do_an.Container;

import android.app.Notification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NotificationReadAll {

    @SerializedName("result")
    @Expose
    private int result;

    @SerializedName("msg")
    @Expose
    private String msg;

    @SerializedName("quantity")
    @Expose
    private int quantity;

    @SerializedName("quantityUnread")
    @Expose
    private int quantityUnread;

    @SerializedName("data")
    @Expose
    private List<Notification> data;

    public int getResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getQuantityUnread() {
        return quantityUnread;
    }

    public List<Notification> getData() {
        return data;
    }
}
