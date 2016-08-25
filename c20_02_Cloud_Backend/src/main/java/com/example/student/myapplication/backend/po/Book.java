package com.example.student.myapplication.backend.po;

import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;

public class Book {

    private long key;
    private String title;
    private String author;
    private int price;
    private long time;

    public Book() { }

    public Book(Entity entity) {
        setKey(entity.getKey().getId());
        setTitle(entity.getProperty("title").toString());
        setAuthor(entity.getProperty("author").toString());
        setPrice(Integer.parseInt(entity.getProperty("price").toString()));
        setTime(Long.parseLong(entity.getProperty("time").toString()));
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
