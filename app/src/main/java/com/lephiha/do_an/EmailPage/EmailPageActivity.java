package com.lephiha.do_an.EmailPage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.R;

public class EmailPageActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private final FragmentManager manager = getSupportFragmentManager();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        setupComponent();
        setupEmailFragmet();
        setupEvent();
    }

    private void setupComponent() {
        btnBack = findViewById(R.id.btnBack);
        GlobaleVariable globaleVariable = (GlobaleVariable) this.getApplication();
        sharedPreferences = this.getApplication().getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tooltip.setLocale(this, sharedPreferences);
    }

    //set email fragment
    private void setupEmailFragmet() {
        String fragmentTag = "EmailFragment";
        Fragment fragment = new EmailFragment1();

        //1
        FragmentTransaction transaction = manager.beginTransaction();

        //2
        String serviceId = getIntent().getStringExtra("serviceId");
        //3
        Bundle bundle = new Bundle();
        bundle.putString("serviceId", serviceId);
        fragment.getArguments();

        //4
        transaction.replace(R.id.frameLayout, fragment, fragmentTag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setupEvent() {
        btnBack.setOnClickListener(view -> finish());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
