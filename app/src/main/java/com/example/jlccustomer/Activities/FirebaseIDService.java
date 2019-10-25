package com.example.jlccustomer.Activities;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";
  public static String Token;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
       // super.onTokenRefresh();
        Token = FirebaseInstanceId.getInstance().getToken();
        Log.d("Refreshedtoken:" , Token);

    /*    // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);*/
    }

}
