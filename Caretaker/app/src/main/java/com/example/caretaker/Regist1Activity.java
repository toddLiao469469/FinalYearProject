package com.example.caretaker;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class Regist1Activity extends AppCompatActivity {
    File tempImg1;
    Uri fileUri;
    String username;
    String password;
    String name;
    String imageString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist1);
        final EditText editText = (EditText) findViewById(R.id.editText);
        final EditText editText2 = (EditText) findViewById(R.id.editText2);
        final EditText editText3 = (EditText) findViewById(R.id.editText3);
        final Button button = (Button) findViewById(R.id.button2);
        final Button button2 = (Button) findViewById(R.id.button);
        final Intent intent = new Intent(Regist1Activity.this, Regist2Activity.class);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = editText.getText().toString();
                password = editText2.getText().toString();
                name = editText3.getText().toString();
                final DatabaseReference account = FirebaseDatabase.getInstance().getReference("Account").child(username);
                final DatabaseReference general = FirebaseDatabase.getInstance().getReference("Data").child(username).child("general");
                SharedPreferences setting = getSharedPreferences("user", MODE_PRIVATE);
                setting.edit()
                        .putString("PREF_USERID", username)
                        .putString("PREF_Password", password)
                        .apply();

                if ((username.equals("")) || (password.equals("")) || (imageString.equals(""))||(name.equals(""))) {
                    Toast.makeText(Regist1Activity.this, "請填入完整資訊", Toast.LENGTH_SHORT).show();
                } else {
                    final DatabaseReference create_data = FirebaseDatabase.getInstance().getReference("Data");
                    create_data.child(username).child("data").child("heartbeat").setValue("0");
                    create_data.child(username).child("data").child("time").setValue("yyyy/mm/dd hh:mm");
                    create_data.child(username).child("data").child("latitude").setValue("0");
                    create_data.child(username).child("data").child("longitude").setValue("0");
                    create_data.child(username).child("data").child("location").setValue("null");

                    account.setValue(password);
                    general.child("name").setValue(name);
                    general.child("image").setValue(imageString);
                    startActivity(intent);
                }

            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //檢查是否取得權限
                int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
                //沒有權限時
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Regist1Activity.this,
                            new String[]{Manifest.permission.CAMERA},
                            1);
                } else {
                    openCamera();
                }
            }


        });

    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        tempImg1 = new File(Regist1Activity.this.getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
        fileUri = Uri.fromFile(tempImg1);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            final ImageView imageView = (CircleImageView) findViewById(R.id.imageView);
            Bundle extras = data.getExtras();
            Bitmap image = (Bitmap) extras.get("data");
            Bitmap imageBitmap = centerSquareScaleBitmap(image, 140);
            imageView.setImageBitmap(imageBitmap);
            imageString = encodeToBase64(imageBitmap, Bitmap.CompressFormat.JPEG, 100);


        }
    }

    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength) {
        if (null == bitmap || edgeLength <= 0) {
            return null;
        }
        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();

        if (widthOrg > edgeLength && heightOrg > edgeLength) {
            //压缩到一个最小长度是edgeLength的bitmap
            int longerEdge = (int) (edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
            int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
            int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
            Bitmap scaledBitmap;

            try {
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
            } catch (Exception e) {
                return null;
            }

            //从图中截取正中间的正方形部分。
            int xTopLeft = (scaledWidth - edgeLength) / 2;
            int yTopLeft = (scaledHeight - edgeLength) / 2;

            try {
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
                scaledBitmap.recycle();
            } catch (Exception e) {
                return null;
            }
        }
        return result;
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    private static final int CUSTOM_NUMBER = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //這個"CUSTOM_NUMBER"就是上述的自訂意義的請求代碼
        // private static final int CUSTOM_NUMBER = 1;
        if (requestCode == CUSTOM_NUMBER) {
            //假如允許了
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
                Toast.makeText(this, "已經拿到CAMERA權限囉!", Toast.LENGTH_SHORT).show();
            }
            //假如拒絕了
            else {
                //do something
                Toast.makeText(this, "CAMERA權限FAIL", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
