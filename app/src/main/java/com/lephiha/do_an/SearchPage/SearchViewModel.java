package com.lephiha.do_an.SearchPage;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lephiha.do_an.Container.DoctorReadAll;
import com.lephiha.do_an.Container.ServiceReadAll;
import com.lephiha.do_an.Container.SpecialityReadAll;
import com.lephiha.do_an.Repository.DoctorRepository;
import com.lephiha.do_an.Repository.ServiceRepository;
import com.lephiha.do_an.Repository.SpecialRespository;

import java.util.Map;

public class SearchViewModel extends ViewModel {

    private MutableLiveData<SpecialityReadAll> specialityReadAll = new MutableLiveData<>();
    private MutableLiveData<DoctorReadAll> doctorReadAllResponse = new MutableLiveData<>();
    private MutableLiveData<ServiceReadAll> serviceReadAllResponse = new MutableLiveData<>();
    private MutableLiveData<Boolean> animation = new MutableLiveData<>();

    private SpecialRespository specialRespository;
    private DoctorRepository doctorRepository;
    private ServiceRepository serviceRepository;

    //getter
    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }

    public MutableLiveData<SpecialityReadAll> getSpecialityReadAll() {
        return specialityReadAll;
    }

    public MutableLiveData<ServiceReadAll> getServiceReadAllResponse() {
        return serviceReadAllResponse;
    }

    public MutableLiveData<DoctorReadAll> getDoctorReadAllResponse() {
        return doctorReadAllResponse;
    }

    //create repo

    public void instantiate() {
        if (specialRespository == null) {
            specialRespository = new SpecialRespository();
        }
        if(doctorRepository == null) {
            doctorRepository = new DoctorRepository();

        }
        if (serviceRepository == null) {
            serviceRepository = new ServiceRepository();
        }
    }

    /** Doctor read all **/

    public void doctorReadAll(Map<String , String > headers, Map<String, String> parameters) {
        doctorReadAllResponse = doctorRepository.readAll(headers, parameters);
        animation = doctorRepository.getAnimation();
    }

    /** speciality read all */

    public void specialityReadAll(Map<String, String> headers, Map<String, String> parameters) {
        specialityReadAll = specialRespository.readAll(headers, parameters);
        animation = specialRespository.getAnimation();
    }

    /** Service read all */

    public void serviceReadAll (Map<String, String> headers, Map<String , String> parameters) {
        serviceReadAllResponse = serviceRepository.readAll(headers, parameters);
        animation= serviceRepository.getAnimation();
    }
}
