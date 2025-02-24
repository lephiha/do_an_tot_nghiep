package com.lephiha.do_an.AlarmPage;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.lephiha.do_an.R;

public class AlarmPageActivity extends AppCompatActivity {

    private final String TAG = "Alarm activity";
    private final FragmentManager manager = getSupportFragmentManager();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmpage);

        String fragmentTag = "AlarmFragment";
        Fragment fragment = new AlarmPageFragment();

        //1
        FragmentTransaction transaction = manager.beginTransaction();

        try {
            //2
            transaction.replace(R.id.frameLayout, fragment, fragmentTag);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        catch (Exception e) {
            System.out.println(TAG);
            e.getStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
