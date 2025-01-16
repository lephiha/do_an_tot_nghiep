package com.lephiha.do_an.Container;

import android.content.Intent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lephiha.do_an.Model.User;

public class Login {
    @SerializedName("result")
    @Expose
    private Integer result;
    @SerializedName("msg")
    @Expose
    private String msg;

    @SerializedName("accessToken")
    @Expose
    private String accessToken;

    @SerializedName("data")
    @Expose
    private User data;

    public Integer getResult() {

        return result;
    }

    public String getMsg() {

        return msg;
    }

    public String getAccessToken() {

        return accessToken;
    }

    public User getData() {

        return data;
    }
}
