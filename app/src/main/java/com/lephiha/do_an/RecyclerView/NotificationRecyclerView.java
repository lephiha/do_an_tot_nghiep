package com.lephiha.do_an.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.TextAppearanceInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.lephiha.do_an.AppointmentPage.AppointmentpageInfoActivity;
import com.lephiha.do_an.BookingPage.BookingPageInfoActivity;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.Model.Notification;
import com.lephiha.do_an.R;

import java.util.List;
import java.util.Objects;

public class NotificationRecyclerView extends RecyclerView.Adapter<NotificationRecyclerView.ViewHolder> {

    private final Context context;
    private final List<Notification> list;
    private final NotificationRecyclerView.Callback callback;

    public NotificationRecyclerView(Context context, List<Notification> list, NotificationRecyclerView.Callback callback) {
        this.context = context;
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public NotificationRecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_notification, parent, false);
        return  new NotificationRecyclerView.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationRecyclerView.ViewHolder holder, int position) {

        Notification notification = list.get(position);

        int id = notification.getId();
        String message = notification.getMessage();
        int recordId = notification.getRecordId();
        String recordType = notification.getRecordType();
        String createAt = Tooltip.beautifierDatetime(context, notification.getCreateAt());
        int isRead = notification.getIsRead();

        //set text color
        int colorRead = context.getColor(R.color.colorTextBlack);
        holder.message.setTextColor(colorRead);

        //set text colot for unread notif
        if (isRead == 0) {
            int colorUnread = context.getColor(R.color.colorTextBlack);
            holder.message.setTextColor(colorUnread);
        }

        holder.datetime.setText(createAt);
        holder.message.setText(message);
        holder.layout.setOnClickListener(view -> {
            //set color for notification we click on

            holder.message.setTextColor(colorRead);

            //update its status from unread to read
            if (isRead == 0 ) {
                callback.markAsRead(String.valueOf(id));
            }

            //base on record type to open corresponding activity
            Intent intent;

            if (Objects.equals(recordType, "booking")) {
                intent = new Intent(context, BookingPageInfoActivity.class);
            }
            else {
                intent = new Intent(context, AppointmentpageInfoActivity.class);
            }
            intent.putExtra("id", String.valueOf(recordId));
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView datetime;
        private final LinearLayout layout;

        private final TextView message;

        public ViewHolder(@NonNull android.view.View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.elementLayout);
            datetime = itemView.findViewById(R.id.elementDatetime);
            message = itemView.findViewById(R.id.elementMessage);
        }
    }


    public interface Callback {

        void markAsRead(String notificationId);
    }
}
