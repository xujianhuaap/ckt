package me.ketie.app.android.widget;

/**
 * Created by henjue on 2015/4/2.
 */
public class Point implements Cloneable {
    public float x, y;

    public Point(Point in) {
        this.x = in.x;
        this.y = in.y;
    }

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new Point(this);
    }

    public void offset(float offsetX, float offsetY) {
        this.x += offsetX;
        this.y += offsetY;
    }
}
