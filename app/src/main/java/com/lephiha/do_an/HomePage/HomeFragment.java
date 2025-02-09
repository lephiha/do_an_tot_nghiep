package com.lephiha.do_an.HomePage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lephiha.do_an.Container.SpecialityReadAll;
import com.lephiha.do_an.Container.SpecialityReadByID;
import com.lephiha.do_an.Container.Weather;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import androidx.appcompat.widget.TooltipCompat;

public class HomeFragment extends Fragment {

    private final String TAG = "Home Fragment";
    private GlobaleVariable globaleVariable;
    private RecyclerView recyclerViewSpeciality;
    private RecyclerView recyclerViewDoctor;
    private RecyclerView recyclerViewHandbook;
    private RecyclerView recyclerViewRecommendedPages;

    private EditText searchBar;
    private TextView txtReadMoreSpeciality;
    private  TextView txtReadMoreDoctor;

    private Context context;
    private RecyclerView recyclerViewButton;

    private TextView txtDate;
    private TextView txtWeather;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout frag
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

    //componet
    private void setupComponent(View view) {
        context = requireContext();
        globaleVariable = (GlobaleVariable) requireActivity().getApplication();

        recyclerViewSpeciality = view.findViewById(R.id.recyclerViewSpeciality);
        recyclerViewDoctor = view.findViewById(R.id.recyclerViewDoctor);
        recyclerViewButton = view.findViewById(R.id.recyclerViewButton);
        recyclerViewHandbook = view.findViewById(R.id.recyclerViewHandbook);
        recyclerViewRecommendedPages = view.findViewById(R.id.recyclerViewRecommendedPages);

        searchBar= view.findViewById(R.id.searchBar);
        txtReadMoreSpeciality = view.findViewById(R.id.txtReadMoreSpeciality);
        txtReadMoreDoctor = view.findViewById(R.id.txtReadMoreDoctor);

        txtWeather = view.findViewById(R.id.txtWeather);
        txtDate = view.findViewById(R.id.txtDate);

    }

    //view model

    private void setupViewModel() {
        //1- declare
        HomePageViewModel viewModel = new ViewModelProvider(this).get(HomePageViewModel.class);
        viewModel.instantiate();

        //2- prepare header + parameters
        Map<String , String> header = globaleVariable.getHeaders();
        header.put("type", "patient");

        //3- listen speciality Read all
        Map<String, String> paramSpeciality = new HashMap<>();
        viewModel.specialityReadAll(header, paramSpeciality);

        viewModel.getSpecialityReadAllResponse().observe(getViewLifecycleOwner(), response -> {
            int result = response.getResult();
            if (result == 1) {
                List<Speciality> list = response.getData();
                setupRecyclerViewSpeciality(list);
            }
        });

        //4- listen doc read all
        Map<String, String> paramDoctor = new HashMap<>();
        viewModel.doctorReadAll(header, paramDoctor);

        viewModel.getDoctorReadAllResponse().observe(getViewLifecycleOwner(), response -> {
            int result = response.getResult();
            if (result == 1 ) {
                List<Doctor> list = response.getData();
                setupRecyclerViewDoctor(list);
            }
        });
    }
    //recycler view speciality

    private void setupRecyclerViewSpeciality(List<Speciality> list) {
        SpecialityRecyclerView specialityAdapter = new SpecialityRecyclerView(requireActivity(), list, R.layout.recycler_view_element_speciality);
        recyclerViewSpeciality.setAdapter(specialityAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewSpeciality.setLayoutManager(manager);
    }

    //recycler view doctor
    private void setupRecyclerViewDoctor(List<Doctor> list) {
        DoctorRecyclerView doctorAdapter = new DoctorRecyclerView(requireActivity(), list);
        recyclerViewDoctor.setAdapter(doctorAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewDoctor.setLayoutManager(manager);
    }

    //setup event button
    @SuppressLint({"UnspecifiedImmutableFlag", "ShortAlarm"})
    private void setupEvent() {

        //search bar
        searchBar.setOnClickListener(v -> {
            Intent intent = new Intent(context, SearchActivity.class);
            startActivity(intent);
        });

        //read more speciality
        txtReadMoreSpeciality.setOnClickListener(v -> {
            Intent intent = new Intent(context, SearchActivity.class);
            String filter = context.getString(R.string.speciality);

            intent.putExtra("filterKey", filter);
            startActivity(intent);
        });

        //read more doctor
        txtReadMoreDoctor.setOnClickListener(v -> {
            Intent intent = new Intent(context, SearchActivity.class);
            String filter = context.getString(R.string.doctor);

            intent.putExtra("filterKey", filter);
            startActivity(intent);
        });

    }

    //setup recycler view button

    private void setupRecyclerViewButton() {

        Setting setting0 = new Setting(R.drawable.ic_i_exam_speciality, "specialityExamination", getString(R.string.speciality_examination));
        Setting setting1 = new Setting(R.drawable.ic_exam_general, "generalExamination", getString(R.string.general_examination));
        Setting setting2 = new Setting(R.drawable.ic_exam_heart, "heartExamination", getString(R.string.heart_examination));
        Setting setting3 = new Setting(R.drawable.ic_exam_pregnant, "pregnantExamination", getString(R.string.pregnant_examination));
        Setting setting4 = new Setting(R.drawable.ic_exam_tooth, "toothExamination", getString(R.string.tooth_examination));
        Setting setting5 = new Setting(R.drawable.ic_exam_eye, "eyeExamination", getString(R.string.eye_examination));
        Setting setting6 = new Setting(R.drawable.ic_exam_medical_test, "medicalTestExamination", getString(R.string.medical_test_examination));
        Setting setting7 = new Setting(R.drawable.ic_exam_covid19, "covid19", getString(R.string.covid19_examination));
        Setting setting8 = new Setting(R.drawable.ic_medical_home, "chatAI", getString(R.string.chat_ai));


        List<Setting> list = new ArrayList<>();
        list.add(setting0);
        list.add(setting1);
        list.add(setting2);
        list.add(setting3);
        list.add(setting4);
        list.add(setting5);
        list.add(setting6);
        list.add(setting7);
        list.add(setting8);

        ButtonRecyclerView buttonAdapter = new ButtonRecyclerView(context, list);
        recyclerViewButton.setAdapter(buttonAdapter);

        GridLayoutManager manager = new GridLayoutManager(requireContext(),3);
        recyclerViewButton.setLayoutManager(manager);

    }

    //setup recycler view handbook

    private void setupRecyclerViewHandbook() {

        Handbook handbook0 = new Handbook(
                "https://static-images.vnncdn.net/files/publish/2022/12/20/benh-vien-viet-duc-290.jpg",
                "Ba tiếng chờ đợi, 10 phút khám - Xếp hàng dài, căng tai nghe để không mất lượt",
                "https://vietnamnet.vn/ba-tieng-cho-doi-10-phut-kham-2092139.html"
        );

        Handbook handbook1 = new Handbook(
                "https://image.thanhnien.vn/w2048/Uploaded/2022/uqvpoqiw/2022_12_18/giay-ve-sinh-9535.jpg",
                "Chuyên gia nói gì về giờ đi vệ sinh tốt nhất trong ngày?",
                "https://thanhnien.vn/chuyen-gia-noi-gi-ve-gio-di-ve-sinh-tot-nhat-trong-ngay-post1533096.html"
        );

        Handbook handbook2 = new Handbook(
                "https://image.thanhnien.vn/w2048/Uploaded/2022/wobjohb/2022_12_17/p1-9988.jpeg",
                "Đâu là dấu hiệu cảnh báo một người sắp qua đời?",
                "https://thanhnien.vn/dau-la-dau-hieu-canh-bao-mot-nguoi-sap-qua-doi-post1533048.html"
        );

        Handbook handbook3 = new Handbook(
                "https://image.thanhnien.vn/w2048/Uploaded/2022/uqvpoqiw/2022_12_18/intermittent-fasting3-8817.jpg",
                "Đã tìm ra cách có thể 'chữa khỏi' bệnh tiểu đường?",
                "https://thanhnien.vn/da-tim-ra-cach-co-the-chua-khoi-benh-tieu-duong-post1532438.html"
        );

        Handbook handbook4 = new Handbook(
                "https://image.thanhnien.vn/w2048/Uploaded/2022/aeymrexqam/2022_12_19/ph-1-3698.jpg",
                "Giải pháp cải thiện ho, khò khè, khó thở",
                "https://thanhnien.vn/giai-phap-cai-thien-ho-kho-khe-kho-tho-post1533364.html");

        Handbook handbook5 = new Handbook(
                "https://image.thanhnien.vn/w2048/Uploaded/2022/otpfeclyrbn/2022_12_19/1-4028.jpg",
                "Vì sao thủy đậu, sởi nguy hiểm dịp tết?",
                "https://thanhnien.vn/vi-sao-thuy-dau-soi-nguy-hiem-dip-tet-post1533380.html");

        Handbook handbook6 = new Handbook(
                "https://image.thanhnien.vn/w2048/Uploaded/2022/pwvosgyk/2022_12_16/image1-725.jpg",
                "Sữa tắm chiết xuất lá trầu không với công thức kháng khuẩn không gây khô da",
                "https://thanhnien.vn/sua-tam-chiet-xuat-la-trau-khong-voi-cong-thuc-khang-khuan-khong-gay-kho-da-post1532652.html");
        Handbook handbook7 = new Handbook(
                "https://image.thanhnien.vn/w2048/Uploaded/2022/uqvpoqiw/2022_12_10/whole-grain-bread-1-4791.jpg",
                "5 loại thực phẩm phổ biến có thể gây mệt mỏi kinh niên",
                "https://thanhnien.vn/5-loai-thuc-pham-pho-bien-co-the-gay-met-moi-kinh-nien-post1530554.html");
        Handbook handbook8 = new Handbook(
                "https://image.thanhnien.vn/w2048/Uploaded/2022/wobjohb/2022_12_19/skmoi-4370.png",
                "Làm điều này sau mỗi bữa ăn, bạn sẽ sống thọ hơn",
                "https://thanhnien.vn/lam-dieu-nay-sau-moi-bua-an-ban-se-song-tho-hon-post1532440.html");
        Handbook handbook9 = new Handbook(
                "https://suckhoedoisong.qltns.mediacdn.vn/324455921873985536/2022/12/18/photo-1671368369791-16713683707381806268917.jpg",
                "Chế độ ăn uống lành mạnh có lợi cho mạch máu",
                "https://suckhoedoisong.vn/che-do-an-uong-lanh-manh-co-loi-cho-mach-mau-169221218202106904.htm");
        Handbook handbook10 = new Handbook(
                "https://suckhoedoisong.qltns.mediacdn.vn/thumb_w/640/324455921873985536/2022/12/18/benh-phoi-tac-nghen-man-tinh-nen-an-gi1-167137876802272682938.jpg",
                "Người bệnh phổi tắc nghẽn mạn tính cần được chăm sóc và ăn uống thế nào trong mùa đông?",
                "https://suckhoedoisong.vn/nguoi-benh-phoi-tac-nghen-man-tinh-can-duoc-cham-soc-va-an-uong-the-nao-trong-mua-dong-169221218231139085.htm"
        );
        Handbook handbook11 = new Handbook(
                "https://suckhoedoisong.qltns.mediacdn.vn/324455921873985536/2022/12/16/photo-1671178122606-16711781237731874719199.jpg",
                "8 thực phẩm bổ dưỡng giúp bạn ấm áp trong thời tiết lạnh",
                "https://suckhoedoisong.vn/8-thuc-pham-bo-duong-giup-ban-am-ap-trong-thoi-tiet-lanh-169221216152025663.htm");
        Handbook handbook12 = new Handbook(
                "https://suckhoedoisong.qltns.mediacdn.vn/thumb_w/640/324455921873985536/2022/12/16/ca-rota-1671208804452168719323.jpg",
                "7 loại rau củ tốt nhất cho người bệnh gan nhiễm mỡ",
                "https://suckhoedoisong.vn/7-loai-rau-cu-tot-nhat-cho-nguoi-benh-gan-nhiem-mo-169221216234817123.htm");
        Handbook handbook13 = new Handbook(
                "https://suckhoedoisong.qltns.mediacdn.vn/thumb_w/640/324455921873985536/2022/12/16/a-va-nguoi-benh-dai-thao-duong-an-gao-lut-the-nao-16711647068152070374816.jpg",
                "Bí quyết ăn cơm gạo lứt tốt nhất cho người bệnh đái tháo đường",
                "https://suckhoedoisong.vn/bi-quyet-an-com-gao-lut-tot-nhat-cho-nguoi-benh-dai-thao-duong-169221216114128467.htm");

        Handbook handbook14 = new Handbook(
                "https://suckhoedoisong.qltns.mediacdn.vn/thumb_w/640/324455921873985536/2022/12/1/dtd2-16698836686521411857134.jpg",
                "10 loại thực phẩm người bệnh đái tháo đường nên tránh",
                "https://suckhoedoisong.vn/10-loai-thuc-pham-nguoi-benh-dai-thao-duong-nen-tranh-169221201153930742.htm");

        Handbook handbook15 = new Handbook(
                "https://suckhoedoisong.qltns.mediacdn.vn/324455921873985536/2022/12/12/rau-16708612126451164928123.jpg",
                "Lời khuyên về chế độ ăn uống và lối sống dành cho người bệnh gan nhiễm mỡ",
                "https://suckhoedoisong.vn/loi-khuyen-ve-che-do-an-uong-va-loi-song-danh-cho-nguoi-benh-gan-nhiem-mo-169221212231350623.htm"
        );
        Handbook handbook16 = new Handbook(
                "https://cdn.bookingcare.vn/fr/w1000/2022/12/18/164937-dia-chi-kham-tai-mui-hong-quan-binh-thanh.png",
                "Top 5 phòng khám Tai mũi họng tốt quận Bình Thạnh",
                "https://bookingcare.vn/cam-nang/top-5-phong-kham-tai-mui-hong-tot-quan-binh-thanh-p3048.html");

        List<Handbook> list = new ArrayList<>();
        list.add(handbook0);
        list.add(handbook1);
        list.add(handbook2);
        list.add(handbook3);
        list.add(handbook4);
        list.add(handbook5);
        list.add(handbook6);
        list.add(handbook7);
        list.add(handbook8);
        list.add(handbook9);
        list.add(handbook10);
        list.add(handbook11);
        list.add(handbook12);
        list.add(handbook13);
        list.add(handbook14);
        list.add(handbook15);
        list.add(handbook16);

        HandbookRecyclerView handBookAdapter = new HandbookRecyclerView(context, list, R.layout.recycler_view_handbook);
        recyclerViewHandbook.setAdapter(handBookAdapter);

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

    //get current weather

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
            public void onResponse(@NonNull Call<Weather> call,@NonNull Response<Weather> response) {
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
