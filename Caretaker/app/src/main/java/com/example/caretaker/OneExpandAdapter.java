package com.example.caretaker;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class OneExpandAdapter extends BaseAdapter {

    private Context context;
    private int currentItem = -1;
    String username;
    String delete_user;
    ArrayList<String> userlist = new ArrayList<>();
    ArrayList<String> imagelist = new ArrayList<>();
    ArrayList<String> namelist = new ArrayList<>();
    ArrayList<String> locationlist = new ArrayList<>();
    ArrayList<String> heartbeatlist = new ArrayList<>();
    ArrayList<String> latitudelist = new ArrayList<>();
    ArrayList<String> longitudelist = new ArrayList<>();
    ArrayList<String> timelist = new ArrayList<>();

    public OneExpandAdapter(Context context, String username) {
        super();
        this.context = context;
        this.username = username;

        requestHeader();
    }


    public void requestHeader() {
        //將User名單塞入list
        final DatabaseReference data = FirebaseDatabase.getInstance().getReference("User").child(username);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    userlist.add(ds.getKey());
                    Log.d("list", ds.getKey());

                }
                requestData();
                data.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getCount() {
        return namelist.size();
    }

    @Override
    public Object getItem(int position) {
        return namelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private void update(String name, String location, String heartbeat) {
        if (name != null) {
            namelist.add(name);
        }
        if (location != null) {
            locationlist.add(location);
        }
        if (heartbeat != null) {
            heartbeatlist.add(heartbeat);
        }
        if (namelist.size() == locationlist.size() && namelist.size() == heartbeatlist.size()) {
            notifyDataSetChanged();
        }
    }

    private void long_lat_update(String longitude, String latitude, String time) {
        if (longitude != null) {
            longitudelist.add(longitude);
        }
        if (latitude != null) {
            latitudelist.add(latitude);
        }
        if (time != null) {
            timelist.add(time);
        }
        if (longitudelist.size() == latitudelist.size() && latitudelist.size() == timelist.size()) {
            notifyDataSetChanged();
        }
    }

    private void requestData() {
        for (final String i : userlist) {
            Log.d("datalist", i);
            final DatabaseReference general = FirebaseDatabase.getInstance().getReference("Data").child(i).child("general");
            final DatabaseReference data = FirebaseDatabase.getInstance().getReference("Data").child(i).child("data");

            general.child("image").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        imagelist.add(dataSnapshot.getValue().toString());
                        general.child("image").removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            general.child("name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {

                        Log.d(i + " name", dataSnapshot.getValue().toString());
                        update((dataSnapshot.getValue().toString()), null, null);
                        general.child("name").removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            data.child("location").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Log.d(i + " location", dataSnapshot.getValue().toString());
                        update(null, dataSnapshot.getValue().toString(), null);
                        data.child("location").removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            data.child("heartbeat").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Log.d(i + " heartbeat", dataSnapshot.getValue().toString());
                        update(null, null, dataSnapshot.getValue().toString());
                        data.child("heartbeat").removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            data.child("longitude").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Log.d(i + " longitude", dataSnapshot.getValue().toString());
                        long_lat_update(dataSnapshot.getValue().toString(), null, null);
                        data.child("longitude").removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            data.child("latitude").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Log.d(i + " latitude", dataSnapshot.getValue().toString());
                        long_lat_update(null, dataSnapshot.getValue().toString(), null);
                        data.child("latitude").removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            data.child("time").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Log.d(i + " time", dataSnapshot.getValue().toString());
                        long_lat_update(null, null, dataSnapshot.getValue().toString());
                        data.child("time").removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Log.d("e04", "幹");
        convertView = LayoutInflater.from(context).inflate(
                R.layout.style_listview, parent, false);
        holder = new ViewHolder();
        holder.showArea = (ConstraintLayout) convertView.findViewById(R.id.layout_showArea);
        holder.hideArea = (ConstraintLayout) convertView.findViewById(R.id.layout_hideArea);
        holder.location = (TextView) convertView.findViewById(R.id.location_line);
        holder.name = (TextView) convertView.findViewById(R.id.name_line);
        holder.heatbeat = (TextView) convertView.findViewById(R.id.heartbeat_line);
        holder.Btn_delete = (Button) convertView.findViewById(R.id.delete);
        holder.Btn_heartBeatRecord = (Button) convertView.findViewById(R.id.heartbeat);
        holder.BtnlocationRecord = (Button) convertView.findViewById(R.id.locationRecord);
        holder.image = (CircleImageView) convertView.findViewById(R.id.button2);
        try {
            convertView.setTag(holder);
            byte[] decodedString = Base64.decode(imagelist.get(position), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.image.setImageBitmap(decodedByte);
            holder.heatbeat.setText((heartbeatlist.get(position)));
            holder.location.setText(locationlist.get(position));
            holder.name.setText(namelist.get(position));

        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.showArea.setTag(position);

        if (currentItem == position) {
            holder.showArea.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600));
            holder.hideArea.setVisibility(View.VISIBLE);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Fragment infoFragment = new InfoFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("user", userlist.get(position));
                    infoFragment.setArguments(bundle);
                    ft.add(R.id.fragment, infoFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
        } else {
            holder.hideArea.setVisibility(View.GONE);
            holder.showArea.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 450));
        }

        holder.showArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tag = (Integer) v.getTag();
                if (tag == currentItem) {
                    currentItem = -1;
                } else {
                    currentItem = tag;
                }
                notifyDataSetChanged();
            }
        });


        //TODO 設定三個btn動作

        holder.BtnlocationRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment mapsFragment = new MapsFragment();
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude", Double.parseDouble(latitudelist.get(position)));
                bundle.putDouble("longitude", Double.parseDouble(longitudelist.get(position)));
                bundle.putString("location", locationlist.get(position));
                bundle.putString("time", timelist.get(position));
                bundle.putString("username", username);
                bundle.putString("user", userlist.get(position));
                bundle.putString("name", namelist.get(position));
                mapsFragment.setArguments(bundle);
                ft.add(R.id.fragment, mapsFragment);
                ft.addToBackStack(null);
                ft.commit();

            }
        });
        holder.Btn_heartBeatRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment heartbeatFragment = new HeartbeatFragment();
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putString("user", userlist.get(position));
                bundle.putString("name", namelist.get(position));
                heartbeatFragment.setArguments(bundle);
                ft.add(R.id.fragment, heartbeatFragment);
                ft.addToBackStack(null);
                ft.commit();

            }
        });

        holder.Btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("警告").setIcon(R.drawable.warning).setMessage("是否刪除 " + namelist.get(position) + " ?")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                delete_user = userlist.get(position);

                                Log.d("position", "" + position);
                                //Log.d("user1", namelist.get(0) + " " + namelist.get(1));
                                if (userlist.size() > 0) {
                                    userlist.remove(position);
                                    imagelist.remove(position);
                                    namelist.remove(position);
                                    locationlist.remove(position);
                                    heartbeatlist.remove(position);
                                    latitudelist.remove(position);
                                    longitudelist.remove(position);
                                    timelist.remove(position);

                                    notifyDataSetChanged();
                                }
                                //Log.d("user2", namelist.get(0) + " ");
                                try {
                                    DatabaseReference delete_data = FirebaseDatabase.getInstance().getReference("User").child(username).child(delete_user);
                                    Log.d("delete", delete_user);
                                    delete_data.removeValue();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog about_dialog = builder.create();
                about_dialog.show();

            }
        });
        return convertView;
    }

    private static class ViewHolder {
        private ConstraintLayout showArea;
        private ConstraintLayout hideArea;

        private TextView name;
        private TextView location;
        private TextView heatbeat;
        private ImageView image;
        private Button BtnlocationRecord;
        private Button Btn_heartBeatRecord;
        private Button Btn_delete;

    }


}