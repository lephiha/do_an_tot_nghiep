package com.lephiha.do_an.GuidePage;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.R;

public class GuidePageActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private WebView wvwLocation;
    private AppCompatButton btnOpentWithGoogleMap;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        setupComponent();
        setupEvent();
    }

    private void setupComponent() {
        btnBack = findViewById(R.id.btnBack);
        wvwLocation = findViewById(R.id.wvwDescription);
        btnOpentWithGoogleMap = findViewById(R.id.btnOpenWithGoogleMap);

        GlobaleVariable globaleVariable = (GlobaleVariable) this.getApplication();
        sharedPreferences = this.getApplication().getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Tooltip.setLocale(this, sharedPreferences);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupEvent() {
        //btn back
        btnBack.setOnClickListener(view -> finish());

        /* GOOGLE MAP */
        String location =
                "<html>\n" +
                        "   <body>\n" +
                        "       <iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d4973.142292709014!2d105.78987176861756!3d20.966682784941383!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3135acd63a57cd41%3A0x7fa4693546088d8f!2zQuG7h25oIHZp4buHbiBRdcOibiB5IDEwMywgUC4gUGjDumMgTGEsIEjDoCDEkMO0bmcsIEjDoCBO4buZaSwgVmnhu4d0IE5hbQ!5e0!3m2!1svi!2s!4v1740358699109!5m2!1svi!2s\" " +
                        "           width=\"600\" height=\"450\" \n" +
                        "           style=\"border:0;\" allowfullscreen=\"\"\n" +
                        "           loading=\"lazy\" \n" +
                        "           referrerpolicy=\"no-referrer-when-downgrade\">\n" +
                        "       </iframe>\n" +
                        "   </body>\n" +
                        "</html>";

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(this.getString(R.string.loading));
        progressDialog.setCancelable(false);

        wvwLocation.requestFocus();
        wvwLocation.getSettings().setJavaScriptEnabled(true);
        wvwLocation.getSettings().setGeolocationEnabled(true);
        wvwLocation.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    progressDialog.show();
                }
                if (progress == 100) {
                    progressDialog.dismiss();
                }
            }
        });
        wvwLocation.loadDataWithBaseURL(null, location, "text/HTML", "UTF-8", null);

        //button open with ggmap
        btnOpentWithGoogleMap.setOnClickListener(view -> {
            Uri uri = Uri.parse("https://www.google.com/maps/place/Military+Hospital+103/@20.9675462,105.7865676,16z/data=!3m1!4b1!4m6!3m5!1s0x3135acd619f70e15:0xf954a67e24a41382!8m2!3d20.9675412!4d105.7891425!16s%2Fg%2F11flt8tjxf?hl=en&entry=ttu&g_ep=EgoyMDI1MDIxOS4xIKXMDSoASAFQAw%3D%3D");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            finish();
        });
    }
}
