package com.lephiha.do_an.chatbotAI;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.lephiha.do_an.R;

public class ChatHeadService extends Service {

    private WindowManager windowManager;
    private View chatHeadView;
    private WindowManager.LayoutParams params;
    private boolean isChatHeadVisible = false;
    private final String TAG = "ChatHeadService";

    private BroadcastReceiver chatActivityReceiver;
    public static final String ACTION_HIDE_CHAT_HEAD = "HIDE_CHAT_HEAD";
    public static final String ACTION_SHOW_CHAT_HEAD = "SHOW_CHAT_HEAD";
    public static final String ACTION_HOME_FRAGMENT_SHOWN = "HOME_FRAGMENT_SHOWN";
    public static final String ACTION_HOME_FRAGMENT_HIDDEN = "HOME_FRAGMENT_HIDDEN";

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "ChatHeadService onCreate");

        try {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            if (windowManager == null) {
                Log.e(TAG, "WindowManager is null");
                stopSelf();
                return;
            }

            chatHeadView = LayoutInflater.from(this).inflate(R.layout.layout_chat_head, null, false);
            Log.d(TAG, "Chat head view inflated");

            int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                    ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    type,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    PixelFormat.TRANSLUCENT);


            params.gravity = Gravity.TOP | Gravity.START;


            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            int screenHeight = displayMetrics.heightPixels;
            params.x = screenWidth/20;
            params.y = screenHeight/2;

            windowManager.addView(chatHeadView, params);
            Log.d(TAG, "Chat head added to WindowManager at initial position x=" + params.x + ", y=" + params.y);

            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_HIDE_CHAT_HEAD);
            filter.addAction(ACTION_SHOW_CHAT_HEAD);
            filter.addAction(ACTION_HOME_FRAGMENT_SHOWN);
            filter.addAction(ACTION_HOME_FRAGMENT_HIDDEN);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            chatActivityReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent != null && intent.getAction() != null) {
                        switch (intent.getAction()) {
                            case ACTION_HIDE_CHAT_HEAD:
                                Log.d(TAG, "Received broadcast: HIDE_CHAT_HEAD");
                                hideChatHead();
                                break;
                            case ACTION_SHOW_CHAT_HEAD:
                                Log.d(TAG, "Received broadcast: SHOW_CHAT_HEAD");
                                showChatHead();
                                break;
                            case ACTION_HOME_FRAGMENT_SHOWN:
                                Log.d(TAG, "Received broadcast: HOME_FRAGMENT_SHOWN");
                                showChatHead();
                                break;
                            case ACTION_HOME_FRAGMENT_HIDDEN:
                                Log.d(TAG, "Received broadcast: HOME_FRAGMENT_HIDDEN");
                                hideChatHead();
                                break;
                            case Intent.ACTION_SCREEN_OFF:
                                Log.d(TAG, "Screen off detected");
                                hideChatHead();
                                break;
                        }
                    }
                }
            };
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(chatActivityReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
            } else {
                registerReceiver(chatActivityReceiver, filter);
            }
            Log.d(TAG, "BroadcastReceiver registered");

            ImageView chatHeadImage = chatHeadView.findViewById(R.id.chat_head_image);
            ImageView closeButton = chatHeadView.findViewById(R.id.close_button);

            if (chatHeadImage == null) {
                Log.e(TAG, "chatHeadImage is null");
                stopSelf();
                return;
            }

            chatHeadImage.setOnTouchListener(new View.OnTouchListener() {
                private float initialX;
                private float initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = params.x;
                            initialY = params.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            closeButton.setVisibility(View.VISIBLE);
                            Log.d(TAG, "Chat head touched (ACTION_DOWN) at rawX=" + initialTouchX + ", rawY=" + initialTouchY);
                            return true;
                        case MotionEvent.ACTION_UP:
                            float deltaX = event.getRawX() - initialTouchX;
                            float deltaY = event.getRawY() - initialTouchY;
                            if (Math.abs(deltaX) < 10 && Math.abs(deltaY) < 10) {
                                Log.d(TAG, "Chat head clicked, starting ChatActivity");
                                Intent intent = new Intent(ChatHeadService.this, ChatActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            closeButton.setVisibility(View.GONE);
                            Log.d(TAG, "Chat head released (ACTION_UP)");
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            params.x = (int) (initialX + (event.getRawX() - initialTouchX));
                            params.y = (int) (initialY + (event.getRawY() - initialTouchY));
                            if (isChatHeadVisible) {
                                try {
                                    windowManager.updateViewLayout(chatHeadView, params);
                                    Log.d(TAG, "Chat head moved to x=" + params.x + ", y=" + params.y);
                                } catch (Exception e) {
                                    Log.e(TAG, "Failed to update chat head position: " + e.getMessage(), e);
                                }
                            }
                            return true;
                    }
                    return false;
                }
            });

            closeButton.setOnClickListener(v -> {
                Log.d(TAG, "Close button clicked, stopping service");
                stopSelf();
            });

            // Ban đầu ẩn bong bóng
            hideChatHead();
        } catch (Exception e) {
            Log.e(TAG, "Error in ChatHeadService onCreate: " + e.getMessage(), e);
            stopSelf();
        }
    }

    private void hideChatHead() {
        if (isChatHeadVisible) {
            chatHeadView.setVisibility(View.GONE);
            isChatHeadVisible = false;
            Log.d(TAG, "Chat head hidden");
        }
    }

    private void showChatHead() {
        if (!isChatHeadVisible) {
            chatHeadView.setVisibility(View.VISIBLE);
            isChatHeadVisible = true;
            try {
                windowManager.updateViewLayout(chatHeadView, params);
                Log.d(TAG, "Chat head shown at x=" + params.x + ", y=" + params.y);
            } catch (Exception e) {
                Log.e(TAG, "Failed to show chat head: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (chatHeadView != null) {
                windowManager.removeView(chatHeadView);
                Log.d(TAG, "Chat head removed from WindowManager");
            }
            if (chatActivityReceiver != null) {
                unregisterReceiver(chatActivityReceiver);
                Log.d(TAG, "BroadcastReceiver unregistered");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in ChatHeadService onDestroy: " + e.getMessage(), e);
        }
        Log.d(TAG, "ChatHeadService onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}