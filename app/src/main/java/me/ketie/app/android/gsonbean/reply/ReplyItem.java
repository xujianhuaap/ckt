package me.ketie.app.android.gsonbean.reply;

import me.ketie.app.android.gsonbean.RUser;
import me.ketie.app.android.gsonbean.Voice;

/**
 * Created by android on 15-4-17.
 */
public class ReplyItem {
    private int id;//评论/回复的id
    private int uid;//发布评论/回复的用户id
    private int ruid;//被回复用户的id
    private int cid;//作品id
    private int rid;//赞不明确(前端无用)
    private int fid;//文件ID 当type为1时 fid对应file表id(即：语音ID)type为0时fid为0直接取字段content
    private int type;//回复内容类型:0文本,1语音,2图片
    private long timeleng;//当type为1时 表示语音时长，其他该字段为0
    private String content;//当type为0时 表示评论/回复的文字内容其他为空
    private int encode;//是否解码,前端无用
    private int status;//状态(前端无用)
    private long datetime;//发表评论/回复的时间
    private ReplyItemUser user;//发表评论/回复的用户信息
    private Voice voice;//语音信息

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getRuid() {
        return ruid;
    }

    public void setRuid(int ruid) {
        this.ruid = ruid;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTimeleng() {
        return timeleng;
    }

    public void setTimeleng(long timeleng) {
        this.timeleng = timeleng;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getEncode() {
        return encode;
    }

    public void setEncode(int encode) {
        this.encode = encode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    public ReplyItemUser getUser() {
        return user;
    }

    public void setUser(ReplyItemUser user) {
        this.user = user;
    }

    public Voice getVoice() {
        return voice;
    }

    public void setVoice(Voice voice) {
        this.voice = voice;
    }

    public RUser getRuser() {
        return ruser;
    }

    public void setRuser(RUser ruser) {
        this.ruser = ruser;
    }

    private RUser ruser;//被回复的用户信息
    public boolean isText(){
        return this.type==0;
    }

}
