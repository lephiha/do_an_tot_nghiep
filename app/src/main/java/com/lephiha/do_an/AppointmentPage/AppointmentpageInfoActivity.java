package com.lephiha.do_an.AppointmentPage;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.LoadingScreen;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.R;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AppointmentpageInfoActivity extends AppCompatActivity {

    private final String TAG = "Appointment Info Activity";
    private Map<String, String> header;

    private Dialog dialog;
    private LoadingScreen loadingScreen;

    //data from recyclerView
    private String appointmentId; // day la id cua appointment maf benh nhan cho den luot
    private String myPosition; //day la vi tri patient trong hang doi
    private String doctorId;
    private boolean appointmentStatus = true; //la trang thai appointment == false, giấu recyclerView appointmen queue

    private CircleImageView imgDoctorAvatar;
    private TextView txtDoctorName;

    private TextView txtSpecialityName;
    private TextView txtLocation;

    private TextView txtPatientName;
    private TextView txtNumericalOrder;
    private TextView txtPatientBirthday;

    private TextView txtPosition;
    private TextView txtPatientPhone;
    private TextView txtPatientReason;

    private TextView txtAppointmentDate;
    private TextView txtAppointmentTime;

    private TextView txtStatusCancel;
    private TextView txtStatusDone;
    private TextView txtStatusProcessing;

    private AppCompatButton btnWatchMedicalRecord;
    private AppCompatButton btnWatchMedicalTreatment;

    private ImageButton btnBack;
    private RecyclerView appointmentQueueRecyclerView;
    private TextView appointmentQueueTitle;

    private AppointmentViewModel viewModel;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointmentpage_in4);

        setupComponent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (appointmentStatus) {
            getAppointmenQueue();
        }
    }

    //set component
    private void setupComponent() {
        GlobaleVariable globaleVariable = (GlobaleVariable) this.getApplication();
        dialog = new Dialog(this);
        loadingScreen = new LoadingScreen(this);

        header = globaleVariable.getHeaders();

        //data from appointment recyclerView
        appointmentId = getIntent().getStringExtra("id");
        myPosition = getIntent().getStringExtra("position");
        doctorId = getIntent().getStringExtra("doctorId");

        txtPosition = findViewById(R.id.txtPosition);
        imgDoctorAvatar = findViewById(R.id.imgDoctorAvatar);
        txtDoctorName = findViewById(R.id.txtDoctorName);

        txtSpecialityName = findViewById(R.id.txtSpecialityName);
        txtLocation = findViewById(R.id.txtLocation);

        txtPatientName = findViewById(R.id.txtPatientName);
        txtNumericalOrder = findViewById(R.id.txtNumericalOrder);
        txtPatientBirthday = findViewById(R.id.txtPatientBirthday);

        txtPatientPhone = findViewById(R.id.txtPatientPhone);
        txtPatientReason = findViewById(R.id.txtPatientReason);

        txtAppointmentDate = findViewById(R.id.txtDate);
        txtAppointmentTime = findViewById(R.id.txtAppointmentTime);

        txtStatusCancel = findViewById(R.id.txtStatusCancel);
        txtStatusDone = findViewById(R.id.txtStatusDone);
        txtStatusProcessing = findViewById(R.id.txtStatusProcessing);

        btnWatchMedicalRecord = findViewById(R.id.btnWatchMedicalRecord);
        btnWatchMedicalTreatment = findViewById(R.id.btnWatchMedicalTreatment);

        btnBack = findViewById(R.id.btnBack);
        appointmentQueueRecyclerView = findViewById(R.id.appointmentQueueRecyclerView);
        appointmentQueueTitle = findViewById(R.id.appointmentQueueTitle);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
    }

    //getAppointmentQueu: this function send GET request to get current appointment queue
    // send GET rq when appointmentStatus flag == true

    private void getAppointmenQueue() {
        if (!appointmentStatus) {
            return;
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("doctor_id", doctorId);
        parameters.put("date", Tooltip.getToday());
        parameters.put("order[column]", "position");
        parameters.put("order[dir]", "asc");
        parameters.put("length", "3");
        parameters.put("status", "processing");
        viewModel.getQueue(header, parameters);

        //get latest in4 from appointment

    }

}
