package com.example.caretaker;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {
    Bundle bundle;
    ImageView imageView;
    TextView name_txt;
    TextView phone_txt;
    TextView height_txt;
    TextView weight_txt;
    TextView address_txt;
    Button button;

    String user;
    String name;
    String phone;
    String height;
    String weight;
    String address;
    String imageString;

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void setArguments(Bundle args) {
        this.bundle = args;
    }

    @Override
    public void onDestroyView() {
        ((AppCompatActivity) getContext()).getSupportActionBar().setTitle("看護資料");
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_info, container, false);

        imageView = v.findViewById(R.id.imageView3);
        name_txt = v.findViewById(R.id.textView);
        phone_txt = v.findViewById(R.id.textView2);
        height_txt = v.findViewById(R.id.textView3);
        weight_txt = v.findViewById(R.id.textView4);
        address_txt = v.findViewById(R.id.textView5);
        button = v.findViewById(R.id.button);
        user = bundle.getString("user");

        final DatabaseReference general = FirebaseDatabase.getInstance().getReference("Data").child(user).child("general");

        general.child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageString = dataSnapshot.getValue().toString();
                byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(decodedByte);
                general.child("image").removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        general.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.getValue().toString();
                name_txt.setText("姓名: " + name);
                ((AppCompatActivity) getContext()).getSupportActionBar().setTitle(name + "的個人資料");
                general.child("name").removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        general.child("phone").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                phone = dataSnapshot.getValue().toString();
                phone_txt.setText("手機: " + phone);
                general.child("phone").removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        general.child("height").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                height = dataSnapshot.getValue().toString();
                height_txt.setText("身高: " + height + " cm");
                general.child("height").removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        general.child("weight").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                weight = dataSnapshot.getValue().toString();
                weight_txt.setText("體重: " + weight + " kg");
                general.child("weight").removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        general.child("address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                address = dataSnapshot.getValue().toString();
                address_txt.setText("居住地: " + address);
                general.child("address").removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });

        return v;
    }

    private static final int REQUEST_CALL = 1;

    public void makePhoneCall() {
        String number = phone;
        if (number.trim().length() > 0) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + number;

                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        } else {
            Log.d("PhoneCallPermission", "is failed");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Log.d("PhoneCall", "is failed");
            }
        }
    }
}
