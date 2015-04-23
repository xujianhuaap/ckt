package me.ketie.app.android.model;

/**
 * Created by android on 15-4-17.
 */
public class Voice {
    private int id;
    private String url;
    private long timeleng;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTimeleng() {
        return timeleng;
    }

    public void setTimeleng(long timeleng) {
        this.timeleng = timeleng;
    }
}
