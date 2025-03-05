package com.lephiha.do_an.SettingPage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.LoadingScreen;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.R;

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
    }
}
