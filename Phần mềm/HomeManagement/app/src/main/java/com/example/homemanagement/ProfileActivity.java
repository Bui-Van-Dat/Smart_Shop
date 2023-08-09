package com.example.homemanagement;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    EditText edtNameUpdate;
    TextView btnUpdate;
    ImageView imgAvatarUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initUi();
        setUserInfo();
        initListener();
    }

    private void initListener() {
        imgAvatarUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRequestPermission();
            }
        });
    }

    private void onClickRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
//            openGallery();
            return;
        }
//        openGallery();
//        if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
//
//        }
    }

    private void setUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        edtNameUpdate.setText(user.getDisplayName());
        Glide.with(this).load(user.getPhotoUrl()).error(R.drawable.me).into(imgAvatarUpdate);
    }

    private void initUi() {
        edtNameUpdate = (EditText) findViewById(R.id.edtNameUpdate);
        btnUpdate = (TextView) findViewById(R.id.btnUpdate);
        imgAvatarUpdate = (ImageView) findViewById(R.id.img_avatar_update);
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}