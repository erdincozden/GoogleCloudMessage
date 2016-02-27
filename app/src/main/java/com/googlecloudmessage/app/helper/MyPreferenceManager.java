package com.googlecloudmessage.app.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.UserDictionary;
import com.googlecloudmessage.app.model.User;

/**
 * Created by erdinc on 2/17/16.
 */
public class MyPreferenceManager {

    private String TAG = MyPreferenceManager.class.getSimpleName();

    SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;

    Context context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME="cloud_message";
    private static final String KEY_USER_ID="user_id";
    private static final String KEY_USER_NAME="user_name";
    private static final String KEY_USER_EMAIL="user_email";
    private static final String KEY_NOTIFICATIONS="notifications";

    public MyPreferenceManager(Context context) {
        this.context = context;
        sharedPreferences=context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor=sharedPreferences.edit();
    }

    public void addNotification(String notification){
        String oldNotification=getNotifications();

        if(oldNotification != null){
            oldNotification +="|"+ notification;
        }else{
            oldNotification=notification;
        }
        editor.putString(KEY_NOTIFICATIONS,oldNotification);
        editor.commit();
    }

    public User getUser(){
        if(sharedPreferences.getString(KEY_USER_ID,null)!=null){
            String id,name,email;
            id=sharedPreferences.getString(KEY_USER_ID,null);
            name=sharedPreferences.getString(KEY_USER_NAME,null);
            email=sharedPreferences.getString(KEY_USER_EMAIL,null);

            User user=new User(id,name,email);
            return  user;
        }
        return null;
    }

    public String getNotifications() {
        return sharedPreferences.getString(KEY_NOTIFICATIONS,null);
    }
    public void clear(){
        editor.clear();
        editor.commit();
    }
}


















