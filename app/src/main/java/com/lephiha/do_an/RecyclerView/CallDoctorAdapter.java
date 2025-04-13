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



        // Xử lý nút "Đặt lịch"
        holder.callDoctorListItemBinding.materialButton.setOnClickListener(v -> listener.onItemCliked(doctor));
        holder.callDoctorListItemBinding.materialButton.setText("Đặt lịch"); // Đặt text cho nút

        // Xử lý nút "Gọi" (giả sử bạn muốn gọi video, có thể cần logic khác)
        holder.callDoctorListItemBinding.materialButton2.setOnClickListener(v -> listener.call(doctor.getId().toString())); // Truyền ID bác sĩ để xử lý cuộc gọi
        holder.callDoctorListItemBinding.materialButton2.setText("Gọi Video"); // Đặt text cho nút

        // Logic hiển thị nút (ví dụ: luôn hiển thị cả hai nút)
        holder.callDoctorListItemBinding.materialButton.setVisibility(View.VISIBLE);
        holder.callDoctorListItemBinding.materialButton2.setVisibility(View.VISIBLE);


    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }
}