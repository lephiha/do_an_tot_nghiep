package com.lephiha.do_an.SpecialityPage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.LoadingScreen;
import com.lephiha.do_an.Model.Doctor;
import com.lephiha.do_an.Model.Speciality;
import com.lephiha.do_an.R;
import com.lephiha.do_an.RecyclerView.DoctorRecyclerView;
import com.lephiha.do_an.configAPI.Constant;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpecialityActivity extends AppCompatActivity {

    private final String TAG = "Speciality Activity";
    private TextView txtName;
    private WebView wvwDescription;
    private RecyclerView recyclerViewDoctor;

    private GlobaleVariable globaleVariable;
    private String specialityId;

    private LoadingScreen loadingScreen;
    private Dialog dialog;
    private ImageView imgAvatar;

    private ImageButton btnBack;
    private SharedPreferences sharedPreferences;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speciality);

        setupComponent();
        setupViewModel();
        setupEvent();
    }

    //componet
    private void setupComponent() {
        txtName = findViewById(R.id.txtName);
        wvwDescription = findViewById(R.id.wvwDescription);
        recyclerViewDoctor = findViewById(R.id.recyclerViewDoctor);

        imgAvatar = findViewById(R.id.imgAvatar);
        btnBack = findViewById(R.id.btnBack);
        loadingScreen = new LoadingScreen(this);
        dialog = new Dialog(this);

        sharedPreferences = this.getApplication().getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);
        globaleVariable = (GlobaleVariable) this.getApplication();
    }

    //view model
    private void setupViewModel() {
        //declare
        SpecialityViewModel viewModel = new ViewModelProvider(this).get(SpecialityViewModel.class);
        viewModel.instantiate();

        //set up header and send request
        Map<String, String> header = globaleVariable.getHeaders();
        viewModel.specialityReadById(header, specialityId);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("speciality_id", specialityId);
        viewModel.doctorReadAll(header, parameters);

        //animation
        viewModel.getAnimation().observe(this, aBoolean -> {
            if (aBoolean) {
                loadingScreen.start();
            } else {
                loadingScreen.stop();
            }
        });

        //listent for response

        viewModel.getDoctorReadAllResponse().observe(this, response -> {
            int result = response.getResult();

            try {
                if (result == 1) {
                    List<Doctor> list = response.getData();
                    setupDoctorRecyclerView(list);
                }
                else {
                    Toast.makeText(this, getString(R.string.oops_there_is_an_issue), Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (Exception ex) {
                System.out.println(TAG);
                System.out.println(ex.getMessage());
                Toast.makeText(this, getString(R.string.oops_there_is_an_issue), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.getSpecialityReadByIdResponse().observe(this, response -> {
            int result = response.getResult();

            try {
                if (result == 1) {
                    Speciality speciality = response.getData();
                    printSpecialityInformation(speciality);
                }
                else {
                    Toast.makeText(this, getString(R.string.oops_there_is_an_issue), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            catch (Exception ex) {
                System.out.println(TAG);
                System.out.println(ex.getMessage());
                Toast.makeText(this, getString(R.string.oops_there_is_an_issue), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    //event

    private void setupEvent() {
        btnBack.setOnClickListener(v -> finish());
    }

    //setup doctor recycler view

    private void setupDoctorRecyclerView(List<Doctor> list) {
        DoctorRecyclerView doctorAdapter = new DoctorRecyclerView(this, list);
        recyclerViewDoctor.setAdapter(doctorAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewDoctor.setLayoutManager(manager);
    }

    //print speciality information
    private void printSpecialityInformation(Speciality speciality) {
        String name = speciality.getName();
        String description = "<html>" +
                "<style>body{font-size: 11px}</style>"+
                "<body>"+  speciality.getDescription() +
                "</body>" +
                "</html>";

        String image = Constant.UPLOAD_URI() + speciality.getImage();

        txtName.setText(name);
        Picasso.get().load(image).into(imgAvatar);
        wvwDescription.loadDataWithBaseURL(null, description, "text/HTML", "UTF-8", null);
    }


}
