package com.lephiha.do_an.Container;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationMarkAllAsRead {
    @SerializedName("result")
    @Expose
    private int result;

    @SerializedName("msg")
    @Expose
    private String msg;

    public int getResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }
}
