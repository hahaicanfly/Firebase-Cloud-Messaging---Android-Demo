package com.akira.firebasecm_demo;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by akira on 2016/11/15.
 */
public class FcmInstanceIdService extends FirebaseInstanceIdService {

    private String TAG = "FCM";
    private FirebaseInstanceId firebaseInstanceId;
    private FirebaseParam param;


    @Override
    public void onTokenRefresh() {
        Log.d(TAG,"onTokenRefresh");

        param = FirebaseParam.getInstance();
        firebaseInstanceId = FirebaseInstanceId.getInstance();

        String mToken = firebaseInstanceId.getToken();
        String mId = firebaseInstanceId.getId();
        param.setmToken(mToken);
        param.setmId(mId);
        Log.d(TAG, "mToken: " + mToken + "  mId: " + mId);

        
    }



}
