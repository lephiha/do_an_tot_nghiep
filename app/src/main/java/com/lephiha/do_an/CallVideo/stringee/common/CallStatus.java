package com.lephiha.do_an.CallVideo.stringee.common;

public enum CallStatus {
    INCOMING("Incoming"), CALLING("Calling"), RINGING("Ringing"), STARTING("Starting"), STARTED("Started"), BUSY("Busy"), ENDED("Ended");

    private final String value;

    CallStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}