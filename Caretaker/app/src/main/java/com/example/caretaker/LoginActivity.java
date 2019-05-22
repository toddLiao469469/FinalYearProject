package com.example.caretaker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private String username;
    private String password;
    private int usernameCheck = 0;
    private String passwordTrue;

    ArrayList<String> accountlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button login = findViewById(R.id.button1);
        final Button regist = findViewById(R.id.button3);
        final Button third_login = findViewById(R.id.button2);

        final EditText username_edit = (EditText) findViewById(R.id.editText);
        final EditText password_edit = (EditText) findViewById(R.id.editText2);

        final Intent regist_intent = new Intent(LoginActivity.this, Regist1Activity.class);
        final Intent login_intent = new Intent(LoginActivity.this, CareActivity.class);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();


        SharedPreferences setting = getSharedPreferences("user", MODE_PRIVATE);
        username_edit.setText(setting.getString("PREF_USERID", ""));
        password_edit.setText(setting.getString("PREF_Password", ""));

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = username_edit.getText().toString();
                password = password_edit.getText().toString();

                SharedPreferences setting = getSharedPreferences("user", MODE_PRIVATE);
                setting.edit()
                        .putString("PREF_USERID", username)
                        .putString("PREF_Password", password)
                        .apply();

                final DatabaseReference account = database.getReference("Account");


                //final DatabaseReference create_data = FirebaseDatabase.getInstance().getReference("Data");
//                create_data.child("hef111").child("data").child("heartbeat").setValue("1");
//                create_data.child("hef111").child("data").child("time").setValue("1");
//                create_data.child("hef111").child("data").child("latitude").setValue("1");
//                create_data.child("hef111").child("data").child("longitude").setValue("1");
//                create_data.child("hef111").child("data").child("location").setValue("1");

                account.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            accountlist.add(ds.getKey());
                            Log.d("list", ds.getKey());
                        }
                        checkAccount(account);
                        account.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            private void checkAccount(final DatabaseReference account) {
                int usernameCheck = 0;
                if (username.length() > 0) {
                    for (int i = 0; i < accountlist.size(); i++) {
                        if (accountlist.get(i).equals(username)) {
                            usernameCheck =1;
                        }
                    }
                    if(usernameCheck ==1){
                        account.child(username).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                passwordTrue = dataSnapshot.getValue().toString();
                                if (password.equals(passwordTrue)) {
                                    Toast.makeText(LoginActivity.this, "登入成功", Toast.LENGTH_SHORT).show();
                                    startActivity(login_intent);
                                }
                                else{
                                    Toast.makeText(LoginActivity.this, "請輸入正確的密碼", Toast.LENGTH_SHORT).show();
                                }
                                account.child(username).removeEventListener(this);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }else{
                        Toast.makeText(LoginActivity.this, "請輸入正確的帳號", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(LoginActivity.this, "請輸入帳號與密碼", Toast.LENGTH_SHORT).show();
                }
            }
        });


        third_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "尚未開放", Toast.LENGTH_SHORT).show();
            }
        });
        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(regist_intent);
            }
        });

    }
}
