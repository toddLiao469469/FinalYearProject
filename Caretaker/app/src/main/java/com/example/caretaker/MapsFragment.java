package com.example.caretaker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsFragment extends Fragment{
    private GoogleMap googleMap;
    Bundle bundle;
    double latitude = 0;
    double longitude = 0;
    String location;
    String time;
    String username;
    String user;
    String name;
    MapView mMapView;
    private static final int LOCATION_REQUEST_CODE =1;
    @Override
    public void setArguments(@Nullable Bundle args) {
        this.bundle = args;
    }


    @Override
    public void onDestroyView() {
        ((AppCompatActivity)getContext()).getSupportActionBar().setTitle("看護資料");
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        latitude = bundle.getDouble("latitude");
        longitude = bundle.getDouble("longitude");
        location = bundle.getString("location");
        time = bundle.getString("time");
        username = bundle.getString("username");
        user = bundle.getString("user");
        name = bundle.getString("name");


        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately
        ((AppCompatActivity)getContext()).getSupportActionBar().setTitle(name+"的地點紀錄");
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        final Button  button= (Button)view.findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                Fragment locationfragment = new LocationFragment();
                FragmentManager fm =getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                bundle.putString("username",username);
                bundle.putString("user",user);
                bundle.putString("name",name);
                locationfragment.setArguments(bundle);
                ft.replace(R.id.fragment,locationfragment);
                ft.addToBackStack(null);
                ft.commit();

            }
        });
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

        } else {
            openMap();
        }



        return view;
    }

    private void openMap() {
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(latitude, longitude);

                googleMap.addMarker(new MarkerOptions().position(sydney).title("紀錄時間:"+time).snippet("地址:"+location));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        //抓不到結果
        if (requestCode == LOCATION_REQUEST_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openMap();
            } else {
                // Permission Denied
                Toast.makeText(getActivity(), "目前無法查看地圖", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



}
