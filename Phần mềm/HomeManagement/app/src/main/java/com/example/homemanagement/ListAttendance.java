package com.example.homemanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
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

public class ListAttendance extends AppCompatActivity {

    RecyclerView rcvAttendance;
    AttendanceAdapter mAttendanceAdapter;
    List<Student> mListStudents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_attendance);
        initUi();
        getListStudentsFromDatabase();
    }

    private void initUi() {
        rcvAttendance = findViewById(R.id.rcvAttendance);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvAttendance.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvAttendance.addItemDecoration(dividerItemDecoration);

        mListStudents = new ArrayList<>();
        mAttendanceAdapter = new AttendanceAdapter(mListStudents, new AttendanceAdapter.IClickListener() {
            @Override
            public void onClickUpdateItem(Student student) {
                openDialogUpdateItem(student);
            }
        });

        rcvAttendance.setAdapter(mAttendanceAdapter);
    }

    private void openDialogUpdateItem(Student student) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_update);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        Button btnMiss = dialog.findViewById(R.id.btnMiss);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView tvAs = dialog.findViewById(R.id.tvAs);

        tvAs.setText(String.valueOf(student.getAbsent()));
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnMiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("list_users");

                int newAbsent = Integer.parseInt((String) tvAs.getText()) + 1;
                student.setAbsent(newAbsent);

                myRef.child(String.valueOf(student.getId())).updateChildren(student.toMap(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    private void getListStudentsFromDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("list_users");

        //Cach 1
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(mListStudents != null){
//                    mListStudents.clear();
//                }
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    Student student = dataSnapshot.getValue(Student.class);
//                    mListStudents.add(student);
//                }
//                mAttendanceAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Student student = snapshot.getValue(Student.class);
                if (student != null){
                    mListStudents.add(student);
                }
                mAttendanceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Student student = snapshot.getValue(Student.class);
                if(mListStudents == null || mListStudents.isEmpty()){
                    return;
                }

                for (int i=0; i < mListStudents.size(); i++){
                    if(student.getId() == mListStudents.get(i).getId()){
                        mListStudents.set(i,student);
                    }
                }
                mAttendanceAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}