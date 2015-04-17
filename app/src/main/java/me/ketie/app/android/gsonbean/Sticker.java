package me.ketie.app.android.gsonbean;

/**
 * <pre>
 * Description:
 * 2015/4/1414:45
 *
 * @author henjue
 *         email:henjue@gmail.com
 * @version 1.0
 *          </pre>
 */
public class Sticker {
    private int id, status;
    private String url, link;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
