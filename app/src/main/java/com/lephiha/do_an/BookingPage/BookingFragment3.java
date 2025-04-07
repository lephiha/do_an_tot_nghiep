package com.lephiha.do_an.BookingPage;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.lephiha.do_an.Container.BookingPhotoDelete;
import com.lephiha.do_an.Container.BookingPhotoUpload;
import com.lephiha.do_an.Helper.Dialog;
import com.lephiha.do_an.Helper.GlobaleVariable;
import com.lephiha.do_an.Helper.LoadingScreen;
import com.lephiha.do_an.Model.Photo;
import com.lephiha.do_an.R;
import com.lephiha.do_an.RecyclerView.BookingPhotoRecyclerView;
import com.lephiha.do_an.configAPI.HTTPRequest;
import com.lephiha.do_an.configAPI.HTTPService;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BookingFragment3 extends Fragment {

    private final String TAG = "Booking Fragment 3";

    private String bookingId;

    private AppCompatButton btnNext;
    private AppCompatButton btnUpload;
    private RecyclerView recyclerView;
    private BookingPhotoRecyclerView adapter;

    private LinearLayout layout;
    private Context context;
    private Activity activity;

    private Map<String, String> header;
    private GlobaleVariable globaleVariable;
    private LoadingScreen loadingScreen;
    private Dialog dialog;

    private BookingViewModel viewModel;
    private List<Photo> list;
    private int photoId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflate the layout
        View view = inflater.inflate(R.layout.fragment_booking3, container, false );

        setupComponent(view);
        setupViewModel();
        setupEvent();
        return view;
    }

    //setup component

    private void setupComponent(View view) {
        Bundle bundle = getArguments();
        assert bundle != null;
        bookingId = (String) bundle.get("bookingId");

        context = requireContext();
        activity = requireActivity();
        globaleVariable = (GlobaleVariable) activity.getApplication();
        header = globaleVariable.getHeaders();
        dialog = new Dialog(context);
        loadingScreen = new LoadingScreen(activity);

        recyclerView = view.findViewById(R.id.recyclerView);
        layout = view.findViewById(R.id.linearLayout);
        btnUpload = view.findViewById(R.id.btnUpload);
        btnNext = view.findViewById(R.id.btnNext);
    }


    //setup event

    private void setupEvent() {
        //btn upload
        btnUpload.setOnClickListener(view->{
            verifyStoragePermissions(activity);

            Intent intent = new Intent();
            intent.setType("image/*");//allows any image file type. Change * to specific extension to limit it
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            openGalleryToPickPhoto.launch(intent);
        });

        //btn Next
        btnNext.setOnClickListener(view -> {
            String fragmentTag = "bookingFragment2";
            BookingFragment2 nextFragment = new BookingFragment2();

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, nextFragment, fragmentTag)
                    .addToBackStack(fragmentTag)
                    .commit();
        });
    }

    //setup recyclerView
    private void setupRecyclerView(List<Photo> list) {
        adapter = new BookingPhotoRecyclerView(context, list);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
    }

    //callback

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAbsoluteAdapterPosition();
            photoId = list.get(position).getId();

            removePhotoFromList(photoId);
            list.remove(position);
            adapter.notifyItemRemoved(position);// trigger event one photo deleted from list
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(context, R.color.colorRed))
                    .addActionIcon(R.drawable.ic_delete)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    //send request to remove photo from list

    private void removePhotoFromList(int photoId) {
        //2
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        //3
        Call<BookingPhotoDelete> container = api.bookingPhotoDelete(header, photoId);

        //4
        container.enqueue(new Callback<BookingPhotoDelete>() {
            @Override
            public void onResponse(@NonNull Call<BookingPhotoDelete> call,@NonNull Response<BookingPhotoDelete> response) {
                if (response.isSuccessful()) {
                    BookingPhotoDelete content = response.body();
                    assert content != null;
                    int result = content.getResult();
                    Snackbar snackbar;
                    if (result == 1) {
                        snackbar= Snackbar.make(layout, "Success", Snackbar.LENGTH_SHORT);
                    }
                    else {
                        snackbar = Snackbar.make(layout, "Fail", Snackbar.LENGTH_SHORT);
                    }
                    snackbar.show();
                }
                if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        System.out.println(jObjError);
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookingPhotoDelete> call,@NonNull Throwable t) {
                System.out.println(TAG);
                System.out.println("Error from function removePhotoFromList(): " + t.getMessage());
            }
        });
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    //open gallerry to pick photo

    private final ActivityResultLauncher<Intent> openGalleryToPickPhoto = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    Uri uri = data.getData();

                    uploadPhotoToServer(uri);
                }
                else {
                    System.out.println(TAG);
                    System.out.println("Error - openGalleryToPickPhoto");
                }
            }
    );

    //create a http request to upload photo

    private void uploadPhotoToServer(Uri uri) {
        if (uri == null) {
            Log.e(TAG, "URI is null, cannot upload photo");
            dialog.announce();
            dialog.show(R.string.attention, "Không thể lấy ảnh từ thư viện.", R.drawable.ic_info);
            dialog.btnOK.setOnClickListener(v -> dialog.close());
            return; // Dừng hàm nếu URI null.
        }

        try {
            InputStream inputStream = activity.getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Log.e(TAG, "InputStream is null, cannot upload photo");
                dialog.announce();
                dialog.show(R.string.attention, "Lỗi đọc file ảnh.", R.drawable.ic_info);
                dialog.btnOK.setOnClickListener(v -> dialog.close());
                return;
            }

            File tempFile = File.createTempFile("image", ".jpg", activity.getCacheDir());
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            fileOutputStream.close();

            RequestBody id = RequestBody.create(MediaType.parse("multipart/form-data"), bookingId);
            RequestBody requestBodyFile = RequestBody.create(MediaType.parse("multipart/form-data"), tempFile);
            MultipartBody.Part actualFile = MultipartBody.Part.createFormData("file", tempFile.getName(), requestBodyFile);

            String accessToken = globaleVariable.getAccessToken();
            String type = "Patient";
            Retrofit service = HTTPService.getInstance();
            HTTPRequest api = service.create(HTTPRequest.class);
            Call<BookingPhotoUpload> container = api.bookingPhotoUpload(accessToken, type, id, actualFile);

            container.enqueue(new Callback<BookingPhotoUpload>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<BookingPhotoUpload> call, @NonNull Response<BookingPhotoUpload> response) {
                    if (response.isSuccessful()) {
                        BookingPhotoUpload content = response.body();
                        if (content != null && content.getResult() == 1) {
                            viewModel.bookingPhotoReadAll(header, bookingId);
                            adapter.notifyDataSetChanged();
                        } else {
                            dialog.announce();
                            dialog.show(R.string.attention, content != null ? content.getMsg() : "Upload failed", R.drawable.ic_info);
                            dialog.btnOK.setOnClickListener(view -> dialog.close());
                        }
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Log.e(TAG, "Error body: " + jObjError);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BookingPhotoUpload> call, @NonNull Throwable t) {
                    Log.e(TAG, "Upload onFailure", t);
                    dialog.announce();
                    dialog.show(R.string.attention, "Lỗi kết nối mạng hoặc server.",R.drawable.ic_info);
                    dialog.btnOK.setOnClickListener(v->dialog.close());
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "IOException during file processing", e);
            dialog.announce();
            dialog.show(R.string.attention, "Lỗi xử lý file ảnh.", R.drawable.ic_info);
            dialog.btnOK.setOnClickListener(v->dialog.close());

        } catch (Exception e){
            Log.e(TAG, "Exception during upload", e);
            dialog.announce();
            dialog.show(R.string.attention, "Lỗi upload ảnh.", R.drawable.ic_info);
            dialog.btnOK.setOnClickListener(v->dialog.close());

        }
    }

    //setup view model
    private void setupViewModel() {
        //declare
        viewModel = new ViewModelProvider(this).get(BookingViewModel.class);
        viewModel.instantiate();

        //send request
        viewModel.bookingPhotoReadAll(header, bookingId);
        viewModel.getBookingPhotoReadAllResponse().observe((LifecycleOwner) context, response -> {
            try {
                int result = response.getResult();
                // result = 1 => luu thong tin vao home
                if (result == 1) {
                    list = response.getData();
                    System.out.println(TAG);
                    System.out.println("photo size: "+ list.size());
                    setupRecyclerView(list);
                }
                if (result == 0) {
                    dialog.announce();
                    dialog.show(R.string.attention, getString(R.string.check_your_internet_connection), R.drawable.ic_info);
                    dialog.btnOK.setOnClickListener(view->{
                        dialog.close();
                        activity.finish();
                    });
                }
            }
            catch (Exception e) {
                System.out.println(TAG);
                System.out.println(e);

                dialog.announce();
                dialog.show(R.string.attention, getString(R.string.check_your_internet_connection), R.drawable.ic_info);
                dialog.btnOK.setOnClickListener(view->{
                    dialog.close();
                    activity.finish();
                });
            }
        });

        //animation
        viewModel.getAnimation().observe((LifecycleOwner) context, aBoolean -> {
            if (aBoolean) loadingScreen.start();
            else {
                loadingScreen.stop();
            }
        });
    }


}
