package com.lephiha.do_an.LoginPage;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.ImageButton;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.lephiha.do_an.Helper.LoadingScreen;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.R;

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

        setupComponent();
        setupEvent();
        setupViewModel();
    }

    private void setupComponent() {

    }

    private void setupEvent() {

    }

    private void setupViewModel() {

    }

}
