package com.example.homemanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddMonth extends AppCompatActivity {

    EditText edtWeek, edtDate;
    TextView btnAddWeek;

    RecyclerView rcvWeeks;

    WeekAdapter mWeekAdapter;
    List<Week> mListWeeks;
    private WeekAdapter.IClickListener2 mIClickListener2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_month);

        edtDate = (EditText) findViewById(R.id.edtDate);
        edtWeek = (EditText) findViewById(R.id.edtWeek);
        btnAddWeek = (TextView) findViewById(R.id.btnAddWeek);

        rcvWeeks = (RecyclerView) findViewById(R.id.rcv_weeks);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvWeeks.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvWeeks.addItemDecoration(dividerItemDecoration);

        mListWeeks = new ArrayList<>();
        mWeekAdapter = new WeekAdapter(mListWeeks, new WeekAdapter.IClickListener2() {
            @Override
            public void onClickUpdateItem(Week week) {
                startActivity(new Intent(getApplicationContext(), ListAttendance.class));
            }
        });

        rcvWeeks.setAdapter(mWeekAdapter);


        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Select();
            }
        });

        btnAddWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int weekNumber = Integer.parseInt(edtWeek.getText().toString().trim());
                String date = edtDate.getText().toString().trim();
                Week week = new Week(date,weekNumber);
                onClickAddWeek(week);
            }
        });
        getListWeeksFromDb();
    }

    private void getListWeeksFromDb() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("list_weeks");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(mListWeeks != null){
                    mListWeeks.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Week week = dataSnapshot.getValue(Week.class);
                    mListWeeks.add(week);
                }
                mWeekAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onClickAddWeek(Week week) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("list_weeks");

        String pathObject = String.valueOf(week.getNumberWeek());
        myRef.child(pathObject).setValue(week);
    }

    private void Select() {
        Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(i, i1, i2);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                edtDate.setText(simpleDateFormat.format(calendar.getTime()));
            }
        },year,month,date);
        datePickerDialog.show();
    }
}