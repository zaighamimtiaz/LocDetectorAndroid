package com.example.hassan.servicetest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseInstanceIdService";

    public MyFirebaseInstanceIdService() {
    }

    @Override
    public void onTokenRefresh() {

        // Get updated token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
    }

}
