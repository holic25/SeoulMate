package kr.co.travelmaker.seoulmate.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.otto.Bus;

import java.util.List;

import kr.co.travelmaker.seoulmate.FirebaseMessageActivity;
import kr.co.travelmaker.seoulmate.LoginActivity;
import kr.co.travelmaker.seoulmate.MainActivity;
import kr.co.travelmaker.seoulmate.R;
import kr.co.travelmaker.seoulmate.bus.BusProvider;
import kr.co.travelmaker.seoulmate.model.Member;

public class MyFirebaseMessagingService extends FirebaseMessagingService{

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN",s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("datalog","onMessageReceived");
        // Check if message contains a data payload.

        SharedPreferences pref = getSharedPreferences("messenger_notification", MODE_PRIVATE);
        Boolean set_notification = pref.getBoolean("set_notification",true);
        Log.d("datalog","onMessageReceived : set_notification = "+set_notification);

        if(set_notification) {
            if (remoteMessage.getData().size() > 0) {
                String title = remoteMessage.getData().get("title").toString();
                String text = remoteMessage.getData().get("text").toString();
                String fromUid = remoteMessage.getData().get("fromUid").toString();
                sendNotification(title,text,fromUid);
            }
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendNotification(String title, String text, String fromUid) {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager
                .getRunningTasks(Integer.MAX_VALUE);
        boolean isActivityFound = false;

        if (services.get(0).topActivity.getPackageName().toString().equalsIgnoreCase(getApplicationContext().getPackageName().toString())) {
            isActivityFound = true;
        }

        Intent intent = null;

        if (isActivityFound) {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("page",1);
        } else {
            intent = new Intent(this, FirebaseMessageActivity.class);
            intent.putExtra("destinationUid",fromUid);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,channelId)
                .setSmallIcon(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP ? R.drawable.ic_notification: R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
