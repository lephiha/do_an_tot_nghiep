package com.lephiha.do_an.BookingPage;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lephiha.do_an.Container.BookingPhotoReadAll;
import com.lephiha.do_an.Container.BookingReadByID;
import com.lephiha.do_an.Container.DoctorReadByID;
import com.lephiha.do_an.Container.ServiceReadByID;
import com.lephiha.do_an.Repository.BookingPhotoRepository;
import com.lephiha.do_an.Repository.BookingRepository;
import com.lephiha.do_an.Repository.DoctorRepository;
import com.lephiha.do_an.Repository.ServiceRepository;

import java.util.Map;

public class BookingViewModel extends ViewModel {

    private MutableLiveData<ServiceReadByID> serviceReadByIdResponse;
    private MutableLiveData<BookingReadByID> bookingReadByIdResponse;

    private MutableLiveData<Boolean> animation;
    private ServiceRepository serviceRepository;
    private BookingRepository bookingRepository;
    private BookingPhotoRepository bookingPhotoRepository;
    private DoctorRepository doctorRepository;
    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }

    //instantiate repository

    public void instantiate() {
        if (serviceRepository == null) {
            serviceRepository = new ServiceRepository();
        }

        if (bookingRepository == null) {
            bookingRepository = new BookingRepository();
        }
        if (doctorRepository == null) {
            doctorRepository = new DoctorRepository();
        }
        if (bookingPhotoRepository == null) {
            bookingPhotoRepository = new BookingPhotoRepository();
        }
    }

    /** Service read by id */

    public MutableLiveData<ServiceReadByID> getServiceReadByIdResponse() {
        if (serviceReadByIdResponse == null) {
            serviceReadByIdResponse = new MutableLiveData<>();
        }
        return serviceReadByIdResponse;
    }

    public void serviceReadById(Map<String, String> header, String serviceId) {
        serviceReadByIdResponse = serviceRepository.readByID(header, serviceId);
        animation = serviceRepository.getAnimation();
    }

    /** Booking read by id **/
    public MutableLiveData<BookingReadByID> getBookingReadByIdResponse() {
        if (bookingReadByIdResponse == null) {
            bookingReadByIdResponse = new MutableLiveData<>();
        }
        return bookingReadByIdResponse;
    }

    public void bookingReadByID(Map<String, String > header, String bookingId) {
        bookingReadByIdResponse = bookingRepository.readByID(header, bookingId);
        animation = bookingRepository.getAnimation();
    }

    /** Booking photo read all **/

    private MutableLiveData<BookingPhotoReadAll> bookingPhotoReadAllResponse;
    public MutableLiveData<BookingPhotoReadAll> getBookingPhotoReadAllResponse() {
        if (bookingPhotoReadAllResponse == null) {
            bookingPhotoReadAllResponse = new MutableLiveData<>();
        }
        return bookingPhotoReadAllResponse;
    }

    public void bookingPhotoReadAll(Map<String , String > header, String bookingId) {
        bookingPhotoReadAllResponse = bookingPhotoRepository.readAll(header, bookingId);
        animation = bookingPhotoRepository.getAnimation();
    }

    /** Doctor read by id **/
    private MutableLiveData<DoctorReadByID> doctorReadById = new MutableLiveData<>();
    public MutableLiveData<DoctorReadByID> getDoctorReadByIdResponse() {
        return doctorReadById;
    }
    public void doctorReadByID(Map<String, String> header, String doctorId) {
        animation = doctorRepository.getAnimation();
        doctorReadById = doctorRepository.readById(header, doctorId);
    }
}
