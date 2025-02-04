package com.lephiha.do_an.HomePage;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lephiha.do_an.Container.DoctorReadAll;
import com.lephiha.do_an.Container.SpecialityReadAll;
import com.lephiha.do_an.Container.SpecialityReadByID;
import com.lephiha.do_an.Repository.DoctorRepository;
import com.lephiha.do_an.Repository.SpecialRespository;

import java.util.Map;

public class HomePageViewModel extends ViewModel {

    private MutableLiveData<Boolean> animation;
    private MutableLiveData<SpecialityReadAll> specialityReadAllResponse;
    private MutableLiveData<DoctorReadAll> doctorReadAllResponse;

    private SpecialRespository specialRespository;
    private DoctorRepository doctorRepository;

    public MutableLiveData<Boolean> getAnimation() {
        if (animation != null) {
            animation = new MutableLiveData<>();
        }
        return animation;
    }

    /** create spe repository **/

    public void instantiate() {
        if (specialRespository == null) {
            specialRespository = new SpecialRespository();
        }
        if (doctorRepository == null) {
            doctorRepository = new DoctorRepository();
        }
    }

    /** Speciality **/

    public MutableLiveData<SpecialityReadAll> getSpecialityReadAllResponse() {
        if (specialityReadAllResponse == null) {
            specialityReadAllResponse = new MutableLiveData<>();
        }
        return specialityReadAllResponse;
    }

    public void specialityReadAll (Map<String , String> headers, Map<String, String > parameters) {
        specialityReadAllResponse = specialRespository.readAll(headers, parameters);
        animation = specialRespository.getAnimation();
    }

    /** Doctor **/
    public MutableLiveData<DoctorReadAll> getDoctorReadAllResponse() {
        if (doctorReadAllResponse == null) {
            doctorReadAllResponse = new MutableLiveData<>();
        }
        return doctorReadAllResponse;
    }

    public void doctorReadAll(Map<String , String> header, Map<String , String > parameters) {
        doctorReadAllResponse = doctorRepository.readAll(header, parameters);
        animation = doctorRepository.getAnimation();
    }


}
