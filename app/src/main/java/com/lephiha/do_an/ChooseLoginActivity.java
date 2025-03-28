package com.lephiha.do_an;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.lephiha.do_an.LoginDoctor.DoctorLoginActivity;
import com.lephiha.do_an.LoginPage.LoginActivity;

public class ChooseLoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login);

        // component btn
        Button btnDoctor = findViewById(R.id.btnDoctorLogin);
        Button btnPatient = findViewById(R.id.btnPatientLogin);

        // Khi chọn Đăng nhập Bác sĩ
        btnDoctor.setOnClickListener(view -> {
            Intent intent = new Intent(ChooseLoginActivity.this, DoctorLoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Khi chọn Đăng nhập Bệnh nhân
        btnPatient.setOnClickListener(view -> {
            Intent intent = new Intent(ChooseLoginActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
