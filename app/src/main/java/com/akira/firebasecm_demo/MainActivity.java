package com.akira.firebasecm_demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private Button btnLogToken;
    private FirebaseInstanceId firebaseInstanceId;
    private TextView tvMsg, tvOptionsTitle, tvOptionsKey, tvOptionsValue, tvToken;
    private EditText edUserName;
    private CheckBox swNews, swGames,swWeather,swTech;


    /**
     * On initial startup of your app, the FCM SDK generates a registration token for the client app
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseInstanceId = FirebaseInstanceId.getInstance();
        checkPlayServicesAvailable();

        init();
    }

    private void init() {

        tvToken = (TextView) findViewById(R.id.tvToken);
        tvMsg = (TextView) findViewById(R.id.tvMsg);
        tvOptionsTitle = (TextView) findViewById(R.id.tvOptionsTitle);
        tvOptionsKey = (TextView) findViewById(R.id.tvOptionsKey);
        tvOptionsValue = (TextView) findViewById(R.id.tvOptionsValue);
        edUserName = (EditText) findViewById(R.id.edUserName);

        btnLogToken = (Button) findViewById(R.id.btnLogToken);
        btnLogToken.setOnClickListener(listener);

        swNews = (CheckBox) findViewById(R.id.swNews);
        swGames = (CheckBox) findViewById(R.id.swGames);
        swWeather = (CheckBox) findViewById(R.id.swWeather);
        swTech = (CheckBox) findViewById(R.id.swTech);

        swNews.setOnCheckedChangeListener(checkedlistener);
        swGames.setOnCheckedChangeListener(checkedlistener);
        swWeather.setOnCheckedChangeListener(checkedlistener);
        swTech.setOnCheckedChangeListener(checkedlistener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServicesAvailable();
    }


    /**
     * BroadcastReceiver
     **/

// 註冊 Receiver
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");

        /**
         *  從背景點擊 firebase通知，帶回 console 自訂資料 value
         * **/
        String msg = "";
        Intent intent = getIntent();

        //定義 console 自訂資料中用到的 key
        final String[] keyStrArray = {"notify", "json", "key", "msg"};

        for (String keyStr : keyStrArray) {
            msg = intent.getStringExtra(keyStr);

            if (msg != null) {
                Log.d(TAG, "msg: " + msg);
                tvOptionsKey.setText(keyStr);
                tvOptionsValue.setText(msg);
            }
        }

        /**
         * App 在前景時的處理
         * **/
        //LocalBroadcastManager
        IntentFilter filter = new IntentFilter(MyFirebaseMessagingService.FCM_RESULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
    }

    // 收到廣播後要執行的事件
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.d(TAG, "BroadcastReceiver ");

            // [進階選項 - 標題]
            String titleStr =  intent.getStringExtra(MyFirebaseMessagingService.FCM_NOTIFY_TITLE);

            if(titleStr!= null){
                Log.d(TAG, "mTitle: " + titleStr);
                tvOptionsTitle.setText(titleStr);
            }

            // [訊息文字]
            String bodyContent = intent.getStringExtra(MyFirebaseMessagingService.FCM_NOTIFY_BODY);

            if(bodyContent!= null) {
                Log.d(TAG, "mContent: " + bodyContent);
                tvMsg.setText(bodyContent);
            }

            // [自訂資料]
            String msgStr = intent.getStringExtra(MyFirebaseMessagingService.FCM_MSG);

            if(msgStr!= null) {
                Log.d(TAG, "msgStr: " + msgStr);
                decodeJson(msgStr);
            }

        }
    };

    private void decodeJson(final String message){
        JSONObject jsonMessage;
        try {
            jsonMessage = new JSONObject(message);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        final String[] keyStrArray =  {"notify", "json", "key", "msg",};
        for (String keyStr : keyStrArray) {
            final String value = jsonMessage.optString(keyStr);
            if(!value.equals("") && value != null) {
                Log.d(TAG, " key: " + keyStr + "  value: " + value);
                tvOptionsKey.setText(keyStr);
                tvOptionsValue.setText(value);
            }
        }

    }

    // 取消註冊 Receiver
    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }


    public View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnLogToken:
                    checkEmpty();
                    break;
            }
        }
    };


    private void checkEmpty(){
        String mUserName = edUserName.getText().toString().trim();
        if(mUserName.matches("")){
            Toast.makeText(this,"請輸入名稱",Toast.LENGTH_LONG).show();
            return;
        }
        String mToken = firebaseInstanceId.getToken();
        Log.d(TAG, "Token is : " + mToken);
        tvToken.setText(mToken);

        edUserName.setEnabled(false);

        new registToken2DB(mUserName,mToken).main();

    }

    /**
     * 訂閱主題 :
     * subscribe & unsubscribe Topic
     **/

    //region Topic
    public void subscribeTopic(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }

    public void unSubscribeFromTopic(String topic) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    }

    //region Topic Checkbox
    public CompoundButton.OnCheckedChangeListener checkedlistener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {
            switch (compoundButton.getId()) {

                //Topic 的格式 format [a-zA-Z0-9-_.~%]{1,900}

                case R.id.swNews:
                    Log.d(TAG, "訂閱 News: " + ischecked);

                    if (ischecked) {
                        subscribeTopic("news");
                    } else {
                        unSubscribeFromTopic("news");
                    }

                    break;
                case R.id.swGames:
                    Log.d(TAG, "訂閱 Games: " + ischecked);

                    if (ischecked) {
                        subscribeTopic("game");
                    } else {
                        unSubscribeFromTopic("game");
                    }
                    break;

                case R.id.swWeather:
                    Log.d(TAG, "訂閱 氣象: " + ischecked);

                    if (ischecked) {
                        subscribeTopic("Weather");
                    } else {
                        unSubscribeFromTopic("Weather");
                    }
                    break;

                case R.id.swTech:
                    Log.d(TAG, "訂閱 科技: " + ischecked);

                    if (ischecked) {
                        subscribeTopic("Tech");
                    } else {
                        unSubscribeFromTopic("Tech");
                    }
                    break;


            }
        }
    };
    //endregion

//endregion


    /**
     * makeGooglePlayServicesAvailable
     **/
    private void checkPlayServicesAvailable() {
        if (!makeGooglePlayServicesAvailable()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("GooglePlayServices Error")
                    .setMessage("need to update GooglePlayServices");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            });
        }
    }

    private boolean makeGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int result = apiAvailability.isGooglePlayServicesAvailable(this);
        if (result == ConnectionResult.SUCCESS) {
            apiAvailability.makeGooglePlayServicesAvailable(this);
            return true;
        }
        return false;
    }


}
