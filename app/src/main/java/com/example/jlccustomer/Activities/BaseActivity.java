package com.example.jlccustomer.Activities;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class BaseActivity extends AppCompatActivity {

    String placeId = "EidJU0JUIFJvYWQsIFNlY3RvciA0MywgQ2hhbmRpZ2FyaCwgSW5kaWEiLiosChQKEgnvbA3F0u0PORG7gz1dcbsxXhIUChIJuUmV3zLsDzkRRMt_ZTLNgrs";

    GoogleApiClient client = new GoogleApiClient() {
        @Override
        public boolean hasConnectedApi(@NonNull Api<?> api) {
            return false;
        }

        @NonNull
        @Override
        public ConnectionResult getConnectionResult(@NonNull Api<?> api) {
            return null;
        }

        @Override
        public void connect() {

        }

        @Override
        public ConnectionResult blockingConnect() {
            return null;
        }

        @Override
        public ConnectionResult blockingConnect(long l, @NonNull TimeUnit timeUnit) {
            return null;
        }

        @Override
        public void disconnect() {

        }

        @Override
        public void reconnect() {

        }

        @Override
        public PendingResult<Status> clearDefaultAccountAndReconnect() {
            return null;
        }

        @Override
        public void stopAutoManage(@NonNull FragmentActivity fragmentActivity) {

        }

        @Override
        public boolean isConnected() {
            return false;
        }

        @Override
        public boolean isConnecting() {
            return false;
        }

        @Override
        public void registerConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks) {

        }

        @Override
        public boolean isConnectionCallbacksRegistered(@NonNull ConnectionCallbacks connectionCallbacks) {
            return false;
        }

        @Override
        public void unregisterConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks) {

        }

        @Override
        public void registerConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener) {

        }

        @Override
        public boolean isConnectionFailedListenerRegistered(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
            return false;
        }

        @Override
        public void unregisterConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener) {

        }

        @Override
        public void dump(String s, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strings) {

        }
    };

    void getLatLngFromPlaceId(String placeId) {
//        EidJU0JUIFJvYWQsIFNlY3RvciA0MywgQ2hhbmRpZ2FyaCwgSW5kaWEiLiosChQKEgnvbA3F0u0PORG7gz1dcbsxXhIUChIJuUmV3zLsDzkRRMt_ZTLNgrs

        Places.GeoDataApi.getPlaceById(client, placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {

                    @Override
                    public void onResult(PlaceBuffer p) {
                        if (p.getStatus().isSuccess()) {
                            final Place mPlace = p.get(0);
                            LatLng qLoc = mPlace.getLatLng();
                            Log.e("getLatLngFromPlaceId", "Lat : "+qLoc.latitude+"--Lng : "+qLoc.longitude);
                            //you can use lat with qLoc.latitude;
                            //and long with qLoc.longitude;
                        }
                        p.release();
                    }
                });
    }
}
