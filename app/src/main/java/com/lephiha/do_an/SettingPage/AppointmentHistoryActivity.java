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
import com.lephiha.do_an.Model.Appointment;
import com.lephiha.do_an.R;
import com.lephiha.do_an.RecyclerView.Appointment2RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentHistoryActivity extends AppCompatActivity {

    private final String TAG = "Appointment History Activity";
    private ImageButton btnBack;
    private RecyclerView appointmentRecyclerView;

    private Map<String , String> header;
    private Dialog dialog;
    private LoadingScreen loadingScreen;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_history);

        setupComponent();
        setupViewModel();
        setupEvent();
    }

    //set component
    private void setupComponent() {
        GlobaleVariable globaleVariable = (GlobaleVariable) this.getApplication();
        sharedPreferences = this.getApplication()
                .getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);

        btnBack = findViewById(R.id.btnBack);
        appointmentRecyclerView = findViewById(R.id.appointmentRecyclerView);

        header = globaleVariable.getHeaders();
        dialog = new Dialog(this);
        loadingScreen = new LoadingScreen(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tooltip.setLocale(this, sharedPreferences);
    }

    private void setupViewModel() {
        SettingsViewModel viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        viewModel.instantiate();

        //send request
        Map<String, String> parameters = new HashMap<>();
        parameters.put("order[dir]", "desc");
        parameters.put("order[column]", "date");
        viewModel.readAll(header, parameters);
        viewModel.getReadAllResponse().observe(this, response -> {
            try {
                int result = response.getResult();

                if (result == 1) { //luu tt user va vao homepage
                    List<Appointment> list = response.getData();
                    setupRecyclerView(list);
                }

                if (result == 0 ) {
                    System.out.println(TAG);
                    System.out.println("Read All");
                    System.out.println("shut down by result = 0");
                    System.out.println("msg: " + response.getMsg());
                    dialog.announce();
                    dialog.show(R.string.attention, getString(R.string.oops_there_is_an_issue), R.drawable.ic_info);
                    dialog.btnOK.setOnClickListener(view -> {
                        dialog.close();
                        finish();
                    });
                }
            }
            catch (Exception e) {
                System.out.println(TAG);
                System.out.println("READ ALL");
                System.out.println("shut down by exception");
                System.out.println(e);
                /*Neu truy van lau qua ma khong nhan duoc phan hoi thi cung dong ung dung*/
                dialog.announce();
                dialog.show(R.string.attention, getString(R.string.check_your_internet_connection), R.drawable.ic_info);
                dialog.btnOK.setOnClickListener(view -> {
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
    private void setupRecyclerView(List<Appointment> list) {
        Appointment2RecyclerView appointmentAdapter = new Appointment2RecyclerView(this, list);
        appointmentRecyclerView.setAdapter(appointmentAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        appointmentRecyclerView.setLayoutManager(manager);
    }

    //setup event
    private void setupEvent() {
        btnBack.setOnClickListener(view -> finish());
    }
}
