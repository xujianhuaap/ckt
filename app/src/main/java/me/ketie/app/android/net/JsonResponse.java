package me.ketie.app.android.net;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by henjue on 2015/4/9.
 */
public abstract class JsonResponse implements Response<JSONObject> {
    @Override
    public JSONObject buildResponse(String response, String url, int actionId) {
        try {
            return new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }
}
