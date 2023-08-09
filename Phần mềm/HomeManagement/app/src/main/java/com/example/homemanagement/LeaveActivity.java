package com.example.homemanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LeaveActivity extends AppCompatActivity {

    RecyclerView rcvLeaves;
    LeaveAdapter mLeaveAdapter;
    List<Leave> mListLeaves;

    TextView btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);
        initUi();
        getListLeavesFromDatabase();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

    private void initUi() {
        btnBack = (TextView) findViewById(R.id.btnBack);
        rcvLeaves = findViewById(R.id.rcv_leaves);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvLeaves.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvLeaves.addItemDecoration(dividerItemDecoration);

        mListLeaves = new ArrayList<>();
        mLeaveAdapter = new LeaveAdapter(mListLeaves);
        rcvLeaves.setAdapter(mLeaveAdapter);
    }


    private void getListLeavesFromDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef2 = database.getReference("list_leaves");

        //Cach 1
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(mListLeaves != null){
                    mListLeaves.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Leave leave = dataSnapshot.getValue(Leave.class);
                    mListLeaves.add(leave);
                }
                mLeaveAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}