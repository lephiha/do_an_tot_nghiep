package com.lephiha.do_an.Helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.lephiha.do_an.HomePage.HomePageActivity;
import com.lephiha.do_an.R;
import com.lephiha.do_an.configAPI.Constant;

import java.util.Objects;

public class Notification extends android.app.Notification{

    int notificationId = 1896;
    private final Context context;
    private final String CHANNEL_ID = Constant.APP_NAME();
    private final NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder builder;

    public Notification(Context context)
    {
        this.context = context;
        notificationManager = NotificationManagerCompat.from(context);
    }

    /** this function always runs whenever the application opens
     *
     * Notice 1:  that the NotificationCompat.Builder constructor requires that you provide a channel ID.
     * This is required for compatibility with Android 8.0 (API level 26) and higher,
     * but is ignored by older versions
     *
     * Notice 2: Before you can deliver the notification on Android 8.0 and higher,
     * you must register your app's notification channel with the system by passing
     * an instance of NotificationChannel to createNotificationChannel().
     * So the following code is blocked by a condition on the SDK_INT version:
     */

    public void createChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            String description = context.getString(R.string.app_description);

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
    }

    //this function is used to create notification

    public void setup(String title, String text, String bigText) {
        //mở app khi click vào notification
        Intent intent = new Intent(context, HomePageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        this.builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_leeha)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }

    public void show() {
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            NotificationCompat.Builder builder = this.builder;
            notificationManager.notify(notificationId, builder.build());
        } else {
            // Handle the case where notifications are not enabled
            System.out.println("Notifications are not enabled for this app.");
        }
    }
}
