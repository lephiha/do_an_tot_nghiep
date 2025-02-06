package com.lephiha.do_an.NotificationPage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.LoadingScreen;
import com.lephiha.do_an.R;

import java.util.Map;

public class NotificationFragment extends Fragment {

    private final String TAG = "Notification Fragment";
    private RecyclerView recyclerView;

    private Dialog dialog;
    private LoadingScreen loadingScreen;

    private Context context;
    private Activity activity;

    private TextView txtMarkAllAsRead;
    private NotificationViewModel notificationViewModel;

    private SwipeRefreshLayout swipeRefreshLayout;
    private Map<String, String> header;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        /**setupComponent(view);
        setupViewModel();
        setupEvent();**/

        return view;
    }
}
