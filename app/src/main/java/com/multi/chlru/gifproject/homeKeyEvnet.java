package com.multi.chlru.gifproject;

public class homeKeyEvnet {
    //1. onUserLeaveHint()는 홈키 구분용이지만, 엑티비티전환시에도 실행된다.
    //2-1. 엑티비티 전환 또는 홈키실행시 ==> onPause.
    //2-2. 다시 실행하면 onResume.
    //2-3. 때문에, 엑티비티와 홈키간의 종료를 구분하는 것은 intent실행전에 Flag상태를 모두 true상태로 적용시킨다.

    //※ onUserLeaveHint()와 onPause()를 모든 엑티비티의 끝에 적용함으로서 Homekey시 엑티비티종료 적용
    //※ 엑티비티간의 Intent를 통한 onPause는 디버그가능하지만, 회원가입, 관리자의 뒤로가기위젯은 적용이 X

    private static boolean homeflag; //다른 엑티비티 실행여부
    private static boolean homestatus; //홈+엑티비티

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
