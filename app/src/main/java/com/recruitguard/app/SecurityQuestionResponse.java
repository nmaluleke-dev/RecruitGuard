package com.recruitguard.app;

import com.google.gson.annotations.SerializedName;

public class SecurityQuestionResponse {
    @SerializedName("question")
    private Integer question;

    public Integer getQuestion() { return question; }
}
