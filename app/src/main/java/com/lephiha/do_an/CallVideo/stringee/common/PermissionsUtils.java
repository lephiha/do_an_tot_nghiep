package com.lephiha.do_an.CallVideo.stringee.common;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import androidx.core.app.ActivityCompat;

public class PermissionsUtils {
    private static volatile PermissionsUtils instance;
    public static final int REQUEST_PERMISSION = 1;
    public String[] permissions = {permission.RECORD_AUDIO, permission.CAMERA};

    public PermissionsUtils() {
        if (VERSION.SDK_INT >= VERSION_CODES.S) {
            permissions = new String[]{permission.RECORD_AUDIO, permission.CAMERA, permission.BLUETOOTH_CONNECT};
        }
        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            permissions = new String[]{permission.RECORD_AUDIO, permission.CAMERA, permission.BLUETOOTH_CONNECT, permission.POST_NOTIFICATIONS};
        }
    }

    public static PermissionsUtils getInstance() {
        if (instance == null) {
            synchronized (PermissionsUtils.class) {
                if (instance == null) {
                    instance = new PermissionsUtils();
                }
            }
        }
        return instance;
    }

    public boolean verifyPermissions(int[] grantResults) {
        if (grantResults.length < 1) {
            return false;
        }

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void requestPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSION);
    }

    public boolean shouldRequestPermissionRationale(Activity activity) {
        boolean showSetting = false;
        for (String perm : permissions) {
            if (activity.checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED && !activity.shouldShowRequestPermissionRationale(perm)) {
                showSetting = true;
                break;
            }
        }
        return showSetting;
    }

    public boolean checkSelfPermission(Context context) {
        for (String perm : permissions) {
            if (ActivityCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
