package com.akira.firebasecm_demo;

/**
 * Created by akira on 2016/11/17.
 */
public class FirebaseParam {

    private static FirebaseParam instance;

    public String mToken;
    public String mId;

    public static FirebaseParam getInstance(){
        if(instance ==null){
            synchronized (FirebaseParam.class){
                if(instance ==null){
                    instance = new FirebaseParam();
                }
            }
        }
        return instance;
    }


    public String getmToken() {
        return mToken;
    }

    public void setmToken(String mToken) {
        this.mToken = mToken;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }
}
