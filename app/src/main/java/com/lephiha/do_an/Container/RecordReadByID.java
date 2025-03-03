package com.lephiha.do_an.Container;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecordReadByID {
    @SerializedName("result")
    @Expose
    private int result;

    @SerializedName("msg")
    @Expose
    private String msg;

    @SerializedName("data")
    private Record data;

    public int getResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    public com.lephiha.do_an.Model.Record getData() {
        return data;
    }
}
