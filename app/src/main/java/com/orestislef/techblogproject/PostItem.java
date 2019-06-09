package com.orestislef.techblogproject;

public class PostItem {


    String Title,Excerpt, Content,Date;
    String image;

    public PostItem() {
    }


    public PostItem(String title, String excerpt, String content, String date, String image) {
        Title = title;
        Excerpt = excerpt;
        Content = content;
        Date = date;
        this.image = image;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setContent(String content) {
        Content = content;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return Title;
    }

    public String getContent() {
        return Content;
    }

    public String getDate() {
        return Date;
    }

    public String getImage() {
        return image;
    }
}
