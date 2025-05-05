package com.lephiha.do_an.TreatmentPage;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lephiha.do_an.Container.AppointmentReadAll;
import com.lephiha.do_an.Container.TreatmentReadAll;
import com.lephiha.do_an.Container.TreatmentReadByID;
import com.lephiha.do_an.Repository.AppointmentRepository;
import com.lephiha.do_an.Repository.TreatmentRepository;

import java.util.Map;

public class    TreatmentViewModel extends ViewModel {

    private AppointmentRepository appointmentRepository;

    private TreatmentRepository treatmentRepository;

    public void instantiate() {
        if (treatmentRepository == null) {
            treatmentRepository = new TreatmentRepository();
        }
        if (appointmentRepository == null) {
            appointmentRepository = new AppointmentRepository();
        }
    }

    private MutableLiveData<Boolean> animation = new MutableLiveData<>();
    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }

    //appointment - read all

    private MutableLiveData<AppointmentReadAll> appointmentReadAllResponse = new MutableLiveData<>();
    public MutableLiveData<AppointmentReadAll> getAppointmentReadAllResponse() {
        return appointmentReadAllResponse;
    }

    public void  appointmentReadAll(Map<String, String> header, Map<String, String> parameters) {
        animation = appointmentRepository.getAnimation();
        appointmentReadAllResponse = appointmentRepository.readAll(header, parameters);
    }

    //treatment - read all of an appointment

    private MutableLiveData<TreatmentReadAll> treatmentReadAllResponse = new MutableLiveData<>();
    public MutableLiveData<TreatmentReadAll> getTreatmentReadAllResponse() {
        return treatmentReadAllResponse;
    }

    public void treatmentReadAll (Map<String, String> header, String appointmentId) {
        animation = treatmentRepository.getAnimation();
        treatmentReadAllResponse = treatmentRepository.readAll(header, appointmentId);
    }

    //treatment - read by id of a treatment from an appointment

    private MutableLiveData<TreatmentReadByID> treatmentReadByIDResponse = new MutableLiveData<>();

    public MutableLiveData<TreatmentReadByID> getTreatmentReadByIDResponse() {
        return treatmentReadByIDResponse;
    }

    public void treatmentReadByID(Map<String, String> header, String treatmentId) {
        animation = treatmentRepository.getAnimation();
        treatmentReadByIDResponse = treatmentRepository.readByID(header, treatmentId);
    }
}
