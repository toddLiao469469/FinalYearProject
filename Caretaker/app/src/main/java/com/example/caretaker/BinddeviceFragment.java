package com.example.caretaker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

@SuppressLint("SetTextI18n")
public class BinddeviceFragment extends Fragment {

    public BinddeviceFragment() {

    }

    @Override
    public void onDestroyView() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("看護資料");
        super.onDestroyView();
    }

    private TextView mBluetoothStatus;
    private TextView mReadBuffer;

    private ListView mDevicesListView;
    private Switch bluetoothswitch;
    private static String bluetoothdevice;

    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //這邊是跟arduino 之間的什麼設定，因為這個我自己也沒有很懂，所以我都沒有更動。

    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1;
    // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2;
    // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3;
    // used in bluetooth handler to identify message status


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity) getContext()).getSupportActionBar().setTitle("綁定裝置");
        final View view = inflater.inflate(R.layout.fragment_binddevice, container, false);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction("Bluetooth_Connect_Fail");
        intentFilter.addAction("Bluetooth_Connect_Success");
        intentFilter.addAction("Bluetooth_Data_Receive");
        getActivity().registerReceiver(blReceiver, intentFilter);

        //初始化元件
        mBluetoothStatus = (TextView) view.findViewById(R.id.bluetoothStatus);
        mReadBuffer = (TextView) view.findViewById(R.id.readBuffer);
        bluetoothswitch = (Switch) view.findViewById(R.id.switch1);
        mBTArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                TextView tv = (TextView) v.findViewById(android.R.id.text1);
                tv.setTextSize(18);
                tv.setTextColor(Color.WHITE);
                return v;
            }
        };

        // get a handle on the bluetooth radio
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mDevicesListView = (ListView) view.findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(mBTArrayAdapter);// assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        if (mBTAdapter.isEnabled()) {
            mBluetoothStatus.setText(bluetoothdevice);
            bluetoothswitch.setChecked(true);
            getdevicelist();
        } else {
            mBluetoothStatus.setText("bluetooth off");
        }


        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            mBluetoothStatus.setText("Status: Bluetooth not found");
            Toast.makeText(getActivity().getApplicationContext(), "Bluetooth device not found!", Toast.LENGTH_SHORT).show();
        } else {
            bluetoothswitch.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (bluetoothswitch.isChecked()) {
                        bluetoothOn(view);
                    } else {
                        bluetoothOff(view);
                    }
                }
            });
            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getdevicelist();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }

        return view;
    }

    private void bluetoothOn(View view) {

        getLocationPermission();
        BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            if (!mBTAdapter.isEnabled()) {//如果藍芽沒開啟
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);//跳出視窗
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                getdevicelist();
                Toast.makeText(getActivity().getApplicationContext(), "Bluetooth is already on",
                        Toast.LENGTH_SHORT).show();

            }
        }
    }

    //定義當按下跳出是否開啟藍芽視窗後要做的內容
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent Data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                mBluetoothStatus.setText("bluetooth on");
                Log.d("Bluetooth", String.valueOf(mBTAdapter.isEnabled()));
                getdevicelist();
                Toast.makeText(getActivity().getApplicationContext(), "Bluetooth on", Toast.LENGTH_SHORT).show();
            } else{
                mBluetoothStatus.setText("bluetooth off");
                Log.d("switch_isCheck", String.valueOf(mBTAdapter.isEnabled()));
                bluetoothswitch.setChecked(false);
                Toast.makeText(getActivity().getApplicationContext(), "Bluetooth off", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void bluetoothOff(View view) {
        mBTArrayAdapter.clear();
        mBluetoothStatus.setText("bluetooth off");
        mBTAdapter.disable(); // turn off bluetooth
        Log.d("Bluetooth", String.valueOf(mBTAdapter.isEnabled()));
    }


    //廣播
    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                Log.d("BluetoothService", intent.getAction());
                switch (intent.getAction()) {
                    case BluetoothDevice.ACTION_FOUND:
                        BluetoothDevice device =
                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                        mBTArrayAdapter.notifyDataSetChanged();
                        break;
                    case "Bluetooth_Connect_Fail":
                        mBluetoothStatus.setText("Connection Failed");
                        break;
                    case "Bluetooth_Connect_Success":
                        mBluetoothStatus.setText("Connected to Device: " + intent.getStringExtra("name"));
                        bluetoothdevice = "Connected to Device: " + intent.getStringExtra("name");
                        getlocation();
                        break;
                    case "Bluetooth_Data_Receive":
                        mReadBuffer.setText(intent.getStringExtra("data"));
                        break;
                }
            }
        }
    };

    //將搜尋到的藍芽放進清單

    private void getdevicelist() {
        mBTArrayAdapter.clear(); // clear items
        mBTAdapter.startDiscovery(); //開始尋找
        mPairedDevices = mBTAdapter.getBondedDevices();
        if (mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : mPairedDevices)

                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

//            Toast.makeText(getActivity().getApplicationContext(), "Show Paired Devices",
//                    Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getActivity().getApplicationContext(), "Bluetooth off",
                    Toast.LENGTH_SHORT).show();
    }


    private AdapterView.OnItemClickListener mDeviceClickListener = new
            AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

                    if (!mBTAdapter.isEnabled()) {
                        Toast.makeText(getActivity().getBaseContext(), "Bluetooth off",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mBluetoothStatus.setText("Connecting...");
                    // Get the device MAC address, which is the last 17 chars in the View
                    String info = ((TextView) v).getText().toString();
                    final String address = info.substring(info.length() - 17);
                    final String name = info.substring(0, info.length() - 17);

                    // Spawn a new thread to avoid blocking the GUI one
                    new Thread() {
                        public void run() {
                            //取得裝置MAC找到連接的藍芽裝置
                            if (getActivity() != null) {
                                BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                                Intent bluetoothService = new Intent();
                                bluetoothService.setClass(getActivity(), BluetoothService.class);
                                bluetoothService.putExtra("device", device);
                                bluetoothService.putExtra("name", name);
                                getActivity().startService(bluetoothService);
                                Intent dataService = new Intent();
                                dataService.setClass(getActivity(), DataService.class);
                                getActivity().startService(dataService);
                            } else {
                                Log.d("Error", "Activity null");
                            }
                        }
                    }.start();
                }
            };


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws
            IOException {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connection with BT device using UUID

    }

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            Toast.makeText(getActivity().getApplicationContext(), "已啟用位置權限", Toast.LENGTH_SHORT).show();
        }
    }

    private LocationManager mLocationManager;

    private boolean checkGPSisOpen(){
        boolean isOpen;
        mLocationManager = (LocationManager) (getActivity().getSystemService(Context.LOCATION_SERVICE));
        isOpen = (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
        return isOpen;
    }
    private void getlocation() {
        if (checkGPSisOpen()) {
            openLocationService();
        } else {
            Toast.makeText(getActivity(), "請開啟定位服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        return;
    }
    private void openLocationService(){
        Intent locationService = new Intent();
        locationService.setClass(getActivity(), LocationService.class);
        getActivity().startService(locationService);
        Log.d("LocationService","Start");
    }
}
