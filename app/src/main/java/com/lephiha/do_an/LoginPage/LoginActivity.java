package com.lephiha.do_an.LoginPage;


import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.ImageButton;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.lephiha.do_an.Helper.LoadingScreen;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.HomePage.HomePageActivity;
import com.lephiha.do_an.Model.User;
import com.lephiha.do_an.R;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "Login Activity";

    private EditText txtPhoneNumber;
    private AppCompatButton btnGetVerificationCode;
    private ImageButton btnGoogleLogin;
    private String phoneNumber;

    //login phone number
    private FirebaseAuth firebaseAuth;
    //login goggle
    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;

    private Dialog dialog;
    private LoadingScreen loadingScreen;
    private LoginViewModel viewModel;

    private GlobaleVariable globaleVariable;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo Firebase nếu chưa được khởi tạo
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }

        // Tiếp tục với phần còn lại của mã
        FirebaseAuth auth = FirebaseAuth.getInstance();

        setupComponent();
        setupEvent();
        setupViewModel();
    }
    /** setup componet */
    private void setupComponent() {
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        btnGetVerificationCode = findViewById(R.id.btnGetVerificationCode);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);

        dialog = new Dialog(this);
        loadingScreen = new LoadingScreen(this);

        globaleVariable = (GlobaleVariable)this.getApplication();
        sharedPreferences = this.getApplication().getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);

        //login with phone
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.setLanguageCode("vi");

        //login with google
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

    }
    /** setup event*/
    private void setupEvent() {
        //button get confirm code
        btnGetVerificationCode.setOnClickListener(view -> {
            phoneNumber = txtPhoneNumber.getText().toString();

            //1. verify input
            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(this, R.string.do_not_let_phone_number_empty, Toast.LENGTH_SHORT).show();
                return;
            }
            if (phoneNumber.length() == 10) {
                Toast.makeText(this, R.string.only_enter_number_except_first_zero, Toast.LENGTH_SHORT).show();
                return;
            }
            String phoneNumberFormatted = "+84" + phoneNumber;

            //2. setup phone auth op
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phoneNumberFormatted) //phone to verify
                    .setTimeout(60L, TimeUnit.SECONDS) //timeout + unit
                    .setActivity(this)  //activity(call binding
                    .setCallbacks(
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    /** Callback này sẽ được gọi trong hai trường hợp sau:

                                    1. Xác minh ngay lập tức (Instant verification): Trong một số trường hợp, số điện thoại
                                     có thể được xác minh ngay lập tức mà không cần gửi hoặc nhập mã xác minh.
                                    2. Tự động lấy mã (Auto-retrieval): Trên một số thiết bị, dịch vụ Google Play
                                     có thể tự động phát hiện mã xác minh SMS đến và thực hiện xác minh mà không cần người dùng thao tác.
                                     */

                                    System.out.println(TAG);
                                    System.out.println("onVerificationCompleted");
                                    System.out.println("signInWithPhoneAuthCredential has been called !");
                                    signInWithPhoneAuthCredential(phoneAuthCredential);
                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    Toast.makeText(LoginActivity.this, getString(R.string.verification_failed), Toast.LENGTH_SHORT).show();
                                    System.out.println(TAG);
                                    System.out.println("Error: "+e.getMessage());
                                    System.out.println(e);
                                }

                                @Override
                                public void onCodeSent(@NonNull String verificationId,
                                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                    // The SMS verification code has been sent to the provided phone number, we
                                    // now need to ask the user to enter the code and then construct a credential
                                    // by combining the code with a verification ID.

                                    System.out.println(TAG);
                                    System.out.println("onCodeSent");
                                    System.out.println("phone number: " + phoneNumber);
                                    System.out.println("verification Id: " + verificationId);

                                    Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
                                    intent.putExtra("verificationId", verificationId);
                                    intent.putExtra("phoneNumber", phoneNumber);
                                    startActivity(intent);
                                }
                                @Override
                                public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                                    super.onCodeAutoRetrievalTimeOut(s);
                                    System.out.println(TAG);
                                    System.out.println("onCodeAutoRetrievalTimeOut");
                                    System.out.println(s);
                                }

                            }
                    )
                    .build();

            //3. setup phone auth provider
            PhoneAuthProvider.verifyPhoneNumber(options);
        });

        /** button google login */
        btnGoogleLogin.setOnClickListener(view -> {
            Log.d(TAG, "BUTTON GOOGLE LOGIN CLICKED");

            Intent intent = googleSignInClient.getSignInIntent();
            startGoogleSigninForResult.launch(intent);
        });
    }

    /** setup view model */
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        viewModel.getAnimation().observe(this, aBoolean -> {
            if (aBoolean) {
                loadingScreen.start();
            }
            else {
                loadingScreen.stop();
            }
        });

        //set up dialog

        dialog.announce();
        dialog.btnOK.setOnClickListener(view->dialog.close());

        /** option login with phone */
        viewModel.getLoginWithPhoneResponse().observe(this, loginResponse -> {
            if (loginResponse == null) {
                dialog.show(getString(R.string.attention),
                        getString(R.string.oops_there_is_an_issue),
                        R.drawable.ic_close);
                return;
            }

            int result = loginResponse.getResult();
            String message = loginResponse.getMsg();

            // case 1. login success
            if (result == 1) {
                //lay db tu api ra
                String token = loginResponse.getAccessToken();
                User user = loginResponse.getData();

                int patientId = user.getId();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("id", patientId);
                editor.apply();
                Log.d(TAG, "Saved patientId: " + patientId);

                //lay db vao global variable
                globaleVariable.setAccessToken("JWT" + token);
                globaleVariable.setAuthUser(user);

                //luu accessToken vao sharedRe..
                sharedPreferences.edit().putString("accessToken", "JWT" + token.trim()).apply();

                //hien thi thong bao login tc
                Toast.makeText(this, getString(R.string.login_successfully), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                startActivity(intent);
            }
            //case 2. login fail
            else {
                dialog.show(getString(R.string.attention),
                        message,
                        R.drawable.ic_close);
                Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
            }
        });

        /** option login with google */
        viewModel.getLoginWithGoogleResponse().observe(this, response->{
            int result = response.getResult();
            String message = response.getMsg();

            if( result == 1)
            {
                /*Lay du lieu tu API ra*/
                String token = response.getAccessToken();
                User user = response.getData();


                // Save patientId to SharedPreferences
                int patientId = user.getId();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("id", patientId);
                editor.putString("call_token", user.getToken());
                editor.apply();
                Log.d(TAG, "Saved patientId: " + patientId);


                /*Lay du lieu vao Global Variable*/
                globaleVariable.setAccessToken( "JWT " + token );
                globaleVariable.setAuthUser(user);

                /*luu accessToken vao Shared Reference*/
                sharedPreferences.edit().putString("accessToken", "JWT " + token.trim()).apply();

                /*hien thi thong bao la dang nhap thanh cong*/
                Toast.makeText(this, getString(R.string.login_successfully), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                startActivity(intent);
            }
            else
            {
                dialog.show(getString(R.string.attention),
                        message,
                        R.drawable.ic_close);
                Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** sign in with phone auth credential */
    private void signInWithPhoneAuthCredential (PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();

                        assert user != null;
                        String phone = "0" + phoneNumber;
                        String password = user.getUid();
                        System.out.println(TAG);
                        System.out.println("phone: " + phone);
                        System.out.println("password: " + password);

                        viewModel.loginWithPhone(phone, password);
                    }
                    else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(LoginActivity.this, "Exception", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "error: " + task.getException() );
                        }
                    }
                });
    }
    /** start sign up activiry for result */

    private final ActivityResultLauncher<Intent> startGoogleSigninForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                int statusResult = result.getResultCode();
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

                if (statusResult == RESULT_OK) {
                    //1- get mai + pass to server to auth/sign up
                    assert account != null;
                    String email = account.getEmail();
                    String password = account.getId();

                    //2- login
                    viewModel.loginWithGooge(email, password);
                }
                else  {
                    Toast.makeText(this, R.string.oops_there_is_an_issue, Toast.LENGTH_SHORT).show();
                }
            }
    );

}
