package com.sayan.awesomenote;

public class fireBaseModel {

    private String title;
    private String content;
    private String id;

    public fireBaseModel(){

    }

    public String getId() {
        return id;
    }

    public void setUid(String id) {
        this.id = id;
    }

    public  String getTitle() {
        return title;
    }

    public fireBaseModel(String title, String content,String uid) {
        this.title = title;
        this.content = content;
        this.id=id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public  String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
