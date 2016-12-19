package com.akira.firebasecm_demo;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by akira on 2016/11/15
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String TAG = "MyFirebaseMessagingService";
    private LocalBroadcastManager mBroadcast;
    static final public String FCM_MSG = "firebase_cloud_messaging";
    static final public String FCM_NOTIFY_TITLE = "firebase_notify_title";
    static final public String FCM_NOTIFY_BODY = "firebase_notify_body";
    static final public String FCM_RESULT = "firebase_cloud_messaging_result";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mBroadcast = LocalBroadcastManager.getInstance(this);
    }


    /**
     * 只有App 在前景的時候才會觸發 onMessageReceived
     * **/
    @Override
    public void onMessageReceived(RemoteMessage RemoteMessage) {
        Log.d(TAG, "onMessageReceived");

        //Notification Body is title in console
        if (RemoteMessage.getNotification() != null) {

            //進階選項 - 標題
            String msgTitle = RemoteMessage.getNotification().getTitle();

            // 訊息文字
            String msgContent = RemoteMessage.getNotification().getBody();

            //送往 UI
            sendBroadcast(FCM_NOTIFY_TITLE,msgTitle);
            sendBroadcast(FCM_NOTIFY_BODY,msgContent );

            Log.d(TAG, "RemoteMessage.getNotification().getTitle(): " + msgTitle);
            Log.d(TAG, "RemoteMessage.getNotification().getBody() : "+msgContent);
        }



        final String[] keyStrArray = {"notify", "json", "key", "msg",};

        String msg = "";

        if (RemoteMessage.getData().size() > 0) {
            Log.d(TAG, "RemoteMessage.getData():  " + RemoteMessage.getData());
            sendBroadcast(FCM_MSG, RemoteMessage.getData().toString());


            for (String keyStr : keyStrArray) {
                if (RemoteMessage.getData().get(keyStr) != null) {
                    msg = RemoteMessage.getData().get(keyStr);
                    Log.d(TAG, "Message data k-v [  " + keyStr + " : " + msg + " ]");

                    break;
                }
            }
        }


        /**
         * Customer define message type 自行定義通知事件
         * **/

        msg = RemoteMessage.getData().get("notify");
        if (msg != null) {
            Log.d(TAG, "this is notify msg: " + msg);
            sendNotification(msg);
        }


        msg = RemoteMessage.getData().get("json");
        if (msg != null) {
            Log.d(TAG, "this is json msg: " + msg);
            jsonMsg(msg);
        }


    }

    public void sendBroadcast(String type, String message) {
        Intent intent = new Intent(FCM_RESULT);

        if (message != null) {
            intent.putExtra(type, message);
            mBroadcast.sendBroadcast(intent);
        }
    }


    public void sendNotification(final String msg) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Notify")
                .setContentText(msg /*jsonMessage.optString("Title")*/)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setSmallIcon(R.mipmap.ic_bob)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            notificationBuilder.setSmallIcon(R.mipmap.ic_cat);
//        } else {
//            notificationBuilder.setSmallIcon(R.mipmap.icon_minions);
//        }
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify("Notify", 0 /* ID of notification */, notificationBuilder.build());

    }

    public void jsonMsg(String msg) {

//        {
//            "Title":"Json 推播標題",
//            "Content":"這是推播內容"
//         }

        JSONObject jsonMessage;
        try {
            jsonMessage = new JSONObject(msg);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        final String Title = jsonMessage.optString("Title");
        final String Content = jsonMessage.optString("Content");


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(Title)
                .setContentText(Content /*jsonMessage.optString("Title")*/)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setSmallIcon(R.mipmap.ic_bob)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            notificationBuilder.setSmallIcon(R.mipmap.ic_cat);
//        } else {
//            notificationBuilder.setSmallIcon(R.mipmap.icon_minions);
//        }
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify("Notify", 0 /* ID of notification */, notificationBuilder.build());
    }



}
