package com.lephiha.do_an.HomePage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lephiha.do_an.Container.SpecialityReadAll;
import com.lephiha.do_an.Container.SpecialityReadByID;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Model.Doctor;
import com.lephiha.do_an.Model.Setting;
import com.lephiha.do_an.Model.Speciality;
import com.lephiha.do_an.R;
import com.lephiha.do_an.RecyclerView.DoctorRecyclerView;
import com.lephiha.do_an.RecyclerView.SpecialityRecyclerView;
import com.lephiha.do_an.SearchPage.SearchActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomeFragment extends Fragment {

    private final String TAG = "Home Fragment";
    private GlobaleVariable globaleVariable;
    private RecyclerView recyclerViewSpeciality;
    private RecyclerView recyclerViewDoctor;
    private RecyclerView recyclerViewHandbook;
    private RecyclerView recyclerViewRecommendedPages;

    private EditText searchBar;
    private TextView txtReadMoreSpeciality;
    private  TextView txtReadMoreDoctor;

    private Context context;
    private RecyclerView recyclerViewButton;

    private TextView txtDate;
    private TextView txtWeather;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout frag
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        setupComponent(view);
        setupViewModel();
        setupEvent();

        getCurrentWeather();

        setupRecyclerViewButton();
        setupRecyclerViewHandbook();
        setupRecyclerViewRecommendedPages();

        return view;
    }

    //componet
    private void setupComponent(View view) {
        context = requireContext();
        globaleVariable = (GlobaleVariable) requireActivity().getApplication();

        recyclerViewSpeciality = view.findViewById(R.id.recyclerViewSpeciality);
        recyclerViewDoctor = view.findViewById(R.id.recyclerViewDoctor);
        recyclerViewButton = view.findViewById(R.id.recyclerViewButton);
        recyclerViewHandbook = view.findViewById(R.id.recyclerViewHandbook);
        recyclerViewRecommendedPages = view.findViewById(R.id.recyclerViewRecommendedPages);

        searchBar= view.findViewById(R.id.searchBar);
        txtReadMoreSpeciality = view.findViewById(R.id.txtReadMoreSpeciality);
        txtReadMoreDoctor = view.findViewById(R.id.txtReadMoreDoctor);

        txtWeather = view.findViewById(R.id.txtWeather);
        txtDate = view.findViewById(R.id.txtDate);

    }

    //view model

    private void setupViewModel() {
        //1- declare
        HomePageViewModel viewModel = new ViewModelProvider(this).get(HomePageViewModel.class);
        viewModel.instantiate();

        //2- prepare header + parameters
        Map<String , String> header = globaleVariable.getHeaders();
        header.put("type", "patient");

        //3- listen speciality Read all
        Map<String, String> paramSpeciality = new HashMap<>();
        viewModel.specialityReadAll(header, paramSpeciality);

        viewModel.getSpecialityReadAllResponse().observe(getViewLifecycleOwner(), response -> {
            int result = response.getResult();
            if (result == 1) {
                List<Speciality> list = response.getData();
                setupRecyclerViewSpeciality(list);
            }
        });

        //4- listen doc read all
        Map<String, String> paramDoctor = new HashMap<>();
        viewModel.doctorReadAll(header, paramDoctor);

        viewModel.getDoctorReadAllResponse().observe(getViewLifecycleOwner(), response -> {
            int result = response.getResult();
            if (result == 1 ) {
                List<Doctor> list = response.getData();
                setupRecyclerViewDoctor(list);
            }
        });
    }
    //recycler view speciality

    private void setupRecyclerViewSpeciality(List<Speciality> list) {
        SpecialityRecyclerView specialityAdapter = new SpecialityRecyclerView(requireActivity(), list, R.layout.recycler_view_element_speciality);
        recyclerViewSpeciality.setAdapter(specialityAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewSpeciality.setLayoutManager(manager);
    }

    //recycler view doctor
    private void setupRecyclerViewDoctor(List<Doctor> list) {
        DoctorRecyclerView doctorAdapter = new DoctorRecyclerView(requireActivity(), list, R.layout.recycler_view_element_doctor);
        recyclerViewDoctor.setAdapter(doctorAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewDoctor.setLayoutManager(manager);
    }

    //setup event button
    @SuppressLint({"UnspecifiedImmutableFlag", "ShortAlarm"})
    private void setupEvent() {

        //search bar
        searchBar.setOnClickListener(v -> {
            Intent intent = new Intent(context, SearchActivity.class);
            startActivity(intent);
        });

        //read more speciality
        txtReadMoreSpeciality.setOnClickListener(v -> {
            Intent intent = new Intent(context, SearchActivity.class);
            String filter = context.getString(R.string.speciality);

            intent.putExtra("filterKey", filter);
            startActivity(intent);
        });

        //read more doctor
        txtReadMoreDoctor.setOnClickListener(v -> {
            Intent intent = new Intent(context, SearchActivity.class);
            String filter = context.getString(R.string.doctor);

            intent.putExtra("filterKey", filter);
            startActivity(intent);
        });

    }

    //setup recycler view button

    private void setupRecyclerViewButton() {

        Setting setting0 = new Setting(R.drawable.ic_i_exam_speciality, "specialityExamination", getString(R.string.speciality_examination));
        Setting setting1 = new Setting(R.drawable.ic_exam_general, "generalExamination", getString(R.string.general_examination));
        Setting setting2 = new Setting(R.drawable.ic_exam_heart, "heartExamination", getString(R.string.heart_examination));
        Setting setting3 = new Setting(R.drawable.ic_exam_pregnant, "pregnantExamination", getString(R.string.pregnant_examination));
        Setting setting4 = new Setting(R.drawable.ic_exam_tooth, "toothExamination", getString(R.string.tooth_examination));
        Setting setting5 = new Setting(R.drawable.ic_exam_eye, "eyeExamination", getString(R.string.eye_examination));
        Setting setting6 = new Setting(R.drawable.ic_exam_medical_test, "medicalTest", getString(R.string.medical_test_examination));
        Setting setting7 = new Setting(R.drawable.ic_exam_covid19, "covid19Test", getString(R.string.covid19_examination));


        List<Setting> list = new ArrayList<>();
        list.add(setting0);
        list.add(setting1);
        list.add(setting2);
        list.add(setting3);
        list.add(setting4);
        list.add(setting5);
        list.add(setting6);
        list.add(setting7);


    }
}
