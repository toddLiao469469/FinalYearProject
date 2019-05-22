package com.example.caretaker;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {

    Bundle bundle;
    String username;
    String name;
    String phone;
    String height;
    String weight;
    String address;
    String phone2;
    String imageString;
    File tempImg1;
    Uri fileUri;
    private Button image_btn;
    private Button update_btn;
    private ImageView imageView;
    private TextView name_txt = null;
    private TextView phone_txt = null;
    private TextView height_txt = null;
    private TextView weight_txt = null;
    private TextView address_txt = null;
    private TextView phone2_txt = null;

    @Override
    public void setArguments(Bundle args) {
        this.bundle = args;
    }

    public EditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView() {
        ((AppCompatActivity) getContext()).getSupportActionBar().setTitle(name + "的個人資料");
        super.onDestroyView();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit, container, false);

        name_txt = v.findViewById(R.id.editText4);
        phone_txt = v.findViewById(R.id.editText6);
        height_txt = v.findViewById(R.id.editText7);
        weight_txt = v.findViewById(R.id.editText8);
        address_txt = v.findViewById(R.id.editText9);
        phone2_txt = v.findViewById(R.id.editText10);
        imageView = v.findViewById(R.id.imageView3);
        image_btn = v.findViewById(R.id.button6);
        update_btn = v.findViewById(R.id.button5);

        username = bundle.getString("username");

        Log.d("username", username);
        final DatabaseReference general = FirebaseDatabase.getInstance().getReference("Data").child(username).child("general");
        general.child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageString = dataSnapshot.getValue().toString();
                byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                imageView.setImageBitmap(decodedByte);
                general.child("image").removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        general.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = String.valueOf(dataSnapshot.getValue());
                name_txt.setText(name);
                ((AppCompatActivity) getContext()).getSupportActionBar().setTitle(name + "的個人資料");
                general.child("name").removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        general.child("phone").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                phone = dataSnapshot.getValue().toString();
                phone_txt.setText(phone);
                general.child("phone").removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        general.child("height").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                height = dataSnapshot.getValue().toString();
                height_txt.setText(height);
                general.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        general.child("weight").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                weight = dataSnapshot.getValue().toString();
                weight_txt.setText(weight);
                general.child("weight").removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        general.child("address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                address = dataSnapshot.getValue().toString();
                address_txt.setText(address);
                general.child("address").removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        general.child("emergency").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                phone2 = dataSnapshot.getValue().toString();
                Log.d("emergency", phone2);
                phone2_txt.setText(phone2);
                general.child("emergency").removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
                //沒有權限時
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            1);
                } else {
                    openCamera();
                }
            }
        });
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = name_txt.getText().toString();
                phone = phone_txt.getText().toString();
                height = height_txt.getText().toString();
                weight = weight_txt.getText().toString();
                address = address_txt.getText().toString();
                phone2 =phone2_txt.getText().toString();
                if((imageString.equals(""))||(name.equals(""))||(height.equals("")) || (weight.equals("")) || (phone.equals("")) || (phone2.equals("")) || (address.equals(""))){
                    Toast.makeText(getActivity(), "請輸入完整資訊", Toast.LENGTH_LONG).show();
                }else{
                    general.child("name").setValue(name);
                    general.child("phone").setValue(phone);
                    general.child("height").setValue(height);
                    general.child("weight").setValue(weight);
                    general.child("address").setValue(address);
                    general.child("emergency").setValue(phone2);
                    general.child("image").setValue(imageString);

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Fragment userFragment = new UserFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("username", username);
                    userFragment.setArguments(bundle);
                    ft.add(R.id.fragment, userFragment);
                    //ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });
        return v;
    }
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        tempImg1 = new File(getActivity().getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
        fileUri = Uri.fromFile(tempImg1);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
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
                Toast.makeText(getContext(), "已經拿到CAMERA權限囉!", Toast.LENGTH_SHORT).show();
            }
            //假如拒絕了
            else {
                //do something
                Toast.makeText(getContext(), "CAMERA權限FAIL", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
