package com.example.caretaker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class Regist3Activity extends AppCompatActivity {
    private IntentIntegrator scanIntegrator;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist3);


        final Button button_A = (Button) findViewById(R.id.button1);
        final Button button_B = (Button) findViewById(R.id.button2);
        final Button button_S = (Button) findViewById(R.id.button3);
        final Intent intent1 = new Intent(Regist3Activity.this, CareActivity.class);
        final Intent intent2 = new Intent(Regist3Activity.this, QRcodeActivity.class);
        SharedPreferences setting = getSharedPreferences("user", MODE_PRIVATE);
        username = setting.getString("PREF_USERID", "");

        button_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanIntegrator = new IntentIntegrator(Regist3Activity.this);
                scanIntegrator.setPrompt("請掃描你想要監護人的QR code");
                scanIntegrator.setTimeout(300000);
                scanIntegrator.setOrientationLocked(false);
                scanIntegrator.initiateScan();
            }
        });


        button_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(intent2);
            }
        });
        button_S.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(intent1);
            }
        });


    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            if (scanningResult.getContents() != null) {

                final Bundle bundle1 = new Bundle();
                final Intent intent1 = new Intent(Regist3Activity.this, CareActivity.class);
                String scanContent = scanningResult.getContents();
                final DatabaseReference getuser = FirebaseDatabase.getInstance().getReference("User").child(username).child(scanContent);
                getuser.setValue(1);
                startActivity(intent1);

                if (!scanContent.equals("")) {
                    Toast.makeText(getApplicationContext(), "掃描內容: " + scanContent, Toast.LENGTH_LONG).show();
                    Log.d("QQ", scanContent);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
            Toast.makeText(getApplicationContext(), "發生錯誤", Toast.LENGTH_LONG).show();
        }
    }
}
