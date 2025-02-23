package com.lephiha.do_an.AppointmentPage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.lephiha.do_an.Helper.LoadingScreen;
import com.lephiha.do_an.R;

import java.util.Map;

public class AppointmentFragment extends Fragment {

    private final String TAG = "Appointment Fragment";

    private RecyclerView recyclerView;


    private Context context;
    private Activity activity;
    private Dialog dialog;
    private LoadingScreen loadingScreen;

    private Map<String, String> header;
    private LinearLayout lytNoAppointment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);

        //setupComponent(view);
        //setupViewModel();

        return view;
    }

}
