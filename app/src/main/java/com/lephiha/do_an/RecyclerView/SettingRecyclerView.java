package com.lephiha.do_an.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lephiha.do_an.AlarmPage.AlarmPageActivity;
import com.lephiha.do_an.EmailPage.EmailPageActivity;
import com.lephiha.do_an.GuidePage.GuidePageActivity;
import com.lephiha.do_an.HomePage.HomePageActivity;
import com.lephiha.do_an.Model.Setting;
import com.lephiha.do_an.R;
import com.lephiha.do_an.SettingPage.AppearanceActivity;
import com.lephiha.do_an.SettingPage.AppointmentHistoryActivity;
import com.lephiha.do_an.SettingPage.BookingHistoryActivity;
import com.lephiha.do_an.SettingPage.InformationActivity;
import com.lephiha.do_an.WebPage.WebpageActivity;
import com.lephiha.do_an.configAPI.Constant;

import java.util.List;

public class SettingRecyclerView extends RecyclerView.Adapter<SettingRecyclerView.ViewHolder> {

    private Context context;
    private final List<Setting> list;

    public SettingRecyclerView(Context context, List<Setting> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recycler_view_setting, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingRecyclerView.ViewHolder holder, int position) {

        Setting element = list.get(position);

        holder.icon.setImageResource(element.getIcon());
        holder.name.setText(element.getName());
        holder.layout.setOnClickListener(view -> {
            Intent intent;

            switch (element.getId()) {
                case "appearance":
                    intent = new Intent(context, AppearanceActivity.class);
                    context.startActivity(intent);
                    break;

                case "appointmentHistory":
                    intent = new Intent(context, AppointmentHistoryActivity.class);
                    context.startActivity(intent);
                    break;

                case "bookingHistory":
                    intent = new Intent(context, BookingHistoryActivity.class);
                    context.startActivity(intent);
                    break;

                case "exit":
                    HomePageActivity.getInstance().exit();
                    break;

                case "aboutUs":
                    intent = new Intent(context, WebpageActivity.class);
                    intent.putExtra("url", Constant.VIDEO_PATH());
                    context.startActivity(intent);
                    break;

                case "information":
                    intent = new Intent(context, InformationActivity.class);
                    context.startActivity(intent);
                    break;

                case "emailUs":
                    intent = new Intent(context, EmailPageActivity.class);
                    context.startActivity(intent);
                    break;

                case "guide":
                    intent = new Intent(context, GuidePageActivity.class);
                    context.startActivity(intent);
                    break;

                case "remider":
                    intent = new Intent(context, AlarmPageActivity.class);
                    context.startActivity(intent);
                    break;
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView name;
        private final ImageView icon;
        private final LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.elementName);
            icon = itemView.findViewById(R.id.elementIcon);
            layout = itemView.findViewById(R.id.elementLayout);
        }
    }
}
