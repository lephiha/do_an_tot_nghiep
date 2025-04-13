package com.lephiha.do_an.LoginDoctor; // Đảm bảo đúng package name

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

import com.lephiha.do_an.HomePage.HomePageActivity;
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


    private static final String API_URL = "http://192.168.56.1:8080/Do_an_tot_nghiep_lph/api/app/video_call/login_doctor.php";


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
                String email = editEmail.getText().toString().trim(); // trim() để loại bỏ khoảng trắng thừa
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
                // Gọi phương thức đăng nhập
                loginDoctor(email, password);
            }
        });
    }

    // Phương thức quản lý trạng thái loading UI
    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            btnLogin.setEnabled(false); // Vô hiệu hóa nút
            progressBar.setVisibility(View.VISIBLE); // Hiện vòng xoay
        } else {
            btnLogin.setEnabled(true); // Kích hoạt lại nút
            progressBar.setVisibility(View.GONE); // Ẩn vòng xoay
        }
    }


    // Phương thức gửi yêu cầu đăng nhập
    private void loginDoctor(String email, String password) {
        // Tạo JSON body chứa email và mật khẩu
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON body", e);
            setLoadingState(false); // Bật lại UI nếu lỗi tạo JSON
            Toast.makeText(DoctorLoginActivity.this, "Lỗi tạo yêu cầu", Toast.LENGTH_SHORT).show();
            return; // Không tiếp tục nếu không tạo được JSON
        }

        // Tạo RequestBody từ JSON
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);

        // Tạo HTTP request
        Request request = new Request.Builder()
                .url(API_URL) // Sử dụng biến URL đã khai báo
                .post(body)
                .build();

        Log.d(TAG, "Sending request to: " + API_URL);
        Log.d(TAG, "Request body: " + jsonBody.toString());

        // Gửi yêu cầu không đồng bộ
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Lỗi mạng hoặc không kết nối được server
                Log.e(TAG, "Login request failed", e);
                runOnUiThread(() -> {
                    setLoadingState(false); // Cập nhật UI
                    Toast.makeText(DoctorLoginActivity.this, "Lỗi mạng hoặc không kết nối được máy chủ.", Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Có phản hồi từ server (thành công hoặc lỗi HTTP)
                final String responseData = response.body().string(); // Đọc body chỉ một lần
                final int responseCode = response.code();
                Log.d(TAG, "Response code: " + responseCode);
                Log.d(TAG, "Response data: " + responseData);

                // Xử lý kết quả trên UI thread
                runOnUiThread(() -> {
                    setLoadingState(false); // Cập nhật UI

                    if (response.isSuccessful()) { // Kiểm tra mã HTTP 2xx
                        try {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            int result = jsonResponse.getInt("result"); // Dùng tên key từ PHP trả về

                            if (result == 1) { // Đăng nhập thành công dựa theo key 'result' từ PHP
                                String accessToken = jsonResponse.getString("accessToken");
                                // Lấy thêm thông tin user nếu cần
                                // JSONObject userInfo = jsonResponse.optJSONObject("user_info");
                                // String doctorName = userInfo != null ? userInfo.optString("name", "Bác sĩ") : "Bác sĩ";

                                // Lưu access token vào SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE); // Đổi tên Prefs
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("accessToken", accessToken);
                                // editor.putString("doctorName", doctorName); // Lưu thêm tên nếu cần
                                editor.apply(); // Sử dụng apply() cho hiệu năng tốt hơn commit()

                                Log.i(TAG, "Login successful. AccessToken saved.");

                                // Chuyển hướng đến trang chủ
                                Toast.makeText(DoctorLoginActivity.this, jsonResponse.getString("msg"), Toast.LENGTH_SHORT).show(); // Hiển thị thông báo thành công
                                Intent intent = new Intent(DoctorLoginActivity.this, HomePageActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Xóa các activity trước đó
                                startActivity(intent);
                                finish(); 

                            } else {
                                // Đăng nhập thất bại (result == 0)
                                String message = jsonResponse.optString("msg", "Thông tin đăng nhập không chính xác."); // Lấy msg, có giá trị mặc định
                                Log.w(TAG, "Login failed: " + message);
                                Toast.makeText(DoctorLoginActivity.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // Lỗi khi phân tích JSON trả về từ server
                            Log.e(TAG, "Error parsing JSON response: " + responseData, e);
                            Toast.makeText(DoctorLoginActivity.this, "Lỗi xử lý dữ liệu từ máy chủ.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Lỗi HTTP (mã 4xx, 5xx)
                        Log.e(TAG, "Login request unsuccessful. Code: " + responseCode);
                        String errorMessage = "Đăng nhập thất bại (Mã lỗi: " + responseCode + ")";
                        // Cố gắng đọc thông điệp lỗi từ server nếu có
                        try {
                            JSONObject jsonError = new JSONObject(responseData);
                            errorMessage = jsonError.optString("msg", errorMessage);
                        } catch (JSONException jsonException) {
                            // Không phải JSON hoặc không có 'msg', giữ lỗi mặc định
                            Log.w(TAG, "Response body is not valid JSON or doesn't contain 'msg'");
                        }

                        Toast.makeText(DoctorLoginActivity.this, errorMessage , Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    } 
}