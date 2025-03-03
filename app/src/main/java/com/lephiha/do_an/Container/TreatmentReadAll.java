package com.lephiha.do_an.Container;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lephiha.do_an.Model.Treatment;

import java.util.List;

public class TreatmentReadAll {

    @SerializedName("result")
    @Expose
    private int result;

    @SerializedName("quantity")
    @Expose
    private int quantity;

    @SerializedName("data")
    @Expose
    private List<Treatment> data;

    public int getResult() {
        return result;
    }

    public int getQuantity() {
        return quantity;
    }

    public List<Treatment> getData() {
        return data;
    }
}
