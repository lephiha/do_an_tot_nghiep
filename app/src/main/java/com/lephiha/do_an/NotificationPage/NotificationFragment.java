package com.lephiha.do_an.NotificationPage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lephiha.do_an.Container.NotificationMarkAllAsRead;
import com.lephiha.do_an.Container.NotificationMarkAsRead;
import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.LoadingScreen;
import com.lephiha.do_an.HomePage.HomePageActivity;
import com.lephiha.do_an.Model.Notification;
import com.lephiha.do_an.R;
import com.lephiha.do_an.RecyclerView.NotificationRecyclerView;
import com.lephiha.do_an.configAPI.HTTPRequest;
import com.lephiha.do_an.configAPI.HTTPService;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NotificationFragment extends Fragment implements NotificationRecyclerView.Callback {

    private final String TAG = "Notification Fragment";
    private RecyclerView recyclerView;

    private Dialog dialog;
    private LoadingScreen loadingScreen;

    private Context context;
    private Activity activity;

    private TextView txtMarkAllAsRead;
    private NotificationViewModel viewModel;

    private SwipeRefreshLayout swipeRefreshLayout;
    private Map<String, String> header;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        setupComponent(view);
        setupViewModel();
        setupEvent();

        return view;
    }

    //set Component
    private void setupComponent(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        GlobaleVariable globaleVariable = (GlobaleVariable) requireActivity().getApplication();

        context = requireContext();
        activity = requireActivity();

        dialog = new Dialog(context);
        loadingScreen = new LoadingScreen(activity);

        txtMarkAllAsRead = view.findViewById(R.id.txtMarkAllAsRead);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        header = globaleVariable.getHeaders();
    }

    //set ViewModel
    private void setupViewModel() {
        //1- declare
        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        viewModel.instantiate();

        //2
        viewModel.readAll(header);

        //3- listen for response
        viewModel.getReadAllResponse().observe((LifecycleOwner) context, response -> {
            try {
                int result = response.getResult();
                if (result == 1) { //luu thong tin user va vao homepage

                    //print notification
                    List<Notification> list = response.getData();
                    setupRecyclerView(list);

                }
                if (result == 0) {// thong bao va thoat app

                    dialog.announce();
                    dialog.show(R.string.attention, getString(R.string.check_your_internet_connection), R.drawable.ic_close);
                    dialog.btnOK.setOnClickListener(view -> {
                        dialog.close();
                        activity.finish();
                    });
                }
            }
            catch (Exception e) {
                dialog.announce();
                dialog.show(R.string.attention, this.getString(R.string.check_your_internet_connection), R.drawable.ic_info);
                dialog.btnOK.setOnClickListener(view->{
                    dialog.close();
                    activity.finish();
                });
            }
        });

        //4- get animation
        viewModel.getAnimation().observe((LifecycleOwner) context, aBoolean -> {
            if (aBoolean) {
                loadingScreen.start();
            } else {
                loadingScreen.stop();
            }
        });
    }

    private void setupRecyclerView(List<Notification> list) {

        NotificationRecyclerView notificationAdapter = new NotificationRecyclerView(context, list, this);
        recyclerView.setAdapter(notificationAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
    }

    //set Event

    private void setupEvent() {
        txtMarkAllAsRead.setOnClickListener(view -> {
            loadingScreen.start();
            markAllAsRead();
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.readAll(header);
            HomePageActivity.getInstance().setNumberOnNotificationIcon();
            swipeRefreshLayout.setRefreshing(false);
        });
    }


    public void markAllAsRead() {
        //1 setup retrofit
        Retrofit services = HTTPService.getInstance();
        HTTPRequest api = services.create(HTTPRequest.class);

        //3- make request
        Call<NotificationMarkAllAsRead> container = api.notificationMarkAllAsRead(header);
        //4- listen for response
        container.enqueue(new Callback<NotificationMarkAllAsRead>() {
            @Override
            public void onResponse(@NonNull Call<NotificationMarkAllAsRead> call,@NonNull Response<NotificationMarkAllAsRead> response) {
                loadingScreen.stop();
                if (response.isSuccessful()) {
                    NotificationMarkAllAsRead content = response.body();
                    assert content != null;
                    int result = content.getResult();
                    if (result == 1) {
                        //update recyclerView & number of unread notification
                        viewModel.readAll(header);
                        HomePageActivity.getInstance().setNumberOnNotificationIcon();

                        //show successful message dialog
                        Dialog dialog = new Dialog(context);
                        dialog.announce();
                        dialog.btnOK.setOnClickListener(view -> dialog.close());
                        String title = context.getString(R.string.success);
                        String message = context.getString(R.string.successful_action);
                        dialog.show(title, message, R.drawable.ic_check);
                    }
                }
                if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        System.out.println(jObjError);
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationMarkAllAsRead> call, @NonNull Throwable t) {
                System.out.println("Notification Fragment markAllAsRead - Read All - error: " + t.getMessage());
            }
        });
    }

    @Override
    public void markAsRead(String notificationId) {
        if (TextUtils.isEmpty(notificationId)) {
            return;
        }

        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //3
        Call<NotificationMarkAsRead> container = api.notificationMarkAsRead(header, notificationId);

        //4
        container.enqueue(new Callback<NotificationMarkAsRead>() {
            @Override
            public void onResponse(@NonNull Call<NotificationMarkAsRead> call,@NonNull Response<NotificationMarkAsRead> response) {
                if (response.isSuccessful()) { //update number of unread notification
                    NotificationMarkAsRead content = response.body();
                    assert content != null;
                    int result = content.getResult();
                    if (result == 1) {
                        HomePageActivity.getInstance().setNumberOnNotificationIcon();
                    }
                    if (response.errorBody() != null) { //fail -> show message
                        System.out.println(response);
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            System.out.println(jObjError);
                        }
                        catch (Exception e) {
                            System.out.println(TAG);
                            System.out.println("Exception: " + e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationMarkAsRead> call, @NonNull Throwable t) {
                System.out.println("Notification Fragment markAsRead - Read All - error: " + t.getMessage());
            }
        });
    }
}
