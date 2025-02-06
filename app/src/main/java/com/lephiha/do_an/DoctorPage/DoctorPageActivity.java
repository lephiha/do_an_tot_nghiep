package com.lephiha.do_an.DoctorPage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import com.lephiha.do_an.BookingPage.BookingPageActivity;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.LoadingScreen;
import com.lephiha.do_an.Model.Doctor;
import com.lephiha.do_an.R;
import com.lephiha.do_an.configAPI.Constant;
import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorPageActivity extends AppCompatActivity {

    private final String TAG = "DoctorPage Activity";

    private String doctorId;
    private CircleImageView imgAvatar;
    private TextView txtName;

    private TextView txtSpeciality;
    private TextView txtPhoneNumber;
    private WebView wvwDescription;

    private DoctorPageViewModel viewModel;
    private GlobaleVariable globalVariable;
    private LoadingScreen loadingScreen;

    private ImageButton btnBack;
    private AppCompatButton btnCreateBooking;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_doctor);

        setupComponent();
        setupViewModel();
        setupEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*tu bo nho ROM cua thiet bi lay ra ngon ngu da cai dat cho ung dung*/
        String language = sharedPreferences.getString("language", getString(R.string.vietnamese));


//        System.out.println(TAG);
//        System.out.println(language);
        String vietnamese = getString(R.string.vietnamese);
        String deutsch = getString(R.string.deutsch);


        Locale myLocale = new Locale("en");
        if(Objects.equals(language, vietnamese))
        {
            myLocale = new Locale("vi");
        }
        if(Objects.equals(language,deutsch))
        {
            myLocale = new Locale("de");
        }


        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();


        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(myLocale);


        Locale.setDefault(myLocale);
        resources.updateConfiguration(configuration, displayMetrics);
    }

    //setup component

    private void setupComponent() {
        doctorId = getIntent().getStringExtra("doctorId");
        wvwDescription = findViewById(R.id.wvwDescription);

        imgAvatar = findViewById(R.id.imgAvatar);
        txtName = findViewById(R.id.txtName);
        txtSpeciality = findViewById(R.id.txtSpeciality);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);

        globalVariable = (GlobaleVariable) this.getApplication();
        sharedPreferences = this.getApplication().getSharedPreferences(globalVariable.getSharedReferenceKey(), MODE_PRIVATE);

        loadingScreen = new LoadingScreen(this);
        btnBack = findViewById(R.id.btnBack);
        btnCreateBooking = findViewById(R.id.btnCreateBooking);
    }

    //setup view model

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DoctorPageViewModel.class);
        viewModel.instantiate();

        Map<String, String> headers = globalVariable.getHeaders();
        viewModel.readById(headers, doctorId);

        viewModel.getResponse().observe(this, response -> {
            try {
                int result = response.getResult();
                if (result == 1) {
                    Doctor doctor = response.getData();
                    printDoctorInfo(doctor);
                }
                else {
                    Toast.makeText(this, getString(R.string.oops_there_is_an_issue), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            catch (Exception e) {
                System.out.println(TAG);
                System.out.println("setupViewModel - error: " + e.getMessage());
                Toast.makeText(this, getString(R.string.oops_there_is_an_issue), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.getAnimation().observe(this, aBoolean -> {
            if (aBoolean) {
                loadingScreen.start();
            } else {
                loadingScreen.stop();
            }
        });
    }

    //print doc in4
    private void printDoctorInfo(Doctor doctor) {
        String name = doctor.getName();
        String avatar = Constant.UPLOAD_URI() + doctor.getAvatar();
        String phone = doctor.getPhone();

        String description = "<html>" +
                "<style>body{font-size: 11px}</style>"+
                "<body>"+  doctor.getDescription() +
                "</body>" +
                "</html>";

        String speciality = doctor.getSpeciality().getName();

        txtName.setText(name);
        txtSpeciality.setText(speciality);
        txtPhoneNumber.setText(phone);
        Picasso.get().load(avatar).into(imgAvatar);

        wvwDescription.loadDataWithBaseURL(null, description, "text/HTML", "UTF-8", null);
    }

    //setup event

    private void setupEvent() {
        btnBack.setOnClickListener(view -> finish());

        btnCreateBooking.setOnClickListener(view -> {
            Intent intent = new Intent(this, BookingPageActivity.class);
            intent.putExtra("doctorId", doctorId);
            startActivity(intent);
        });
    }

}
