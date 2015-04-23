package me.ketie.app.android.model;

/**
 * <pre>
 * Description:
 * 2015/4/1414:42
 *
 * @author henjue
 *         email:henjue@gmail.com
 * @version 1.0
 *          </pre>
 */
public class Topic {
    private String name, description;
    private int id, uid, hot;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getHot() {
        return hot;
    }

    public void setHot(int hot) {
        this.hot = hot;
    }

}
