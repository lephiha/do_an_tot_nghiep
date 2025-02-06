package com.lephiha.do_an.ServicePage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lephiha.do_an.BookingPage.BookingPageActivity;
import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.LoadingScreen;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.Model.Doctor;
import com.lephiha.do_an.Model.Service;
import com.lephiha.do_an.R;
import com.lephiha.do_an.RecyclerView.DoctorRecyclerView;
import com.lephiha.do_an.configAPI.Constant;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceActivity extends AppCompatActivity {

    private final String TAG = "Service Activity";

    private String serviceId;

    private GlobaleVariable globaleVariable;
    private Dialog dialog;
    private LoadingScreen loadingScreen;

    private WebView wvwDescription;
    private TextView txtName;
    private ImageView imgAvatar;

    private ImageButton btnBack;
    private AppCompatButton btnCreateBooking;
    private RecyclerView doctorRecyclerView;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        setupComponent();
        setupViewModel();
        setupEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tooltip.setLocale(this, sharedPreferences);
    }

    //set component
    private void setupComponent() {
        serviceId = getIntent().getStringExtra("serviceId");
        globaleVariable = (GlobaleVariable) this.getApplication();
        sharedPreferences = this.getApplication()
                .getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);

        dialog = new Dialog(this);
        loadingScreen = new LoadingScreen(this);

        wvwDescription = findViewById(R.id.wvwDescription);
        txtName = findViewById(R.id.txtName);
        imgAvatar = findViewById(R.id.imgAvatar);

        btnBack = findViewById(R.id.btnBack);
        btnCreateBooking = findViewById(R.id.btnCreateBooking);
        doctorRecyclerView = findViewById(R.id.doctorRecyclerView);
    }

    //set view model
    private void setupViewModel() {
        ServiceViewModel viewModel = new ViewModelProvider(this).get(ServiceViewModel.class);
        viewModel.instantiate();

        //set up header and send request
        Map<String, String> header = globaleVariable.getHeaders();

        //listent response
        viewModel.readById(header, serviceId);
        viewModel.getResponse().observe(this, response -> {
            try {
                int result = response.getResult();

                if (result == 1) {
                    Service service = response.getData();
                    printServiceInfo(service);
                }
                if( result == 0)
                {
                    dialog.announce();
                    dialog.show(R.string.attention, getString(R.string.check_your_internet_connection), R.drawable.ic_info);
                    dialog.btnOK.setOnClickListener(view->{
                        dialog.close();
                        finish();
                    });
                }
            }
            catch (Exception ex) {
                /*Neu truy van lau qua ma khong nhan duoc phan hoi thi cung dong ung dung*/
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

        //doc read all
        Map<String, String> parameter = new HashMap<>();
        parameter.put("service_id", serviceId);
        viewModel.doctorReadAll(header, parameter);
        viewModel.getDoctorReadAllResponse().observe(this, response -> {

            try {
                int result = response.getResult();
                if (result == 1) { //luu thong tin vao hompage
                    List<Doctor> list = response.getData();
                    setupDoctorRecyclerView(list);
                }
                else {
                    dialog.announce();
                    dialog.show(R.string.attention, getString(R.string.check_your_internet_connection), R.drawable.ic_info);
                    dialog.btnOK.setOnClickListener(view -> {
                        dialog.close();
                        finish();
                    });
                }
            }
            catch (Exception ex) {
                dialog.announce();
                dialog.show(R.string.attention, getString(R.string.check_your_internet_connection), R.drawable.ic_info);
                dialog.btnOK.setOnClickListener(view -> {
                    dialog.close();
                    finish();
                });
            }
        });
    }

    private void printServiceInfo(Service service) {
        String image = Constant.UPLOAD_URI() + service.getImage();
        String name = service.getName();
        String description = "<html>"+
                "<style>body{font-size: 11px}</style>"+
                service.getDescription() + "</body></html>";

        txtName.setText(name);

        if (service.getImage().length() > 0) {
            Picasso.get().load(image).into(imgAvatar);
        }
        wvwDescription.loadData(description, "text/HTML", "UTF-8");
    }

    private void setupDoctorRecyclerView(List<Doctor> list) {
        DoctorRecyclerView doctorAdapter = new DoctorRecyclerView(this, list);
        doctorRecyclerView.setAdapter(doctorAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        doctorRecyclerView.setLayoutManager(manager);
    }


    //set event
    private void setupEvent() {
        btnBack.setOnClickListener(v -> finish());

        btnCreateBooking.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingPageActivity.class);
            intent.putExtra("serviceId", serviceId);
            startActivity(intent);
        });
    }
}
