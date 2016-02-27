package com.googlecloudmessage.app.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.appdatasearch.GetRecentContextCall;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.googlecloudmessage.app.R;
import com.googlecloudmessage.app.app.Config;
import com.googlecloudmessage.app.app.EndPoints;
import com.googlecloudmessage.app.app.MyApplication;
import com.googlecloudmessage.app.model.User;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by erdinc on 2/17/16.
 */
public class GcmIntentService extends IntentService {

    private static final String TAG=GcmIntentService.class.getSimpleName();

    public GcmIntentService() {
        super(TAG);
    }

    public static final String KEY="key";
    public static final String TOPIC="topic";
    public static final String SUBSCRIBE="subscribe";
    public static final String UNSUBSCRIBE="unsubscribe";

    @Override
    protected void onHandleIntent(Intent intent) {
        String key=intent.getStringExtra(KEY);
        if (key.equals(SUBSCRIBE)) {
            String topic = intent.getStringExtra(TOPIC);
            subscribeToTopic(topic);

        } else if (key.equals(UNSUBSCRIBE)) {
        } else {
            registerGCM();
        }
    }

    private void registerGCM() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        InstanceID instanceID=InstanceID.getInstance(this);
        try {
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE,null);
            Log.e(TAG,"GCM SERVICE TOKEN:"+token);
            sendRegistrationServer(token);
            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER,true).apply();
        } catch (IOException e) {
            Log.e(TAG,"Failed to complete token refresh!",e);
            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER,false).apply();
        }
        Intent registrationComplete=new Intent(Config.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

    }

    private void sendRegistrationServer(final String token) {
        User user= MyApplication.getInstance().getPreferenceManager().getUser();
        if (user ==null){
            return;
        }

        String endPoint= EndPoints.USER.replace("_ID_",user.getId());
        Log.e(TAG,"endpoint:"+endPoint);

        StringRequest strReq = new StringRequest(Request.Method.PUT,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {
                        // broadcasting token sent to server
                        Intent registrationComplete = new Intent(Config.SENT_TOKEN_TO_SERVER);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(registrationComplete);
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to send gcm registration id to our sever. " + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("gcm_registration_id", token);

                Log.e(TAG, "params: " + params.toString());
                return params;
            }
        };
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    private void subscribeToTopic(String topic) {
        GcmPubSub pubSub=GcmPubSub.getInstance(MyApplication.getInstance().getApplicationContext());
        InstanceID instanceID=InstanceID.getInstance(MyApplication.getInstance().getApplicationContext());
        String token=null;

        try {
            token=instanceID.getToken(MyApplication.getInstance().getApplicationContext().getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE,null);
            if(token!=null){
                pubSub.subscribe(token,"/token/"+topic,null);
                Log.e(TAG,"Subscribed to topic:"+topic);
            }else{
                Log.e(TAG,"error:gcm registarion id is null");
            }

        } catch (IOException e) {
            Log.e(TAG,"Topic subscribe error.Topic:"+topic+",error:"+e.getMessage());
            Toast.makeText(MyApplication.getInstance().getApplicationContext(),"Topic registarion error"+topic
            +" error:"+e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }
}

















