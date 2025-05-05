package com.lephiha.do_an.DoctorPage.HomePageDoctor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lephiha.do_an.Container.Weather;
import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.Tooltip;
import com.lephiha.do_an.Model.Doctor;
import com.lephiha.do_an.Model.Handbook;
import com.lephiha.do_an.Model.Setting;
import com.lephiha.do_an.Model.Speciality;
import com.lephiha.do_an.R;
import com.lephiha.do_an.RecyclerView.ButtonRecyclerView;
import com.lephiha.do_an.RecyclerView.DoctorRecyclerView;
import com.lephiha.do_an.RecyclerView.HandbookRecyclerView;
import com.lephiha.do_an.RecyclerView.SpecialityRecyclerView;
import com.lephiha.do_an.SearchPage.SearchActivity;
import com.lephiha.do_an.configAPI.Constant;
import com.lephiha.do_an.configAPI.HTTPRequest;
import com.lephiha.do_an.configAPI.HTTPService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeDoctorFragment extends Fragment {

    private final String TAG = "HomeDoctorFragment";

    private GlobaleVariable globaleVariable;
    private RecyclerView recyclerViewSpeciality;
    private RecyclerView recyclerViewDoctor;
    private RecyclerView recyclerViewHandbook;
    private RecyclerView recyclerViewRecommendedPages;


    private EditText searchBar;
    private TextView txtReadMoreSpeciality;
    private TextView txtReadMoreDoctor;

    private Context context;
    private RecyclerView recyclerViewButton;

    private TextView txtDate;
    private TextView txtWeather;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        setupComponent(view);
        setupViewModel();

        setupEvent();
        getCurrentWeather();

        setupRecyclerViewButton();
        setupRecyclerViewHandbook();
        setupRecyclerViewRecommendedPages();



        return view;
    }

    private void setupComponent(View view)
    {
        context = requireContext();
        globaleVariable = (GlobaleVariable) requireActivity().getApplication();


        recyclerViewSpeciality = view.findViewById(R.id.recyclerViewSpeciality);
        recyclerViewDoctor = view.findViewById(R.id.recyclerViewDoctor);
        recyclerViewButton = view.findViewById(R.id.recyclerViewButton);
        recyclerViewHandbook = view.findViewById(R.id.recyclerViewHandbook);
        recyclerViewRecommendedPages = view.findViewById(R.id.recyclerViewRecommendedPages);

        searchBar = view.findViewById(R.id.searchBar);
        txtReadMoreSpeciality = view.findViewById(R.id.txtReadMoreSpeciality);
        txtReadMoreDoctor = view.findViewById(R.id.txtReadMoreDoctor);

        txtWeather = view.findViewById(R.id.txtWeather);
        txtDate = view.findViewById(R.id.txtDate);
    }

    private void setupViewModel() {
        /*Step 1 - declare*/
        HomeDoctorViewModel viewModel = new ViewModelProvider(this).get(HomeDoctorViewModel.class);
        viewModel.instantiate();

        /*Step 2 - prepare header & parameters*/
        Map<String, String> header = globaleVariable.getHeaders();
        header.put("type", "doctor"); // Sửa từ "patient" thành "doctor"

        /*Step 3 - listen speciality Read All */
        Map<String, String> paramsSpeciality = new HashMap<>();
        viewModel.specialityReadAll(header, paramsSpeciality);

        viewModel.getSpecialityReadAllResponse().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                int result = response.getResult();
                if (result == 1) {
                    List<Speciality> list = response.getData();
                    setupRecyclerViewSpeciality(list);
                }
            } else {
                // Handle the case when response is null (e.g. show an error message or retry)
                System.out.println("Response is null");
            }
        });


        /*Step 4 - listen doctor read all*/
        Map<String, String> paramsDoctor = new HashMap<>();
        viewModel.doctorReadAll(header, paramsDoctor);

        viewModel.getDoctorReadAllResponse().observe(getViewLifecycleOwner(), response -> {
            int result = response.getResult();
            if (result == 1) {
                List<Doctor> list = response.getData();
                setupRecyclerViewDoctor(list);
            }
        });
    }

    private void setupRecyclerViewSpeciality(List<Speciality> list)
    {
        SpecialityRecyclerView specialityAdapter = new SpecialityRecyclerView(requireActivity(), list, R.layout.recycler_view_element_speciality);
        recyclerViewSpeciality.setAdapter(specialityAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewSpeciality.setLayoutManager(manager);
    }

    private void setupRecyclerViewDoctor(List<Doctor> list)
    {
        DoctorRecyclerView doctorAdapter = new DoctorRecyclerView(requireActivity(), list);
        recyclerViewDoctor.setAdapter(doctorAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewDoctor.setLayoutManager(manager);
    }

    @SuppressLint({"UnspecifiedImmutableFlag", "ShortAlarm"})
    private void setupEvent()
    {
        /*SEARCH BAR*/
        searchBar.setOnClickListener(view->{
            Intent intent = new Intent(requireContext(), SearchActivity.class);
            startActivity(intent);
        });


        /*TXT READ MORE SPECIALITY*/
        txtReadMoreSpeciality.setOnClickListener(view->{
            Intent intent = new Intent(context, SearchActivity.class);
            String filterKey  = context.getString(R.string.speciality);

            intent.putExtra("filterKey", filterKey );
            startActivity(intent);
        });

        /*TXT READ MORE DOCTOR*/
        txtReadMoreDoctor.setOnClickListener(view->{
            Intent intent = new Intent(context, SearchActivity.class);
            String filterKey  = context.getString(R.string.doctor);

            intent.putExtra("filterKey", filterKey );
            startActivity(intent);
        });
    }

    private void setupRecyclerViewButton() {
        Setting setting1 = new Setting(R.drawable.ic_medical_home, "call_video", getString(R.string.call_video));
        Setting setting2 = new Setting(R.drawable.shortcut_state_appointment, "appointment", getString(R.string.appointment_doc));

        List<Setting> list = new ArrayList<>();
        list.add(setting1);
        list.add(setting2);

        ButtonRecyclerView buttonAdapter = new ButtonRecyclerView(requireActivity(), list);
        recyclerViewButton.setAdapter(buttonAdapter);

        GridLayoutManager manager = new GridLayoutManager(requireContext(), 3);
        recyclerViewButton.setLayoutManager(manager);
    }

    private void setupRecyclerViewHandbook() {
        Handbook handbook0 = new Handbook(
                "https://image.thanhnien.vn/w2048/Uploaded/2022/uqvpoqiw/2022_12_18/giay-ve-sinh-9535.jpg",
                "Chuyên gia nói gì về giờ đi vệ sinh tốt nhất trong ngày?",
                "https://thanhnien.vn/chuyen-gia-noi-gi-ve-gio-di-ve-sinh-tot-nhat-trong-ngay-post1533096.html");
        Handbook handbook1 = new Handbook(
                "https://image.thanhnien.vn/w2048/Uploaded/2022/wobjohb/2022_12_17/p1-9988.jpeg",
                "Đâu là dấu hiệu cảnh báo một người sắp qua đời?",
                "https://thanhnien.vn/dau-la-dau-hieu-canh-bao-mot-nguoi-sap-qua-doi-post1533048.html");
        Handbook handbook2 = new Handbook(
                "https://image.thanhnien.vn/w2048/Uploaded/2022/uqvpoqiw/2022_12_18/intermittent-fasting3-8817.jpg",
                "Đã tìm ra cách có thể 'chữa khỏi' bệnh tiểu đường?",
                "https://thanhnien.vn/da-tim-ra-cach-co-the-chua-khoi-benh-tieu-duong-post1532438.html");
        Handbook handbook3 = new Handbook(
                "https://image.thanhnien.vn/w2048/Uploaded/2022/aeymrexqam/2022_12_19/ph-1-3698.jpg",
                "Giải pháp cải thiện ho, khò khè, khó thở",
                "https://thanhnien.vn/giai-phap-cai-thien-ho-kho-khe-kho-tho-post1533364.html");
        Handbook handbook4 = new Handbook(
                "https://static-images.vnncdn.net/files/publish/2022/12/20/benh-vien-viet-duc-290.jpg",
                "Ba tiếng chờ đợi, 10 phút khám - Xếp hàng dài, căng tai nghe để không mất lượt",
                "https://vietnamnet.vn/ba-tieng-cho-doi-10-phut-kham-2092139.html");

        List<Handbook> list = new ArrayList<>();
        list.add(handbook0);
        list.add(handbook1);
        list.add(handbook2);
        list.add(handbook3);
        list.add(handbook4);

        HandbookRecyclerView handbookAdapter = new HandbookRecyclerView(requireActivity(), list, R.layout.recycler_view_handbook);
        recyclerViewHandbook.setAdapter(handbookAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewHandbook.setLayoutManager(manager);
    }

    private void setupRecyclerViewRecommendedPages() {
        Handbook handbook0 = new Handbook(
                "https://suckhoedoisong.qltns.mediacdn.vn/thumb_w/1200/324455921873985536/2021/10/10/baoskds-1633861575912643818497-49-0-295-393-crop-1633861583124643700229.png",
                "Báo sức khỏe đời sống - cơ quan ngôn luận của Bộ Y tế",
                "https://suckhoedoisong.vn/dinh-duong.htm");

        Handbook handbook1 = new Handbook(
                "https://play-lh.googleusercontent.com/WKLidAunta9pcv-nvtXaln9LY6YkGUdYN3GOfivc4ti4mfGEHEq1MOM0DN8U2Ic6oJw=w480-h960-rw",
                "Sức Khoẻ - Sổ tay dinh dưỡng, giữ gìn đời sống gia đình",
                "https://thanhnien.vn/suc-khoe/");

        Handbook handbook2 = new Handbook(
                "https://static.mediacdn.vn/covid19.gov.vn/image/default_share.jpg",
                "Bộ Y tế - Cổng thông tin điện tử",
                "https://moh.gov.vn/");

        Handbook handbook3 = new Handbook(
                "https://upload.wikimedia.org/wikipedia/vi/thumb/2/22/Vietnamnet-Logo.png/285px-Vietnamnet-Logo.png",
                "Sức khỏe đời sống - Cẩm nang chăm sóc sức khỏe gia đình",
                "https://vietnamnet.vn/suc-khoe"
        );


        List<Handbook> list = new ArrayList<>();
        list.add(handbook0);
        list.add(handbook1);
        list.add(handbook2);
        list.add(handbook3);

        HandbookRecyclerView handBookAdapter = new HandbookRecyclerView(context, list, R.layout.recycler_view_handbook2);
        recyclerViewHandbook.setAdapter(handBookAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewHandbook.setLayoutManager(manager);
    }

    private void getCurrentWeather() {
        //1
        Retrofit service = HTTPService.getOpenWeatherMapInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //2
        Map<String, String> parameters = new HashMap<>();
        String latitude = "21.0278"; // Vĩ độ Hà Nội
        String longitude = "105.8412"; // Kinh độ Hà Nội
        String apiKey = Constant.OPEN_WEATHER_MAP_API_KEY(); //apikey tren open weather map

        parameters.put("lat", latitude);
        parameters.put("lon", longitude);
        parameters.put("appid", apiKey);
        Call<Weather> container = api.getCurrentWeather(parameters);

        //3

        container.enqueue(new Callback<Weather>() {
            @Override
            public void onResponse(@NonNull Call<Weather> call, @NonNull Response<Weather> response) {
                if (response.isSuccessful()) {
                    Weather content = response.body();
                    assert content != null;
                    printDateAndWeather(content);
                }
                if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        System.out.println(jObjError);
                    }catch (Exception e) {
                        System.out.println(TAG);
                        System.out.println("Exception: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Weather> call,@NonNull Throwable t) {
                System.out.println(TAG);
                System.out.println("getCurrentWeather - error: " + t.getMessage());
            }
        });
    }

    //in ra ngay va thoi tiet

    private void printDateAndWeather(Weather content) {
        String today = Tooltip.getReadableToday(context);
        txtDate.setText(today);

        // Chuyển từ Kelvin sang Celsius
        double tempKelvin = content.getMain().getTemp();
        int tempCelsius = (int) (tempKelvin - 273.15); // Ép về int để lấy số nguyên

        String weatherInfo = tempCelsius + getString(R.string.celsius);
        txtWeather.setText(weatherInfo);
    }


}
