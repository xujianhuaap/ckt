package me.ketie.app.android.widget;

import android.graphics.Bitmap;

public class ImageInfo {
    private String path;
    /**
     * 图片宽 *
     */
    private int width;
    /**
     * 图片高 *
     */
    private int height;
    /**
     * 左上角的x初始坐标 *
     */
    private int x;
    /**
     * 左上角的y初始坐标 *
     */
    private int y;
    private Bitmap bit = null;

    public ImageInfo() {

    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


    public Bitmap getBit() {
        return bit;
    }

    public void setBit(Bitmap bit) {
        this.bit = bit;
    }
}
