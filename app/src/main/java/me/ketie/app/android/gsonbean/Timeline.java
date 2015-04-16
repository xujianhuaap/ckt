package me.ketie.app.android.gsonbean;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * <pre>
 * Description:
 * 2015/4/1315:02
 *
 * @author henjue
 *         email:henjue@gmail.com
 * @version 1.0
 *          </pre>
 */
public class Timeline {
    private String id;
    private String uid;
    private String tid;
    private String content;
    private String imgurl;
    private String attribute;
    private String status;
    private String type;
    private String replynum;
    private String reportnum;
    private String datetime;
    private String elitetime;
    private String praiseType;
    private String praiseNum;
    private User user;

    public String getWidget() {
        return widget;
    }

    public void setWidget(String widget) {
        this.widget = widget;
    }

    @Expose(serialize =false,deserialize=false)
    private String widget;
    private Topic topic;
    private ArrayList<Sticker> sticker;
    public Timeline(){

    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public ArrayList<Sticker> getSticker() {
        return sticker;
    }

    public void setSticker(ArrayList<Sticker> sticker) {
        this.sticker = sticker;
    }

    public ArrayList<PraiseUser> getPraiseUsers() {
        return praiseUsers;
    }

    public void setPraiseUsers(ArrayList<PraiseUser> praiseUsers) {
        this.praiseUsers = praiseUsers;
    }

    private ArrayList<PraiseUser> praiseUsers;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReplynum() {
        return replynum;
    }

    public void setReplynum(String replynum) {
        this.replynum = replynum;
    }

    public String getReportnum() {
        return reportnum;
    }

    public void setReportnum(String reportnum) {
        this.reportnum = reportnum;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getElitetime() {
        return elitetime;
    }

    public void setElitetime(String elitetime) {
        this.elitetime = elitetime;
    }

    public String getPraiseType() {
        return praiseType;
    }

    public void setPraiseType(String praiseType) {
        this.praiseType = praiseType;
    }

    public String getPraiseNum() {
        return praiseNum;
    }

    public void setPraiseNum(String praiseNum) {
        this.praiseNum = praiseNum;
    }
}
