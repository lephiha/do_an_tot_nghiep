package com.lephiha.do_an.DoctorPage;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lephiha.do_an.Container.DoctorReadByID;
import com.lephiha.do_an.Repository.DoctorRepository;

import java.util.Map;

public class DoctorPageViewModel extends ViewModel {

    private MutableLiveData<DoctorReadByID> response;
    private MutableLiveData<Boolean> animation;
    private DoctorRepository repository;

    public MutableLiveData<DoctorReadByID> getResponse() {
        if( response == null)
        {
            response = new MutableLiveData<>();
        }
        return response;
    }

    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }

    //creat respository

    public void instantiate()
    {
        if(repository == null)
        {
            repository = new DoctorRepository();
        }
    }

    //read by id

    public void readById(Map<String, String> headers, String id)
    {
        response = repository.readById(headers, id);
        animation = repository.getAnimation();
    }
}
