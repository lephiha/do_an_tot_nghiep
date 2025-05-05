package com.lephiha.do_an.CallVideo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Model.Booking;
import com.lephiha.do_an.Model.CallDoctor;
import com.lephiha.do_an.Model.Patient;
import com.lephiha.do_an.Model.ReturnData;
import com.lephiha.do_an.R;
import com.lephiha.do_an.configAPI.Constant;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmActivity extends AppCompatActivity {

    TextView clinicname;
    TextView clinicAddress;
    TextView patientName;
    TextView patientNumber;
    TextView patientBirthday;
    TextView patientAddress;
    TextView doctorName;
    TextView doctorSpe;
    TextView scheduledate;

    TextView price;
    MaterialButton payBtn;
    TextView textView;
    private String orderInfo; // Lưu orderInfo để sử dụng trong payBtn
    private GlobaleVariable globaleVariable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm);

        // Khởi tạo GlobaleVariable
        globaleVariable = (GlobaleVariable) getApplication();

        // Get CallDoctor info (not Doctor)
        CallDoctor callDoctor = (CallDoctor) getIntent().getSerializableExtra("callDoctor");
        Booking booking = (Booking) getIntent().getSerializableExtra("booking"); // Có thể null nếu chưa tạo Booking
        String dateText = getIntent().getStringExtra("date");
        String timeText = getIntent().getStringExtra("time");

        // Bind views
        clinicname = findViewById(R.id.clinic_name);
        clinicAddress = findViewById(R.id.clinic_address);
        patientName = findViewById(R.id.patient_name);
        patientNumber = findViewById(R.id.patient_number);
        patientBirthday = findViewById(R.id.patient_birthday);
        patientAddress = findViewById(R.id.patient_location);
        doctorName = findViewById(R.id.doctor_name);
        doctorSpe = findViewById(R.id.doctor_spe);
        scheduledate = findViewById(R.id.date);

        price = findViewById(R.id.price);
        payBtn = findViewById(R.id.payButton);
        textView = findViewById(R.id.navText);

        // Set header text
        textView.setText("Xác nhận thanh toán");

        // Set Clinic name
        clinicname.setText("Bệnh viện Trung ương Quân đội 108");
        clinicAddress.setText(""); // Không có thông tin địa chỉ, để trống

        // Set Doctor name and specialty with null check
        if (callDoctor != null) {
            doctorName.setText(callDoctor.getName() != null ? callDoctor.getName() : "Chưa có thông tin");
            doctorSpe.setText(callDoctor.getSpecialityName() != null ? callDoctor.getSpecialityName() : "Chưa có thông tin"); // Sửa thành getSpecialtyName()
            // Format price
            String priceValue = callDoctor.getPrice();
            if (priceValue != null) {
                try {
                    String priceText = String.format("%,d", Integer.parseInt(priceValue)) + "đ";
                    price.setText(priceText);
                } catch (NumberFormatException e) {
                    price.setText("Chưa có thông tin");
                    Log.e("ConfirmActivity", "Invalid price format: " + priceValue, e);
                }
            } else {
                price.setText("Chưa có thông tin");
            }
        } else {
            doctorName.setText("Chưa có thông tin");
            doctorSpe.setText("Chưa có thông tin");
            price.setText("Chưa có thông tin");
            Toast.makeText(this, "Không tìm thấy thông tin bác sĩ", Toast.LENGTH_SHORT).show();
        }



        // Initialize orderInfo with a default value
        orderInfo = "book unknown " + (booking != null ? booking.getId() : "unknown");

        // Get patient ID from SharedPreferences using the same name as in LoginActivity
        SharedPreferences sp = getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);
        // Debug: Log all SharedPreferences keys and values
        for (String key : sp.getAll().keySet()) {
            Log.d("ConfirmActivity", "SharedPreferences key: " + key + ", value: " + sp.getAll().get(key));
        }
        int patientId = sp.getInt("id", 0);
        Log.d("ConfirmActivity", "Patient ID: " + patientId);
        if (patientId == 0) {
            Log.w("ConfirmActivity", "Patient ID not found in SharedPreferences");
            setDefaultPatientInfo();
        } else {
            // Call API to get patient by ID with headers
            Log.d("ConfirmActivity", "Calling API: /api/app/video_call/get_patient.php?pid=" + patientId);
            Map<String, String> headers = globaleVariable.getHeaders();
            Log.d("ConfirmActivity", "Headers: " + headers.toString());
            Constant.getService().getPatientById(headers, patientId).enqueue(new Callback<Patient>() {
                @Override
                public void onResponse(Call<Patient> call, Response<Patient> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Patient patient = response.body();
                        Log.d("ConfirmActivity", "Patient data: " + patient.getName() + ", " + patient.getPhone() + ", " + patient.getBirthday() + ", " + patient.getAddress());
                        patientName.setText(patient.getName() != null ? patient.getName() : "Chưa có thông tin");
                        patientNumber.setText(patient.getPhone() != null ? patient.getPhone() : "Chưa có thông tin");
                        patientBirthday.setText(patient.getBirthday() != null ? patient.getBirthday() : "Chưa có thông tin");
                        patientAddress.setText(patient.getAddress() != null ? patient.getAddress() : "Chưa có thông tin");

                        // Update orderInfo using patient ID
                        orderInfo = "book " + patient.getId() + " " + (booking != null ? booking.getId() : "unknown");
                    } else {
                        Log.e("ConfirmActivity", "Failed to fetch patient: HTTP " + response.code() + " - " + response.message());
                        setDefaultPatientInfo();
                    }
                }

                @Override
                public void onFailure(Call<Patient> call, Throwable t) {
                    Log.e("ConfirmActivity", "Error fetching patient: " + t.getMessage(), t);
                    setDefaultPatientInfo();
                }
            });
        }

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callDoctor == null || callDoctor.getPrice() == null) {
                    Toast.makeText(ConfirmActivity.this, "Không thể thanh toán: Thiếu thông tin bác sĩ hoặc giá", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (orderInfo == null) {
                    Toast.makeText(ConfirmActivity.this, "Không thể thanh toán: Thiếu thông tin đơn hàng", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lấy ID bác sĩ từ CallDoctor
                int doctorId = callDoctor.getId();  // Lấy ID bác sĩ

                // Gọi API để tạo thanh toán và truyền tham số bác sĩ
                Constant.getService().createPayment(
                                Integer.parseInt(callDoctor.getPrice()),
                                System.currentTimeMillis() / 1000,
                                orderInfo,
                                doctorId)  // Truyền ID bác sĩ vào API
                        .enqueue(new Callback<ReturnData>() {
                            @Override
                            public void onResponse(Call<ReturnData> call, Response<ReturnData> response) {
                                if (response.body() != null) {
                                    ReturnData returnData = response.body();
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(returnData.getData()));
                                    startActivity(i);
                                } else {
                                    Log.d("Retrofit", "API response is null");
                                    Toast.makeText(ConfirmActivity.this, "Lỗi: Không nhận được phản hồi từ server", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ReturnData> call, Throwable throwable) {
                                Log.d("Retrofit", throwable.toString());
                                Toast.makeText(ConfirmActivity.this, "Lỗi: Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    private void setDefaultPatientInfo() {
        patientName.setText("Chưa có thông tin");
        patientNumber.setText("Chưa có thông tin");
        patientBirthday.setText("Chưa có thông tin");
        patientAddress.setText("Chưa có thông tin");
        Toast.makeText(this, "Không thể lấy thông tin bệnh nhân", Toast.LENGTH_SHORT).show();
    }
}