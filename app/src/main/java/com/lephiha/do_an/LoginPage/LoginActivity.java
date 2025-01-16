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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.lephiha.do_an.Helper.LoadingScreen;
import com.lephiha.do_an.Helper.GlobaleVariable;
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

        setupComponent();
        setupEvent();
        setupViewModel();
    }

    private void setupComponent() {
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        btnGetVerificationCode = findViewById(R.id.btnGetVerificationCode);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);

        dialog = new Dialog(this);
        loadingScreen = new LoadingScreen(this);

        globaleVariable = (GlobaleVariable)this.getApplication();
        sharedPreferences = this.getApplication().getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);

        //login phone number
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.setLanguageCode("vi");

        //login gg
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    private void setupEvent() {
        // button get code
        btnGetVerificationCode.setOnClickListener(view -> {
            phoneNumber = txtPhoneNumber.getText().toString();

            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(this, R.string.do_not_let_phone_number_empty, Toast.LENGTH_SHORT).show();
                return;
            }

            if(phoneNumber.length() == 10) {
                Toast.makeText(this, R.string.only_enter_number_except_first_zero, Toast.LENGTH_SHORT).show();
                return;
            }

            String phoneNumberFormatted = "+84" + phoneNumber;

            //phone auth option
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth).setPhoneNumber(phoneNumberFormatted) //verify
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this) //activity for callback binding
                    .setCallbacks(
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                    System.out.println(TAG);
                                    System.out.println("onVerificationCompleted");
                                    System.out.println("signInWithPhoneAuthCredential has been called !");
                                    signInWithPhoneAuthCredential(credential);
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

                                    Intent intent = new Intent(LoginActivity.this, VertificationActivity.class);
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

            // set up phone auth provider
            PhoneAuthProvider.verifyPhoneNumber(options);
        });

        //button gg login
        btnGoogleLogin.setOnClickListener(view -> {
            Log.d(TAG, "BUTTON GOOGLE LOGIN CLICKED");

            Intent intent = googleSignInClient.getSignInIntent();
            startGoogleSignInForResult.launch(intent);
        });

    }

    private void setupViewModel() {
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

        //set dialog
        dialog.announce();
        dialog.btnOK.setOnClickListener(view->dialog.close());

        //option login with phone number
        viewModel.getLoginWithPhoneRespone().observe(this, response -> {
            int result = response.getResult();
            String message = response.getMsg();

            if (result == 1) {
                //lay du lieu api ra

                String token = response.getAccessToken();
                User user = response.getData();
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

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCanceledListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = task.getResult().getUser();

                assert user != null ;
                String phone = "0" + phoneNumber;
                String password = user.getUid();
                System.out.println(TAG);
                System.out.println("phone: " + phone);
                System.out.println("password: " + password);

                viewModel.loginWithPhone (phone, password);
            }
            else {
                if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    // verif cod enter valid
                    Toast.makeText(LoginActivity.this, "Exception", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "error: "+ task.getException());
                }
            }
        });
    }

    // start sign up for result

    private final ActivityResultLauncher<Intent> startGoogleSignInForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                int statusResult = result.getResultCode();
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

                if ( statusResult == RESULT_OK) {
                    // 1. get email & pass to server to auth ~ signup
                    assert account != null;
                    String email = account.getEmail();
                    String password = account.getId();
                    // 2. login
                    viewModel.loginWithGoogle(email, password);
                }
                else {
                    Toast.makeText(this, R.string.oops_there_is_an_issue, Toast.LENGTH_SHORT).show();
                }
            }
    );

}
