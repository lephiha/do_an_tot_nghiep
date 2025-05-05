package com.lephiha.do_an.Model;

public class ChatResponse {
    private String status;
    private ChatData data;
    private String message;

    public String getStatus() {
        return status;
    }

    public ChatData getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public static class ChatData {
        private String answer;

        public String getAnswer() {
            return answer;
        }
    }
}