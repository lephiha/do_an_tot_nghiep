package com.lephiha.do_an.CallVideo.stringee.activity;

import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


import com.lephiha.do_an.CallVideo.stringee.common.CallStatus;
import com.lephiha.do_an.CallVideo.stringee.common.Constant2;
import com.lephiha.do_an.CallVideo.stringee.common.NotificationUtils;
import com.lephiha.do_an.CallVideo.stringee.common.SensorManagerUtils;
import com.lephiha.do_an.CallVideo.stringee.listener.OnCallListener;
import com.lephiha.do_an.CallVideo.stringee.manager.CallManager;
import com.lephiha.do_an.CallVideo.stringee.service.MyMediaProjectionService;
import com.lephiha.do_an.R;
import com.lephiha.do_an.databinding.ActivityVideoCallBinding;
import com.lephiha.do_an.databinding.ActivityVoiceCallBinding;
import com.lephiha.do_an.databinding.LayoutIncomingCallBinding;
import com.stringee.video.StringeeVideoTrack;


import com.lephiha.do_an.CallVideo.stringee.listener.OnCallListener;
import com.lephiha.do_an.CallVideo.stringee.manager.CallManager;
import com.lephiha.do_an.CallVideo.stringee.service.MyMediaProjectionService;


import org.webrtc.RendererCommon;

import java.util.ArrayList;
import java.util.List;

public class CallActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityVideoCallBinding videoCallBinding;
    private ActivityVoiceCallBinding voiceCallBinding;
    private LayoutIncomingCallBinding incomingCallBinding;
    private CallManager callManager;
    private SensorManagerUtils sensorManagerUtils;
    private final List<StringeeVideoTrack> remoteShareTrackList = new ArrayList<>();
    private StringeeVideoTrack localShareTrack;
    private StringeeVideoTrack remoteShareTrack;
    private boolean isVideoCall;
    private boolean isIncomingCall;
    private boolean isStringeeCall;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }

        videoCallBinding = ActivityVideoCallBinding.inflate(getLayoutInflater());
        voiceCallBinding = ActivityVoiceCallBinding.inflate(getLayoutInflater());

        isVideoCall = getIntent().getBooleanExtra(Constant2.PARAM_IS_VIDEO_CALL, false);
        setContentView(isVideoCall ? videoCallBinding.getRoot() : voiceCallBinding.getRoot());
        incomingCallBinding = isVideoCall ? videoCallBinding.vIncomingCall : voiceCallBinding.vIncomingCall;

        NotificationUtils.getInstance(this).cancelNotification(Constant2.INCOMING_CALL_ID);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        });

        isIncomingCall = getIntent().getBooleanExtra(Constant2.PARAM_IS_INCOMING_CALL, false);
        isStringeeCall = getIntent().getBooleanExtra(Constant2.PARAM_IS_STRINGEE_CALL, false);

        callManager = CallManager.getInstance(this);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                Intent intent = new Intent(this, MyMediaProjectionService.class);
                intent.setAction(Constant2.ACTION_START_FOREGROUND_SERVICE);
                intent.putExtras(result.getData());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }
            }
        });
        sensorManagerUtils = SensorManagerUtils.getInstance(this).initialize(getLocalClassName());

        if (!isVideoCall) {
            sensorManagerUtils.turnOn();
        }

        incomingCallBinding.btnAnswer.setOnClickListener(this);
        incomingCallBinding.btnReject.setOnClickListener(this);
        if (!isVideoCall) {
            voiceCallBinding.btnEnd.setOnClickListener(this);
            voiceCallBinding.btnMute.setOnClickListener(this);
            voiceCallBinding.btnSpeaker.setOnClickListener(this);
        } else {
            videoCallBinding.btnEnd.setOnClickListener(this);
            videoCallBinding.btnMute.setOnClickListener(this);
            videoCallBinding.btnCamera.setOnClickListener(this);
            videoCallBinding.btnSwitch.setOnClickListener(this);
            videoCallBinding.btnShare.setOnClickListener(this);
        }

        incomingCallBinding.getRoot().setVisibility(callManager.getCallStatus() != CallStatus.INCOMING ? View.GONE : View.VISIBLE);
        if (isVideoCall) {
            videoCallBinding.vInCall.setVisibility(callManager.getCallStatus() != CallStatus.INCOMING ? View.VISIBLE : View.GONE);
        } else {
            voiceCallBinding.vInCall.setVisibility(callManager.getCallStatus() != CallStatus.INCOMING ? View.VISIBLE : View.GONE);
        }

        initCall();
        if (isVideoCall) {
            videoCallBinding.vShareBtn.setVisibility(isStringeeCall ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        runOnUiThread(() -> {
            if (callManager.getCallStatus() == CallStatus.STARTED || callManager.getCallStatus() == CallStatus.CALLING || callManager.getCallStatus() == CallStatus.RINGING) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

                sensorManagerUtils = SensorManagerUtils.getInstance(this).initialize(getLocalClassName());
                if (!isVideoCall) {
                    sensorManagerUtils.turnOff();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    setShowWhenLocked(false);
                    setTurnScreenOn(false);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        runOnUiThread(() -> {
            if (callManager.getCallStatus() == CallStatus.STARTED || callManager.getCallStatus() == CallStatus.CALLING || callManager.getCallStatus() == CallStatus.RINGING) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

                sensorManagerUtils = SensorManagerUtils.getInstance(this).initialize(getLocalClassName());
                if (!isVideoCall) {
                    sensorManagerUtils.turnOn();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    setShowWhenLocked(true);
                    setTurnScreenOn(true);
                }
            }
        });
    }

    private void initCall() {
        callManager.registerEvent(new OnCallListener() {
            @Override
            public void onCallStatus(CallStatus status) {
                runOnUiThread(() -> {
                    if (!isVideoCall) {
                        voiceCallBinding.tvStatus.setText(status.getValue());
                    }
                    incomingCallBinding.getRoot().setVisibility(status != CallStatus.INCOMING ? View.GONE : View.VISIBLE);
                    if (isVideoCall) {
                        videoCallBinding.vInCall.setVisibility(status != CallStatus.INCOMING ? View.VISIBLE : View.GONE);
                    } else {
                        voiceCallBinding.vInCall.setVisibility(status != CallStatus.INCOMING ? View.VISIBLE : View.GONE);
                    }
                    if (status == CallStatus.ENDED || status == CallStatus.BUSY) {
                        dismiss();
                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> dismiss());
            }

            @Override
            public void onReceiveLocalStream() {
                runOnUiThread(() -> {
                    if (isVideoCall) {
                        FrameLayout.LayoutParams childParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                        childParams.gravity = Gravity.CENTER;

                        videoCallBinding.vLocal.removeAllViews();
                        videoCallBinding.vLocal.addView(callManager.getLocalView(), childParams);
                        callManager.renderLocalView();
                    }
                });
            }

            @Override
            public void onReceiveRemoteStream() {
                runOnUiThread(() -> {
                    if (isVideoCall) {
                        FrameLayout.LayoutParams childParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                        childParams.gravity = Gravity.CENTER;

                        videoCallBinding.vRemote.removeAllViews();
                        videoCallBinding.vRemote.addView(callManager.getRemoteView(), childParams);
                        callManager.renderRemoteView();
                    }
                });
            }

            @Override
            public void onSpeakerChange(boolean isOn) {
                runOnUiThread(() -> {
                    voiceCallBinding.btnSpeaker.setBackgroundResource(isOn ? R.drawable.btn_ic_selector : R.drawable.btn_ic_selected_selector);
                    voiceCallBinding.btnSpeaker.setImageResource(isOn ? R.drawable.ic_speaker_on : R.drawable.ic_speaker_off);
                });
            }

            @Override
            public void onMicChange(boolean isOn) {
                runOnUiThread(() -> {
                    if (isVideoCall) {
                        videoCallBinding.btnMute.setBackgroundResource(!isOn ? R.drawable.btn_ic_selector : R.drawable.btn_ic_selected_selector);
                        videoCallBinding.btnMute.setImageResource(!isOn ? R.drawable.ic_mic_off : R.drawable.ic_mic_on);
                    } else {
                        voiceCallBinding.btnMute.setBackgroundResource(!isOn ? R.drawable.btn_ic_selector : R.drawable.btn_ic_selected_selector);
                        voiceCallBinding.btnMute.setImageResource(!isOn ? R.drawable.ic_mic_off : R.drawable.ic_mic_on);
                    }
                });
            }

            @Override
            public void onVideoChange(boolean isOn) {
                runOnUiThread(() -> {
                    videoCallBinding.btnCamera.setBackgroundResource(isOn ? R.drawable.btn_ic_selected_selector : R.drawable.btn_ic_selector);
                    videoCallBinding.btnCamera.setImageResource(isOn ? R.drawable.ic_cam_on : R.drawable.ic_cam_off);
                });
            }

            @Override
            public void onSharing(boolean isSharing) {
                runOnUiThread(() -> {
                    videoCallBinding.btnShare.setBackgroundResource(isSharing ? R.drawable.btn_ic_selector : R.drawable.btn_ic_selected_selector);
                    videoCallBinding.btnShare.setImageResource(isSharing ? R.drawable.ic_share_off : R.drawable.ic_share);
                });
            }

            @Override
            public void onTimer(String duration) {
                runOnUiThread(() -> {
                    if (!isVideoCall) {
                        voiceCallBinding.tvTime.setText(duration);
                    } else {
                        videoCallBinding.tvTime.setText(duration);
                    }
                });
            }

            @Override
            public void onVideoTrackAdded(StringeeVideoTrack stringeeVideoTrack) {
                runOnUiThread(() -> {
                    if (!isStringeeCall && isVideoCall) {
                        if (stringeeVideoTrack.isLocal()) {
                            videoCallBinding.vShare.setVisibility(View.VISIBLE);
                            videoCallBinding.vLocalShare.setVisibility(View.VISIBLE);
                            localShareTrack = stringeeVideoTrack;
                            FrameLayout.LayoutParams childParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                            childParams.gravity = Gravity.CENTER;

                            videoCallBinding.vLocalShare.removeAllViews();
                            videoCallBinding.vLocalShare.addView(localShareTrack.getView2(CallActivity.this), childParams);
                            localShareTrack.renderView2(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                        } else {
                            videoCallBinding.vShare.setVisibility(View.VISIBLE);
                            videoCallBinding.vRemoteShare.setVisibility(View.VISIBLE);
                            if (remoteShareTrack == null) {
                                remoteShareTrack = stringeeVideoTrack;
                                FrameLayout.LayoutParams childParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                                childParams.gravity = Gravity.CENTER;

                                videoCallBinding.vRemoteShare.removeAllViews();
                                videoCallBinding.vRemoteShare.addView(remoteShareTrack.getView2(CallActivity.this), childParams);
                                remoteShareTrack.renderView2(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                            }
                            remoteShareTrackList.add(stringeeVideoTrack);
                        }
                    }
                });
            }

            @Override
            public void onVideoTrackRemoved(StringeeVideoTrack stringeeVideoTrack) {
                if (!isStringeeCall && isVideoCall) {
                    if (stringeeVideoTrack.isLocal()) {
                        videoCallBinding.vLocalShare.setVisibility(View.GONE);
                        videoCallBinding.vLocalShare.removeAllViews();
                        localShareTrack = null;
                    } else {
                        for (int i = 0; i < remoteShareTrackList.size(); i++) {
                            StringeeVideoTrack videoTrack = remoteShareTrackList.get(i);
                            if (videoTrack.getId().equals(stringeeVideoTrack.getId()) || videoTrack.getLocalId().equals(stringeeVideoTrack.getLocalId())) {
                                remoteShareTrackList.remove(i);
                                break;
                            }
                        }
                        if (remoteShareTrack != null) {
                            videoCallBinding.vRemoteShare.removeAllViews();
                            if (remoteShareTrackList.isEmpty()) {
                                remoteShareTrack = null;
                            } else {
                                remoteShareTrack = remoteShareTrackList.get(0);
                                FrameLayout.LayoutParams childParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                                childParams.gravity = Gravity.CENTER;

                                videoCallBinding.vRemoteShare.removeAllViews();
                                videoCallBinding.vRemoteShare.addView(remoteShareTrack.getView2(CallActivity.this), childParams);
                                remoteShareTrack.renderView2(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                            }
                        }
                    }
                    videoCallBinding.vShare.setVisibility(localShareTrack != null || !remoteShareTrackList.isEmpty() ? View.VISIBLE : View.GONE);
                    videoCallBinding.vRemoteShare.setVisibility(!remoteShareTrackList.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }
        });
        if (!isIncomingCall) {
            String to = getIntent().getStringExtra(Constant2.PARAM_TO);
            if (!isVideoCall) {
                voiceCallBinding.tvUser1.setText(to);
            }
            callManager.initializedOutgoingCall(to, isVideoCall, isStringeeCall);
            callManager.makeCall();
        } else {
            incomingCallBinding.tvUser.setText(callManager.getFrom());
            if (!isVideoCall) {
                voiceCallBinding.tvUser1.setText(callManager.getFrom());
            }
            boolean isAnswerFromPush = getIntent().getBooleanExtra(Constant2.PARAM_ACTION_ANSWER_FROM_PUSH, false);
            if (isAnswerFromPush) {
                callManager.answer();
            }
        }
    }

    @Override
    public void onClick(View view) {
        int vId = view.getId();
        if (vId == R.id.btn_answer) {
            callManager.answer();
        } else if (vId == R.id.btn_reject) {
            callManager.endCall(false);
        } else if (vId == R.id.btn_end) {
            callManager.endCall(true);
        } else if (vId == R.id.btn_mute) {
            callManager.mute();
        } else if (vId == R.id.btn_speaker) {
            callManager.changeSpeaker();
        } else if (vId == R.id.btn_camera) {
            callManager.enableVideo();
        } else if (vId == R.id.btn_switch) {
            callManager.switchCamera();
        } else if (vId == R.id.btn_share) {
            if (callManager.isSharing()) {
                callManager.stopSharing();
            } else {
                callManager.prepareShareScreen(this, activityResultLauncher, getSystemService(MediaProjectionManager.class));
            }
        }
    }

    private void dismiss() {
        sensorManagerUtils.releaseSensor();
        callManager.release();

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        finish();
    }
}