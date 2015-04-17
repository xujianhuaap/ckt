package me.ketie.app.android.gsonbean;

/**
 * <pre>
 * Description:
 * 2015/4/1414:50
 *
 * @author henjue
 *         email:henjue@gmail.com
 * @version 1.0
 *          </pre>
 */
public abstract class BaseUser {
    private String nickname, headimg;
    private int id, uid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadimg() {
        return headimg;
    }

    public void setHeadimg(String headimg) {
        this.headimg = headimg;
    }


    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
