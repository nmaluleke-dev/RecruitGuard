package com.recruitguard.app;

import com.google.gson.annotations.SerializedName;

public class ReviewRequest
{
    @SerializedName("postingId")
    private int postingId;

    @SerializedName("userId")
    private int userId;

    public ReviewRequest(int postingId, int userId)
    {
        this.postingId = postingId;
        this.userId = userId;
    }
}