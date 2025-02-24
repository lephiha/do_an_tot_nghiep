package com.lephiha.do_an.AppointmentPage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lephiha.do_an.Container.AppointmentReadByID;
import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.LoadingScreen;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.Model.Appointment;
import com.lephiha.do_an.Model.Queue;
import com.lephiha.do_an.R;
import com.lephiha.do_an.RecordPage.RecordActivity;
import com.lephiha.do_an.RecyclerView.AppointmentQueueRecyclerView;
import com.lephiha.do_an.TreatmentPage.TreatmentActivity;
import com.lephiha.do_an.configAPI.Constant;
import com.lephiha.do_an.configAPI.HTTPRequest;
import com.lephiha.do_an.configAPI.HTTPService;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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
        setupEvent();
        setupViewModel();
        setupUpdateAutomatically();
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
        requestAppointmentInfo(header, appointmentId);
    }

    //send request to get appointment in4
    private void requestAppointmentInfo(Map<String, String> header, String appointmentId) {
        //2
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //3
        Call<AppointmentReadByID> container = api.appointmentReadByID(header, appointmentId);

        //4
        container.enqueue(new Callback<AppointmentReadByID>() {
            @Override
            public void onResponse(@NonNull Call<AppointmentReadByID> call,@NonNull Response<AppointmentReadByID> response) {
                loadingScreen.stop();
                if (response.isSuccessful()) {
                    AppointmentReadByID content = response.body();
                    assert content != null;
                    Appointment appointment = content.getData();
                    printAppointmentIn4(appointment);
                    System.out.println(TAG);
//                    System.out.println("result: " + content.getResult());
//                    System.out.println("msg: " + content.getMsg());

                }
                if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        System.out.println(jObjError);
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AppointmentReadByID> call,@NonNull Throwable t) {
                System.out.println(TAG);
                System.out.println("Read By ID - error: " + t.getMessage());
            }
        });
    }

    //print appointment info
    private void printAppointmentIn4(Appointment appointment) {
        String doctorName = appointment.getDoctor().getName();
        String specialityName = appointment.getSpeciality().getName();
        String location = appointment.getRoom().getLocation() +", "+ appointment.getRoom().getName();

        int position = appointment.getPosition();
        String patientName = appointment.getPatientName();
        String patientBirthday = appointment.getPatientBirthday();
        String patientPhone = appointment.getPatientPhone();

        int numericalOrder = appointment.getNumericalOrder();
        String patientReason = appointment.getPatientReason();
        String appointmentDate = appointment.getDate();

        String appointmentTime = appointment.getAppointmentTime().length() > 0 ? appointment.getAppointmentTime() : getString(R.string.none);
        String status = appointment.getStatus();

        System.out.println(TAG);
//        System.out.println("NECESSARY INFORMATION");
//        System.out.println("doctor name: " + doctorName);
//        System.out.println("doctor avatar: " + Constant.UPLOAD_URI() + appointment.getDoctor().getAvatar());
//        System.out.println("speciality Name: " +specialityName);
//        System.out.println("location: " + location);
//        System.out.println("position: " + position);
//        System.out.println("patient name: " +patientName);
//        System.out.println("numerical order: " + numericalOrder);
//        System.out.println("patient birthday: " + patientBirthday);
//        System.out.println("patient phone: " +patientPhone);
//        System.out.println("patient reason: " + patientReason);
//        System.out.println("appointment date: " + appointmentDate);
//        System.out.println("appointment time: " + appointmentTime);
//        System.out.println("status: " + status);

        //doctor info
        if (appointment.getDoctor().getAvatar().length() > 0) {
            String doctorAvatar = Constant.UPLOAD_URI() + appointment.getDoctor().getAvatar();
            Picasso.get().load(doctorAvatar).into(imgDoctorAvatar);
        }

        txtSpecialityName.setText(specialityName);
        txtDoctorName.setText(doctorName);
        txtLocation.setText(location);

        //status
        if (Objects.equals(status, "processing")) {
            txtStatusProcessing.setVisibility(View.VISIBLE);
            txtStatusDone.setVisibility(View.GONE);
            txtStatusCancel.setVisibility(View.GONE);
            appointmentStatus = true; //show recyclerView appointment queue and send GET request to server
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            appointmentQueueRecyclerView.setVisibility(View.VISIBLE);
            appointmentQueueTitle.setVisibility(View.VISIBLE);
        }
        if (Objects.equals(status, "done")) {
            txtStatusProcessing.setVisibility(View.GONE);
            txtStatusDone.setVisibility(View.VISIBLE);
            txtStatusCancel.setVisibility(View.GONE);
            appointmentStatus = false;// we hide recycler view appointment queue and never send GET request to server
            swipeRefreshLayout.setVisibility(View.GONE);
            appointmentQueueRecyclerView.setVisibility(View.GONE);
            appointmentQueueTitle.setVisibility(View.GONE);
        }
        if(Objects.equals(status, "cancelled"))
        {
            txtStatusProcessing.setVisibility(View.GONE);
            txtStatusDone.setVisibility(View.GONE);
            txtStatusCancel.setVisibility(View.VISIBLE);
            appointmentStatus = false;// we hide recycler view appointment queue and never send GET request to server
            swipeRefreshLayout.setVisibility(View.GONE);
            appointmentQueueRecyclerView.setVisibility(View.GONE);
            appointmentQueueTitle.setVisibility(View.GONE);
        }

        //other information

        txtPosition.setText(String.valueOf(position));
        txtPatientName.setText(patientName);
        txtNumericalOrder.setText(String.valueOf(numericalOrder));
        txtPatientBirthday.setText(patientBirthday);
        txtPatientPhone.setText(patientPhone);
        txtPatientReason.setText(patientReason);
        txtAppointmentDate.setText(appointmentDate);
        txtAppointmentTime.setText(appointmentTime);


        //buttons - show 2 btn when status == done
        if (Objects.equals(status, "done")) {
            btnWatchMedicalTreatment.setVisibility(View.VISIBLE);
            btnWatchMedicalRecord.setVisibility(View.VISIBLE);
        }
        else {
            btnWatchMedicalTreatment.setVisibility(View.GONE);
            btnWatchMedicalRecord.setVisibility(View.GONE);
        }
    }

    private void setupEvent () {
        //btn back
        btnBack.setOnClickListener(view -> finish());

        //btn watch medical treatment
        btnWatchMedicalTreatment.setOnClickListener(view -> {
            Intent intent = new Intent(this, TreatmentActivity.class);
            intent.putExtra("appointmentId", appointmentId);
            this.startActivity(intent);
        });

        //btn watch medical record
        btnWatchMedicalRecord.setOnClickListener(view -> {
            Intent intent = new Intent(this, RecordActivity.class);
            intent.putExtra("appointmentId", appointmentId);
            this.startActivity(intent);
        });

        //swipe refresh layout
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            getAppointmenQueue();
        });
    }

    //set view model (if user appointment is not PROCESSING, hide recyclerView appointment queue and never request current appointment queue)

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);
        viewModel.instantiate();

        //send request read by id
        loadingScreen.start();
        requestAppointmentInfo(header, appointmentId);

        //send request - get appointment queue only when appointment status == true
        if (appointmentStatus) {
            getAppointmenQueue();
            viewModel.getAppointmentQueueResponse().observe(this, response -> {
                try {
                    int result = response.getResult();

                    if (result == 1) { //luu thong tin user vao home
                        List<Queue> list = response.getData();
                        setupAppointmentQueueRecyclerView(list);

                    }
                    if (result == 0 ) {
                        System.out.println(TAG);
                        System.out.println("READ ALL");
                        System.out.println("shut down by result == 0");
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
        }
        //animation
        viewModel.getAnimation().observe(this, aBoolean -> {
            if (aBoolean) {
                loadingScreen.start();
            }
            else {
                loadingScreen.stop();
            }
        });
    }

    //set up appointment queue recyclerView
    private void setupAppointmentQueueRecyclerView(List<Queue> list) {
        AppointmentQueueRecyclerView appointmentAdapter = new AppointmentQueueRecyclerView(this, list, Integer.parseInt(myPosition));
        appointmentQueueRecyclerView.setAdapter(appointmentAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        appointmentQueueRecyclerView.setLayoutManager(manager);
    }

    // this function will run update view model - Get appointment queue - moi 45s if device active
    final Handler handler = new Handler();
    final int delay = 1000 * 45; //delay 45s

    private void setupUpdateAutomatically() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getAppointmenQueue();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

}
