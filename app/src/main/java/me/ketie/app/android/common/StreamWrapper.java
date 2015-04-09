package me.ketie.app.android.common;

import java.io.InputStream;

/**
 * Created by henjue on 2015/4/9.
 */
public class StreamWrapper {
    public final InputStream stream;
    public final String filename;

    public StreamWrapper(InputStream stream, String filename) {
        this.stream = stream;
        this.filename = filename;
    }
}
