package com.example.caretaker;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
public class UserFragment extends Fragment {

    Bundle bundle;
    String username;
    String name;
    String phone;
    String height;
    String weight;
    String address;
    String imageString;
    String phone2;
    private ImageView imageView;
    private TextView name_txt = null;
    private TextView phone_txt = null;
    private TextView height_txt = null;
    private TextView weight_txt = null;
    private TextView address_txt = null;
    private TextView phone2_txt = null;

    public UserFragment() {
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
        View v = inflater.inflate(R.layout.fragment_user, container, false);

        imageView = v.findViewById(R.id.imageView3);
        name_txt = v.findViewById(R.id.textView);
        phone_txt = v.findViewById(R.id.textView2);
        height_txt = v.findViewById(R.id.textView3);
        weight_txt = v.findViewById(R.id.textView4);
        address_txt = v.findViewById(R.id.textView5);
        phone2_txt = v.findViewById(R.id.textView13);

        Button button = v.findViewById(R.id.button5);
        username = bundle.getString("username");
        Log.d("username", username);
        final DatabaseReference general = FirebaseDatabase.getInstance().getReference("Data").child(username).child("general");

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
                name = String.valueOf(dataSnapshot.getValue());
                name_txt.setText(name);
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
                phone_txt.setText(phone);
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
                height_txt.setText(height);
                general.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        general.child("weight").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                weight = dataSnapshot.getValue().toString();
                weight_txt.setText(weight);
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
                address_txt.setText(address);
                general.child("address").removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        general.child("emergency").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                phone2 = dataSnapshot.getValue().toString();
                Log.d("emergency", phone2);
                phone2_txt.setText(phone2);
                general.child("emergency").removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment editFragment = new EditFragment();
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                editFragment.setArguments(bundle);
                ft.add(R.id.fragment, editFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        return v;
    }

}
