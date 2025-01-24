package com.lephiha.do_an.HomePage;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.R;

public class HomePageActivity extends AppCompatActivity {
    private final String TAG = "Homepage Activity";
    private Dialog dialog;
    private GlobaleVariable globaleVariable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
    }
}
