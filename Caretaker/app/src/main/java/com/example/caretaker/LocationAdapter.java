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

public class LocationAdapter extends BaseAdapter {
    Context context;
    private String username;
    private String user;
    private LayoutInflater layoutInflater;

    ArrayList<String> recordlist = new ArrayList<>();
    ArrayList<String> locationlist = new ArrayList<>();
    ArrayList<String> timelist = new ArrayList<>();

    public LocationAdapter(Context context, String username, String user) {
        super();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.username = username;
        this.user = user;
        RequestHeader();
    }

    private void update(String location, String time) {
        if (location != null) {
            locationlist.add(location);
        }
        if (time != null) {
            timelist.add(time);
        }


        if (locationlist.size() == timelist.size()) {
            Log.d("locationsize", "" + locationlist.size());
            Log.d("timesize", "" + timelist.size());
            notifyDataSetChanged();
        }


    }

    private void RequestHeader() {
        recordlist.clear();
        final DatabaseReference locationRecord = FirebaseDatabase.getInstance().getReference().child("Data").child(user).child("record");
        locationRecord.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    recordlist.add(ds.getKey());
                    locationRecord.removeEventListener(this);

                }

                for (String i : recordlist) {
                    Log.d("Recordlist", i);

                    final DatabaseReference location = FirebaseDatabase.getInstance().getReference("Data").child(user).child("record").child(i).child("location");
                    final DatabaseReference time = FirebaseDatabase.getInstance().getReference("Data").child(user).child("record").child(i).child("time");
                    location.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d("location", dataSnapshot.getValue().toString());
                            update(dataSnapshot.getValue().toString(), null);
                            location.removeEventListener(this);

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
           textView2.setText(locationlist.get(position));
       }catch (Exception e){
           e.printStackTrace();
       }
        return v;
    }
}
