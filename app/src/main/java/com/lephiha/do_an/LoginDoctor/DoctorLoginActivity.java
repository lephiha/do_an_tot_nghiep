package com.lephiha.do_an.LoginDoctor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.HomePage.HomePageActivity;
import com.lephiha.do_an.Model.User;
import com.lephiha.do_an.R;

public class DoctorLoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private DoctorLoginViewModel viewModel;

    private GlobaleVariable globaleVariable;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_doctor);

        setupComponent();
        setupViewModel();
        setupEvent();
    }

    private void setupComponent() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnDoctorLogin);

        globaleVariable = (GlobaleVariable) this.getApplication();
        sharedPreferences = getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);

    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DoctorLoginViewModel.class);
    }

    private void setupEvent() {
        btnLogin.setOnClickListener(view -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            Log.d("LoginInfo", "Email: " + email);
            Log.d("LoginInfo", "Password: " + password);


            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.login(email, password, "doctor").observe(this, loginResponse -> {
                if (loginResponse != null) {
                    Log.d("LoginResponse", "Result: " + loginResponse.getResult());
                    Log.d("LoginResponse", "Message: " + loginResponse.getMsg());
                    Log.d("LoginResponse", "Token: " + loginResponse.getAccessToken());
                }

                if (loginResponse != null && loginResponse.getResult() == 1) {
                    String token = loginResponse.getAccessToken();
                    User user = loginResponse.getData();

                    // Lưu token vào SharedPreferences
                    sharedPreferences.edit().putString("accessToken", "JWT " + token.trim()).apply();

                    // Lưu thông tin người dùng vào global variable
                    globaleVariable.setAccessToken("JWT " + token);
                    globaleVariable.setAuthUser(user);

                    // Thông báo thành công và chuyển sang trang HomePageActivity
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(DoctorLoginActivity.this, HomePageActivity.class);
                    startActivity(intent);
                    finish(); // Đảm bảo đóng activity login
                } else {
                    // Hiển thị thông báo lỗi từ API
                    String errorMessage = loginResponse != null ? loginResponse.getMsg() : "Đăng nhập thất bại!";
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

    }
}
