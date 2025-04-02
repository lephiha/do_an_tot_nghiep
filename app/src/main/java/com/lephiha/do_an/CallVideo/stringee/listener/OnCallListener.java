package com.lephiha.do_an.CallVideo.stringee.listener;

import com.lephiha.do_an.CallVideo.stringee.common.CallStatus;
import com.stringee.video.StringeeVideoTrack;

public interface OnCallListener {
    void onCallStatus(CallStatus status);

    void onError(String message);

    void onReceiveLocalStream();

    void onReceiveRemoteStream();

    void onSpeakerChange(boolean isOn);

    void onMicChange(boolean isOn);

    void onVideoChange(boolean isOn);
    void onSharing(boolean isSharing);

    void onTimer(String duration);

    void onVideoTrackAdded(StringeeVideoTrack stringeeVideoTrack);

    void onVideoTrackRemoved(StringeeVideoTrack stringeeVideoTrack);
}
