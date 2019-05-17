package com.example.caretaker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class CareActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String username;
    private static OneExpandAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("看護資料");
        setSupportActionBar(toolbar);

        SharedPreferences setting = getSharedPreferences("user" , MODE_PRIVATE);
        username =setting.getString("PREF_USERID" , "");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.inflateHeaderView(R.layout.nav_header_care);
        final TextView textView = (TextView) header.findViewById(R.id.textView);
        final CircleImageView imageView = (CircleImageView) header.findViewById(R.id.button2);



        if (username != null) {
            final DatabaseReference general = FirebaseDatabase.getInstance().getReference("Data").child(username).child("general");
            general.child("name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    textView.setText(dataSnapshot.getValue().toString());
                    general.child("name").removeEventListener(this);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            general.child("image").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue().toString() != null) {
                        byte[] decodedString = Base64.decode(dataSnapshot.getValue().toString(), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        imageView.setImageBitmap(decodedByte);
                        general.child("image").removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        navigationView.setNavigationItemSelectedListener(this);

        final ListView listView = (ListView) findViewById(R.id.list);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter = new OneExpandAdapter(getApplicationContext(), username);
                listView.setAdapter(adapter);

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        listView.addHeaderView(new View(this));
        listView.addFooterView(new View(this));

        adapter = new OneExpandAdapter(this, username);

        listView.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.care, menu);
        return true;
    }


    @SuppressLint("MissingPermission")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        switch (id) {
            case R.id.general:

                Fragment userFragment = new UserFragment();
                userFragment.setArguments(bundle);
                ft.add(R.id.fragment, userFragment);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.binddevice:
                Fragment binddeviceFragment = new BinddeviceFragment();
                binddeviceFragment.setArguments(bundle);
                ft.add(R.id.fragment, binddeviceFragment);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.caredata:
                Intent intent_care = new Intent(CareActivity.this, CareActivity.class);
                intent_care.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent_care.putExtras(bundle);
                startActivity(intent_care);
                break;
            case R.id.bindfamily:
                Intent intent_family = new Intent(CareActivity.this, Regist3Activity.class);
                intent_family.putExtras(bundle);
                startActivity(intent_family);
                break;
            case R.id.codesetting:
                Fragment codesettingFragment = new CodesettingFragment();
                codesettingFragment.setArguments(bundle);
                ft.add(R.id.fragment,codesettingFragment);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.assesus:
                Fragment assesusFragment = new AssesusFragment();
                assesusFragment.setArguments(bundle);
                ft.add(R.id.fragment,assesusFragment);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.aboutus:

                break;
            case R.id.logout:
                Intent intent_logout = new Intent(CareActivity.this, LoginActivity.class);
                intent_logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_logout);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
