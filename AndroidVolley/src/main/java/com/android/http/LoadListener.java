package com.android.http;

/**
 * LoadListener special for ByteArrayLoadControler
 * 
 * @author steven-pan
 * 
 */
public interface LoadListener {
	
	void onStart();

	void onSuccess(byte[] data, String url, int actionId);

	void onError(Exception e, String url, int actionId);
}
