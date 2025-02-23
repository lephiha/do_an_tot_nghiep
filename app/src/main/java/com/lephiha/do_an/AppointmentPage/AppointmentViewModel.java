package com.lephiha.do_an.AppointmentPage;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lephiha.do_an.Container.AppointmentQueue;
import com.lephiha.do_an.Container.AppointmentReadAll;
import com.lephiha.do_an.Container.AppointmentReadByID;
import com.lephiha.do_an.Repository.AppointmentQueueRepository;
import com.lephiha.do_an.Repository.AppointmentRepository;

import java.util.Map;

public class AppointmentViewModel extends ViewModel {

    private MutableLiveData<Boolean> animation = new MutableLiveData<>();
    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }

    private AppointmentRepository repository;

    private AppointmentQueueRepository queueRepository;
    public void instantiate() {
        if (repository == null) {
            repository = new AppointmentRepository();

        }
        if (queueRepository == null) {
            queueRepository = new AppointmentQueueRepository();
        }
    }

    //appointment read all

    private MutableLiveData<AppointmentReadAll> readAllResponse = new MutableLiveData<>();
    public MutableLiveData<AppointmentReadAll> getReadAllResponse() {
        return readAllResponse;
    }

    public void readAll(Map<String, String> header, Map<String, String> parameters) {
        animation = repository.getAnimation();
        readAllResponse = repository.readAll(header, parameters);
    }

    // appointment read by ID

    private MutableLiveData<AppointmentReadByID> readByIDResponse = new MutableLiveData<>();

    public MutableLiveData<AppointmentReadByID> getReadByIDResponse() {
        return readByIDResponse;
    }
    public void readByID(Map<String, String> header, String appointmentID) {
        //animation = repository.getAnimation();
        readByIDResponse = repository.readByID(header, appointmentID);
    }

    //Queue - readby ID
    private MutableLiveData<AppointmentQueue> appointmentQueueResponse = new MutableLiveData<>();

    public MutableLiveData<AppointmentQueue> getAppointmentQueueResponse() {
        return appointmentQueueResponse;
    }
    public void getQueue(Map<String, String> header, Map<String, String> parameters) {
        animation = repository.getAnimation();
        appointmentQueueResponse = queueRepository.getAppointmentQueue(header, parameters);
    }
}
