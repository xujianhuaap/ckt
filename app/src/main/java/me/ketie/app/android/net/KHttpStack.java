package me.ketie.app.android.net;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpClientStack;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.util.Map;


/**
 * Created by henjue on 2015/3/31.
 */
public class KHttpStack extends HttpClientStack {
    public KHttpStack(HttpClient client) {
        super(client);
    }

    @Override
    public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError {
        return super.performRequest(request, additionalHeaders);
    }

    @Override
    protected void onPrepareRequest(HttpUriRequest request) throws IOException {
        super.onPrepareRequest(request);
    }
}
