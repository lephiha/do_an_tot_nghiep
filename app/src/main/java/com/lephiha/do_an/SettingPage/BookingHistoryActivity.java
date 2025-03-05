package com.lephiha.do_an.SettingPage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.LoadingScreen;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.Model.Booking;
import com.lephiha.do_an.R;
import com.lephiha.do_an.RecyclerView.BookingRecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingHistoryActivity extends AppCompatActivity {

    private final String TAG = "Booking History Activity";

    private ImageButton btnBack;
    private RecyclerView bookingRecyclerView;
    private Map<String, String> header;
    private Dialog dialog;
    private LoadingScreen loadingScreen;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        setupComponent();
        setupViewModel();
        setupEvent();
    }

    //set component
    private void setupComponent() {
        btnBack = findViewById(R.id.btnBack);
        bookingRecyclerView = findViewById(R.id.bookingRecyclerView);

        GlobaleVariable globaleVariable = (GlobaleVariable) this.getApplication();
        sharedPreferences = this.getApplication()
                .getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);

        header = globaleVariable.getHeaders();
        dialog = new Dialog(this);
        loadingScreen = new LoadingScreen(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Tooltip.setLocale(this, sharedPreferences);
    }

    //set viewModel

    private void setupViewModel() {
        //declare
        SettingsViewModel viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        viewModel.instantiate();

        //send request
        Map<String, String> parameters = new HashMap<>();
        parameters.put("length", "20");
        viewModel.bookingReadAll(header, parameters);

        viewModel.getBookingReadAll().observe(this, response -> {
            try {
                int result = response.getResult();

                if (result == 1) {
                    List<Booking> list = response.getData();
                    setupRecyclerView(list);
                }
                if (result == 0) {
                    dialog.announce();
                    dialog.show(R.string.attention, getString(R.string.check_your_internet_connection), R.drawable.ic_info);
                    dialog.btnOK.setOnClickListener(view -> {
                        dialog.close();
                        finish();
                    });
                }
            }
            catch (Exception e) {
                System.out.println(TAG);
                System.out.println(e);

                dialog.announce();
                dialog.show(R.string.attention, getString(R.string.check_your_internet_connection), R.drawable.ic_info);
                dialog.btnOK.setOnClickListener(view->{
                    dialog.close();
                    finish();
                });
            }
        });

        //animation
        viewModel.getAnimation().observe(this, aBoolean -> {
            if (aBoolean) {
                loadingScreen.start();
            } else {
                loadingScreen.stop();
            }
        });
    }

    //setup recycler view
    private void setupRecyclerView(List<Booking> list) {
        BookingRecyclerView bookingAdapter = new BookingRecyclerView(this, list);
        bookingRecyclerView.setAdapter(bookingAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        bookingRecyclerView.setLayoutManager(manager);
    }


    //set event
    private void setupEvent() {
        btnBack.setOnClickListener(view -> finish());
    }
}
