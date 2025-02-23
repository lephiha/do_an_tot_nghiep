package com.lephiha.do_an.AppointmentPage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.LoadingScreen;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.Model.Appointment;
import com.lephiha.do_an.R;
import com.lephiha.do_an.RecyclerView.Appointment1RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentFragment extends Fragment {

    private final String TAG = "Appointment Fragment";

    private RecyclerView appointmentRecyclerView;


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

        setupComponent(view);
        setupViewModel();

        return view;
    }

    private void setupComponent(View view) {
        context = requireContext();
        activity = requireActivity();
        GlobaleVariable globaleVariable= (GlobaleVariable) activity.getApplication();
        header = globaleVariable.getHeaders();

        loadingScreen = new LoadingScreen(activity);
        dialog = new Dialog(context);

        lytNoAppointment = view.findViewById(R.id.lytNoAppointment);
        appointmentRecyclerView = view.findViewById(R.id.recyclerView);
    }

    //set view model
    private void setupViewModel() {
        //declare
        AppointmentViewModel viewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);
        viewModel.instantiate();

        //send request
        Map<String, String> parameter = new HashMap<>();
        String today = Tooltip.getToday();
        parameter.put("date", today);
        viewModel.readAll(header, parameter);

        //listen for response
        viewModel.getReadAllResponse().observe((LifecycleOwner) context, response -> {
            try {
                int result = response.getResult();

                if (result == 1) { //luu thong tin vao homepage
                    List<Appointment> appointments = response.getData();
                    setupRecyclerView(appointments);
                }
                if (result == 0) {
                    System.out.println(TAG + "- result: " + result);
                    dialog.announce();
                    dialog.show(R.string.attention, getString(R.string.check_your_internet_connection), R.drawable.ic_info);
                    dialog.btnOK.setOnClickListener(view -> {
                        dialog.close();
                        activity.finish();
                    });
                }
            }
            catch (Exception e) {
                System.out.println(TAG + "- exception: "+ e.getMessage());
            }
        });

        //animation
        viewModel.getAnimation().observe((LifecycleOwner) context, aBoolean -> {
            if (aBoolean) {
                loadingScreen.start();
            }
            else {
                loadingScreen.stop();
            }
        });
    }

    private void setupRecyclerView(List<Appointment> list) {
        if (list.size() == 0) {
            lytNoAppointment.setVisibility(View.VISIBLE);
            appointmentRecyclerView.setVisibility(View.GONE);
        }
        else {
            lytNoAppointment.setVisibility(View.GONE);
            appointmentRecyclerView.setVisibility(View.VISIBLE);

            Appointment1RecyclerView appointment1RecyclerView = new Appointment1RecyclerView(context, list);
            appointmentRecyclerView.setAdapter(appointment1RecyclerView);

            LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            appointmentRecyclerView.setLayoutManager(manager);
        }
    }

}
