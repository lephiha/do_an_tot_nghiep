package com.lephiha.do_an.chatbotAI;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lephiha.do_an.Model.ChatRequest;
import com.lephiha.do_an.Model.ChatResponse;
import com.lephiha.do_an.Model.Message;
import com.lephiha.do_an.R;
import com.lephiha.do_an.RecyclerView.MessageAdapter;
import com.lephiha.do_an.configAPI.ApiService;
import com.lephiha.do_an.configAPI.Constant;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText userInput;
    private Button sendButton;
    private ProgressBar progressBar;
    private List<Message> messageList;
    private MessageAdapter messageAdapter;
    private ApiService apiService;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Thiết lập toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Gửi broadcast để ẩn bong bóng chat
        Intent hideIntent = new Intent(ChatHeadService.ACTION_HIDE_CHAT_HEAD);
        sendBroadcast(hideIntent);

        // Thiết lập các thành phần
        setupComponent();
        setupData();
        setupEvent();
    }

    private void setupComponent() {
        recyclerView = findViewById(R.id.recyclerView);
        userInput = findViewById(R.id.userInput);
        sendButton = findViewById(R.id.sendButton);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupData() {
        // Khởi tạo dữ liệu
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        // Sử dụng FastAPI service thay vì service cũ
        apiService = Constant.getFastAPIService();

        // Tin nhắn chào mừng
        addMessage(new Message("Chào bạn! Tôi là chatbot y tế. Bạn cần giúp gì?", false));
    }

    private void setupEvent() {
        // Xử lý nút gửi
        sendButton.setOnClickListener(v -> {
            String userMessage = userInput.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                // Hiển thị tin nhắn người dùng
                addMessage(new Message(userMessage, true));
                // Hiển thị loading
                progressBar.setVisibility(View.VISIBLE);
                sendButton.setEnabled(false);
                // Gửi câu hỏi đến API
                getBotResponse(userMessage);
                userInput.setText("");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Gửi broadcast để hiện lại bong bóng chat khi thoát ChatActivity
        Intent showIntent = new Intent(ChatHeadService.ACTION_SHOW_CHAT_HEAD);
        sendBroadcast(showIntent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Xử lý khi nhấn nút quay lại trên toolbar
        onBackPressed();
        return true;
    }

    private void addMessage(Message message) {
        messageList.add(message);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);
    }

    private void getBotResponse(String question) {
        // Tạo request body
        ChatRequest request = new ChatRequest(question);
        Call<ChatResponse> call = apiService.getAnswer(request);

        // Gửi request đến API
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                // Ẩn loading
                progressBar.setVisibility(View.GONE);
                sendButton.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ChatResponse chatResponse = response.body();
                    if ("success".equals(chatResponse.getStatus())) {
                        // Lấy câu trả lời từ data.answer thay vì answer trực tiếp
                        String answer = chatResponse.getData() != null ? chatResponse.getData().getAnswer() : null;
                        if (answer != null && !answer.isEmpty()) {
                            addMessage(new Message(answer, false));
                        } else {
                            addMessage(new Message("Không có câu trả lời từ chatbot.", false));
                        }
                    } else {
                        addMessage(new Message("Không tìm thấy câu trả lời phù hợp.", false));
                    }
                } else {
                    addMessage(new Message("Lỗi khi kết nối với server: " + response.message(), false));
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                // Ẩn loading
                progressBar.setVisibility(View.GONE);
                sendButton.setEnabled(true);
                // Hiển thị lỗi nếu không kết nối được
                addMessage(new Message("Lỗi kết nối: " + t.getMessage(), false));
                Toast.makeText(ChatActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}