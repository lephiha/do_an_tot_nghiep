package com.lephiha.do_an.SettingPage;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import com.lephiha.do_an.Container.PatientProfileChangeAvatar;
import com.lephiha.do_an.Container.PatientProfileChangePersonalInformation;
import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.LoadingScreen;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.Model.User;
import com.lephiha.do_an.R;
import com.lephiha.do_an.configAPI.Constant;
import com.lephiha.do_an.configAPI.HTTPRequest;
import com.lephiha.do_an.configAPI.HTTPService;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class InformationActivity extends AppCompatActivity {
    private final String TAG = "Information Activity";

    private CircleImageView imgAvatar;
    private TextView txtHealthInsuranceNumber;

    private TextView txtEmail;
    private TextView txtName;

    private TextView txtPhone;
    private RadioGroup rgGender;
    private TextView txtBirthday;
    private TextView txtAddress;
    private TextView txtCreatAp;
    private TextView txtUpdateAt;

    private AppCompatButton btnSave;
    private GlobaleVariable globaleVariable;
    private Dialog dialog;
    private LoadingScreen loadingScreen;

    private Map<String, String> header;
    private Uri uriAvatar;
    private AppCompatButton btnUploadAvatar;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        setupComponet();
        showInfo();
        setupEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tooltip.setLocale(this, sharedPreferences);
    }

    private void setupComponet() {
        imgAvatar = findViewById(R.id.imgAvatar);
        txtHealthInsuranceNumber = findViewById(R.id.txtHealthInsuranceNumber);

        txtEmail = findViewById(R.id.txtEmail);
        txtName = findViewById(R.id.txtName);

        txtPhone = findViewById(R.id.txtPhone);
        txtBirthday = findViewById(R.id.txtBirthday);
        txtAddress = findViewById(R.id.txtAddress);
        rgGender = findViewById(R.id.rgGender);

        txtCreatAp = findViewById(R.id.txtCreateAt);
        txtUpdateAt = findViewById(R.id.txtUpdateAt);

        btnSave = findViewById(R.id.btnSave);
        btnUploadAvatar = findViewById(R.id.btnAvatarUpload);

        dialog = new Dialog(this);
        loadingScreen = new LoadingScreen(this);
        globaleVariable = (GlobaleVariable) this.getApplication();

        header = globaleVariable.getHeaders();
        sharedPreferences = this.getApplication().getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);
    }

    //show in4
    private void showInfo() {
        User user = globaleVariable.getAuthUser();

        String avatar = Constant.UPLOAD_URI() + user.getAvatar();
        String id = String.valueOf(user.getId());

        String email = user.getEmail();
        String name = user.getName();

        String phone = user.getPhone();
        int gender = user.getGender();

        String birthday = user.getBirthday();
        String address = user.getAddress();

        String creatAt = Tooltip.beautifierDatetime(this, user.getCreateAt());
        String updateAt = Tooltip.beautifierDatetime(this, user.getUpdateAt());


        if (user.getAvatar().length() > 0) {
            Picasso.get().load(avatar).into(imgAvatar);
        }
        txtHealthInsuranceNumber.setText(id);
        txtEmail.setText(email);

        txtName.setText(name);
        txtPhone.setText(phone);

        if (gender == 1) {
            rgGender.check(R.id.rdMale);
        }
        else {
            rgGender.check(R.id.rdFemale);
        }

        txtBirthday.setText(birthday);
        txtAddress.setText(address);
        txtCreatAp.setText(creatAt);
        txtUpdateAt.setText(updateAt);


    }

    //set event

    private void setupEvent() {

        //getToday
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //date pick for birthday
        DatePickerDialog.OnDateSetListener birthdayDialog = (view13, year1, month1, day1) -> {
            calendar.set(Calendar.YEAR, year1);
            calendar.set(Calendar.MONTH, month1);
            calendar.set(Calendar.DAY_OF_MONTH, day1);

            String dayFormatted = String.valueOf(day1);
            String mothFormatted = String.valueOf(month1+1); //them 1 dv v 0 <= moth <=11

            if (day1 < 10) {
                dayFormatted = "0" + day1;
            }
            if (month1 < 10) {
                mothFormatted = "0" + month1;
            }

            String output = year1 + "-" + mothFormatted + "-" + dayFormatted;
            txtBirthday.setText(output);
        };

        //listen click for button

        //edit text birthday
        txtBirthday.setOnClickListener(birthdayView -> {
            new DatePickerDialog(this, birthdayDialog, year,month,day).show();
        });

        //btn save
        btnSave.setOnClickListener(view -> {
            String name = txtName.getText().toString();
            String gender = rgGender.getCheckedRadioButtonId() == R.id.rdMale ? "1" : "0";
            String birthday = txtBirthday.getText().toString();
            String address = txtAddress.getText().toString();

            loadingScreen.start();
            changePersonnalInfo(name, gender, birthday, address);
        });

        //img avt
        imgAvatar.setOnClickListener(view -> {
            verifyStoragePermission(this);

            Intent intent = new Intent();
            intent.setType("image/*"); //allow bat ky loai fil anh nafo, Change * to specific extension to limit it

            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            openGalleryToPickPhoto.launch(intent);
        });

        //btn upload avt

        btnUploadAvatar.setOnClickListener(view -> {
            if (uriAvatar == null) {
                dialog.announce();
                dialog.btnOK.setOnClickListener(d -> dialog.close());
                dialog.show("attention", "Click on your avatar to select new photo", R.drawable.ic_info);
                return;
            }
            uploadPhotoToServer(uriAvatar);
        });

    }

    private void changePersonnalInfo(String name, String gender, String birthday, String address) {
        //1
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //2
        String action = "personal"; //key de post request: personal, avatar, password
        Call<PatientProfileChangePersonalInformation> container = api.changePersonalInformation(header, action, name, gender, birthday, address);

        //3
        container.enqueue(new Callback<PatientProfileChangePersonalInformation>() {
            @Override
            public void onResponse(@NonNull Call<PatientProfileChangePersonalInformation> call,@NonNull Response<PatientProfileChangePersonalInformation> response) {
                loadingScreen.stop();
                if (response.isSuccessful()) {
                    PatientProfileChangePersonalInformation content = response.body();
                    assert content != null;
                    //update user in4
                    User user = content.getData();
                    globaleVariable.setAuthUser(user);

                    int result = content.getResult();
                    String msg = content.getMsg();
                    dialog.announce();
                    dialog.btnOK.setOnClickListener(view -> dialog.close());
                    if (result == 1) {
                        //show dialog
                        dialog.show("success", "The operation was done successfully", R.drawable.ic_check);

                    }
                    else {
                        dialog.show("Attention", msg, R.drawable.ic_close);
                    }
                }
                if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        System.out.println(TAG);
                        System.out.println(jObjError);
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PatientProfileChangePersonalInformation> call, @NonNull Throwable t) {
                loadingScreen.stop();
                System.out.println(TAG);
                System.out.println("Change Personal Information - error: " + t.getMessage());
            }
        });
    }

    private void uploadPhotoToServer(Uri uri) {
        // 1-setup file path
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndex(projection[0]);
            if (cursor.moveToFirst()) {
                String filePath = cursor.getString(columnIndex);
                cursor.close();

                if (filePath != null) {
                    // 2- config new request
                    Retrofit service = HTTPService.getInstance();
                    HTTPRequest api = service.create(HTTPRequest.class);

                    // 3
                    RequestBody action = RequestBody.create(MediaType.parse("multipart/form-data"), "avatar");

                    File file = new File(filePath);
                    RequestBody requestBodyFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

                    // multipartBody.part is used to send the file name
                    MultipartBody.Part actualFile = MultipartBody.Part.createFormData("file", file.getName(), requestBodyFile);

                    String accessToken = globaleVariable.getAccessToken();
                    String type = "Patient";
                    Call<PatientProfileChangeAvatar> container = api.changeAvatar(accessToken, type, actualFile, action);

                    // 4
                    container.enqueue(new Callback<PatientProfileChangeAvatar>() {
                        @Override
                        public void onResponse(@NonNull Call<PatientProfileChangeAvatar> call, @NonNull Response<PatientProfileChangeAvatar> response) {
                            if (response.isSuccessful()) {
                                PatientProfileChangeAvatar content = response.body();
                                assert content != null;

                                // show successful message
                                dialog.announce();
                                dialog.btnOK.setOnClickListener(view -> dialog.close());
                                dialog.show("Success", "The Operation was done successfully!", R.drawable.ic_check);

                                // update user in app storage
                                if (response.errorBody() != null) {
                                    try {
                                        JSONObject jObject = new JSONObject(response.errorBody().string());
                                        System.out.println(jObject);
                                    } catch (Exception e) {
                                        System.out.println(e.getMessage());
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<PatientProfileChangeAvatar> call, @NonNull Throwable t) {
                            System.out.println(TAG);
                            System.out.println("ERROR");
                            t.printStackTrace();
                        }
                    });
                } else {
                    // Handle the case where filePath is null
                    System.out.println("File path is null");
                }
            } else {
                cursor.close();
                System.out.println("Cursor moveToFirst() returned false");
            }
        } else {
            // Handle the case where cursor is null
            System.out.println("Cursor is null");
        }
    }

    //check if app has permission to write to device storage
    //if app don't permission then user will be prmpted to grant permission

    //storage permission
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermission(Activity activity) {
        //cheeck neeu cho phep
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            //k dc phep nhac user
            ActivityCompat.requestPermissions(
                    activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    //open gallery to pick photo

    private final ActivityResultLauncher<Intent> openGalleryToPickPhoto = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        Uri uri = data.getData();

                        imgAvatar.setImageURI(uri);
                        uriAvatar = uri;
                    }
                    else {

                    }
                }
            }
    );
}

