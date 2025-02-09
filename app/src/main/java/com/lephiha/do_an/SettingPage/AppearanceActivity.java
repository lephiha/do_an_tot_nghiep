package com.lephiha.do_an.SettingPage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.lephiha.do_an.Adapter.FilterOptionAdapter;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.MainActivity;
import com.lephiha.do_an.Model.Option;
import com.lephiha.do_an.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AppearanceActivity extends AppCompatActivity {

    private final String TAG = "Appearance Activity";
    private ImageButton btnBack;
    private Spinner sprLanguage;
    private SwitchCompat switchDarkMode;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_appearance);

        setupComponent();
        setupSpinnerLanguage();
        setupEvent();
    }

    //set component
    private void setupComponent() {
        GlobaleVariable globaleVariable = (GlobaleVariable) this.getApplication();
        sharedPreferences = this.getApplication().getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);

        int darkMode = sharedPreferences.getInt("darkMode", 1); //1- off, 2- on

        btnBack = findViewById(R.id.btnBack);
        sprLanguage = findViewById(R.id.sprLanguage);

        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchDarkMode.setChecked(false);

        if (darkMode == 2) {
            switchDarkMode.setChecked(true);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Tooltip.setLocale(this, sharedPreferences);
    }

    //set spinner language

    private void setupSpinnerLanguage() {
        //prepare option
        List<Option> list = new ArrayList<>();
        Option option1 = new Option(R.drawable.ic_vietnamese_square, "Tiếng Việt");
        Option option2 = new Option(R.drawable.ic_english_square, "English");
        Option option3 = new Option(R.drawable.ic_germany_square, "Deutsch");

        list.add(option1);
        list.add(option2);
        list.add(option3);

        //creat spinner
        FilterOptionAdapter filterOptionAdapter = new FilterOptionAdapter(this, list);
        sprLanguage.setAdapter(filterOptionAdapter);
        sprLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = list.get(position).getName();
                times++;
                setupLanguage(text);
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });

        //set selected language in spin
        String applicationLanguage = sharedPreferences.getString("language", "Tiếng Việt");
        String vietnamese = "Tiếng Việt";
        String english = "English";
        String germany = "Deutsch";

        System.out.println(TAG);
//        System.out.println("application language: " + applicationLanguage);

        if (Objects.equals(applicationLanguage, vietnamese)) {
            sprLanguage.setSelection(0);
        }
        else if (Objects.equals(applicationLanguage, english)) {
            sprLanguage.setSelection(1);
        }
        else if (Objects.equals(applicationLanguage, germany)) {
            sprLanguage.setSelection(2);
        }
    }

    //set event
    private void setupEvent() {
        btnBack.setOnClickListener(view -> finish());

        //Switch on/off
        switchDarkMode.setOnCheckedChangeListener((compoundButton,flag) ->{
            int value;
            if (flag) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                value = AppCompatDelegate.MODE_NIGHT_YES;
            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                value = AppCompatDelegate.MODE_NIGHT_NO;
            }
            sharedPreferences.edit().putInt("darkMode", value).apply();
        } );
    }

    ///set language
    //times af so chon spinner
    //times = 1 nghĩa là lần đầu mở activity để bỏ qua lần đầu

    int times = 0;

    private void setupLanguage(String language) {
        if (times == 1) {
            return;
        }

        String vietnamese = "Tiếng Việt";
        String english = "English";
        String germany = "Deutsch";

        Locale myLocale = new Locale("en");

        if (Objects.equals(language, vietnamese)) {
            myLocale = new Locale("vi");
        }
        if (Objects.equals(language, germany)) {
            myLocale = new Locale("de");
        }

        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();

        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(myLocale);

        resources.updateConfiguration(configuration, dm);
        Intent refresh = new Intent(this, MainActivity.class);
        finish();
        startActivity(refresh);

        //save app's language in ROM
        sharedPreferences.edit().putString("language", language).apply();
    }
}
