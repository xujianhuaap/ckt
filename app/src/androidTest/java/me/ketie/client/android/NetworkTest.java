package me.ketie.client.android;

import android.test.AndroidTestCase;

import java.util.HashMap;

import me.ketie.client.android.net.Builder;
import me.ketie.client.android.net.JsonRequest;

/**
 * Created by henjue on 2015/3/30.
 */
public class NetworkTest extends AndroidTestCase {
    public void testRequest(){
        JsonRequest request = new Builder("path", new HashMap<String, Object>() {{
            put("cid", "cid123");
            put("aid", "aid674");
            put("uid", 111);
        }}).setToken("sdu89afuawodjfcads890fad0uc").build();

    }
}
