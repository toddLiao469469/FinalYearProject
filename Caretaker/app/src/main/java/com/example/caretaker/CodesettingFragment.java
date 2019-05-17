package com.example.caretaker;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class CodesettingFragment extends Fragment {
    String username;
    public static Boolean notificationSwitch_isCheck = false;
    public static Boolean phonecall_isCheck = false;
    //phonecall
    private static final int REQUEST_CALL = 1;

    public CodesettingFragment() {
        // Required empty public constructor
    }
    @Override
    public void onDestroyView() {
        ((AppCompatActivity) getContext()).getSupportActionBar().setTitle("看護資料");
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_codesetting, container, false);
        SharedPreferences setting = getActivity().getSharedPreferences("user" , MODE_PRIVATE);
        username =setting.getString("PREF_USERID" , "");
        Log.d("username",username);
        final Switch notificationSwitch = v.findViewById(R.id.switch2);
        final Switch phonecallSwitch = v.findViewById(R.id.switch3);
        if (notificationSwitch_isCheck == true) {
            notificationSwitch.setChecked(true);
        }
        if (phonecall_isCheck == true) {
            phonecallSwitch.setChecked(true);
        }
        notificationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notificationService = new Intent();
                notificationService.setClass(getActivity(), NotificationService.class);
                if (notificationSwitch.isChecked()) {
                    notificationSwitch_isCheck = true;
                    Log.d("Notification","start");
                    getActivity().startService(notificationService);
                } else {
                    notificationSwitch_isCheck = false;
                    getActivity().stopService(notificationService);
                    Log.d("Notification","stop");

                }
            }
        });
        phonecallSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phonecallSwitch.isChecked()) {
                    phoneCallPermission();
                    Log.d("Phonecall","start");
                    phonecall_isCheck = true;
                } else {
                    Log.d("Phonecall","stop");
                    phonecall_isCheck = false;

                }
            }
        });


        return v;
    }


    public void phoneCallPermission() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            Log.d("PhoneCallPermission", "susses");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PhoneCallPermission", "susses");
            } else {
                Log.d("PhoneCallPermission", "is failed");
            }
        }
    }

}
