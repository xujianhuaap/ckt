package me.ketie.app.android.view;

import android.graphics.Path;

/**
 * Created by henjue on 2015/4/2.
 */
public class PPath extends Path {
    PPath() {

    }

    PPath(Path path) {
        super(path);
    }

    PPath(PPath path) {
        super(path);
    }

    public void lineTo(DrawImageView.Point p) {
        this.lineTo(p.x, p.y);
    }

    public void moveTo(DrawImageView.Point p) {
        this.moveTo(p.x, p.y);
    }
}
