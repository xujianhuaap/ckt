package me.ketie.app.android.controller;

import android.util.Base64;

import java.io.File;
import java.io.UnsupportedEncodingException;

import me.ketie.app.android.network.JsonResponseListener;
import me.ketie.app.android.network.RequestBuilder;

/**
 * 贴纸相关
 * Created by android on 15-4-23.
 */
public class TimelineController {
    /**
     * 获取评论列表
     * @param token
     * @param cid
     * @param page
     * @param listener
     */
    public static void listReply(String token,int cid,int page,JsonResponseListener listener){
        RequestBuilder builder = new RequestBuilder("/hall/replylist",token);
        builder.addParams("cid", cid);
        builder.addParams("page", page);
        builder.post(listener);
    }

    /**
     * 发起评论
     * @param token
     * @param cid
     * @param content
     * @param listener
     */
    public static void addReply(String token,int cid,String content,JsonResponseListener listener){
        addReply(token,cid,content,null,-1,listener);
    }

    /**
     * 发起评论
     * @param token
     * @param cid
     * @param file
     * @param timelength
     * @param listener
     */
    public static void addReply(String token,int cid,File file,int timelength,JsonResponseListener listener){
        addReply(token,cid,null,file,timelength,listener);
    }
    private static void addReply(String token,int cid,String content,File file,int timelength,JsonResponseListener listener){
        try {
            RequestBuilder builder = new RequestBuilder("/hall/addreply",token);
            if (file!=null && content==null) {
                builder.addParams("cid", cid);
                builder.addParams("type", "1");
                builder.addParams("timeleng", timelength);
                builder.addParams("sound", file);
            } else {
                builder.addParams("cid", cid);
                builder.addParams("type", "0");
                builder.addParams("content", Base64.encodeToString(content.getBytes("UTF-8"), 0));
            }
            builder.post(listener);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 首页全部列表
     * @param token
     * @param uid
     * @param page
     * @param listener
     */
    public static void listAll(String token,String uid,int page,JsonResponseListener listener){
        RequestBuilder builder = new RequestBuilder("/hall/getallcontents",token);
        builder.addParams("uid", uid);
        builder.addParams("page", page);
        builder.post(listener);
    }

    /**
     * 首页精选
     * @param token
     * @param uid
     * @param page
     * @param listener
     */
    public static void listBoutique(String token,String uid,int page,JsonResponseListener listener){
        RequestBuilder builder = new RequestBuilder("/hall/finelist",token);
        builder.addParams("uid", uid);
        builder.addParams("page", page);
        builder.post(listener);
    }
}
