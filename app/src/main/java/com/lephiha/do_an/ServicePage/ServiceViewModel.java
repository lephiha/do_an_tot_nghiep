package com.lephiha.do_an.ServicePage;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lephiha.do_an.Container.DoctorReadAll;
import com.lephiha.do_an.Container.ServiceReadByID;
import com.lephiha.do_an.Repository.DoctorRepository;
import com.lephiha.do_an.Repository.ServiceRepository;

import java.util.Map;

public class ServiceViewModel extends ViewModel {

    private MutableLiveData<Boolean> animation;
    private MutableLiveData<ServiceReadByID> response;
    private ServiceRepository repository;
    private DoctorRepository doctorRepository;

    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }

    public MutableLiveData<ServiceReadByID> getResponse() {
        return response;
    }

    //create repository

    public void instantiate() {
        if (repository == null) {
            repository = new ServiceRepository();
        }
        if (doctorRepository == null) {
            doctorRepository = new DoctorRepository();
        }
    }

    //read by id

    public void readById(Map<String, String> header, String serviceId) {
        response = repository.readByID(header, serviceId);
        animation = repository.getAnimation();
    }

    //doctor read all
    private MutableLiveData<DoctorReadAll> doctorReadAllResponse = new MutableLiveData<>();

    public MutableLiveData<DoctorReadAll> getDoctorReadAllResponse() {
        return doctorReadAllResponse;
    }

    public void doctorReadAll(Map<String, String> headers, Map<String, String> parameters) {
        doctorReadAllResponse = doctorRepository.readAll(headers, parameters);
        animation = doctorRepository.getAnimation();
    }
}
