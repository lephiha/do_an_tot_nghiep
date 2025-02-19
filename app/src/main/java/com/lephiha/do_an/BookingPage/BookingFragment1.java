package com.lephiha.do_an.BookingPage;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.LoadingScreen;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.Model.Doctor;
import com.lephiha.do_an.Model.Service;
import com.lephiha.do_an.Model.User;
import com.lephiha.do_an.R;
import com.lephiha.do_an.configAPI.Constant;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

//fragment 1 -> 3 -> 2

public class BookingFragment1 extends Fragment {
    private final String TAG = "BookingFragment1";

    private String serviceId; //neu serviceId = null thi nó sẽ bawng 1 vì api cần serviceId do ràng buộc dữ liệu trong db
    private String doctorId; //doctorId = null thì nó sẽ bằng 0, vì k cần

    private GlobaleVariable globaleVariable;
    private LoadingScreen loadingScreen;

    private Dialog dialog;

    private ImageView imgServiceAvatar;
    private TextView txtServiceName;

    private Activity activity;
    private Context context;
    private AppCompatButton btnConfirm;

    //form
    private EditText txtBookingName;
    private EditText txtBookingPhone;
    private EditText txtPatientName;

    private RadioGroup rdPatientRender;
    private EditText txtPatientBirthday;
    private EditText txtPatientAddress;
    private EditText txtPatientReason;
    private EditText txtAppointmentDate;
    private EditText txtAppointmentTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflate the layout this fragment
        View view = inflater.inflate(R.layout.fragment_booking1, container, false);

        setupComponent(view);
        setupViewModel();
        setupEvent();
        return view;
    }

    //set component

    private void setupComponent(View view) {
        //global variable
        activity = requireActivity();
        context = requireContext();

        globaleVariable = (GlobaleVariable) activity.getApplication();
        loadingScreen = new LoadingScreen(activity);
        dialog = new Dialog(context);
        User user = globaleVariable.getAuthUser();

        Bundle bundle = getArguments();
        assert bundle != null;
        serviceId = bundle.getString("serviceId") != null ? bundle.getString("serviceId") : "";
        doctorId = bundle.getString("doctorId") != null ? bundle.getString("doctorId") : "0";

        System.out.println(TAG);
        System.out.println("serviceId: " + serviceId);
        System.out.println("doctorId: " + doctorId);

        //Form
        imgServiceAvatar = view.findViewById(R.id.imgServiceAvatar);
        txtServiceName = view.findViewById(R.id.txtServiceName);
        btnConfirm = view.findViewById(R.id.btnConfirm);

        txtBookingName = view.findViewById(R.id.txtBookingName);
        txtBookingPhone = view.findViewById(R.id.txtBookingPhone);
        txtPatientName = view.findViewById(R.id.txtPatientName);

        rdPatientRender = view.findViewById(R.id.rdPatientGender);

        txtPatientBirthday = view.findViewById(R.id.txtPatientBirthday);
        txtPatientAddress = view.findViewById(R.id.txtPatientAddress);
        txtPatientReason = view.findViewById(R.id.txtPatientReason);

        txtAppointmentDate = view.findViewById(R.id.txtAppointmentDate);
        txtAppointmentTime = view.findViewById(R.id.txtAppointmentTime);

        //setup form
        txtBookingPhone.setText(user.getPhone());
        txtPatientBirthday.setText(user.getAddress());
        txtPatientAddress.setText(user.getAddress());
        txtAppointmentDate.setText(Tooltip.getToday());
        txtAppointmentTime.setText("9:00");
    }

    //setup View model
    private void setupViewModel() {
        //1.declare
        BookingViewModel viewModel = new ViewModelProvider(this).get(BookingViewModel.class);
        viewModel.instantiate();

        //2.prepare http header
        Map<String , String > header = globaleVariable.getHeaders();

        if (!Objects.equals(doctorId, "0")) {
            viewModel.doctorReadByID(header, doctorId);
        }
        else {
            viewModel.serviceReadById(header, serviceId);
        }

        //3. animation & listen for response
        viewModel.getAnimation().observe((LifecycleOwner) context, aBoolean -> {
            if (aBoolean) {
                loadingScreen.start();
            }
            else {
                loadingScreen.stop();
            }
        });

        //4. get service read by id response
        viewModel.getServiceReadByIdResponse().observe((LifecycleOwner) context, response -> {
            try {
                int result = response.getResult();
                //result = 1 => luuw thong tin vao homepage
                if (result == 1) {
                    Service service = response.getData();
                    printServiceIn4(service);
                }
                //result = 0 => thong bao out
                if (result == 0) {
                    System.out.println(TAG + "-result: " + result);
                    dialog.announce();
                    dialog.show("Attention", "Check your Internet connection", R.drawable.ic_info);
                    dialog.btnOK.setOnClickListener(view -> {
                        dialog.close();
                        activity.finish();
                    });

                }
            }
            catch (Exception e) {
                //neu truy van lau qua k nhan phan hoi thi cung out
                System.out.println(TAG);
                System.out.println("Exception: " + e.getMessage());
                dialog.announce();
                dialog.show("Attention", "Check your internet connection", R.drawable.ic_info);
                dialog.btnOK.setOnClickListener(view -> {
                    dialog.close();
                    activity.finish();
                });
            }
        });

        //5
        viewModel.getDoctorReadByIdResponse().observe((LifecycleOwner) context, response -> {
            try {
                int result = response.getResult();
                if (result == 1) {
                    Doctor doctor = response.getData();
                    printDoctorIn4(doctor);
                }
                if (result == 0 ) {
                    System.out.println(TAG + "-result: " + result);
                    dialog.announce();
                    dialog.show("Attention", "Check your internet connection", R.drawable.ic_info);
                    dialog.btnOK.setOnClickListener(view -> {
                        dialog.close();
                        activity.finish();
                    });
                }
            }
            catch (Exception e) {
                System.out.println(TAG);
                System.out.println("Exception: " + e.getMessage());
                dialog.announce();
                dialog.show(R.string.attention, getString(R.string.check_your_internet_connection), R.drawable.ic_info);
                dialog.btnOK.setOnClickListener(view->{
                    dialog.close();
                    activity.finish();
                });
            }
        });
    }

    //print service in4
    private void printServiceIn4(Service service) {
        String name = service.getName(); //ex: dat kham voi bs LPH
        String image = Constant.UPLOAD_URI() + service.getImage();

        txtServiceName.setText(name);
        if (service.getImage().length() >0) {
            Picasso.get().load(image).into(imgServiceAvatar);
        }
    }

    //print doc in4
    private void printDoctorIn4(Doctor doctor) {
        String name = getString(R.string.create_booking)+ " " + getString(R.string.with)
                +" "+ getString(R.string.doctor)
                +" "+ doctor.getName();

        String image = Constant.UPLOAD_URI() + doctor.getAvatar();

        txtServiceName.setText(name);
        if (doctor.getAvatar().length() >0) {
            Picasso.get().load(image).into(imgServiceAvatar);
        }
    }

    //set event
    private void setupEvent() {
        // prepare time + date picker for buuton
        //get today
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //date picker for birthday (day/month < 10, insert trc value

        DatePickerDialog.OnDateSetListener birthday = (view13, year1, month1, day1) -> {
            calendar.set(Calendar.YEAR, year1);
            calendar.set(Calendar.MONTH, month1);
            calendar.set(Calendar.DAY_OF_MONTH, day1);

            String dayFormatted = String.valueOf(day1);
            String monthFormatted = String.valueOf(month1+1); //+ 1 unit because 0 <= month <=11

            if (day1 < 10) {
                dayFormatted = "0" + day1;
            }
            if (month1 < 10) {
                monthFormatted = "0" + month1;
            }
            String output = year1 + "-" + monthFormatted +"-"+dayFormatted;
            txtPatientBirthday.setText(output);
        };

        
    }
}
