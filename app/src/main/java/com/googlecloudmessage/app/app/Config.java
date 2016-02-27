package com.googlecloudmessage.app.app;

/**
 * Created by erdinc on 2/17/16.
 */
public class Config {

    //single or multiline notification
    public static boolean appendNotificationMessages = true;

    public static final String TOPIC_GLOBAL = "global";

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION="pushNotification";

    public static final int PUSH_TYPE_CHATROOM=1;
    public static final int PUSH_TYPE_USER=2;

    public static final int NOTIFICATION_ID=100;
    public static final int NOTIFICATION_ID_BIG_IMAGE=101;
}
