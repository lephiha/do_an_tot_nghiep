package com.lephiha.do_an.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lephiha.do_an.Model.Message;
import com.lephiha.do_an.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewType == 0 ? R.layout.item_user_message : R.layout.item_bot_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.messageText.setText(message.getText());

        // Định dạng và hiển thị thời gian
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(new Date(message.getTimestamp()));
        holder.messageTime.setText(time);

        // Đặt ảnh đại diện
        holder.messageAvatar.setImageResource(message.isUser() ? R.drawable.img : R.drawable.ic_chat);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).isUser() ? 0 : 1;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        ImageView messageAvatar;
        TextView messageText;
        TextView messageTime;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageAvatar = itemView.findViewById(R.id.messageAvatar);
            messageText = itemView.findViewById(R.id.messageText);
            messageTime = itemView.findViewById(R.id.messageTime);
        }
    }
}