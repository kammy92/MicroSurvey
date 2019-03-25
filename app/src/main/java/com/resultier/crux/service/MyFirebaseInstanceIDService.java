package com.resultier.crux.service;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.resultier.crux.utils.UserDetailsPref;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh () {
        super.onTokenRefresh ();
        UserDetailsPref userDetailsPref = UserDetailsPref.getInstance ();
        userDetailsPref.putStringPref (getApplicationContext (), UserDetailsPref.FIREBASE_ID, FirebaseInstanceId.getInstance ().getToken ());
    }
}

