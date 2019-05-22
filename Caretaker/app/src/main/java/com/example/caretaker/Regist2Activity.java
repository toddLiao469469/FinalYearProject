package com.example.caretaker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Regist2Activity extends AppCompatActivity {

    private String username;
    private String address;
    private String height;
    private String weight;
    private String phone;
    private String phone2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist2);

        final EditText height_edt = (EditText) findViewById(R.id.height);
        final EditText weight_edt = (EditText) findViewById(R.id.weight);
        final EditText address_edt = (EditText) findViewById(R.id.address);
        final EditText phone_edt = findViewById(R.id.phone);
        final EditText phone2_edt = findViewById(R.id.phone2);
        final Button button = (Button) findViewById(R.id.button2);
        SharedPreferences setting = getSharedPreferences("user", MODE_PRIVATE);
        username = setting.getString("PREF_USERID", "");
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference gerenal = database.getReference("Data").child(username).child("general");
        final Intent intent = new Intent(Regist2Activity.this, Regist3Activity.class);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address = address_edt.getText().toString();
                height = height_edt.getText().toString();
                weight = weight_edt.getText().toString();
                phone = phone_edt.getText().toString();
                phone2 = phone2_edt.getText().toString();
                Map<String, Object> userUpdates = new HashMap<>();
                if ((height.equals("")) || (weight.equals("")) || (phone.equals("")) || (phone2.equals("")) || (address.equals(""))) {
                    Toast.makeText(Regist2Activity.this, "請填入完整資訊", Toast.LENGTH_SHORT).show();
                } else {
                    userUpdates.put("height", height);
                    userUpdates.put("weight", weight);
                    userUpdates.put("address", address);
                    userUpdates.put("phone", phone);
                    userUpdates.put("emergency", phone2);
                    gerenal.updateChildren(userUpdates);
                    startActivity(intent);
                }

            }
        });
    }
}
