package com.lephiha.do_an.BookingPage;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.lephiha.do_an.Container.BookingCreate;
import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.LoadingScreen;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.HomePage.HomePageActivity;
import com.lephiha.do_an.Model.Doctor;
import com.lephiha.do_an.Model.Service;
import com.lephiha.do_an.Model.User;
import com.lephiha.do_an.R;
import com.lephiha.do_an.configAPI.Constant;
import com.lephiha.do_an.configAPI.HTTPRequest;
import com.lephiha.do_an.configAPI.HTTPService;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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
        setupEvent(view);
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
        txtPatientBirthday.setText(user.getBirthday());
        txtPatientAddress.setText(user.getAddress());
        txtAppointmentDate.setText(Tooltip.getToday());
        txtAppointmentTime.setText(R.string.default_appointment_time);
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
        String name = getString(R.string.create_booking)
                + " " + getString(R.string.with)
                + " " + getString(R.string.doctor)
                + " " + doctor.getName();// for instance: Đặt lịch khám với bác sĩ Hà
        String image = Constant.UPLOAD_URI() + doctor.getAvatar();

        txtServiceName.setText(name);
        if (doctor.getAvatar().length() >0) {
            Picasso.get().load(image).into(imgServiceAvatar);
        }
    }

    //set event
    private void setupEvent(View view) {
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
            String monthFormatted = String.valueOf(month1+1);// add 1 unit because 0 <= month <=11

            if( day1 < 10)
            {
                dayFormatted = "0" + day1;
            }
            if( month1 < 10 )
            {
                monthFormatted = "0" + month1;
            }
            String output = year1 + "-" + monthFormatted + "-" + dayFormatted;
            txtPatientBirthday.setText(output);
        };

        //date picker for appointment date
        DatePickerDialog.OnDateSetListener appointmentDateDialog = (view13, year1, month1, day1) -> {
            calendar.set(Calendar.YEAR, year1);
            calendar.set(Calendar.MONTH, month1);
            calendar.set(Calendar.DAY_OF_MONTH, day1);

            String dayFormatted = String.valueOf(day1);
            String monthFormatted = String.valueOf(month1+1);
            if( day1 < 10)
            {
                dayFormatted = "0" + day1;
            }
            if( month1 < 10 )
            {
                monthFormatted = "0" + month1;
            }
            String output = year1 + "-" + monthFormatted + "-" + dayFormatted;
            txtAppointmentDate.setText(output);
        };

        //time picker for appointment time
        TimePickerDialog.OnTimeSetListener appointmentTimeDialog = (timePicker, hour, minute) -> {
            String hourFormatted = String.valueOf(hour);
            String minuteFormatted = String.valueOf(minute);
            if(hour < 10)
            {
                hourFormatted = "0" + hour;
            }
            if( minute < 10)
            {
                minuteFormatted = "0" + minute;
            }
            String output = hourFormatted + ":" + minuteFormatted;
            txtAppointmentTime.setText(output);
        };

        //listen click event for buttons
        txtPatientBirthday.setOnClickListener(birthdayView -> new DatePickerDialog(context,birthday,year,month,day).show());

        txtAppointmentDate.setOnClickListener(appointmentDateView-> new DatePickerDialog(context, appointmentDateDialog, year, month, day).show());

        txtAppointmentTime.setOnClickListener(appointmentTimeView-> new TimePickerDialog(context, appointmentTimeDialog, 9, 0, true).show() );

        btnConfirm.setOnClickListener(view1 -> {
            //1. user must fill up all mandatory fields
            boolean flag = areMandatoryFieldsFilledUp();
            if (!flag) {
                return;
            }

            //2. get date that user enters
            String bookingName = txtBookingName.getText().toString();
            String bookingPhone = txtBookingPhone.getText().toString();
            String patientName = txtPatientName.getText().toString();

            int selectedId = rdPatientRender.getCheckedRadioButtonId();
            RadioButton radioButton = view.findViewById(selectedId);
            String patientGender = radioButton.getHint().toString();
            String patientAddress = txtPatientAddress.getText().toString();
            String patientReason = txtPatientReason.getText().toString();

            String patientBirthday = txtPatientBirthday.getText().toString();
            String appointmentDate = txtAppointmentDate.getText().toString();
            String appointmentTime = txtAppointmentTime.getText().toString();

            //3. setup header + body for Post request
            Map<String, String> header = globaleVariable.getHeaders();
            Map<String, String> body = new HashMap<>();
            body.put("serviceId", serviceId);
            body.put("doctorId", doctorId);
            body.put("bookingName", bookingName);
            body.put("bookingPhone", bookingPhone);
            body.put("name", patientName);
            body.put("gender", patientGender);
            body.put("address", patientAddress);
            body.put("reason", patientReason);
            body.put("birthday", patientBirthday);
            body.put("appointmentTime", appointmentTime);
            body.put("appointmentDate", appointmentDate);

            //load truc tiep POST request = retrofit để tránh việc tạo ra nh observer mỗi lần ấn nút gửi yêu cầu
            loadingScreen.start();
            sendBookingCreate(header, body);
        });
    }

    //check mandatory filled up
    private boolean areMandatoryFieldsFilledUp() {
        String bookingName = txtBookingName.getText().toString();
        String  bookingPhone = txtBookingPhone.getText().toString();
        String patientName = txtPatientName.getText().toString();
        String appointmentDate = txtAppointmentDate.getText().toString();
        String appointmentTime = txtAppointmentTime.getText().toString();

        String[] requiredFields = {
                bookingName, bookingPhone, patientName, appointmentDate, appointmentTime
        };

        for (String element : requiredFields) {
            if (TextUtils.isEmpty(element)) {
                dialog.announce();
                dialog.show(R.string.attention,context.getString(R.string.you_do_not_fill_mandatory_field_try_again), R.drawable.ic_info);
                dialog.btnOK.setOnClickListener(view -> dialog.close());
                return false;
            }
        }
        return true;
    }

    private void sendBookingCreate(Map<String, String > header, Map<String,String> body) {
        //2
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //3
        String serviceId = body.get("serviceId");
        String doctorId = body.get("doctorId");
        String bookingName = body.get("bookingName");
        String bookingPhone = body.get("bookingPhone");
        String name = body.get("name");
        String gender = body.get("gender");
        String address = body.get("address");
        String reason = body.get("reason");
        String birthday = body.get("birthday");
        String appointmentTime = body.get("appointmentTime");
        String appointmentDate = body.get("appointmentDate");

        Call<BookingCreate> container = api.bookingCreate(header, doctorId, serviceId,
                bookingName, bookingPhone, name, gender, address, reason, birthday, appointmentTime, appointmentDate);

        //4
        Log.d("BookingRequest", "Sending request with data: " + bookingName + ", " + bookingPhone);
        container.enqueue(new Callback<BookingCreate>() {
            @Override
            public void onResponse(@NonNull Call<BookingCreate> call, @NonNull Response<BookingCreate> response) {
                loadingScreen.stop();
                if(response.isSuccessful())
                {
                    BookingCreate content = response.body();
                    assert content != null;
                    processWithPOSTResponse(content);
                }
                if(response.errorBody() != null)
                {
                    try
                    {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        System.out.println( jObjError );
                    }
                    catch (Exception e) {
                        System.out.println( e.getMessage() );
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookingCreate> call, @NonNull Throwable t) {
                loadingScreen.stop();
                System.out.println("Booking Fragment - Create - error: " + t.getMessage());
            }
        });
    }

    //process with th response from POST request that we send to server

    private void processWithPOSTResponse(BookingCreate response) {
        //1. prepare dialog if error
        dialog.announce();
        dialog.btnOK.setOnClickListener(view -> dialog.close());

        //2. show result

        try
        {
            int result = response.getResult();
            if( result == 1)// create successfully -> go to next booking fragment
            {
                Toast.makeText(context, context.getString(R.string.success), Toast.LENGTH_SHORT).show();
                String fragmentTag = "bookingFragment3";

                Bundle bundle = new Bundle();
                bundle.putString("bookingId", String.valueOf(response.getData().getId()));


                BookingFragment3 nextFragment = new BookingFragment3();
                nextFragment.setArguments(bundle);


                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, nextFragment, fragmentTag)
                        .addToBackStack(fragmentTag)
                        .commit();

                HomePageActivity.getInstance().setNumberOnNotificationIcon();
            }
            else// create failed -> show error message
            {
                String message = response.getMsg();
                dialog.show(R.string.attention, message, R.drawable.ic_info);
            }
        }
        catch (Exception exception)
        {
            System.out.println(TAG);
            System.out.println(exception);
            dialog.show(R.string.attention, context.getString(R.string.oops_there_is_an_issue), R.drawable.ic_info);
        }
    }
}
