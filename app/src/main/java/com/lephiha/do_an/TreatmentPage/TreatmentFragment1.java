package com.lephiha.do_an.TreatmentPage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lephiha.do_an.AppointmentPage.AppointmentFragment;
import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.LoadingScreen;
import com.lephiha.do_an.Model.Treatment;
import com.lephiha.do_an.R;
import com.lephiha.do_an.RecyclerView.TreatmentRecyclerView;

import java.util.List;
import java.util.Map;

public class TreatmentFragment1 extends Fragment {

    private final String TAG = "Treatment Fragment 1";

    private Context context;
    private Activity activity;

    private Dialog dialog;
    private LoadingScreen loadingScreen;

    private Map<String, String> header;

    private TreatmentViewModel viewModel;
    private RecyclerView treatmentRecyclerView;
    private String appointmentId;
    private AppCompatButton btnSetTime;
    private String message;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_treatment1, container, false);

        setupComponent(view);
        setupViewModel();
        setupEvent();

        return view;
    }

    //set component

    private void setupComponent(View view) {
        context = requireContext();
        activity = requireActivity();
        dialog = new Dialog(context);
        loadingScreen = new LoadingScreen(activity);

        GlobaleVariable globaleVariable = (GlobaleVariable) activity.getApplication();

        appointmentId = activity.getIntent().getStringExtra("appointmentId");

        treatmentRecyclerView = view.findViewById(R.id.treatmentRecyclerView);

        header = globaleVariable.getHeaders();

        btnSetTime = view.findViewById(R.id.btnSetAlarm);

    }

    //set viewModel
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(TreatmentViewModel.class);
        viewModel.instantiate();

        //prepare header & parameter
        viewModel.treatmentReadAll(header, appointmentId);
        viewModel.getTreatmentReadAllResponse().observe((LifecycleOwner) context, response -> {
            try {
                int result = response.getResult();
                if  (result ==  1) { //luu thong tin user va vao homepage
                    List<Treatment> treatments = response.getData();
                    message = treatments.get(0).getInstruction();
                    setupRecyclerView(treatments);

                }
                if  (result == 0) {
                    System.out.println(TAG + "- result: "+ result);
                    dialog.announce();
                    dialog.show(R.string.attention, getString(R.string.check_your_internet_connection), R.drawable.ic_info);
                    dialog.btnOK.setOnClickListener(view -> {
                        dialog.close();
                        activity.finish();
                    });
                }
            }
            catch (Exception e) {
                System.out.println(TAG + "- error: "+ e.getMessage());
            }
        });

        //animation
        viewModel.getAnimation().observe((LifecycleOwner) context, aBoolean -> {
            if (aBoolean) {
                loadingScreen.start();
            } else {
                loadingScreen.stop();
            }
        });
    }

    //setup recyclerView

    private void setupRecyclerView(List<Treatment> list) {
        TreatmentRecyclerView treatmentAdapter = new TreatmentRecyclerView(context, list);
        treatmentRecyclerView.setAdapter(treatmentAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        treatmentRecyclerView.setLayoutManager(manager);
    }

    //set Event
    private void setupEvent() {
        btnSetTime.setOnClickListener(view -> {
            String fragmentTag = "AlarmFragment";
            Fragment nextFragment = new AppointmentFragment(); //create alarm

            Bundle bundle = new Bundle();
            bundle.putString("message", message);
            nextFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, nextFragment, fragmentTag)
                    .addToBackStack(fragmentTag)
                    .commit();
        });
    }
}
