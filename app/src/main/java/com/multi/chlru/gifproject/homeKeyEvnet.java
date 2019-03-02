package com.multi.chlru.gifproject;

public class homeKeyEvnet {
    private static boolean homeflag;
    private static boolean homestatus;

    public boolean isHomeflag() {
        return homeflag;
    }

    public void setHomeflag(boolean homeflag) {
        this.homeflag = homeflag;
    }

    public boolean isHomestatus() {
        return homestatus;
    }

    public void setHomestatus(boolean homestatus) {
        this.homestatus = homestatus;
    }

    public homeKeyEvnet(boolean homeflag, boolean homestatus) {
        this.homeflag = homeflag;
        this.homestatus = homestatus;
    }

    public homeKeyEvnet() {
    }
}
