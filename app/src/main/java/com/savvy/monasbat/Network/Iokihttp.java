package com.savvy.monasbat.Network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.StrictMode;
import android.widget.Toast;

import com.savvy.monasbat.R;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static android.os.Looper.getMainLooper;

public class Iokihttp {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    OkHttpClient client = new OkHttpClient();
    public int retry = 0;

    public Call uploadImage(String id, File image, String imageName, String url, final Callback callback) throws IOException {
        System.out.println("URL=" + url);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("img", imageName, RequestBody.create(MEDIA_TYPE_PNG, image))
                .addFormDataPart("id", id)
                .build();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        Request request = new Request.Builder().url(url)
                .post(requestBody).build();
        final Call[] call = new Call[1];

        call[0] = client.newCall(request);
        call[0].enqueue(callback);

        return call[0];
    }
    public Call post(final String url, String json, final Callback callback) {
        System.out.println("url is :" + url);
        final RequestBody body = RequestBody.create(JSON, json);
        Handler handler = new Handler(getMainLooper());
        final Call[] call = new Call[1];
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                call[0] = client.newCall(request);
                call[0].enqueue(callback);
            }
        }, 10000);


        return call[0];
    }
    public boolean isNetworkConnected(Context context) {
        if (isConnected(context)) {
            return true;
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
            return false;
        }

    }
    private boolean isConnected(Context context) {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    public boolean isInternetAvailable(Context context) {
        try {
            InetAddress ipAddr = InetAddress.getByName(context.getResources().getString(R.string.url));
            //You can replace it with your name
            return !ipAddr.equals("");
        } catch (Exception e) {
            return false;
        }
    }
}
