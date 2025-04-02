package com.lephiha.do_an.LoginPage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.LoadingScreen;
import com.lephiha.do_an.HomePage.HomePageActivity;
import com.lephiha.do_an.Model.User;
import com.lephiha.do_an.R;

public class VertificationActivity extends AppCompatActivity{

    private final String TAG = "Verification-Activity";
    private String phoneNumber;
    private String verificationId;

    private String phone;
    private String password;
    private EditText txtVerificationCode;
    private AppCompatButton btnConfirm;
    private FirebaseAuth firebaseAuth;


    private LoginViewModel viewModel;
    private LoadingScreen loadingScreen;
    private Dialog dialog;


    private SharedPreferences sharedPreferences;
    private GlobaleVariable globaleVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vetification);

        getVariable();
        setupComponent();
        setupViewModel();
        setupEvent();
    }

    private void getVariable()
    {
        verificationId = getIntent().getStringExtra("verificationId");
        phoneNumber = getIntent().getStringExtra("phoneNumber");


        if(TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(verificationId) )
        {
            Toast.makeText(this, R.string.empty_phone_number_or_verificationId, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupComponent()
    {
        txtVerificationCode = findViewById(R.id.txtVerificationCode);
        btnConfirm = findViewById(R.id.btnConfirm);

        firebaseAuth = FirebaseAuth.getInstance();
        loadingScreen = new LoadingScreen(this);
        dialog = new Dialog(this);

        globaleVariable = (GlobaleVariable) this.getApplication();
        sharedPreferences = this.getApplication()
                .getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);
    }

    private void setupViewModel()
    {
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        viewModel.getAnimation().observe(this, aBoolean -> {
            if( aBoolean )
            {
                loadingScreen.start();
            }
            else
            {
                loadingScreen.stop();
            }
        });

        /*set up dialog*/
        dialog.announce();
        dialog.btnOK.setOnClickListener(view->dialog.close());

        viewModel.getLoginWithPhoneResponse().observe(this, loginResponse -> {

            if (loginResponse == null) {
                dialog.show(getString(R.string.attention),
                        getString(R.string.oops_there_is_an_issue),
                        R.drawable.ic_close);
                return;
            }

            int result = loginResponse.getResult();
            String message = loginResponse.getMsg();

            /*Case 1 - login successfully*/
            if( result == 1)
            {
                /*Lay du lieu tu API ra*/
                String token = loginResponse.getAccessToken();
                User user = loginResponse.getData();

                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                /*Lay du lieu vao Global Variable*/
                globaleVariable.setAccessToken( "JWT " + token );
                globaleVariable.setAuthUser(user);
                Log.d(TAG,"ACCESS TOKEN: " + globaleVariable.getAccessToken());

                /*luu accessToken vao Shared Reference*/
                sharedPreferences.edit().putString("accessToken", "JWT " + token.trim()).apply();

                /*hien thi thong bao la dang nhap thanh cong*/
                Toast.makeText(this, getString(R.string.login_successfully), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(VertificationActivity.this, HomePageActivity.class);
                startActivity(intent);
            }
            /*Case 2 - login failed*/
            else
            {
                System.out.println(TAG);
                System.out.println("result: " + result);
                System.out.println("msg: " + message);
                dialog.show(getString(R.string.attention),
                        message,
                        R.drawable.ic_close);
                Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();

            }

        });
    }

    private void setupEvent()
    {
        btnConfirm.setOnClickListener(view->{
            /*Step 1 - get verificationCode and create credential*/
            String verificationCode = txtVerificationCode.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, verificationCode);


            System.out.println(TAG);
            System.out.println("Credential: " + credential);

            /*Step 2 - verify and go ahead*/
            signInWithPhoneAuthCredential(credential);
        });
    }


    //this function calls LoginViewModel login() function to get ACCESS TOKEN

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful())
                    {
                        FirebaseUser user = task.getResult().getUser();

                        assert user != null;
                        String phone = "0" + phoneNumber;// append the zero letter in the first position of phone number
                        String password = user.getUid();

                        System.out.println(TAG);
                        System.out.println("signInWithPhoneAuthCredential");
                        System.out.println("Phone: " + phone);
                        System.out.println("Password: " + password);

                        viewModel.loginWithPhone(phone, password);

                    }
                    else
                    {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                        {
                            Toast.makeText(VertificationActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Error: " + task.getException() );
                        }
                    }
                });
    }
}
