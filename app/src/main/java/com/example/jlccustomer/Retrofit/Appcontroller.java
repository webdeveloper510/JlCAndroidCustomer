package com.example.jlccustomer.Retrofit;


import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.jlccustomer.Utils.Constants;
import com.example.jlccustomer.Utils.Event;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.Executor;

public class Appcontroller extends Application {

    public static Double lattitude, longitude;
    public static final String TAG = Appcontroller.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static Appcontroller mInstance;



    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        EventBus.getDefault().register(mInstance);


    }

    public static synchronized Appcontroller getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        req.setRetryPolicy(
                new DefaultRetryPolicy(
                        500000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));

        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d("onterminate", "On terminate called");
        EventBus.getDefault().unregister(mInstance);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Event event) {
        switch (event.getKey()) {

            case Constants.LOCATION_GET:
                //  Log.d("chkData", lattitude + "current Lat from service in Appcontroller" + longitude + " current Lng from service Appcontroller");
                lattitude = event.getLattitude();
                longitude = event.getLongitude();
                Log.d("AppLattitudeLongitude",lattitude+"application"+" "+longitude+"application");

                break;
        }
    }

}
