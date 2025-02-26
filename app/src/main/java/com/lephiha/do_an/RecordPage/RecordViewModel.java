package com.lephiha.do_an.RecordPage;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lephiha.do_an.Container.RecordReadByID;
import com.lephiha.do_an.Repository.RecordRepository;

import java.util.Map;

public class RecordViewModel extends ViewModel {

    private RecordRepository repository;

    private void instantiate() {
        if (repository == null) {
            repository  = new RecordRepository();
        }
    }

    //animation
    private MutableLiveData<Boolean> animation = new MutableLiveData<>();
    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }

    //read by id
    private MutableLiveData<RecordReadByID> readByIDResponse = new MutableLiveData<>();
    public MutableLiveData<RecordReadByID> getReadByIDResponse() {
        return readByIDResponse;
    }

    public void readByID(Map<String, String> header, String appointmentId) {
        animation = repository.getAnimation();
        readByIDResponse = repository.readByID(header, appointmentId);
    }
}
