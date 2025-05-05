package com.lephiha.do_an.LoginDoctor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lephiha.do_an.DoctorPage.HomePageDoctor.HomeDoctorActivity;
import com.lephiha.do_an.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DoctorLoginActivity extends AppCompatActivity {

    private static final String TAG = "DoctorLoginActivity";
    private EditText editEmail, editPassword;
    private Button btnLogin;
    private ProgressBar progressBar;

    private static final String API_URL = "https://profound-platypus-exactly.ngrok-free.app/Do_an_tot_nghiep_lph/api/app/video_call/login_doctor.php";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_doctor);

        // Ánh xạ các view
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        // Ẩn ProgressBar ban đầu
        progressBar.setVisibility(View.GONE);

        // Xử lý sự kiện khi nhấn nút đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    editEmail.setError("Vui lòng nhập email");
                    editEmail.requestFocus();
                    return;
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editEmail.setError("Địa chỉ email không hợp lệ");
                    editEmail.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    editPassword.setError("Vui lòng nhập mật khẩu");
                    editPassword.requestFocus();
                    return;
                }

                setLoadingState(true);
                loginDoctor(email, password);
            }
        });
    }

    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            btnLogin.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            btnLogin.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void loginDoctor(String email, String password) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON body", e);
            setLoadingState(false);
            Toast.makeText(DoctorLoginActivity.this, "Lỗi tạo yêu cầu", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        Log.d(TAG, "Sending request to: " + API_URL);
        Log.d(TAG, "Request body: " + jsonBody.toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Login request failed", e);
                runOnUiThread(() -> {
                    setLoadingState(false);
                    Toast.makeText(DoctorLoginActivity.this, "Lỗi mạng hoặc không kết nối được máy chủ: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                final int responseCode = response.code();
                Log.d(TAG, "Response code: " + responseCode);
                Log.d(TAG, "Response data: " + responseData);

                runOnUiThread(() -> {
                    setLoadingState(false);

                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            int result = jsonResponse.getInt("result");

                            if (result == 1) {
                                // Lấy token từ API (trường "token" trong phản hồi)
                                String token = jsonResponse.optString("token", null);

                                // Kiểm tra nếu token rỗng hoặc null
                                if (token == null || token.isEmpty()) {
                                    Log.w(TAG, "Token from API is empty or null");
                                    Toast.makeText(DoctorLoginActivity.this, "Không tìm thấy token từ máy chủ", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                // Lưu token vào SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("doantotnghiep", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("call_token", token);

                                // Lưu thông tin bác sĩ từ user_info
                                JSONObject userInfo = jsonResponse.optJSONObject("user_info");
                                if (userInfo != null) {
                                    String doctorName = userInfo.optString("name", "Bác sĩ");
                                    String doctorId = userInfo.optString("id", "");
                                    editor.putString("doctor_name", doctorName);
                                    editor.putString("doctor_id", doctorId);
                                    editor.apply();
                                    Log.d(TAG, "Saved doctor name: " + doctorName + ", ID: " + doctorId);
                                }

                                Log.i(TAG, "Login successful. Token saved: " + token);

                                Toast.makeText(DoctorLoginActivity.this, jsonResponse.getString("msg"), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(DoctorLoginActivity.this, HomeDoctorActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                String message = jsonResponse.optString("msg", "Thông tin đăng nhập không chính xác.");
                                Log.w(TAG, "Login failed: " + message);
                                Toast.makeText(DoctorLoginActivity.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing JSON response: " + responseData, e);
                            Toast.makeText(DoctorLoginActivity.this, "Lỗi xử lý dữ liệu từ máy chủ: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.e(TAG, "Login request unsuccessful. Code: " + responseCode);
                        String errorMessage = "Đăng nhập thất bại (Mã lỗi: " + responseCode + ")";
                        try {
                            JSONObject jsonError = new JSONObject(responseData);
                            errorMessage = jsonError.optString("msg", errorMessage);
                        } catch (JSONException jsonException) {
                            Log.w(TAG, "Response body is not valid JSON or doesn't contain 'msg'");
                        }
                        Toast.makeText(DoctorLoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }

        });
    }

    // Hàm để lấy token từ SharedPreferences
    public String getToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("doantotnghiep", MODE_PRIVATE);
        return sharedPreferences.getString("call_token", "");
    }
}
