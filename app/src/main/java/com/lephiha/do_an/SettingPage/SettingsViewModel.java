package com.lephiha.do_an.SettingPage;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lephiha.do_an.Container.AppointmentReadAll;
import com.lephiha.do_an.Container.BookingReadAll;
import com.lephiha.do_an.Repository.AppointmentRepository;
import com.lephiha.do_an.Repository.BookingRepository;

import java.util.Map;

public class SettingsViewModel extends ViewModel {

    private MutableLiveData<Boolean> animation = new MutableLiveData<>();
    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }

    private AppointmentRepository appointmentRepository;
    private BookingRepository bookingRepository;

    public void instantiate() {
        if (appointmentRepository == null) {
            appointmentRepository = new AppointmentRepository();
        }
        if (bookingRepository == null) {
            bookingRepository = new BookingRepository();
        }
    }

    //appointment - read all
    private MutableLiveData<AppointmentReadAll> readAllResponse = new MutableLiveData<>();
    public MutableLiveData<AppointmentReadAll> getReadAllResponse() {
        return readAllResponse;
    }

    public void readAll(Map<String, String> header, Map<String, String> parameters) {
        animation = appointmentRepository.getAnimation();
        readAllResponse = appointmentRepository.readAll(header, parameters);
    }

    //booking - read all
    private MutableLiveData<BookingReadAll> bookingReadAll = new MutableLiveData<>();
    public MutableLiveData<BookingReadAll> getBookingReadAll() {
        return bookingReadAll;
    }
    public void bookingReadAll(Map<String, String> header, Map<String, String> parameters) {
        animation = bookingRepository.getAnimation();
        bookingReadAll = bookingRepository.readAll(header, parameters);
    }


}
