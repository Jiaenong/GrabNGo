package com.example.user.grabngo.Class;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Comment {

    private String customerKey;
    private @ServerTimestamp Date postTime;
    private String commentContent;

    public Comment(String customerKey, Date time, String commentContent)
    {
        this.customerKey = customerKey;
        this.postTime = time;
        this.commentContent = commentContent;
    }

    public Comment()
    {

    }

    public String getCustomerKey() {
        return customerKey;
    }

    public void setCustomerKey(String customerKey) {
        this.customerKey = customerKey;
    }

    public Date getPostTime() {
        return postTime;
    }

    public void setPostTime(Date postTime) {
        this.postTime = postTime;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
