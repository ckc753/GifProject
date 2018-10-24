package com.example.chlru.gifproject;


/**
 * Created by hi on 2018-08-31.
 */

public class GifItem {
    private String downloadUrl;
    private String filename;//파일 이름  ex) sample.gif
    private String gifname;//gif파일의 제목 ex) 댕댕이
    private String day;
    private int number;
    private String category;
    private String pkKey;

    public GifItem() {
    }
    public GifItem(String downloadUrl, String filename, String gifname,String day,int number,String category,String pkKey) {
        this.downloadUrl = downloadUrl;
        this.filename = filename;
        this.gifname = gifname;
        this.day=day;
        this.number=number;
        this.category=category;
        this.pkKey=pkKey;
    }
    public GifItem(String downloadUrl, String filename, String gifname,String day,int number,String category) {
        this.downloadUrl = downloadUrl;
        this.filename = filename;
        this.gifname = gifname;
        this.day=day;
        this.number=number;
        this.category=category;
 }
    public GifItem(String downloadUrl, String filename, String gifname,String day,int number) {
        this.downloadUrl = downloadUrl;
        this.filename = filename;
        this.gifname = gifname;
        this.day=day;
        this.number=number;

    }
    public String getPkKey() {
        return pkKey;
    }

    public void setPkKey(String pkKey) {
        pkKey = pkKey;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getGifname() {
        return gifname;
    }

    public void setG_name(String g_name) {
        this.gifname = gifname;
    }

    public void setGifname(String gifname) {
        this.gifname = gifname;
    }
}
