package com.example.homemanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.homemanagement.api.ApiService;
import com.example.homemanagement.model.JsonReadThingspeak;
import com.example.homemanagement.model.WriteData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    ImageView img_avatar, img_mic;
    TextView txtName, txtEmail, txtStatement, txtTemp, txtHum;
    Switch swDoor;
    LinearLayout linearLayout;
    ImageView btnUpdate;
    Button btnLight1, btnLight2, btnLight3, btnLight4;
    DatabaseReference mData;

//    FirebaseFirestore fStore;
//    FirebaseAuth fAuth;


    //    get data from thingspeak
    public static final String JSON_URL = "https://api.thingspeak.com/channels/2187417/feeds.json?api_key=IMDLO4TIQAI6S9MR&results=1";
    ArrayList<String> arrSensor = new ArrayList<String>();

    private static final int REQUEST_CODE_SPEECH_INPUT = 100;
    private JSONObject obj;


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();
        initListener();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_action_menu);
        mDrawerLayout = findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCallApiReadDataTs();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home: {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                        break;

                    }
                    case R.id.nav_profile: {
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        finish();
                        break;
                    }
                    case R.id.nav_logout: {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), SigninActivity.class));
                        finish();
                        break;
                    }
                    case R.id.nav_hr_m: {
                        startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                        finish();
                        break;
                    }
                    case R.id.nav_hr_e: {
                        startActivity(new Intent(getApplicationContext(), UserActivity.class));
                        finish();
                        break;
                    }
                    case R.id.nav_hr_leave: {
                        startActivity(new Intent(getApplicationContext(), LeaveActivity.class));
                        finish();
                        break;
                    }
                }
                return false;
            }
        });
        showUserInfo();

    }

    @Override
    public void onBackPressed() {

        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }

    }
    private void initListener() {
        btnLight1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if (btnLight1.getText() == "BẬT ĐÈN 1") {
                    clickCallApiControlL1On();
                } else {
                    clickCallApiControlL1Off();
                }

//                if (btnLight1.getText() == "BẬT ĐÈN 1") {
//                    mData.child("alarm").setValue("bật đèn 1");
//                    btnLight1.setText("TẮT ĐÈN 1");
//                    btnLight1.setTextColor((getResources().getColor(R.color.light_on)));
//                } else {
//                    mData.child("alarm").setValue("Tắt Đèn 1");
//                    btnLight1.setText("BẬT ĐÈN 1");
//                    btnLight1.setTextColor((getResources().getColor(R.color.white)));
//                }
            }
        });
        btnLight2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if (btnLight2.getText() == "BẬT ĐÈN 2") {
                    clickCallApiControlL2On();
                } else {
                    clickCallApiControlL2Off();
                }
//                if (btnLight2.getText() == "BẬT ĐÈN 2") {
//                    mData.child("alarm").setValue("bật đèn 2");
//                    btnLight2.setText("TẮT ĐÈN 2");
//                    btnLight2.setTextColor((getResources().getColor(R.color.light_on)));
//                } else {
//                    mData.child("alarm").setValue("Tắt Đèn 2");
//                    btnLight2.setText("BẬT ĐÈN 2");
//                    btnLight2.setTextColor((getResources().getColor(R.color.white)));
//                .....
            }
        });
        btnLight3.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if (btnLight3.getText() == "BẬT ĐÈN 3") {
                    clickCallApiControlL3On();
                } else {
                    clickCallApiControlL3Off();
                }
//                if (btnLight3.getText() == "BẬT ĐÈN 3") {
//                    mData.child("alarm").setValue("bật đèn 3");
//                    btnLight3.setText("TẮT ĐÈN 3");
//                    btnLight3.setTextColor((getResources().getColor(R.color.light_on)));
//                } else {
//                    mData.child("alarm").setValue("Tắt Đèn 3");
//                    btnLight3.setText("BẬT ĐÈN 3");
//                    btnLight3.setTextColor((getResources().getColor(R.color.white)));
//                }
            }
        });
        btnLight4.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if (btnLight4.getText() == "BẬT ĐÈN 4") {
                    clickCallApiControlL4On();
                } else {
                    clickCallApiControlL4Off();
                }
//                if (btnLight4.getText() == "BẬT ĐÈN 4") {
//                    mData.child("alarm").setValue("bật đèn 4");
//                    btnLight4.setText("TẮT ĐÈN 4");
//                    btnLight4.setTextColor((getResources().getColor(R.color.light_on)));
//                } else {
//                    mData.child("alarm").setValue("Tắt Đèn 4");
//                    btnLight4.setText("BẬT ĐÈN 4");
//                    btnLight4.setTextColor((getResources().getColor(R.color.white)));
//                }
            }
        });
        img_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak();
                if (txtStatement.getText().toString().trim() == "bật đèn hồ bơi") {
                    clickCallApiControlL1On();
                }
                if (txtStatement.getText().toString().trim() == "bật đèn phòng khách") {
                    clickCallApiControlL2On();
                }
                if (txtStatement.getText().toString().trim() == "bật đèn phòng ngủ") {
                    clickCallApiControlL3On();
                }
                if (txtStatement.getText().toString().trim() == "bật đèn phòng tắm") {
                    clickCallApiControlL4On();
                }
                if (txtStatement.getText().toString().trim() == "tắt đèn hồ bơi") {
                    clickCallApiControlL1Off();
                }
                if (txtStatement.getText().toString().trim() == "Tắt đèn phòng khách") {
                    clickCallApiControlL2Off();
                }
                if (txtStatement.getText().toString().trim() == "Tắt đèn phòng ngủ") {
                    clickCallApiControlL3Off();
                }
                if (txtStatement.getText().toString().trim() == "Tắt đèn phòng tắm") {
                    clickCallApiControlL4Off();
                }
                if (txtStatement.getText().toString().trim() == "mở cửa") {
                    clickCallApiControlL5Off();
                }
                if (txtStatement.getText().toString().trim() == "đóng cửa") {
                    clickCallApiControlL5Off();
                }
                if (txtStatement.getText().toString().trim() == "bật hết đèn") {
                    clickCallApiControlL6On();

                }
                if (txtStatement.getText().toString().trim() == "tắt hết đèn") {
                    clickCallApiControlL6Off();
                }

            }
        });

        swDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean isOpen = swDoor.isChecked();
                if (isOpen == true) {
                    clickCallApiControlL5On();
                }
                if (isOpen == false) {
                    clickCallApiControlL5Off();
//                    mData.child("alarm").setValue("Close the door");
                    swDoor.setText("CỬA ĐÓNG");
                }
            }
        });
    }

    private void speak() {
        //intent to show speed to text
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //receive voice input

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtStatement.setText(result.get(0));
                    mData.child("alarm").setValue(result.get(0));
                    if (result.get(0) == "bật đèn hồ bơi") {
                        clickCallApiControlL1On();
                    }
                    if (result.get(0) == "bật đèn phòng khách") {
                        clickCallApiControlL2On();
                    }
                    if (result.get(0) == "bật đèn phòng ngủ") {
                        clickCallApiControlL3On();
                    }
                    if (result.get(0) == "bật đèn phòng tắm") {
                        clickCallApiControlL4On();
                    }
                    if (result.get(0) == "tắt đèn hồ bơi") {
                        clickCallApiControlL1Off();
                    }
                    if (result.get(0) == "Tắt đèn phòng khách") {
                        clickCallApiControlL2Off();
                    }
                    if (result.get(0) == "Tắt đèn phòng ngủ") {
                        clickCallApiControlL3Off();
                    }
                    if (result.get(0) == "Tắt đèn phòng tắm") {
                        clickCallApiControlL4Off();
                    }
                    if (result.get(0) == "mở cửa") {
                        clickCallApiControlL5Off();
                    }
                    if (result.get(0) == "đóng cửa") {
                        clickCallApiControlL5Off();
                    }
                    if (result.get(0) == "bật hết đèn") {
                        clickCallApiControlL6On();

                    }
                    if (result.get(0) == "tắt hết đèn") {
                        clickCallApiControlL6Off();
                    }
                }
                break;
            }
        }
    }

    private void initUi() {
        navigationView = findViewById(R.id.nav_view);
        img_avatar = navigationView.getHeaderView(0).findViewById(R.id.img_avatar);
        txtEmail = navigationView.getHeaderView(0).findViewById(R.id.txtEmail);
        txtName = navigationView.getHeaderView(0).findViewById(R.id.txtName);
        txtTemp = (TextView) findViewById(R.id.tempD);
        txtHum = (TextView) findViewById(R.id.humD);

        swDoor = (Switch) findViewById(R.id.swDoor);
        txtStatement = (TextView) findViewById(R.id.txtMic);
        btnLight1 = (Button) findViewById(R.id.light1);
        btnLight2 = (Button) findViewById(R.id.light2);
        btnLight3 = (Button) findViewById(R.id.light3);
        btnLight4 = (Button) findViewById(R.id.light4);
        linearLayout = (LinearLayout) findViewById(R.id.ln_door);
        img_mic = (ImageView) findViewById(R.id.btn_mic);
        btnUpdate = (ImageView) findViewById(R.id.btnUpdateTempHum);

        mData = FirebaseDatabase.getInstance().getReference("list_strings");
        mData.child("alarm").setValue("Tắt Đèn");
    }

    private void showUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();

        if (name == null) {
            txtName.setVisibility(View.GONE);
        } else {
            txtName.setVisibility(View.VISIBLE);
            txtName.setText(name);
        }
        txtEmail.setText(email);
        Glide.with(this).load(photoUrl).error(R.drawable.avatar2).into(img_avatar);
    }
    private void clickCallApiReadDataTs() {
        ApiService.apiService.readDataThingspeak("IMDLO4TIQAI6S9MR", 1).enqueue(new Callback<JsonReadThingspeak>() {
            @Override
            public void onResponse(Call<JsonReadThingspeak> call, Response<JsonReadThingspeak> response) {
                Toast.makeText(MainActivity.this, "Call Api Success", Toast.LENGTH_SHORT).show();
                JsonReadThingspeak readTs = response.body();
                if (readTs != null) {
                    txtTemp.setText(readTs.getFeeds().get(0).getField1() + " °C");
                    txtHum.setText(readTs.getFeeds().get(0).getField2() + " %");
                }
            }

            @Override
            public void onFailure(Call<JsonReadThingspeak> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Call Api Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void clickCallApiControlL1On() {
        ApiService.apiServiceControlL1.writeL1("3YLKCNPRL5NY6RQH", 1).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    int result = response.body();
                    if (result == 0) {
                        Toast.makeText(MainActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Light Pool On", Toast.LENGTH_SHORT).show();
                        btnLight1.setText("TẮT ĐÈN 1");
                        btnLight1.setTextColor((getResources().getColor(R.color.light_on)));
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Command Not Read", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Call Api Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void clickCallApiControlL2On() {
        ApiService.apiServiceControlL1.writeL2("3YLKCNPRL5NY6RQH", 1).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    int result = response.body();
                    if (result == 0) {
                        Toast.makeText(MainActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Light Living Room On", Toast.LENGTH_SHORT).show();
                        btnLight2.setText("TẮT ĐÈN 2");
                        btnLight2.setTextColor((getResources().getColor(R.color.light_on)));
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Command Not Read", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Call Api Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void clickCallApiControlL3On() {
        ApiService.apiServiceControlL1.writeL3("3YLKCNPRL5NY6RQH", 1).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    int result = response.body();
                    if (result == 0) {
                        Toast.makeText(MainActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Light Bedroom On", Toast.LENGTH_SHORT).show();
                        btnLight3.setText("TẮT ĐÈN 3");
                        btnLight3.setTextColor((getResources().getColor(R.color.light_on)));
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Command Not Read", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Call Api Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void clickCallApiControlL4On() {
        ApiService.apiServiceControlL1.writeL4("3YLKCNPRL5NY6RQH", 1).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    int result = response.body();
                    if (result == 0) {
                        Toast.makeText(MainActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Light Bathroom On", Toast.LENGTH_SHORT).show();
                        btnLight4.setText("TẮT ĐÈN 4");
                        btnLight4.setTextColor((getResources().getColor(R.color.light_on)));
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Command Not Read", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Call Api Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void clickCallApiControlL5On() {
        ApiService.apiServiceControlL1.writeDoor("3YLKCNPRL5NY6RQH", 1).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    int result = response.body();
                    if (result == 0) {
                        Toast.makeText(MainActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Door is opening", Toast.LENGTH_SHORT).show();
                        swDoor.setText("CỬA MỞ");
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Command Not Read", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Call Api Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void clickCallApiControlL1Off() {
        ApiService.apiServiceControlL1.writeL1("3YLKCNPRL5NY6RQH", 0).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    int result = response.body();
                    if (result == 0) {
                        Toast.makeText(MainActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Light Pool Off", Toast.LENGTH_SHORT).show();
                        btnLight1.setText("BẬT ĐÈN 1");
                        btnLight1.setTextColor((getResources().getColor(R.color.light_off)));
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Command Not Read", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Call Api Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void clickCallApiControlL2Off() {
        ApiService.apiServiceControlL1.writeL2("3YLKCNPRL5NY6RQH", 0).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    int result = response.body();
                    if (result == 0) {
                        Toast.makeText(MainActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Light Living Room Off", Toast.LENGTH_SHORT).show();
                        btnLight2.setText("BẬT ĐÈN 2");
                        btnLight2.setTextColor((getResources().getColor(R.color.light_off)));
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Command Not Read", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Call Api Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void clickCallApiControlL3Off() {
        ApiService.apiServiceControlL1.writeL3("3YLKCNPRL5NY6RQH", 0).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    int result = response.body();
                    if (result == 0) {
                        Toast.makeText(MainActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Light Bedroom Off", Toast.LENGTH_SHORT).show();
                        btnLight3.setText("BẬT ĐÈN 3");
                        btnLight3.setTextColor((getResources().getColor(R.color.light_off)));
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Command Not Read", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Call Api Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void clickCallApiControlL4Off() {
        ApiService.apiServiceControlL1.writeL4("3YLKCNPRL5NY6RQH", 0).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    int result = response.body();
                    if (result == 0) {
                        Toast.makeText(MainActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Light Bathroom Off", Toast.LENGTH_SHORT).show();
                        btnLight4.setText("BẬT ĐÈN 4");
                        btnLight4.setTextColor((getResources().getColor(R.color.light_off)));
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Command Not Read", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Call Api Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void clickCallApiControlL5Off() {
        ApiService.apiServiceControlL1.writeDoor("3YLKCNPRL5NY6RQH", 0).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    int result = response.body();
                    if (result == 0) {
                        Toast.makeText(MainActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Door is closing", Toast.LENGTH_SHORT).show();
                        swDoor.setText("CỬA ĐÓNG");
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Command Not Read", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Call Api Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void clickCallApiControlL6Off() {
        ApiService.apiServiceControlL1.writeAll("3YLKCNPRL5NY6RQH", 0,0,0,0).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    int result = response.body();
                    if (result == 0) {
                        Toast.makeText(MainActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "All is turning on", Toast.LENGTH_SHORT).show();
                        btnLight1.setText("BẬT ĐÈN 1");
                        btnLight1.setTextColor((getResources().getColor(R.color.light_off)));
                        btnLight2.setText("BẬT ĐÈN 2");
                        btnLight2.setTextColor((getResources().getColor(R.color.light_off)));
                        btnLight3.setText("BẬT ĐÈN 3");
                        btnLight3.setTextColor((getResources().getColor(R.color.light_off)));
                        btnLight4.setText("BẬT ĐÈN 4");
                        btnLight4.setTextColor((getResources().getColor(R.color.light_off)));
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Command Not Read", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Call Api Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void clickCallApiControlL6On() {
        ApiService.apiServiceControlL1.writeAll("3YLKCNPRL5NY6RQH", 1,1,1,1).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    int result = response.body();
                    if (result == 0) {
                        Toast.makeText(MainActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "All is turning off", Toast.LENGTH_SHORT).show();
                        btnLight1.setText("TẮT ĐÈN 1");
                        btnLight1.setTextColor((getResources().getColor(R.color.light_on)));
                        btnLight2.setText("TẮT ĐÈN 2");
                        btnLight2.setTextColor((getResources().getColor(R.color.light_on)));
                        btnLight3.setText("TẮT ĐÈN 3");
                        btnLight3.setTextColor((getResources().getColor(R.color.light_on)));
                        btnLight4.setText("TẮT ĐÈN 4");
                        btnLight4.setTextColor((getResources().getColor(R.color.light_on)));
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Command Not Read", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Call Api Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void checkUserAccessLevel(String uid) {
//        DocumentReference df = fStore.collection("Users").document(uid);
//        // extract the data from the document
//        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                Log.d("TAG", "OnSuccess: " + documentSnapshot.getData());
//                // identify the user access level
//
////                if(documentSnapshot.getString("isAdmin") != null){
////                    //user is admin
////
////                    startActivity(new Intent(getApplicationContext(), AdminActivity.class));
////                    finish();
////                } else {
////                    Toast.makeText(MainActivity.this, "You do not have access", Toast.LENGTH_SHORT).show();
////                }
//
//                if (documentSnapshot.getString("isUser") != null){
//                    startActivity(new Intent(getApplicationContext(), UserActivity.class));
//                    finish();
//                } else {
//                    Toast.makeText(MainActivity.this, "You do not have access", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
}