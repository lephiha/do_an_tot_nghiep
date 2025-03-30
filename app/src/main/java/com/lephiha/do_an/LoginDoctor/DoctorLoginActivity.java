package com.lephiha.do_an.LoginDoctor;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lephiha.do_an.HomePage.HomePageActivity;
import com.lephiha.do_an.R;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DoctorLoginActivity extends AppCompatActivity {

    private EditText editEmail, editPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_doctor);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();

                // Kiểm tra xem email và mật khẩu có trống không
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(DoctorLoginActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                loginDoctor(email, password);
            }
        });
    }

    // Phương thức gửi yêu cầu đăng nhập
    private void loginDoctor(String email, String password) {
        OkHttpClient client = new OkHttpClient();

        // Tạo JSON body chứa email và password
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", password);
            json.put("type", "doctor");  // Xác định đây là đăng nhập bác sĩ
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Tạo HTTP request
        Request request = new Request.Builder()
                .url("http://192.168.56.1/Do_an_tot_nghiep_lph/api/login")
                .post(okhttp3.RequestBody.create(json.toString(), okhttp3.MediaType.parse("application/json; charset=utf-8")))
                .build();

        // Gửi yêu cầu không đồng bộ
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, java.io.IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DoctorLoginActivity.this, "Đăng nhập thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws java.io.IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        int result = jsonResponse.getInt("result");

                        if (result == 1) {
                            String accessToken = jsonResponse.getString("accessToken");

                            // Lưu access token vào SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("accessToken", accessToken);
                            editor.apply();

                            // Chuyển hướng đến HomeActivity
                            Intent intent = new Intent(DoctorLoginActivity.this, HomePageActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String message = jsonResponse.getString("msg");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(DoctorLoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DoctorLoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


}
