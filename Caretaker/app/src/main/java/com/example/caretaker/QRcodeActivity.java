package com.example.caretaker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

@RequiresApi(api = Build.VERSION_CODES.O)
public class QRcodeActivity extends AppCompatActivity {

    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        final Intent intent = new Intent(QRcodeActivity.this, CareActivity.class);
        final Button button = (Button) findViewById(R.id.button2);

        SharedPreferences setting = getSharedPreferences("user" , MODE_PRIVATE);
        username =setting.getString("PREF_USERID" , "");

        ImageView ivCode = (ImageView) findViewById(R.id.ivCode);
        BarcodeEncoder encoder = new BarcodeEncoder();
        try {
            Bitmap bit = encoder.encodeBitmap(new String(username.getBytes("UTF-8"), "ISO-8859-1"), BarcodeFormat.QR_CODE, 300, 300);

            ivCode.setImageBitmap(bit);

        } catch (Exception e) {
            Log.e("e", "e");
            e.printStackTrace();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
    }
}
