package com.lephiha.do_an.SpecialityPage;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lephiha.do_an.Container.DoctorReadAll;
import com.lephiha.do_an.Container.SpecialityReadByID;
import com.lephiha.do_an.Repository.DoctorRepository;
import com.lephiha.do_an.Repository.SpecialRespository;

import java.util.Map;

public class SpecialityViewModel extends ViewModel {

    private MutableLiveData<SpecialityReadByID> specialityReadByIdResponse;
    private MutableLiveData<Boolean> animation;
    private MutableLiveData<DoctorReadAll> doctorReadAllResponse;

    private SpecialRespository specialRespository;
    private DoctorRepository doctorRepository;

    public MutableLiveData<SpecialityReadByID> getSpecialityReadByIdResponse() {
        if (specialityReadByIdResponse == null) {
            specialityReadByIdResponse = new MutableLiveData<>();
        }
        return specialityReadByIdResponse;
    }

    public MutableLiveData<DoctorReadAll> getDoctorReadAllResponse() {
        if (doctorReadAllResponse == null) {
            doctorReadAllResponse = new MutableLiveData<>();
        }
        return doctorReadAllResponse;
    }

    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }

    //create repository
    public void instantiate() {
        if (specialRespository == null) {
            specialRespository = new SpecialRespository();
        }
        if (doctorRepository == null) {
            doctorRepository = new DoctorRepository();
        }
    }

    //read by id

    public void specialityReadById(Map<String, String> headers, String id) {
        specialityReadByIdResponse = specialRespository.readByID(headers, id);
        animation = specialRespository.getAnimation();
    }

    public void doctorReadAll(Map<String, String> headers, Map<String, String> parameters) {
        doctorReadAllResponse = doctorRepository.readAll(headers, parameters);
        animation = doctorRepository.getAnimation();
    }



}
