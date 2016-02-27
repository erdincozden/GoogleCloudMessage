package com.googlecloudmessage.app.gcm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;
import com.googlecloudmessage.app.app.Config;
import com.googlecloudmessage.app.app.MyApplication;
import com.googlecloudmessage.app.model.Message;
import com.googlecloudmessage.app.model.User;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by erdinc on 2/18/16.
 */
public class MyGcmPushReceiver extends GcmListenerService {

    private static final String TAG = MyGcmPushReceiver.class.getSimpleName();
    private NotificationUtils notificationUtils;


    @Override
    public void onMessageReceived(String from, Bundle bundle) {
        String title = bundle.getString("title");
        Boolean isBackGround = Boolean.valueOf(bundle.getBoolean("is_background"));
        String flag = bundle.getString("flag");
        String data = bundle.getString("data");
        Log.d(TAG, "From:" + from + ",title" + title + ",background:" + isBackGround + ",flag:" + flag + ",data:" + data);

        if (flag == null)
            return;

        if (MyApplication.getInstance().getPreferenceManager().getUser() == null) {
            Log.e(TAG, "user not login,skipping push notification");
            return;
        }

        if (from.startsWith("/topics/")) {
            //from message
        }

        switch (Integer.parseInt(flag)) {
            case Config.PUSH_TYPE_CHATROOM:
                processChatRoomPush(title, isBackGround, data);
                break;
            case Config.PUSH_TYPE_USER:
                processUserMessage(title, isBackGround, data);
                break;
        }

    }

    private void processUserMessage(String title, Boolean isBackGround, String data) {
    }

    private void processChatRoomPush(String title, Boolean isBackGround, String data) {

        if (!isBackGround) {

            try {
                JSONObject datObj=new JSONObject(data);
                String chatRoomID=datObj.getString("chat_room_id");
                JSONObject mObj=datObj.getJSONObject("message");

                Message message=new Message();
                message.setMessage(mObj.getString("message"));
                message.setId(mObj.getString("message_id"));
                message.setCreatedAt(mObj.getString("created_at"));

                JSONObject uObj=datObj.getJSONObject("user");
                if(uObj.getString("user_id").equals(MyApplication.getInstance().getPreferenceManager().getUser().getId())){
                    Log.e(TAG,"Skipping the push message as it belongs to some user");
                    return;
                }
                User user=new User();
                user.setId(uObj.getString("user_id"));
                user.setEmail(uObj.getString("email"));
                user.setName(uObj.getString("name"));
                message.setUser(user);

                if(!NotificationUtils.isAPPInBackGround(getApplicationContext())){
                    Intent pushNotification=new Intent(Config.PUSH_NOTIFICATION);
                    pushNotification.putExtra("type",Config.PUSH_TYPE_CHATROOM);
                    pushNotification.putExtra("message",message);
                    pushNotification.putExtra("chat_room_id",chatRoomID);

                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    NotificationUtils notificationUtils=new NotificationUtils();
                    notificationUtils.playNotificationSound();
                }else{
                    Intent resultIntent=new Intent(getApplicationContext(),ChatRoomActivity.class);
                    resultIntent.putExtra("chat_room_id",chatRoomID);
                    showNotificationMessage(getApplicationContext(),title,user.getName()+" : "+message.getMessage())
                }




            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}























