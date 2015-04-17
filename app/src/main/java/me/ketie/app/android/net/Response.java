package me.ketie.app.android.net;

/**
 * Created by henjue on 2015/4/9.
 */
public interface Response<T> {

    void onRequest();

    void onError(Exception e, String url, int actionId);

    void onSuccess(T t, String url, int actionId);

    T buildResponse(String response, String url, int actionId);
}
