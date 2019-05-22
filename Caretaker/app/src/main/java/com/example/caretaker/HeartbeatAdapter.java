package com.example.caretaker;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HeartbeatAdapter extends BaseAdapter {
    Context context;
    private String username;
    private String user;
    private LayoutInflater layoutInflater;

    ArrayList<String> recordlist = new ArrayList<>();
    ArrayList<String> heartbeatlist = new ArrayList<>();
    ArrayList<String> timelist = new ArrayList<>();

    public HeartbeatAdapter(Context context, String username, String user) {
        super();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.username = username;
        this.user = user;
        RequestHeader();
    }

    private void update(String heartbeat, String time) {
        if (heartbeat != null) {
            heartbeatlist.add(heartbeat);
        }
        if (time != null) {
            timelist.add(time);
        }


        if (heartbeatlist.size() == timelist.size()) {
            Log.d("heartbeatsize", "" + heartbeatlist.size());
            Log.d("timesize", "" + timelist.size());
            notifyDataSetChanged();
        }


    }

    public void RequestHeader() {
        recordlist.clear();

        final DatabaseReference Record = FirebaseDatabase.getInstance().getReference("Data").child(user).child("record");
        try{
            Record.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        recordlist.add(ds.getKey());
                        Record.removeEventListener(this);
                    }
                    for (String i : recordlist) {
                        Log.d("Recordlist", i);
                        final DatabaseReference heartbeat = FirebaseDatabase.getInstance().getReference("Data").child(user).child("record").child(i).child("heartbeat");
                        final DatabaseReference time = FirebaseDatabase.getInstance().getReference("Data").child(user).child("record").child(i).child("time");
                        heartbeat.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.d("heartbeat", dataSnapshot.getValue().toString());
                                update(dataSnapshot.getValue().toString(), null);
                                heartbeat.removeEventListener(this);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        time.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.d("time", dataSnapshot.getValue().toString());
                                update(null, dataSnapshot.getValue().toString());
                                time.removeEventListener(this);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return recordlist.size();
    }

    @Override
    public Object getItem(int position) {
        return recordlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = layoutInflater.inflate(R.layout.style_location_list, parent, false);
        TextView textView = v.findViewById(R.id.textView);
        TextView textView2 = v.findViewById(R.id.textView2);
        try {
            textView.setTextColor(Color.WHITE);
            textView2.setTextColor(Color.WHITE);
            textView.setText(timelist.get(position));
            textView2.setText("                "+heartbeatlist.get(position));
        }catch (Exception e){
            e.printStackTrace();
        }
        return v;
    }
}
