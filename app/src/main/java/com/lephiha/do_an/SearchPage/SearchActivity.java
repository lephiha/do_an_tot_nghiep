package com.lephiha.do_an.SearchPage;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lephiha.do_an.Adapter.FilterOptionAdapter;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.Model.Doctor;
import com.lephiha.do_an.Model.Option;
import com.lephiha.do_an.Model.Service;
import com.lephiha.do_an.Model.Speciality;
import com.lephiha.do_an.R;
import com.lephiha.do_an.RecyclerView.DoctorRecyclerView;
import com.lephiha.do_an.RecyclerView.ServiceRecyclerView;
import com.lephiha.do_an.RecyclerView.SpecialityRecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity {

    private final String TAG = "SearchPage Activity";
    private ImageButton btnBack;
    private Spinner sprFilter;

    private SearchView searchView;
    private GlobaleVariable globaleVariable;
    private SearchViewModel viewModel;

    private Map<String, String > header;
    private final Map<String, String> paramDoctor = new HashMap<>();
    private final Map<String,String> paramSpeciality = new HashMap<>();
    private final Map<String, String> paramService = new HashMap<>();

    //filter key
    private String filterKey;

    private RecyclerView doctorRecyclerView;
    private RecyclerView specialityRecyclerView;
    private RecyclerView serviceRecyclerView;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setupComponent();
        setUpFilterSpinner();
        setupViewModel();
        setupEvent();

        /*this block of code below - it handles event click on "READ MORE" button from HomepageActivity*/
        if(getIntent().getStringExtra("filterKey") != null)
        {
            filterKey = getIntent().getStringExtra("filterKey");

            String service = this.getString(R.string.service);
            String speciality = this.getString(R.string.speciality);
            String doctor = this.getString(R.string.doctor);

            int position = 0;
            if(Objects.equals(filterKey, service))
            {
                // do nothing
            }
            else if(Objects.equals(filterKey, speciality))
            {
                position = 1;
            }
            else
            {
                position = 2;
            }
            sprFilter.setSelection(position);
        }

    }

    //set up component
    private void setupComponent() {
        btnBack = findViewById(R.id.btnBack);
        sprFilter = findViewById(R.id.sprFilter);
        searchView = findViewById(R.id.searchView);

        globaleVariable = (GlobaleVariable) this.getApplication();
        sharedPreferences = this.getApplication().getSharedPreferences(globaleVariable.getSharedReferenceKey(), MODE_PRIVATE);
        header = globaleVariable.getHeaders();

        filterKey = this.getString(R.string.service);

        doctorRecyclerView = findViewById(R.id.doctorRecyclerView);
        specialityRecyclerView = findViewById(R.id.specialityRecyclerView);
        serviceRecyclerView = findViewById(R.id.serviceRecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tooltip.setLocale(this, sharedPreferences);
    }

    //set option spinner

    private void setUpFilterSpinner() {
        List<Option> filterOptions = globaleVariable.getFilterOption();
        FilterOptionAdapter filterOptionAdapter = new FilterOptionAdapter(this, filterOptions);
        sprFilter.setAdapter(filterOptionAdapter);
        sprFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterKey = filterOptions.get(position).getName();
                sendRequestFilterKey();
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sendRequestFilterKey() {
        List<Option> options = globaleVariable.getFilterOption();
        String option1 = options.get(0).getName(); //service
        String option2 = options.get(1).getName(); //speciality
        String option3 = options.get(2).getName(); //doctor

        if (filterKey.equals(option2)) {
            viewModel.specialityReadAll(header, paramSpeciality);
            doctorRecyclerView.setVisibility(View.GONE);
            specialityRecyclerView.setVisibility(View.VISIBLE);
            serviceRecyclerView.setVisibility(View.GONE);
        }
        else if (filterKey.equals(option3)) {
            viewModel.serviceReadAll(header, paramService);
            doctorRecyclerView.setVisibility(View.VISIBLE);
            specialityRecyclerView.setVisibility(View.GONE);
            serviceRecyclerView.setVisibility(View.GONE);
        }
        else {
            viewModel.doctorReadAll(header, paramDoctor);
            doctorRecyclerView.setVisibility(View.GONE);
            specialityRecyclerView.setVisibility(View.GONE);
            serviceRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    //setup event

    private void setupEvent() {
        //button back
        btnBack.setOnClickListener(view -> finish());

        //button search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                paramDoctor.put("search", query);
                paramService.put("search", query);
                paramSpeciality.put("search", query);
                sendRequestFilterKey();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    paramDoctor.put("search", "");
                    paramService.put("search", "");
                    paramSpeciality.put("search", "");
                    sendRequestFilterKey();
                    searchView.clearFocus();
                }
                return false;
            }
        });
    }

    //setup view model

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        viewModel.instantiate();

        //doctor
        paramDoctor.put("length", "100"); //param có thể truyỳn trong api
        viewModel.doctorReadAll(header, paramDoctor);
        viewModel.getDoctorReadAllResponse().observe(this, response -> {
            int result = response.getResult();
            if (result == 1) {
                List<Doctor> list = response.getData();
                setupRecyclerViewDoctor(list);
            }
        });

        //speciality
        paramSpeciality.put("length", "100");
        viewModel.specialityReadAll(header, paramSpeciality);
        viewModel.getSpecialityReadAll().observe(this, response -> {
            int result = response.getResult();
            if (result == 1) {
                List<Speciality> list = response.getData();
                setupRecyclerViewSpeciality(list);
            }
        });

        //service

        paramService.put("length", "100");
        viewModel.serviceReadAll(header, paramService);
        viewModel.getServiceReadAllResponse().observe(this, response -> {
            int result = response.getResult();
            if (result == 1) {
                List<Service> list = response.getData();
                setupRecyclerViewService(list);
            }
        });
    }

    private void setupRecyclerViewDoctor(List<Doctor> list) {
        DoctorRecyclerView doctorAdapter = new DoctorRecyclerView(this, list);
        doctorRecyclerView.setAdapter(doctorAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        doctorRecyclerView.setLayoutManager(manager);
    }

    private void setupRecyclerViewSpeciality(List<Speciality> list) {
        SpecialityRecyclerView specialityAdapter = new SpecialityRecyclerView(this, list, R.layout.recycler_view_element_speciality2);
        specialityRecyclerView.setAdapter(specialityAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        specialityRecyclerView.setLayoutManager(manager);
    }

    private void setupRecyclerViewService(List<Service> list) {
        ServiceRecyclerView serviceAdapter = new ServiceRecyclerView(this, list);
        serviceRecyclerView.setAdapter(serviceAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        serviceRecyclerView.setLayoutManager(manager);
    }
}
