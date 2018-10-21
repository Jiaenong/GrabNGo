package com.example.user.grabngo.Class;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Post {
    private String customerKey;
    private String postImage;
    private String content;
    private @ServerTimestamp Date postDate;

    public Post(String customerKey, String postImage, String content, Date postDate)
    {
        this.customerKey = customerKey;
        this.postImage = postImage;
        this.content = content;
        this.postDate = postDate;
    }

    public Post()
    {

    }

    public String getCustomerKey() {
        return customerKey;
    }

    public void setCustomerKey(String customerKey) {
        this.customerKey = customerKey;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }
}
