package com.lephiha.do_an.AlarmPage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.lephiha.do_an.R;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmPageFragment extends Fragment {

    private final String TAG = "Alarm Fragment";

    private AppCompatButton btnConfirm;
    private TextView txtTimeValue;
    private AppCompatButton btnTimepicker;

    private AppCompatCheckBox cbxVibrate;
    private AppCompatCheckBox cbxMonday, cbxTuesday, cbxWednesday, cbxThusday, cbxFriday, cbxSaturday, cbxSunday;

    Calendar calendar = Calendar.getInstance();
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);

    MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(hour).setMinute(minute).build();


    //create ACTION_SET_ALARM intent immediately & listen user's action to add value

    Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
    private TextView EXTRA_MESSAGE;
    int EXTRA_HOUR = 9;
    int EXTRA_MINUTE = 0;

    ArrayList<Integer> EXTRA_DAY = new ArrayList<>();

    private Context context;
    private Activity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        //set sound for alarm
        intent.putExtra(AlarmClock.EXTRA_RINGTONE, R.raw.alarm_sound_3);

        //get message maybe sent from TreatmentFragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            String message = bundle.getString("message");
            EXTRA_MESSAGE.setText(message);
        }

        setupComponent(view);
        setupEvent();

        return view;
    }

    //set component
    private void setupComponent(View view) {
        context = requireContext();
        activity = requireActivity();

        btnConfirm = view.findViewById(R.id.btnConfirm);
        txtTimeValue = view.findViewById(R.id.txtTimeValue);
        btnTimepicker = view.findViewById(R.id.btnTimepicker);
        EXTRA_MESSAGE = view.findViewById(R.id.txtMessage);

        cbxVibrate = view.findViewById(R.id.cbxVibrate);
        cbxMonday = view.findViewById(R.id.cbxMonday);
        cbxThusday = view.findViewById(R.id.cbxThursday);
        cbxTuesday = view.findViewById(R.id.cbxTuesday);
        cbxWednesday = view.findViewById(R.id.cbxWednesday);
        cbxFriday = view.findViewById(R.id.cbxFriday);
        cbxSaturday = view.findViewById(R.id.cbxSaturday);
        cbxSunday = view.findViewById(R.id.cbxSunday);
    }

    //set event

    @SuppressLint("QueryPermissionNeeded")
    private void setupEvent() {
        //btn time picker
        btnTimepicker.setOnClickListener(view -> {

            timePicker.show(getParentFragmentManager(), null);
            timePicker.addOnPositiveButtonClickListener(view1 -> {
                EXTRA_HOUR = timePicker.getHour();
                EXTRA_MINUTE = timePicker.getMinute();

                String hourValue = String.valueOf(EXTRA_HOUR);
                String minuteValue = String.valueOf(EXTRA_MINUTE);

                if (EXTRA_HOUR < 10) {
                    hourValue = "0" + EXTRA_HOUR;

                }
                if (EXTRA_MINUTE < 10) {
                    minuteValue = "0" + EXTRA_MINUTE;
                }

                String timeValue = hourValue+ ":" + minuteValue;
                txtTimeValue.setText(timeValue);
            });
        });

        //checkbox vibrate
        cbxVibrate.setOnCheckedChangeListener(((compoundButton, isVibrate) -> {
            intent.putExtra(AlarmClock.EXTRA_VIBRATE, isVibrate);
        }));

        //checkbox monday-> sunday
        cbxMonday.setOnCheckedChangeListener(((compoundButton, checked) -> {
            if (checked) {
                EXTRA_DAY.add(Calendar.MONDAY);
            }
            else {
                removeElementFromArray(Calendar.MONDAY);
            }
        }));

        cbxTuesday.setOnCheckedChangeListener(((compoundButton, checked) -> {
            if (checked) {
                EXTRA_DAY.add(Calendar.TUESDAY);
            }
            else {
                removeElementFromArray(Calendar.TUESDAY);
            }
        }));
        cbxWednesday.setOnCheckedChangeListener(((compoundButton, checked) -> {
            if (checked) {
                EXTRA_DAY.add(Calendar.WEDNESDAY);
            }
            else {
                removeElementFromArray(Calendar.WEDNESDAY);
            }
        }));
        cbxThusday.setOnCheckedChangeListener(((compoundButton, checked) -> {
            if (checked) {
                EXTRA_DAY.add(Calendar.THURSDAY);
            }
            else {
                removeElementFromArray(Calendar.THURSDAY);
            }
        }));
        cbxFriday.setOnCheckedChangeListener(((compoundButton, checked) -> {
            if (checked) {
                EXTRA_DAY.add(Calendar.FRIDAY);
            }
            else {
                removeElementFromArray(Calendar.FRIDAY);
            }
        }));
        cbxSaturday.setOnCheckedChangeListener(((compoundButton, checked) -> {
            if (checked) {
                EXTRA_DAY.add(Calendar.SATURDAY);
            }
            else {
                removeElementFromArray(Calendar.SATURDAY);
            }
        }));

        cbxSunday.setOnCheckedChangeListener(((compoundButton, checked) -> {
            if (checked) {
                EXTRA_DAY.add(Calendar.SUNDAY);
            }
            else {
                removeElementFromArray(Calendar.SUNDAY);
            }
        }));


        //btn confirm
        btnConfirm.setOnClickListener(view -> {
            intent.putExtra(AlarmClock.EXTRA_HOUR, EXTRA_HOUR);
            intent.putExtra(AlarmClock.EXTRA_MINUTES, EXTRA_MINUTE);
            intent.putExtra(AlarmClock.EXTRA_MESSAGE, EXTRA_MESSAGE.getText().toString());
            intent.putExtra(AlarmClock.EXTRA_DAYS, EXTRA_DAY);

            Intent intentChoose = Intent.createChooser(intent, "Chon App de dat loi nhac nho");
            startActivity(intentChoose);
        });
    }
    

    //remove an element from array by its value

    private void removeElementFromArray(int value) {
        EXTRA_DAY.removeIf(element -> element == value);
    }
}
