package com.lephiha.do_an.BookingPage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.lephiha.do_an.GuidePage.GuidePageActivity;
import com.lephiha.do_an.HomePage.HomePageActivity;
import com.lephiha.do_an.R;

public class BookingFragment2 extends Fragment {

    private AppCompatButton btnHowToExam;
    private AppCompatButton btnHomepage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflate the layout
        View view = inflater.inflate(R.layout.fragment_booking2, container, false);

        setupComponent(view);
        setupEvent();

        return view;
    }

    //setup cpn
    private void setupComponent(View view) {
        btnHomepage = view.findViewById(R.id.btnHomepage);
        btnHowToExam = view.findViewById(R.id.btnHowToExam);
    }

    //set event
    private void setupEvent() {
        btnHomepage.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), HomePageActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        btnHowToExam.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), GuidePageActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });
    }
}
