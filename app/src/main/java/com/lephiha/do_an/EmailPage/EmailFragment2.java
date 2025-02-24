package com.lephiha.do_an.EmailPage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.R;

public class EmailFragment2 extends Fragment {

    private String txtTitle;
    private String txtContent;
    private String txtDescription;

    private AppCompatButton btnSend;
    private Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflate the layout
        View view = inflater.inflate(R.layout.fragment_email2, container, false);

        setupComponent(view);
        setupEvent();

        //get data from email fragment 1
        dialog = new Dialog(requireContext());
        dialog.announce();
        dialog.btnOK.setOnClickListener(view1 -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        Bundle bundle = this.getArguments();
        if (bundle == null) {
            dialog.show(R.string.attention, requireContext().getString(R.string.oops_there_is_an_issue), R.drawable.ic_close);

        }
        else {
            txtTitle = bundle.getString("title");
            txtDescription = bundle.getString("description");
            txtContent = bundle.getString("content");
        }

        return view;

    }

    private void setupComponent (View view) {
        btnSend = view.findViewById(R.id.btnSend);
    }

    private void setupEvent() {
        btnSend.setOnClickListener(view -> {
            String body = requireContext().getString(R.string.description2) + ": " + txtDescription +
                    "\n\n "+ requireContext().getString(R.string.content) + ": " + txtContent;

            Uri uri = Uri.parse("mailto:"). buildUpon()
                    .appendQueryParameter("to", "phihasky@gmail.com")
                    .appendQueryParameter("subject", txtTitle)
                    .appendQueryParameter("body", body).build();

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
            String chooseTitle = requireContext().getString(R.string.select_application_to_send);
            startActivity(Intent.createChooser(emailIntent, chooseTitle));
        });
    }
}
