package com.lephiha.do_an.BookingPage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import com.lephiha.do_an.Container.BookingCancel;
import com.lephiha.do_an.Container.BookingCreate;
import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.LoadingScreen;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.HomePage.HomePageActivity;
import com.lephiha.do_an.Model.Booking;
import com.lephiha.do_an.R;
import com.lephiha.do_an.configAPI.Constant;
import com.lephiha.do_an.configAPI.HTTPRequest;
import com.lephiha.do_an.configAPI.HTTPService;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BookingPageInfoActivity extends AppCompatActivity {

    private final String TAG = "Booking Info Activity";

    private String bookingId;
    private String bookingStatus;

    private TextView txtBookingName;
    private TextView txtBookingPhone;
    private TextView txtPatientName;
    private TextView txtPatientGender;

    private TextView txtPatientBirthday;
    private TextView txtPatientAddress;
    private TextView txtPatientReason;
    private TextView txtDatetime;

    private TextView txtBookingStatus;
    private ImageView imgServiceAvartar;

    private TextView txtServiceName;

    private AppCompatButton btnPhoto;
    private AppCompatButton btnCancel;

    private ImageButton btnBack;

    private Map<String, String> header;

    private Dialog dialog;
    private LoadingScreen loadingScreen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_in4);

        setupComponent();
        setupViewModel();
        setupEvent();
    }

    private void setupComponent() {
        GlobaleVariable globaleVariable = (GlobaleVariable) this.getApplication();
        header = globaleVariable.getHeaders();
        bookingId = getIntent().getStringExtra("id");

        dialog = new Dialog(this);
        loadingScreen = new LoadingScreen(this);

        txtBookingName = findViewById(R.id.txtBookingName);
        txtBookingPhone = findViewById(R.id.txtBookingPhone);
        txtPatientName = findViewById(R.id.txtPatientName);
        txtPatientAddress = findViewById(R.id.txtPatientAddress);
        txtPatientBirthday = findViewById(R.id.txtPatientBirthday);
        txtDatetime = findViewById(R.id.txtDatetime);
        txtPatientGender = findViewById(R.id.txtPatientGender);
        txtPatientReason = findViewById(R.id.txtPatientReason);
        txtBookingStatus = findViewById(R.id.txtBookingStatus);

        btnPhoto = findViewById(R.id.btnPhoto);
        btnCancel = findViewById(R.id.btnCancel);
        btnBack = findViewById(R.id.btnBack);

        imgServiceAvartar = findViewById(R.id.imgServiceAvatar);
        txtServiceName = findViewById(R.id.txtServiceName);
    }

    //set view model
    private void setupViewModel() {
        //declare
        BookingViewModel viewModel = new ViewModelProvider(this).get(BookingViewModel.class);
        viewModel.instantiate();

        //send request to server
        viewModel.bookingReadByID(header, bookingId);
        viewModel.getBookingReadByIdResponse().observe(this, response -> {
            try {
                int result = response.getResult();

                if (result == 1) {
                    Booking booking = response.getData();
                    printBookingin4(booking);
                }
                if (result == 0 ) {
                    dialog.announce();
                    dialog.show(R.string.attention, getString(R.string.check_your_internet_connection), R.drawable.ic_info);
                    dialog.btnOK.setOnClickListener(view->{
                        dialog.close();
                        finish();
                    });
                }
            }
            catch (Exception e) {
                dialog.announce();
                dialog.show(R.string.attention, getString(R.string.check_your_internet_connection), R.drawable.ic_info);
                dialog.btnOK.setOnClickListener(view->{
                    dialog.close();
                    finish();
                });
            }
        });

        //animation
        viewModel.getAnimation().observe(this, aBoolean -> {
            if (aBoolean) {
                loadingScreen.start();
            }
            else {
                loadingScreen.stop();
            }
        });
    }

    //print booking in4
    private void printBookingin4(Booking booking) throws ParseException{
        //1 get data
        String bookingName = booking.getBookingName();
        String bookingPhone = booking.getBookingPhone();

        String name = booking.getName();
        String address = booking.getAddress();

        String birthday = booking.getBirthday();
        String date = booking.getAppointmentDate();

        String time = booking.getAppointmentTime();
        String datetime = date+ " " + getString(R.string.at) + " " + time;

        String gender = booking.getGender() == 1 ? getString(R.string.male) : getString(R.string.female);
        String reason = booking.getReason();

        String serviceName = booking.getService().getName();
        String serviceImage = Constant.UPLOAD_URI() + booking.getService().getImage();

        String status = booking.getStatus();
        bookingStatus = status;

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        //2- tinh khac nhau giua ngay nay va ngay kham. neu khac <0. nghia la booking ngay hqua => k the cancel btn

        Date dateFormatted = formatter.parse(date);
        Date today = new Date();
        assert dateFormatted != null;

        long difference = Tooltip.getDataDifference(today, dateFormatted, TimeUnit.DAYS);
        if (!Objects.equals(status, "processing") || difference < 0) {
            btnBack.setVisibility(TextView.GONE);
        }

        String verified = this.getString(R.string.verified);
        String processing = this.getString(R.string.processing);
        String canceled = this.getString(R.string.cancel);
        int colorProcessing = this.getColor(R.color.colorGreen);
        int colorCancelled = this.getColor(R.color.colorRed);

        if (Objects.equals(status, "verified")) {
            txtBookingStatus.setText(verified);
            txtBookingStatus.setTextColor(colorProcessing);
        }
        else if (Objects.equals(status, "processing")) {
            txtBookingStatus.setText(processing);
            txtBookingStatus.setTextColor(colorProcessing);
        }
        else {
            txtBookingStatus.setText(canceled);
            txtBookingStatus.setTextColor(colorCancelled);
        }


        //3. print booking in4 to UI

        if (booking.getService().getImage().length() >0 ) {
            Picasso.get().load(serviceImage).into(imgServiceAvartar);
        }
        txtServiceName.setText(serviceName);

        txtBookingName.setText(bookingName);
        txtBookingPhone.setText(bookingPhone);

        txtPatientName.setText(name);
        txtPatientGender.setText(gender);

        txtPatientBirthday.setText(birthday);
        txtPatientAddress.setText(address);

        txtPatientReason.setText(reason);
        txtDatetime.setText(datetime);
    }

    //set event
    private void setupEvent() {
        btnBack.setOnClickListener(view -> finish());

        btnPhoto.setOnClickListener(view -> {
            Intent intent = new Intent(this, BookingpagePhotoActivity.class);
            intent.putExtra("bookingId", bookingId);
            intent.putExtra("bookingStatus", bookingStatus);
            startActivity(intent);
        });

        btnCancel.setOnClickListener(view -> {
            Dialog dialog = new Dialog(this);
            dialog.confirm();
            dialog.show(R.string.attention, getString(R.string.are_you_sure_about_that), R.drawable.ic_info);
            dialog.btnOK.setOnClickListener(view1->{
                loadingScreen.start();
                sendCancelRequest();
                dialog.close();
            });
            dialog.btnCancel.setOnClickListener(view1-> dialog.close());

        });
    }

    //cancel booking

    private void sendCancelRequest() {
        //2
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //3
        Call<BookingCancel> container = api.bookingCancel(header, bookingId);

        //4
        container.enqueue(new Callback<BookingCancel>() {
            @Override
            public void onResponse(@NonNull Call<BookingCancel> call,@NonNull Response<BookingCancel> response) {
                loadingScreen.stop();

                if (response.isSuccessful()) {
                    //show dialog message
                    BookingCancel content = response.body();
                    assert content != null;

                    System.out.println(content.getResult());
                    System.out.println(content.getMsg());

                    String title = getString(R.string.success);
                    String message = getString(R.string.successful_action);

                    dialog.announce();
                    dialog.show(title, message, R.drawable.ic_check );
                    dialog.btnOK.setOnClickListener(view->dialog.close());

                    //hide btn cancel
                    btnCancel.setVisibility(View.GONE);

                    //update number of unread notif
                    HomePageActivity.getInstance().setNumberOnNotificationIcon();
                }
                if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        System.out.println(TAG);
                        System.out.println( jObjError );
                    }
                    catch (Exception e) {
                        System.out.println(TAG);
                        System.out.println( e.getMessage() );
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookingCancel> call, @NonNull Throwable t) {
                loadingScreen.stop();
                System.out.println("Booking-page Info Activity - Cancel - error: " + t.getMessage());
            }
        });
    }
}
