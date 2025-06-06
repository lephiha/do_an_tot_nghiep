package com.lephiha.do_an.Container;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lephiha.do_an.Model.Record;

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

    public Record getData() {
        return data;
    }
}
