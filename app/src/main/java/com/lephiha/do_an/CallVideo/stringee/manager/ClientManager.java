package com.lephiha.do_an.CallVideo.stringee.manager;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.lephiha.do_an.CallVideo.stringee.common.Constant2;
import com.lephiha.do_an.CallVideo.stringee.common.NotificationUtils;
import com.lephiha.do_an.CallVideo.stringee.common.Utils;
import com.lephiha.do_an.CallVideo.stringee.listener.OnConnectionListener;
import com.stringee.StringeeClient;
import com.stringee.call.StringeeCall;
import com.stringee.call.StringeeCall2;
import com.stringee.exception.StringeeError;
import com.stringee.listener.StatusListener;
import com.stringee.listener.StringeeConnectionListener;

import org.json.JSONObject;

public class ClientManager {
    private static volatile ClientManager instance;
    private final Context context;
    public ClientManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static ClientManager getInstance(Context context) {
        if (instance == null) {
            synchronized (ClientManager.class) {
                if (instance == null) {
                    instance = new ClientManager(context);
                }
            }
        }
        return instance;
    }

    private StringeeClient stringeeClient;
    private OnConnectionListener listener;

//    private String TOKEN =
//            instance.context.getSharedPreferences("UserData", MODE_PRIVATE).getString("token", "");
//           instance.token;
//            "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLmkzS29oVldyM1E0NWU4TnZlcE1XdXpRQWhlZGF5YS0xNzI4NzI1Mjk3IiwiaXNzIjoiU0suMC5pM0tvaFZXcjNRNDVlOE52ZXBNV3V6UUFoZWRheWEiLCJleHAiOjE3MzEzMTcyOTcsInVzZXJJZCI6InVzZXI0In0.O_QHpvt1AVR06jXRC8TTZQGmsxR0Qk08OPrZ4g_p4IE";
//            "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLmkzS29oVldyM1E0NWU4TnZlcE1XdXpRQWhlZGF5YS0xNzI4NzIzMTQ1IiwiaXNzIjoiU0suMC5pM0tvaFZXcjNRNDVlOE52ZXBNV3V6UUFoZWRheWEiLCJleHAiOjE3MzEzMTUxNDUsInVzZXJJZCI6InVzZXIzIn0.CaTc0Ltp4lfiBzzzrrCpe17GO1a6SgMYFguLHWOXlZ8";
//            "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLmkzS29oVldyM1E0NWU4TnZlcE1XdXpRQWhlZGF5YS0xNzI4NTY1ODYzIiwiaXNzIjoiU0suMC5pM0tvaFZXcjNRNDVlOE52ZXBNV3V6UUFoZWRheWEiLCJleHAiOjE3Mjg1Njk0NjMsInVzZXJJZCI6InVzZXIxIn0.0HXrFo9hN8IwMdt1NAne_BtH0KIFhJGVQflRPhDPlW8";
//            "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLmkzS29oVldyM1E0NWU4TnZlcE1XdXpRQWhlZGF5YS0xNzI4NTY1OTI4IiwiaXNzIjoiU0suMC5pM0tvaFZXcjNRNDVlOE52ZXBNV3V6UUFoZWRheWEiLCJleHAiOjE3Mjg1Njk1MjgsInVzZXJJZCI6InVzZXIyIn0.zvyHNg_6KO4q9LLygOhZxzi-GLNFiGipuhpplnNQtGw";
//            "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLmkzS29oVldyM1E0NWU4TnZlcE1XdXpRQWhlZGF5YS0xNzI4NTY1ODYzIiwiaXNzIjoiU0suMC5pM0tvaFZXcjNRNDVlOE52ZXBNV3V6UUFoZWRheWEiLCJleHAiOjE3Mjg1Njk0NjMsInVzZXJJZCI6InVzZXIxIn0.0HXrFo9hN8IwMdt1NAne_BtH0KIFhJGVQflRPhDPlW8";
    public boolean isInCall = false;
    public boolean isPermissionGranted = true;

    public StringeeClient getStringeeClient() {
        return stringeeClient;
    }

    public void addOnConnectionListener(OnConnectionListener listener) {
        this.listener = listener;
    }

    public void connect() {
        if (stringeeClient == null) {
            stringeeClient = new StringeeClient(context);
//            Set host
//            List<SocketAddress> socketAddressList = new ArrayList<>();
//            socketAddressList.add(new SocketAddress("YOUR_IP", YOUR_PORT));
//            stringeeClient.setHost(socketAddressList);

            stringeeClient.setConnectionListener(new StringeeConnectionListener() {
                @Override
                public void onConnectionConnected(final StringeeClient stringeeClient, boolean isReconnecting) {
                    Utils.runOnUiThread(() -> {
                        Log.d(Constant2.TAG, "onConnectionConnected");
                        if (listener != null) {
                            listener.onConnect("Connected as: " + stringeeClient.getUserId());
                        }
                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Log.d(Constant2.TAG, "getInstanceId failed", task.getException());
                                return;
                            }

                            // Get new token
                            String refreshedToken = task.getResult();
                            stringeeClient.registerPushToken(refreshedToken, new StatusListener() {
                                @Override
                                public void onSuccess() {
                                    Log.d(Constant2.TAG, "registerPushToken success");
                                }

                                @Override
                                public void onError(StringeeError error) {
                                    Log.d(Constant2.TAG, "registerPushToken error: " + error.getMessage());
                                }
                            });
                        });
                    });
                }

                @Override
                public void onConnectionDisconnected(StringeeClient stringeeClient, boolean isReconnecting) {
                    Utils.runOnUiThread(() -> {
                        Log.d(Constant2.TAG, "onConnectionDisconnected");
                        if (listener != null) {
                            listener.onConnect("Disconnected");
                        }
                    });
                }

                @Override
                public void onIncomingCall(final StringeeCall stringeeCall) {
                    Utils.runOnUiThread(() -> {
                        Log.d(Constant2.TAG, "onIncomingCall: callId - " + stringeeCall.getCallId());
                        if (isInCall) {
                            stringeeCall.reject(new StatusListener() {
                                @Override
                                public void onSuccess() {

                                }
                            });
                        } else {
                            CallManager.getInstance(context).initializedIncomingCall(stringeeCall);
                            CallManager.getInstance(context).initAnswer();
                            NotificationUtils.getInstance(context).showIncomingCallNotification(stringeeCall.getFrom(), true, stringeeCall.isVideoCall());
                        }
                    });
                }

                @Override
                public void onIncomingCall2(StringeeCall2 stringeeCall2) {
                    Utils.runOnUiThread(() -> {
                        Log.d(Constant2.TAG, "onIncomingCall2: callId - " + stringeeCall2.getCallId());
                        if (isInCall) {
                            stringeeCall2.reject(new StatusListener() {
                                @Override
                                public void onSuccess() {

                                }
                            });
                        } else {
                            CallManager.getInstance(context).initializedIncomingCall(stringeeCall2);
                            CallManager.getInstance(context).initAnswer();
                            NotificationUtils.getInstance(context).showIncomingCallNotification(stringeeCall2.getFrom(), false, stringeeCall2.isVideoCall());
                        }
                    });
                }

                @Override
                public void onConnectionError(StringeeClient stringeeClient, final StringeeError stringeeError) {
                    Utils.runOnUiThread(() -> {
                        Log.d(Constant2.TAG, "onConnectionError: " + stringeeError.getMessage());
                        if (listener != null) {
                            listener.onConnect(stringeeError.getMessage());
                        }
                    });
                }

                @Override
                public void onRequestNewToken(StringeeClient stringeeClient) {
                    // Get new token here and connect to Stringe server
                    Utils.runOnUiThread(() -> {
                        Log.d(Constant2.TAG, "onRequestNewToken");
                        if (listener != null) {
                            listener.onConnect("Request new token");
                        }
                    });
                }

                @Override
                public void onCustomMessage(String from, JSONObject msg) {
                    Utils.runOnUiThread(() -> Log.d(Constant2.TAG, "onCustomMessage: from - " + from + " - msg - " + msg));
                }

                @Override
                public void onTopicMessage(String from, JSONObject msg) {

                }
            });
        }
        if (!stringeeClient.isConnected()) {
            stringeeClient.connect(instance.context.getSharedPreferences("doantotnghiep", MODE_PRIVATE).getString("call_token", ""));
        }
    }
}
