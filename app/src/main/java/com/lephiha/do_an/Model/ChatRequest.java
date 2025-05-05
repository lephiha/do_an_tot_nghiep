package com.lephiha.do_an.Model;

public class ChatRequest {
    private String question;

    public ChatRequest(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}