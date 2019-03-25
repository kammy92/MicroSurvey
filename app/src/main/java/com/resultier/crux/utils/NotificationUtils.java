package com.resultier.crux.utils;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Patterns;

import com.resultier.crux.R;
import com.resultier.crux.activity.MainActivity;
import com.resultier.crux.models.Notification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class NotificationUtils {
    private static String TAG = NotificationUtils.class.getSimpleName ();
    int notification_id = new Random ().nextInt ((200 - 100) + 1) + 100;
    private Context mContext;
    
    public NotificationUtils (Context mContext) {
        this.mContext = mContext;
    }
    
    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground (Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService (Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses ();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals (context.getPackageName ())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks (1);
            ComponentName componentInfo = taskInfo.get (0).topActivity;
            if (componentInfo.getPackageName ().equals (context.getPackageName ())) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }
    
    // Clears notification tray messages
    public static void clearNotifications (Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll ();
    }
    
    public static long getTimeMilliSec (String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse (timeStamp);
            return date.getTime ();
        } catch (ParseException e) {
            e.printStackTrace ();
        }
        return 0;
    }
    
    public void showNotificationMessage (Notification notification) {
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder (mContext);
        final Uri alarmSound = Uri.parse (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName () + "/raw/notification");
        
        //Check for empty push message
        PendingIntent pendingIntent = null;
        PendingIntent pendingIntent2 = null;
        if (TextUtils.isEmpty (notification.getMessage ()))
            return;
        
        Bitmap largeIcon = BitmapFactory.decodeResource (mContext.getResources (), R.mipmap.ic_launcher);
    
        Intent intent2 = new Intent (mContext, MainActivity.class);
        intent2.putExtra (AppConfigTags.NOTIFICATION_TYPE, notification.getNotification_type ());
        intent2.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent2 = PendingIntent.getActivity (mContext, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        
        switch (notification.getNotification_type ()) {
            case 1:
                break;
            case 996:
                //Simple Test Notification
                try {
                    JSONObject jsonObject = notification.getPayload ();
                    mBuilder
//                            .setSmallIcon (R.drawable.ic_notification)
                            .setContentTitle (notification.getTitle ())
                            .setPriority (notification.getNotification_priority ())
                            .setLargeIcon (largeIcon)
                            .setAutoCancel (true)
                            .setContentIntent (pendingIntent2)
                            .setContentText (notification.getMessage ());
                    
                    if (jsonObject.getString (AppConfigTags.NOTIFICATION_SUB_TEXT).length () > 0) {
                        mBuilder.setSubText (jsonObject.getString (AppConfigTags.NOTIFICATION_SUB_TEXT));
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace ();
                }
                
                break;
            case 997:
                //Big Text Test Notification
                try {
                    JSONObject jsonObject = notification.getPayload ();
                    NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle ();
                    bigText.bigText (notification.getMessage ());
                    bigText.setBigContentTitle (notification.getTitle ());
                    
                    mBuilder
                            .setContentTitle (notification.getTitle ())
//                            .setSmallIcon (R.drawable.ic_notification)
                            .setPriority (notification.getNotification_priority ())
                            .setContentText (notification.getMessage ())
                            .setAutoCancel (true)
                            .setContentIntent (pendingIntent2)
                            .setLargeIcon (largeIcon);
                    
                    if (jsonObject.getString (AppConfigTags.NOTIFICATION_SUB_TEXT).length () > 0) {
                        bigText.setSummaryText (jsonObject.getString (AppConfigTags.NOTIFICATION_SUB_TEXT));
                        mBuilder.setSubText (jsonObject.getString (AppConfigTags.NOTIFICATION_SUB_TEXT));
                    }
                    mBuilder.setStyle (bigText);
                } catch (Exception e) {
                    e.printStackTrace ();
                }
                break;
            case 998:
                //Inbox Style Test Notification
                try {
                    JSONObject jsonObject = notification.getPayload ();
                    mBuilder.setContentTitle (notification.getTitle ())
//                            .setSmallIcon (R.drawable.ic_notification)
                            .setContentText (notification.getMessage ())
                            .setLargeIcon (largeIcon)
                            .setPriority (notification.getNotification_priority ())
                            .setAutoCancel (true)
                            .setContentIntent (pendingIntent2)
                            .build ();
                    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle ();
                    JSONArray jsonArray = jsonObject.getJSONArray (AppConfigTags.NOTIFICATION_LINES);
                    for (int i = 0; i < jsonArray.length (); i++) {
                        inboxStyle.addLine (jsonArray.getString (i));
                    }
                    mBuilder.setStyle (inboxStyle);
                    if (jsonObject.getString (AppConfigTags.NOTIFICATION_SUB_TEXT).length () > 0) {
                        mBuilder.setSubText (jsonObject.getString (AppConfigTags.NOTIFICATION_SUB_TEXT));
                    }
                } catch (Exception e) {
                    e.printStackTrace ();
                }
                break;
            case 999:
                try {
                    JSONObject jsonObject = notification.getPayload ();
                    //Big Picture Test Notification
                    Bitmap bitmap = null;
                    if (notification.getImage_url () != null && notification.getImage_url ().length () > 4 && Patterns.WEB_URL.matcher (notification.getImage_url ()).matches ()) {
                        bitmap = getBitmapFromURL (notification.getImage_url ());
                    } else {
                        bitmap = getBitmapFromURL ("http://bloodondemand.com/img/02.jpg");
                    }
                    NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle ();
                    bigPictureStyle.setBigContentTitle (notification.getTitle ());
                    bigPictureStyle.setSummaryText (notification.getMessage ());
                    bigPictureStyle.bigPicture (bitmap);
                    
                    mBuilder
//                            .setSmallIcon (R.drawable.ic_notification)
                            .setContentTitle (notification.getTitle ())
                            .setContentText (notification.getMessage ())
                            .setLargeIcon (largeIcon)
                            .setContentIntent (pendingIntent2)
                            .setAutoCancel (true)
                            .setPriority (jsonObject.getInt (AppConfigTags.NOTIFICATION_PRIORITY));
                    
                    if (jsonObject.getString (AppConfigTags.NOTIFICATION_SUB_TEXT).length () > 0) {
//                        bigPictureStyle.setSummaryText (jsonObject.getString (AppConfigTags.NOTIFICATION_SUB_TEXT));
                        mBuilder.setSubText (jsonObject.getString (AppConfigTags.NOTIFICATION_SUB_TEXT));
                    }
                    
                    mBuilder.setStyle (bigPictureStyle);
                } catch (JSONException e) {
                    e.printStackTrace ();
                }
                break;
    
            default:
                try {
                    JSONObject jsonObject = notification.getPayload ();
                    mBuilder
                            .setSmallIcon (R.drawable.ic_notification)
                            .setContentTitle (notification.getTitle ())
                            .setPriority (notification.getNotification_priority ())
                            .setLargeIcon (largeIcon)
                            .setAutoCancel (true)
                            .setContentIntent (pendingIntent2)
                            .setContentText (notification.getMessage ());
            
                    if (jsonObject.getString (AppConfigTags.NOTIFICATION_SUB_TEXT).length () > 0) {
                        mBuilder.setSubText (jsonObject.getString (AppConfigTags.NOTIFICATION_SUB_TEXT));
                    }
            
                } catch (Exception e) {
                    e.printStackTrace ();
                }
        
                break;
        }
        
        
        NotificationManager mNotifyManager = (NotificationManager) mContext.getSystemService (Context.NOTIFICATION_SERVICE);
        mNotifyManager.notify (notification_id, mBuilder.build ());
    }
    
    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    public Bitmap getBitmapFromURL (String strURL) {
        try {
            URL url = new URL (strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection ();
            connection.setDoInput (true);
            connection.connect ();
            InputStream input = connection.getInputStream ();
            Bitmap myBitmap = BitmapFactory.decodeStream (input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace ();
            return null;
        }
    }
    
    // Playing notification sound
    public void playNotificationSound () {
        try {
            Uri alarmSound = Uri.parse (ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName () + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone (mContext, alarmSound);
            r.play ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}


/*
                switch (notification.getNotification_style ()) {
                    case 1: // Simple Notification
                        mBuilder
                                .setSmallIcon (R.drawable.ic_logo)
                                .setContentTitle (notification.getTitle ())
                                .setPriority (notification.getNotification_priority ())
                                .setContentText (notification.getMessage ());
                        break;
                    case 2: // Inbox Style Notification
                        mBuilder.setContentTitle (notification.getMessage ())
                                .setSmallIcon (R.drawable.ic_logo)
                                .setContentText ("A request for blood has been generated")
                                .setSubText ("Let's donate some blood. Are you free?")
                                .setUsesChronometer (true)
                                .setShowWhen (true)
                                .setStyle (new NotificationCompat.InboxStyle ()
                                        .addLine ("Hi"))
                                .setPriority (notification.getNotification_priority ())
                                .setDefaults (Notification.DEFAULT_VIBRATE)
                                .setColor (mContext.getResources ().getColor (R.color.text_color_black))
//                .setSmallIcon (R.drawable.ic_donate)
                                .build ();
                        break;
                    case 3: // Big Text Style Notification
                        Bitmap icon1 = BitmapFactory.decodeResource (mContext.getResources (), R.mipmap.ic_launcher);
                        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle ();
                        bigText.bigText ("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.");
                        bigText.setBigContentTitle ("Big Text Notification");
                        bigText.setSummaryText ("By: Author of Lorem ipsum");
                        
                        mBuilder.setSmallIcon (R.mipmap.ic_launcher)
                                .setContentTitle ("Big Text notification")
                                .setContentText ("This is test of big text style notification.")
                                .setLargeIcon (icon1)
                                .setStyle (bigText);
                        break;
                    case 4: // Big Picture Notification
                        try {
                            JSONObject jsonObject = notification.getPayload ();
                            Intent notificationIntent = new Intent (mContext, MainActivity.class);
                            notificationIntent.putExtra (AppConfigTags.EVENT_ID, jsonObject.getInt (AppConfigTags.EVENT_ID));
                            notificationIntent.putExtra ("type", jsonObject.getString ("type"));
                            notificationIntent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            pendingIntent = PendingIntent.getActivity (mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            Bitmap bitmap = null;
                            if (notification.getImage_url () != null && notification.getImage_url ().length () > 4 && Patterns.WEB_URL.matcher (notification.getImage_url ()).matches ()) {
                                bitmap = getBitmapFromURL (notification.getImage_url ());
                            } else {
                                bitmap = getBitmapFromURL ("http://bloodondemand.com/img/02.jpg");
                            }
                            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle ();

//                        bigPictureStyle.setSummaryText ("");
//                        bigPictureStyle.setBigContentTitle ("");
//                        bigPictureStyle.bigLargeIcon ();
                            bigPictureStyle.bigPicture (bitmap);
                            
                            mBuilder
                                    .setSmallIcon (R.drawable.ic_logo)
                                    .setContentTitle (notification.getTitle ())
                                    .setContentText (notification.getMessage ())
                                    // .setContentText ("This is test of big picture notification.")
                                    .setStyle (bigPictureStyle)
                                    .setAutoCancel (true)
                                    .setContentIntent (pendingIntent);
                            
                        } catch (JSONException e) {
                            e.printStackTrace ();
                        }
                        
                        break;
                    default:
                        break;
                }
 
*/