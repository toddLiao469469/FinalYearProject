package com.example.caretaker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;
import static com.example.caretaker.CodesettingFragment.notificationSwitch_isCheck;

public class NotificationService extends Service {

    String username;
    private Handler handler_notification = null;
    private HandlerThread handlerThread_notification = null;
    private String handlerThread_notification_name = "handlerThread_notification_name";


    ArrayList<String> userlist = new ArrayList<>();

    public class LocalBinder extends Binder {
        NotificationService getService() {
            return NotificationService.this;
        }
    }

    private NotificationService.LocalBinder mLocBin = new NotificationService.LocalBinder();

    @Override
    public IBinder onBind(Intent arg0) {
        return mLocBin;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

            handlerThread_notification = new HandlerThread(handlerThread_notification_name);
            handlerThread_notification.start();
            handler_notification = new Handler(handlerThread_notification.getLooper());
            Log.d("NotificationService", "start");
            handler_notification.post(runnable_notification);
            final SharedPreferences setting = getSharedPreferences("user", MODE_PRIVATE);
            this.username = setting.getString("PREF_USERID", "");



        return START_NOT_STICKY;
    }

    private Runnable runnable_notification = new Runnable() {
        @Override
        public void run() {
            //要做的事情寫在這
            userlist.clear();
            final DatabaseReference User = FirebaseDatabase.getInstance().getReference("User").child(username);
            User.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        userlist.add(ds.getKey());
                        Log.d("userlist", ds.getKey());
                    }
                    for (final String i : userlist) {
                        final DatabaseReference user = FirebaseDatabase.getInstance().getReference("Data").child(i);
                        user.child("data").child("heartbeat").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if ((Integer.parseInt(dataSnapshot.getValue().toString()) > 100) || (Integer.parseInt(dataSnapshot.getValue().toString()) < 50)) {//心跳範圍--推播
                                    Log.d("notification_isCheck", notificationSwitch_isCheck.toString());
                                    if (notificationSwitch_isCheck.equals(true)) {
                                        PushNotification("CareTaker", "您的家人" + i + "最近的心跳是" + dataSnapshot.getValue().toString() + "\n" + "快去關心他吧!!!");
                                    }
                                }
                                user.child("data").child("heartbeat").removeEventListener(this);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    User.removeEventListener(this);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            handler_notification.postDelayed(this, 60000);

        }
    };

    @Override
    public void onDestroy() {
        handlerThread_notification.quit();
        super.onDestroy();
        Log.d(TAG, "onDestroy: Service stop");
    }

    public static int Notify_ID = 1;

    private void PushNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            Notification.Builder builder = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.heart)
                    .setWhen(System.currentTimeMillis())
                    .setShowWhen(true)
                    .setAutoCancel(false)
                    .setVibrate(new long[]{500, 700, 100, 100, 100, 100})
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setLights(Color.YELLOW, 1000, 1000)
                    .setContentTitle(title)
                    .setStyle(new Notification.BigTextStyle().bigText(body))
                    .setContentText("您有一則新訊息");
            Intent intent = new Intent(this, CareActivity.class);
            Bundle bundle = new Bundle();
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pIntent = PendingIntent.getActivity(this, 1, intent, 0);
            builder.setContentIntent(pIntent);         //这句是重点
            builder.setFullScreenIntent(pIntent, true);
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel channelName = new NotificationChannel(
                        "name",
                        "Channel name",
                        NotificationManager.IMPORTANCE_HIGH);
                channelName.setDescription("For name");
                channelName.enableLights(true);
                channelName.enableVibration(true);
                notificationManager.createNotificationChannel(channelName);
                builder.setChannelId("name");
            }
            notificationManager.notify(Notify_ID++, builder.build());
        }
    }
}
