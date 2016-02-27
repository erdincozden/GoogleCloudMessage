package com.googlecloudmessage.app.gcm;

import android.content.Intent;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by erdinc on 2/18/16.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {

    private static final String TAG=MyInstanceIDListenerService.class.getSimpleName();

    @Override
    public void onTokenRefresh(){
        Log.e(TAG,"onTokenRefresh");
        Intent intent=new Intent(this, GcmListenerService.class);
        startService(intent);
    }
}
