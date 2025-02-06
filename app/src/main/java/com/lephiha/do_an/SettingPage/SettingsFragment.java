package com.lephiha.do_an.SettingPage;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends Fragment {

    private RecyclerView settingRecyclerView;
    private Context context;

    private CircleImageView imgAvatar;
    private TextView txtName;
    private GlobaleVariable globaleVariable;

    private TextView txtHealthInsuranceNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        //setupComponent(view);
        //setupRecyclerView();
        return view;
    }
}
