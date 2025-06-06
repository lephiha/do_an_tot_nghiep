package com.lephiha.do_an.CallVideo;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lephiha.do_an.CallVideo.stringee.activity.CallActivity;
import com.lephiha.do_an.CallVideo.stringee.common.Constant2;
import com.lephiha.do_an.CallVideo.stringee.common.PermissionsUtils;
import com.lephiha.do_an.CallVideo.stringee.common.Utils;
import com.lephiha.do_an.CallVideo.stringee.manager.ClientManager;
import com.lephiha.do_an.Model.CallDoctor;
import com.lephiha.do_an.R;
import com.lephiha.do_an.RecyclerView.CallDoctorAdapter;
import com.lephiha.do_an.configAPI.Constant;
import com.lephiha.do_an.viewModel.ChooseCallDoctorListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseDoctorActivity extends AppCompatActivity implements ChooseCallDoctorListener{

        RecyclerView recyclerView;
    List<CallDoctor> doctorList;
    TextView navText;
    ImageView back;
    ClientManager clientManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose_call_doctor);

        recyclerView = findViewById(R.id.doctorRecyclerview);
        navText = findViewById(R.id.navText);
        back = findViewById(R.id.back);
        back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        navText.setText("Tư vấn khám bệnh qua video");

        // ⚠️ Hardcode ID để test nhanh
        int id = 1; // <-- Thay bằng ID hợp lệ đang có trên database
        android.util.Log.d("DEBUG_CALL", "ID user gửi API: " + id);

        Constant.getService().getCallDoctor(id).enqueue(new Callback<List<CallDoctor>>() {
            @Override
            public void onResponse(Call<List<CallDoctor>> call, Response<List<CallDoctor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    doctorList = response.body();
                    android.util.Log.d("DEBUG_CALL", "Số bác sĩ nhận được: " + doctorList.size());

                    if (!doctorList.isEmpty()) {
                        CallDoctorAdapter adapter = new CallDoctorAdapter(ChooseDoctorActivity.this, doctorList, ChooseDoctorActivity.this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(ChooseDoctorActivity.this));
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        android.util.Log.w("DEBUG_CALL", "Danh sách bác sĩ rỗng.");
                    }

                } else {
                    android.util.Log.e("DEBUG_CALL", "Lỗi phản hồi: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<CallDoctor>> call, Throwable t) {
                android.util.Log.e("DEBUG_CALL", "Lỗi gọi API: " + t.getMessage(), t);
            }
        });
    }



    @Override
    public void onItemCliked(CallDoctor callDoctor) {
        Intent intent = new Intent(ChooseDoctorActivity.this, ConfirmActivity.class);
        intent.putExtra("callDoctor", callDoctor);
        startActivity(intent);
    }

    @Override
    public void call(String callId) {

        clientManager = ClientManager.getInstance(ChooseDoctorActivity.this);

        initAndConnectStringee();

        requestPermission();
        makeCall(true,true,callId);
//        Intent intent = new Intent(ChooseCallDoctorActivity.this, StringeeActivity.class);
//        intent.putExtra("callId", callId);
//        startActivity(intent);
    }

//    private void requestPermission() {
//        if (!PermissionsUtils.getInstance().checkSelfPermission(this)) {
//            PermissionsUtils.getInstance().requestPermissions(this);
//        }
//    }
private static final int PERMISSION_REQUEST_CODE = 123;

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            // Yêu cầu quyền
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_CODE);
        } else {
            // Quyền đã được cấp, thực hiện cuộc gọi video
            makeCall(true, true, "callId");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isGranted = PermissionsUtils.getInstance().verifyPermissions(grantResults);
        if (requestCode == PermissionsUtils.REQUEST_PERMISSION) {
            clientManager.isPermissionGranted = isGranted;
            if (!isGranted) {
                if (PermissionsUtils.getInstance().shouldRequestPermissionRationale(this)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.app_name);
                    builder.setMessage("Permissions must be granted for the call");
                    builder.setPositiveButton("Ok", (dialogInterface, id) -> dialogInterface.cancel());
                    builder.setNegativeButton("Settings", (dialogInterface, id) -> {
                        dialogInterface.cancel();
                        // open app setting
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    });
                    builder.create().show();
                }
            }
        }
    }

    public void initAndConnectStringee() {
//        clientManager.addOnConnectionListener(status -> runOnUiThread(() -> binding.tvStatus.setText(status)));
        clientManager.connect();
    }

    private void makeCall(boolean isStringeeCall, boolean isVideoCall, String callId) {
        if (Utils.isStringEmpty(callId) || !clientManager.getStringeeClient().isConnected()) {
            return;
        }
        if (!clientManager.isPermissionGranted) {
            PermissionsUtils.getInstance().requestPermissions(this);
            return;
        }
        Intent intent = new Intent(this, CallActivity.class);
        intent.putExtra(Constant2.PARAM_TO, callId);
        intent.putExtra(Constant2.PARAM_IS_VIDEO_CALL, isVideoCall);
        intent.putExtra(Constant2.PARAM_IS_INCOMING_CALL, false);
        intent.putExtra(Constant2.PARAM_IS_STRINGEE_CALL, isStringeeCall);
        startActivity(intent);
    }
}
