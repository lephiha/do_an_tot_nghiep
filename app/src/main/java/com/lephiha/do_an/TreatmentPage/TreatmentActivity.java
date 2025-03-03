package com.lephiha.do_an.TreatmentPage;

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

public class TreatmentActivity extends AppCompatActivity {

    private final String TAG = "Treatment Activity";

    private ImageButton btnBack;
    private SharedPreferences sharedPreferences;

    private final FragmentManager manager = getSupportFragmentManager();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment);

        setupComponent();
        setupEvent();
        setupTreatmentFragment1();
    }

    //setup component
    private void setupComponent() {
        btnBack = findViewById(R.id.btnBack);
        GlobaleVariable globaleVariable = (GlobaleVariable) this.getApplication();
        sharedPreferences = this.getApplication()
                .getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tooltip.setLocale(this, sharedPreferences);
    }

    private void setupTreatmentFragment1() {
        String fragmentTag = "Treatment Fragment 1";
        Fragment fragment = new TreatmentFragment1();

        //1
        FragmentTransaction transaction = manager.beginTransaction();

        //2
        transaction.replace(R.id.frameLayout, fragment, fragmentTag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //set event
    private void setupEvent() {
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
