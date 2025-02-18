package com.lephiha.do_an.BookingPage;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.lephiha.do_an.R;

public class BookingPageActivity extends AppCompatActivity {

    private final String TAG = "Booking-page Activity";

    private ImageButton btnBack;
    private final FragmentManager manager = getSupportFragmentManager();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_bookingpage);

        setupBookingFragment1();
        setupComponent();
        setupEvent();
    }

    private void setupBookingFragment1() {
        String fragmentTag = "BookingFrament1";
        Fragment fragment = new BookingFragment1();

        //step 1
        FragmentTransaction transaction = manager.beginTransaction();

        //step 2
        String serviceId = getIntent().getStringExtra("serviceId");
        String doctorId = getIntent().getStringExtra("doctorId");

        //step3
        Bundle bundle = new Bundle();
        bundle.putString("serviceId", serviceId);
        bundle.putString("doctorId", doctorId);
        fragment.setArguments(bundle);

        //4
        transaction.replace(R.id.frameLayout, fragment, fragmentTag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setupComponent() {
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupEvent() {
        btnBack.setOnClickListener(view -> finish());
    }

    //override btn back of device
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
