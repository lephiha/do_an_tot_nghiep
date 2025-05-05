package com.lephiha.do_an.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lephiha.do_an.Model.CallDoctor;
import com.lephiha.do_an.R;
import com.lephiha.do_an.databinding.CallDoctorListItemBinding;
import com.lephiha.do_an.viewModel.ChooseCallDoctorListener;

import java.util.List;

public class CallDoctorAdapter extends RecyclerView.Adapter<CallDoctorAdapter.CallDoctorHolder> {

    private Context context;
    private List<CallDoctor> doctorList;
    private ChooseCallDoctorListener listener;

    public CallDoctorAdapter(Context context, List<CallDoctor> doctorList, ChooseCallDoctorListener listener) {
        this.context = context;
        this.doctorList = doctorList;
        this.listener = listener;
    }

    public class CallDoctorHolder extends RecyclerView.ViewHolder {
        private CallDoctorListItemBinding callDoctorListItemBinding;

        public CallDoctorHolder(CallDoctorListItemBinding callDoctorListItemBinding) {
            super(callDoctorListItemBinding.getRoot());
            this.callDoctorListItemBinding = callDoctorListItemBinding;
        }
    }

    @NonNull
    @Override
    public CallDoctorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CallDoctorListItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.call_doctor_list_item,
                parent,
                false
        );
        return new CallDoctorHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CallDoctorHolder holder, int position) {
        CallDoctor doctor = doctorList.get(position);
        String formattedPrice = String.format("%,dđ", Integer.parseInt(doctor.getPrice())); // Định dạng giá và thêm "đ"

        // Hiển thị Avatar bằng Glide
        Glide.with(context)
                .load(doctor.getAvatar())
                .placeholder(R.drawable.dialog_background_danger)
                .error(R.drawable.default_avatar)
                .into(holder.callDoctorListItemBinding.imageView);

        holder.callDoctorListItemBinding.textView10.setText(formattedPrice);
        holder.callDoctorListItemBinding.textView8.setText(doctor.getSpecialityName());
        holder.callDoctorListItemBinding.textView6.setText("Bác sĩ " + doctor.getName());

        // Kiểm tra trạng thái thanh toán và hiển thị nút phù hợp
        if (doctor.getPaid() == 1) {
            // Nếu đã thanh toán: Hiển thị nút "Gọi Video", ẩn nút "Thanh toán ngay"
            holder.callDoctorListItemBinding.materialButton.setVisibility(View.GONE);  // Ẩn nút thanh toán
            holder.callDoctorListItemBinding.materialButton2.setVisibility(View.VISIBLE);  // Hiển thị nút gọi video
            holder.callDoctorListItemBinding.materialButton2.setText("Gọi Video");
            holder.callDoctorListItemBinding.materialButton2.setOnClickListener(v -> listener.call(String.valueOf(doctor.getId())));
        } else {
            // Nếu chưa thanh toán: Hiển thị nút "Thanh toán ngay", ẩn nút "Gọi Video"
            holder.callDoctorListItemBinding.materialButton.setVisibility(View.VISIBLE);  // Hiển thị nút thanh toán
            holder.callDoctorListItemBinding.materialButton2.setVisibility(View.GONE);  // Ẩn nút gọi video
            holder.callDoctorListItemBinding.materialButton.setText("Thanh toán ngay");
            holder.callDoctorListItemBinding.materialButton.setOnClickListener(v -> listener.onItemCliked(doctor));
        }

//            holder.callDoctorListItemBinding.materialButton2.setVisibility(View.VISIBLE);  // Hiển thị nút gọi video
//            holder.callDoctorListItemBinding.materialButton2.setText("Gọi Video");
//            holder.callDoctorListItemBinding.materialButton2.setOnClickListener(v -> listener.call(String.valueOf(doctor.getId())));
    }


    @Override
    public int getItemCount() {
        return doctorList.size();
    }
}