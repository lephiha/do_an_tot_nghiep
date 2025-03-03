package com.lephiha.do_an.NotificationPage;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lephiha.do_an.Container.NotificationReadAll;
import com.lephiha.do_an.Repository.NotificationRepository;

import java.util.Map;

public class NotificationViewModel extends ViewModel {

    private NotificationRepository repository;
    private MutableLiveData<Boolean> animation = new MutableLiveData<>();

    private  MutableLiveData<NotificationReadAll> readAllResponse = new MutableLiveData<>();

    //animation
    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }

    public void instantiate()
    {
        if( repository == null)
        {
            repository = new NotificationRepository();
        }
    }

    //getter
    public MutableLiveData<NotificationReadAll> getReadAllResponse() {
        return readAllResponse;
    }
    public void readAll (Map<String, String> header)
    {
        animation = repository.getAnimation();
        readAllResponse = repository.readAll(header);
    }

}
