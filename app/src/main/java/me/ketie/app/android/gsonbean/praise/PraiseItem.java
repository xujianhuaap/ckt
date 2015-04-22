package me.ketie.app.android.gsonbean.praise;

import java.util.ArrayList;

/**
 * Created by android on 15-4-21.
 */
public class PraiseItem {
    private int num;
    private int praiseType;
    private ArrayList<PraiseUser> user=new ArrayList<PraiseUser>();

    public ArrayList<PraiseUser> getUser() {
        return user;
    }

    public void setUser(ArrayList<PraiseUser> user) {
        this.user = user;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getPraiseType() {
        return praiseType;
    }

    public void setPraiseType(int praiseType) {
        this.praiseType = praiseType;
    }

}
