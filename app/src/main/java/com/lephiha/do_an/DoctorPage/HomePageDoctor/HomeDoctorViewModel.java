package com.lephiha.do_an.DoctorPage.HomePageDoctor;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lephiha.do_an.Container.DoctorReadAll;
import com.lephiha.do_an.Container.SpecialityReadAll;
import com.lephiha.do_an.Repository.DoctorRepository;
import com.lephiha.do_an.Repository.SpecialRespository;

import java.util.Map;

public class HomeDoctorViewModel extends ViewModel {

    private MutableLiveData<Boolean> animation;
    private MutableLiveData<SpecialityReadAll> specialityReadAllResponse;
    private MutableLiveData<DoctorReadAll> doctorReadAllResponse;

    private SpecialRespository specialityRepository;
    private DoctorRepository doctorRepository;

    public MutableLiveData<Boolean> getAnimation() {
        if( animation == null )
        {
            animation = new MutableLiveData<>();
        }
        return animation;
    }

    public void instantiate()
    {
        if(specialityRepository == null)
        {
            specialityRepository = new SpecialRespository();
        }
        if(doctorRepository == null)
        {
            doctorRepository = new DoctorRepository();
        }
    }

    public MutableLiveData<SpecialityReadAll> getSpecialityReadAllResponse() {
        if( specialityReadAllResponse == null )
        {
            specialityReadAllResponse = new MutableLiveData<>();
        }
        return specialityReadAllResponse;
    }

    public void specialityReadAll(Map<String, String> headers, Map<String, String> parameters)
    {
        specialityReadAllResponse = specialityRepository.readAll(headers, parameters);
        animation = specialityRepository.getAnimation();
    }

    public MutableLiveData<DoctorReadAll> getDoctorReadAllResponse() {
        if( doctorReadAllResponse == null )
        {
            doctorReadAllResponse = new MutableLiveData<>();
        }
        return doctorReadAllResponse;
    }

    public void doctorReadAll(Map<String, String> headers, Map<String, String> parameters)
    {
        doctorReadAllResponse = doctorRepository.readAll(headers, parameters);
        animation = doctorRepository.getAnimation();
    }
}
