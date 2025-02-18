package com.lephiha.do_an.BookingPage;

import android.app.Activity;
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

import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.LoadingScreen;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.Model.User;
import com.lephiha.do_an.R;

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

}
