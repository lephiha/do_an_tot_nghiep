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
import androidx.recyclerview.widget.RecyclerView;

import com.lephiha.do_an.Adapter.FilterOptionAdapter;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.Model.Option;
import com.lephiha.do_an.R;

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
        sprFilter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
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
        
    }
}
