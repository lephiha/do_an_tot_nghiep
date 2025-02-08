package com.lephiha.do_an.SettingPage;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Model.Setting;
import com.lephiha.do_an.Model.User;
import com.lephiha.do_an.R;
import com.lephiha.do_an.RecyclerView.SettingRecyclerView;
import com.lephiha.do_an.configAPI.Constant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

        setupComponent(view);
        setupRecyclerView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showInfo();
    }

    private void showInfo() {
        if( globaleVariable.getAuthUser() == null )
        {
            return;
        }
        User user = globaleVariable.getAuthUser();
        int id = user.getId();
        String heathInsuranceNumber = context.getString(R.string.health_insurance_number) + ": " + id;
        String name = user.getName();
        String avatar = Constant.UPLOAD_URI() + user.getAvatar();

        if( user.getAvatar().length() >0)
        {
            Picasso.get().load(avatar).into(imgAvatar);
        }
        txtName.setText(name);
        txtHealthInsuranceNumber.setText(heathInsuranceNumber);
    }

    //setup component

    private void setupComponent(View view) {
        context = requireContext();
        settingRecyclerView = view.findViewById(R.id.settingRecyclerView);

        imgAvatar = view.findViewById(R.id.imgAvatar);
        txtName = view.findViewById(R.id.txtName);
        txtHealthInsuranceNumber = view.findViewById(R.id.txtHealthInsuranceNumber);

        globaleVariable = (GlobaleVariable) requireActivity().getApplication();

        showInfo();
    }

    //set recyclerview
    private void setupRecyclerView() {
        Setting setting0 = new Setting(R.drawable.ic_leeha, "aboutUs", getString(R.string.about_us));
        Setting setting1 = new Setting(R.drawable.ic_appointment_history, "appointmentHistory", getString(R.string.appointment_history));
        Setting setting2 = new Setting(R.drawable.ic_appointment_history, "bookingHistory", getString(R.string.booking_history));
        Setting setting3 = new Setting(R.drawable.ic_reminder, "remider", context.getString(R.string.reminder));
        Setting setting4 = new Setting(R.drawable.ic_personal_information, "information", getString(R.string.personal_information));
        Setting setting5 = new Setting(R.drawable.ic_appearance, "appearance", getString(R.string.appearance));
        Setting setting6 = new Setting(R.drawable.ic_email_us, "emailUs", context.getString(R.string.email_us));
        Setting setting7 = new Setting(R.drawable.ic_guide, "guide", context.getString(R.string.guide));
        Setting setting8 = new Setting(R.drawable.ic_exit, "exit", getString(R.string.exit));

        List<Setting> list = new ArrayList<>();
        list.add(setting0);
        list.add(setting1);
        list.add(setting2);
        list.add(setting3);
        list.add(setting4);
        list.add(setting5);
        list.add(setting6);
        list.add(setting7);
        list.add(setting8);

        SettingRecyclerView settingAdapter = new SettingRecyclerView(context, list);
        settingRecyclerView.setAdapter(settingAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        settingRecyclerView.setLayoutManager(manager);
    }

}


