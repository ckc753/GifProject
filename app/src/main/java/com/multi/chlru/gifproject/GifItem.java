package com.multi.chlru.gifproject;


public class GifItem {
    private String downloadUrl;
    private String filename;//파일 이름  ex) sample.gif
    private String gifname;//gif파일의 제목 ex) 댕댕이
    private String day;
    private int number;
    private String category;
    private String pkKey;  //데이터들이 저장된 Primary Key
    private String member; //pk값을 저장할 변수추가 (ManagerActivity와 ManagerRecyclerAdapter에 member추가된 생성자로 변경)
    private String jpgUrl;
    private String caNum;
    private int viewCount;
    private int downCount;
    private int goodCount;
    public GifItem() {
    }
    public GifItem(String jpgUrl,String downloadUrl, String filename, String gifname,String day,String caNum,int number,String category,String member,String pkKey,int viewCount,int downCount,int goodCount) {
        this.jpgUrl = jpgUrl;
        this.downloadUrl = downloadUrl;
        this.filename = filename;
        this.gifname = gifname;
        this.day = day;
        this.caNum = caNum;
        this.number = number;
        this.category = category;
        this.member = member;
        this.pkKey = pkKey;
        this.viewCount = viewCount;
        this.downCount = downCount;
        this.goodCount = goodCount;

    }

    //백업용 생성자
    public GifItem(String jpgUrl,String downloadUrl, String filename, String gifname,String day,String caNum,int number,String category,String member) {
        this.jpgUrl=jpgUrl;
        this.downloadUrl = downloadUrl;
        this.filename = filename;
        this.gifname = gifname;
        this.day=day;
        this.caNum=caNum;
        this.number=number;
        this.category=category;
        this.member=member;

    }
    public GifItem(String jpgUrl,String downloadUrl, String filename, String gifname,String day,int number,String category,String member,String pkKey) {
        this.jpgUrl=jpgUrl;
        this.downloadUrl = downloadUrl;
        this.filename = filename;
        this.gifname = gifname;
        this.day=day;
        this.number=number;
        this.category=category;
        this.member=member;
        this.pkKey=pkKey;
    }

    //매니저GIF용 생성자
    public GifItem(String jpgUrl,String downloadUrl, String filename, String gifname,String day,int number,String category,String member) {
        this.jpgUrl=jpgUrl;
        this.downloadUrl = downloadUrl;
        this.filename = filename;
        this.gifname = gifname;
        this.day=day;
        this.number=number;
        this.category=category;
        this.member=member;
    }
   /* public GifItem(String jpgUrl,String downloadUrl, String filename, String gifname,String day,int number) {
        this.jpgUrl=jpgUrl;
        this.downloadUrl = downloadUrl;
        this.filename = filename;
        this.gifname = gifname;
        this.day=day;
        this.number=number;

    }*/
    //fragment1사용//////////////////////////////////////////////////////
    /*public GifItem(String jpgUrl,String downloadUrl, String filename, String gifname,String day,int number,String caNum) {
        this.jpgUrl=jpgUrl;
        this.downloadUrl = downloadUrl;
        this.filename = filename;
        this.gifname = gifname;
        this.day=day;
        this.number=number;
        this.caNum=caNum;
    }*/
    public GifItem(String jpgUrl,String downloadUrl, String filename, String gifname,String day,int number,String caNum,String pkKey,int viewCount,int downCount,int goodCount) {
        this.jpgUrl=jpgUrl;
        this.downloadUrl = downloadUrl;
        this.filename = filename;
        this.gifname = gifname;
        this.day=day;
        this.number=number;
        this.caNum=caNum;
        this.pkKey = pkKey;
        this.viewCount = viewCount;
        this.downCount = downCount;
        this.goodCount = goodCount;
    }
////////////////////////////////////////////////////////////////////
    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getDownCount() {
        return downCount;
    }

    public void setDownCount(int downCount) {
        this.downCount = downCount;
    }

    public int getGoodCount() {
        return goodCount;
    }

    public void setGoodCount(int goodCount) {
        this.goodCount = goodCount;
    }

    public String getCaNum() {
        return caNum;
    }

    public void setCaNum(String caNum) {
        this.caNum = caNum;
    }

    public String getJpgUrl() {
        return jpgUrl;
    }

    public void setJpgUrl(String jpgUrl) {
        this.jpgUrl = jpgUrl;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
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
