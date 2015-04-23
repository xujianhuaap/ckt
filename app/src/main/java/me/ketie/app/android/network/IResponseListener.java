package me.ketie.app.android.network;

/**
 * Created by henjue on 2015/4/9.
 */
public abstract class IResponseListener<T> {

    public abstract void onRequest();

    public abstract void onError(Exception e, String url, int actionId);

    public abstract void onSuccess(T t, String url, int actionId);

    abstract T buildResponse(String response, String url, int actionId);
}
