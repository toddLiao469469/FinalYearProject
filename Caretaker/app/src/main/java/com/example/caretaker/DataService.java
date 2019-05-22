package com.example.caretaker;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Binder;
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
import java.util.Calendar;

import static android.support.constraint.Constraints.TAG;
import static com.example.caretaker.CodesettingFragment.phonecall_isCheck;

public class DataService extends Service {
    Double getlatitude;
    Double getlongitude;
    String getlocation;
    String getheartbeat;
    ArrayList<String> recordlist = new ArrayList<>();

    private Handler handler_1 = null;
    private HandlerThread handlerThread_1 = null;
    private String handlerThread_1_name = "handlerThread_1_name";
    public String username;


    public class LocalBinder extends Binder {
        DataService getService() {
            return DataService.this;
        }
    }

    private DataService.LocalBinder mLocBin = new LocalBinder();

    @Override
    public IBinder onBind(Intent arg0) {
        return mLocBin;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences setting = getSharedPreferences("user", MODE_PRIVATE);
        this.username = setting.getString("PREF_USERID", "");
        Log.d("DataService", "start");

        handlerThread_1 = new HandlerThread(handlerThread_1_name);
        handlerThread_1.start();
        handler_1 = new Handler(handlerThread_1.getLooper());
        handler_1.post(runnable_1);


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Bluetooth_Data_Receive");
        intentFilter.addAction("Location_Data_Receive");
        intentFilter.addAction("Latitude_Data_Receive");
        intentFilter.addAction("Longitude_Data_Receive");
        registerReceiver(blReceiver, intentFilter);
        return super.onStartCommand(intent, flags, startId);
    }

    private Runnable runnable_1 = new Runnable() {
        @Override
        public void run() {
            //要做的事情寫在這
            recordlist.clear();
            updateData();

            final DatabaseReference record = FirebaseDatabase.getInstance().getReference("Data").child(username).child("record");
            record.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        recordlist.add(ds.getKey());
                        //Log.d("list", ds.getKey());
                    }
                    //Log.d("record.size", String.valueOf(recordlist.size()));
                    if (recordlist.size() > 10) {
                        String delete_record = recordlist.get(0);
                        record.child(delete_record).removeValue();
                        recordlist.remove(0);
                    }
                    record.removeEventListener(this);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            handler_1.postDelayed(this, 60000);

        }
    };

    private void updateData() {

        Log.d("DataServiceGetData", "heartbeat:" + getheartbeat + "latitude:" + getlatitude + " longitude:" + getlongitude + " address:" + getlocation);
        if ((getheartbeat != null) && (getlocation != null) && (getlatitude != null) && (getlongitude != null)) {
            DatabaseReference record = FirebaseDatabase.getInstance().getReference("Data").child(username).child("record");
            DatabaseReference data = FirebaseDatabase.getInstance().getReference("Data").child(username).child("data");
            String time = timefont();
            String keytime = keytimefont();
            Log.d("time", time);
            Log.d("location", getlocation);
            Log.d("heartbeat", getheartbeat);
            data.child("latitude").setValue(getlatitude);
            data.child("longitude").setValue(getlongitude);
            data.child("location").setValue(getlocation);
            data.child("heartbeat").setValue(getheartbeat);
            data.child("time").setValue(time);
            record.child(keytime).child("latitude").setValue(getlatitude);
            record.child(keytime).child("longitude").setValue(getlongitude);
            record.child(keytime).child("location").setValue(getlocation);
            record.child(keytime).child("heartbeat").setValue(getheartbeat);
            record.child(keytime).child("time").setValue(time);
            Log.d("updatedata", keytime);
           Log.d("phonecall_isCheck",phonecall_isCheck.toString());
            if (phonecall_isCheck.equals(true)) {
                if ((Integer.parseInt(getheartbeat) > 100) || Integer.parseInt(getheartbeat) < 50) {  //心跳範圍--打電話給緊急連絡人
                    makePhoneCall();
                }
            }
        } else {
            Log.d("updatedata", "failed");
        }
    }

    private  String number = "0975879856";
    public void makePhoneCall() {
        final DatabaseReference general = FirebaseDatabase.getInstance().getReference("Data").child(username).child("general");
        general.child("emergency").addValueEventListener(new ValueEventListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                number = dataSnapshot.getValue().toString();
                if (number.trim().length() > 0) {
                    String dial = "tel:" + number;
                    Log.d("phonecall", dial+ "  susses");
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(dial));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Context context = getApplicationContext();
                    context.startActivity(intent);
                }
                general.child("emergency").removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                Log.d("BluetoothService", intent.getAction());
                switch (intent.getAction()) {
                    case "Bluetooth_Data_Receive":
                        String heartbeat = intent.getStringExtra("data");
                        if (heartbeat.length() == 3) {
                            getheartbeat = heartbeat;
                            Log.d("gethearbeat", getheartbeat);
                        } else {
                            Log.d("Get heartbeat", " failed");
                        }
                        break;

                    case "Location_Data_Receive":
                        getlocation = intent.getStringExtra("location");

                        break;
                    case "Latitude_Data_Receive":
                        getlatitude = Double.parseDouble(intent.getStringExtra("latitude"));
                        break;
                    case "Longitude_Data_Receive":
                        getlongitude = Double.parseDouble(intent.getStringExtra("longitude"));
                        break;
                }
            }
        }
    };

    public void onDestroy() {
        this.unregisterReceiver(blReceiver);
        super.onDestroy();
        Log.d(TAG, "onDestroy: Service stop");
    }
    private String keytimefont() {
        Calendar mCal;
        mCal = Calendar.getInstance();
        String time;
        String year = String.valueOf(mCal.get(Calendar.YEAR));
        String month = String.valueOf(mCal.get(Calendar.MONTH) + 1);
        String day = String.valueOf(mCal.get(Calendar.DAY_OF_MONTH));
        String hour = String.valueOf(mCal.get(Calendar.HOUR_OF_DAY));
        String min = String.valueOf(mCal.get(Calendar.MINUTE));
        if (month.length() == 1) {
            month = '0' + month;
        }
        if (day.length() == 1) {
            day = '0' + day;
        }
        if (hour.length() == 1) {
            hour = '0' + hour;
        }
        if (min.length() == 1) {
            min = '0' + min;
        }
        time = year + month + day + hour + min;
        return time;
    }

    private String timefont() {
        Calendar mCal;
        mCal = Calendar.getInstance();
        String time;
        String year = String.valueOf(mCal.get(Calendar.YEAR));
        String month = String.valueOf(mCal.get(Calendar.MONTH) + 1);
        String day = String.valueOf(mCal.get(Calendar.DAY_OF_MONTH));
        String hour = String.valueOf(mCal.get(Calendar.HOUR_OF_DAY));
        String min = String.valueOf(mCal.get(Calendar.MINUTE));
        if (month.length() == 1) {
            month = '0' + month;
        }
        if (day.length() == 1) {
            day = '0' + day;
        }
        if (hour.length() == 1) {
            hour = '0' + hour;
        }
        if (min.length() == 1) {
            min = '0' + min;
        }
        time = year + "/" + month + "/" + day + " " + hour + ":" + min;
        return time;
    }

}
