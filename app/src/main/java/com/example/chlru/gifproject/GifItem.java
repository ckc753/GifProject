package com.example.chlru.gifproject;


/**
 * Created by hi on 2018-08-31.
 */

public class GifItem {
    String downloadUrl;
    String filename;//파일 이름  ex) sample.gif
    String gifname;//gif파일의 제목 ex) 댕댕이
    String day;
    int number;

    public GifItem() {
    }

    public GifItem(String downloadUrl, String filename, String gifname,String day,int number) {
        this.downloadUrl = downloadUrl;
        this.filename = filename;
        this.gifname = gifname;
        this.day=day;
        this.number=number;


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

    @Override
    public String toString() {
        return "gifItem{" +
                "downloadUrl=" + downloadUrl +
                ", filename='" + filename + '\'' +
                ", g_name='" + gifname + '\'' +
                '}';
    }

}
