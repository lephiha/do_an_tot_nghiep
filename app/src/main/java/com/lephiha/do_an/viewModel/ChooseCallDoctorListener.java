package com.lephiha.do_an.viewModel;

import com.lephiha.do_an.Model.CallDoctor;

public interface ChooseCallDoctorListener {
    void onItemCliked(CallDoctor callDoctor);
    void call(String callId);
}
