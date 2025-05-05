package com.lephiha.do_an.RecordPage;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.LoadingScreen;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.Model.Record;
import com.lephiha.do_an.R;
import com.lephiha.do_an.configAPI.Constant;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class RecordActivity extends AppCompatActivity {
    private final String TAG = "Record Activity";

    private String appointmentId;

    private Dialog dialog;
    private LoadingScreen loadingScreen;
    private Map<String, String> header;

    private ImageView imgDoctorAvatar;
    private TextView txtDoctorName;
    private TextView txtSpecialityName;
    private TextView txtDatetime;

    private TextView txtAppointmentReason;
    private TextView txtStatusBefore;
    private TextView txtStatusAfter;

    private TextView txtReason;
    private WebView wvwDescription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        setupComponent();
        setupViewModel();
    }

    //setup component
    private void setupComponent() {
        dialog = new Dialog(this);
        GlobaleVariable globaleVariable = (GlobaleVariable) this.getApplication();
        header = globaleVariable.getHeaders();
        loadingScreen = new LoadingScreen(this);

        appointmentId = getIntent().getStringExtra("appointmentId");

        if(TextUtils.isEmpty(appointmentId) )
        {
            System.out.println(TAG);
            System.out.println("appointmentId is empty !");
            dialog.announce();
            dialog.show(R.string.attention, getString(R.string.oops_there_is_an_issue), R.drawable.ic_close);
            dialog.btnOK.setOnClickListener(view -> {
                dialog.close();
                this.finish();
            });
        }


        imgDoctorAvatar = findViewById(R.id.imgDoctorAvatar);
        txtDoctorName = findViewById(R.id.txtDoctorName);

        txtSpecialityName = findViewById(R.id.txtSpecialityName);
        txtDatetime = findViewById(R.id.txtDatetime);

        txtAppointmentReason = findViewById(R.id.txtAppointmentReason);
        txtStatusAfter = findViewById(R.id.txtStatusAfter);

        txtStatusBefore = findViewById(R.id.txtStatusBefore);
        txtReason = findViewById(R.id.txtReason);
        wvwDescription = findViewById(R.id.wvwDescription);
    }

    private void setupViewModel() {
        //setup view model
        RecordViewModel viewModel = new ViewModelProvider(this).get(RecordViewModel.class);
        viewModel.instantiate();

        //send request
        viewModel.readByID(header, appointmentId);
        viewModel.getReadByIDResponse().observe(this, response->{
            try {
                int result = response.getResult();
                /*result == 1 => luu thong tin nguoi dung va vao homepage*/
                if (result == 1) {
                    Record record = response.getData();
                    printRecordIn4(record);
                }
                /*result == 0 => thong bao va thoat ung dung*/
                if (result == 0) {
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

            } catch (Exception ex) {
                System.out.println(TAG);
                System.out.println("READ ALL");
                System.out.println("shut down by exception");
                System.out.println(ex);
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

    //print record info
    private void printRecordIn4(Record record) {
        //doctor in4

        if( record.getDoctor().getAvatar().length() > 0)
        {
            String avatar = Constant.UPLOAD_URI() + record.getDoctor().getAvatar();
            Picasso.get().load(avatar).into(imgDoctorAvatar);
        }
        String doctorName = getString(R.string.doctor) + " " + record.getDoctor().getName();
        txtDoctorName.setText(doctorName);

        String datetime = Tooltip.beautifierDatetime(this, record.getCreateAt());
        txtDatetime.setText(datetime);

        String appointmentReason = record.getAppointment().getPatientReason();
        txtAppointmentReason.setText(appointmentReason);

        String statusBefore = record.getStatusBefore();
        txtStatusBefore.setText(statusBefore);

        String statusAfter = record.getStatusAfter();
        txtStatusAfter.setText(statusAfter);

        String description = "<html>" +
                "<style>body{font-size: 11px}</style>" +
                "<body>"+ record.getDescription()
                +"</body></html>";
        wvwDescription.loadDataWithBaseURL(null, description, "text/HTML", "UTF-8", null);
    }
}
