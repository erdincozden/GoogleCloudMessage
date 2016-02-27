package com.googlecloudmessage.app.gcm;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityManagerCompat;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;
import com.googlecloudmessage.app.R;
import com.googlecloudmessage.app.app.Config;
import com.googlecloudmessage.app.app.MyApplication;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * Created by erdinc on 2/17/16.
 */
public class NotificationUtils {

    private static String TAG = NotificationUtils.class.getSimpleName();

    private Context context;

    public NotificationUtils() {

    }

    public NotificationUtils(Context context) {
        this.context = context;
    }

    public void showNotificationMessage(String title, String message, String timestamp, Intent intent, String imageURL) {
        if (TextUtils.isEmpty(message))
            return;

        final int icon = R.mipmap.ic_launcher;
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent = PendingIntent
                .getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + context.getPackageName() + "raw/notification");
        if (!TextUtils.isEmpty(imageURL)) {
            if (imageURL != null && imageURL.length() > 4 && Patterns.WEB_URL.matcher(imageURL).matches()) {
                Bitmap bitmap = getBitmapFromURL(imageURL);
                if (bitmap != null) {
                    showBigNotification(bitmap, builder, icon, title, message, timestamp, resultPendingIntent, alarmSound);
                } else {
                    showSmallNotification(builder, icon, title, message, timestamp, resultPendingIntent, alarmSound);
                }
            }
        } else {
            showSmallNotification(builder, icon, title, message, timestamp, resultPendingIntent, alarmSound);
            playNotificationSound();
        }

    }

    public void playNotificationSound() {
        Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + MyApplication.getInstance().getApplicationContext().getPackageName() + "/raw/notification");

        Ringtone ringtone = RingtoneManager.getRingtone(MyApplication.getInstance().getApplicationContext(), alarmSound);
        ringtone.play();
    }

    private void showSmallNotification(NotificationCompat.Builder builder, int icon, String title,
                                       String message, String timestamp, PendingIntent resultPendingIntent, Uri alarmSound) {

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        if (Config.appendNotificationMessages) {
            MyApplication.getInstance().getPreferenceManager().addNotification(message);
            String oldNotification = MyApplication.getInstance().getPreferenceManager().getNotifications();

            List<String> messages = Arrays.asList(oldNotification.split("\\|"));
            for (int i = messages.size() - 1; i >= 0; i--) {
                inboxStyle.addLine(messages.get(i));
            }
        } else {
            inboxStyle.addLine(message);
        }

        Notification notification;
        notification = builder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true).setContentTitle(title).setContentIntent(resultPendingIntent)
                .setSound(alarmSound).setStyle(inboxStyle).setWhen(getTimeMiliSec(timestamp))
                .setSmallIcon(R.drawable.ic_notification_small)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                .setContentText(message).build();

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Config.NOTIFICATION_ID_BIG_IMAGE, notification);

    }

    private long getTimeMiliSec(String timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timestamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }

    }

    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder builder, int icon, String title,
                      String message, String timestamp, PendingIntent resultPendingIntent, Uri alarmSound) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);
        Notification notification;
        notification = builder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(bigPictureStyle)
                .setWhen(getTimeMiliSec(timestamp))
                .setSmallIcon(R.drawable.ic_notification_small)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Config.NOTIFICATION_ID_BIG_IMAGE, notification);
    }

    private Bitmap getBitmapFromURL(String imageURL) {

        try {
            URL url = new URL(imageURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream input = connection.getInputStream();
            Bitmap mybitmap = BitmapFactory.decodeStream(input);
            return mybitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void clearNotifications(){
        NotificationManager notificationManager=
                (NotificationManager) MyApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
    public static boolean isAPPInBackGround(Context context){
        boolean isInBackGround=true;
        ActivityManager activityManager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.KITKAT_WATCH){
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos=activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo: runningAppProcessInfos){
                if(processInfo.importance==ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                    for(String activeProcess:processInfo.pkgList){
                        isInBackGround=false;
                    }
                }
            }
        }else{
            List<ActivityManager.RunningTaskInfo> taskInfo=activityManager.getRunningTasks(1);
            ComponentName componentName=taskInfo.get(0).topActivity;
            if(componentName.getPackageName().equals(context.getPackageName())){
                isInBackGround=false;
            }
        }
        return isInBackGround;
    }
}











