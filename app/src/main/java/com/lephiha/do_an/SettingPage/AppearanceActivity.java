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
        // Áp dụng chế độ tối trước khi setContentView
        GlobaleVariable globaleVariable = (GlobaleVariable) this.getApplication();
        sharedPreferences = this.getApplication().getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);
        int darkMode = sharedPreferences.getInt("darkMode", AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(darkMode);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_appearance);

        setupComponent();
        setupSpinnerLanguage();
        setupEvent();
    }

    // Set component
    private void setupComponent() {
        btnBack = findViewById(R.id.btnBack);
        sprLanguage = findViewById(R.id.sprLanguage);
        switchDarkMode = findViewById(R.id.switchDarkMode);

        int darkMode = sharedPreferences.getInt("darkMode", AppCompatDelegate.MODE_NIGHT_NO);
        switchDarkMode.setChecked(darkMode == AppCompatDelegate.MODE_NIGHT_YES);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tooltip.setLocale(this, sharedPreferences);
    }

    // Set spinner language
    private void setupSpinnerLanguage() {
        // Prepare options
        List<Option> list = new ArrayList<>();
        Option option1 = new Option(R.drawable.ic_vietnamese_square, getString(R.string.vietnamese));
        Option option2 = new Option(R.drawable.ic_english_square, getString(R.string.english));
        Option option3 = new Option(R.drawable.ic_germany_square, getString(R.string.deutsch));

        list.add(option1);
        list.add(option2);
        list.add(option3);

        // Create spinner
        FilterOptionAdapter filterAdapter = new FilterOptionAdapter(this, list);
        sprLanguage.setAdapter(filterAdapter);
        sprLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = list.get(position).getName();
                setupLanguage(text);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Set selected language in spinner
        String applicationLanguage = sharedPreferences.getString("language", getString(R.string.vietnamese));
        String vietnamese = getString(R.string.vietnamese);
        String english = getString(R.string.english);
        String germany = getString(R.string.deutsch);

        if (Objects.equals(applicationLanguage, vietnamese)) {
            sprLanguage.setSelection(0);
        } else if (Objects.equals(applicationLanguage, english)) {
            sprLanguage.setSelection(1);
        } else if (Objects.equals(applicationLanguage, germany)) {
            sprLanguage.setSelection(2);
        }
    }

    // Set event
    private void setupEvent() {
        btnBack.setOnClickListener(view -> finish());

        // Switch on/off
        switchDarkMode.setOnCheckedChangeListener((compoundButton, flag) -> {
            int mode = flag ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            AppCompatDelegate.setDefaultNightMode(mode);
            sharedPreferences.edit().putInt("darkMode", mode).apply();
            recreate(); // Làm mới activity để áp dụng chế độ tối ngay lập tức
        });
    }

    // Set language
    private void setupLanguage(String language) {
        String currentLanguage = sharedPreferences.getString("language", getString(R.string.vietnamese));
        if (currentLanguage.equals(language)) {
            return; // Không làm gì nếu ngôn ngữ không thay đổi
        }

        Locale myLocale = new Locale("en");
        if (language.equals(getString(R.string.vietnamese))) {
            myLocale = new Locale("vi");
        } else if (language.equals(getString(R.string.deutsch))) {
            myLocale = new Locale("de");
        }

        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(myLocale);
        resources.updateConfiguration(configuration, dm);

        // Lưu ngôn ngữ mới
        sharedPreferences.edit().putString("language", language).apply();

        // Khởi động lại activity hiện tại
        Intent refresh = new Intent(this, AppearanceActivity.class);
        startActivity(refresh);
        finish();
    }
}