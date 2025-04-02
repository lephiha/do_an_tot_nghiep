package com.lephiha.do_an.CallVideo.stringee.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lephiha.do_an.CallVideo.stringee.common.Constant2;
import com.lephiha.do_an.CallVideo.stringee.common.NotificationUtils;
import com.lephiha.do_an.CallVideo.stringee.manager.CallManager;


public class RejectCallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtils.getInstance(context).cancelNotification(Constant2.INCOMING_CALL_ID);
        CallManager.getInstance(context).endCall(false);
    }
}