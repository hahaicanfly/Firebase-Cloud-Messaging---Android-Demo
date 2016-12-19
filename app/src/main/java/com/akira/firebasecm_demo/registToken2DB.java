package com.akira.firebasecm_demo;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by akira on 2016/12/13.
 * 將使用者的Token 和 暱稱 存到 firebase database
 *
 */
public class registToken2DB {

    private DatabaseReference mRef;
    private FirebaseDatabase database;

    private String mUserName;
    private String mTokem;

    public registToken2DB(String mUserName, String mTokem) {
        this.mUserName = mUserName;
        this.mTokem = mTokem;
    }

    public void main(){

        Log.d("registToken2DB","main");
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("user");

        mRef.child(mUserName).setValue(mUserName);
        mRef.child(mUserName).setValue(mTokem);
    }


}
