package com.example.caretaker;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class BluetoothService extends Service {

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "BluetoothService";
    private String _receiveData = "";
    private Thread connect;

    public class LocalBinder extends Binder {
        BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    private LocalBinder mLocBin = new LocalBinder();

    @Override
    public IBinder onBind(Intent arg0) {
        return mLocBin;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        if (connect != null) {
            if (!connect.isAlive()){
                connect.run();
            }
        } else {
            connect = new Thread(new ConnectRun(intent));
            connect.run();
        }
        return START_STICKY;
    }

    public class ConnectRun implements Runnable {

        private Intent intent;

        ConnectRun(Intent intent) {
            this.intent = intent;
        }

        @Override
        public void run() {
            Log.d(TAG, "onStartCommand: Start");
            BluetoothSocket mBTSocket = null;
            ConnectedThread mConnectedThread = null;
            try {
                BluetoothDevice device = intent.getParcelableExtra("device");
                mBTSocket = device.createRfcommSocketToServiceRecord(BTMODULEUUID);
                mBTSocket.connect();
                mConnectedThread = new ConnectedThread(mBTSocket);
                mConnectedThread.start();
                Log.d(TAG, "onStartCommand: Connect Success!");
                sendBroadcast(new Intent().setAction("Bluetooth_Connect_Success").putExtra("name", intent.getStringExtra("name")));
            } catch (Exception e) {
                Log.d(TAG, "onStartCommand: Connect Failed");
                e.printStackTrace();
                if (mBTSocket != null) {
                    try {
                        mBTSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                Toast.makeText(getApplicationContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Please restart the bluetooth", Toast.LENGTH_SHORT).show();

                if (mConnectedThread != null) {
                    mConnectedThread.bluetoothClose();
                }
                stopSelf();
                sendBroadcast(new Intent().setAction("Bluetooth_Connect_Fail"));
            }
        }
    }

    private class ConnectedThread extends Thread {
        private InputStream mmInStream;
        private BluetoothSocket mmSocket;

        ConnectedThread(BluetoothSocket socket) {

            Log.d(TAG, "ConnectedThread: Start Thread");
            Log.d(TAG, "ConnectedThread: " + (socket == null ? "socket null" : socket.toString()));
            mmSocket = socket;
            if (mmSocket != null) {
                try {
                    mmInStream = mmSocket.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "ConnectedThread: socket is null!!");
            }
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            Log.d(TAG, "run: Start receive data");
            while (true) {
                try {
                    if (mmInStream == null) {
                        Log.d(TAG, "run: mmInStream null!!");
                    } else {
                        bytes = mmInStream.available();
                        if (bytes != 0) {
                            Log.d(TAG, "run: Receive data!!");
                            SystemClock.sleep(100);
                            bytes = mmInStream.available();
                            bytes = mmInStream.read(buffer, 0, bytes);
                            String readMessage;
                            if (_receiveData.length() == 3) {
                                _receiveData = "";
                            }
                            try {
                                readMessage = new String(buffer, "UTF-8");
                                readMessage = readMessage.substring(0, 1);
                                _receiveData += readMessage;
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            Log.d(TAG, "run: " + _receiveData);
                            sendBroadcast(new Intent().setAction("Bluetooth_Data_Receive").putExtra("data", _receiveData));

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        void bluetoothClose() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Service stop");
    }

}
