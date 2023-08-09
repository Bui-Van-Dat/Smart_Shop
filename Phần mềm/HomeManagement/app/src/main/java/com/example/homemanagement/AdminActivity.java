package com.example.homemanagement;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdminActivity extends AppCompatActivity {

    EditText edtId, edtName, edtTeam;
    TextView btnAddUser;
    TextView btnAttendance;
    RecyclerView rcvStudents;
    StudentAdapter mStudentAdapter;
    List<Student> mListStudents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Khởi tạo OnBackPressedCallback
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Thực hiện hành động của bạn ở đây, ví dụ: chuyển về activity trước đó
                finish();
            }
        };

        // Đăng ký OnBackPressedCallback với hệ thống
        getOnBackPressedDispatcher().addCallback(this, callback);

        initUi();
        btnAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddMonth.class));
            }
        });
        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = Integer.parseInt(edtId.getText().toString().trim());
                String name = edtName.getText().toString().trim();
                int team = Integer.parseInt(edtTeam.getText().toString().trim());
                Student student = new Student(id, name, team, 0);
                onClickAddStudent(student);
            }
        });
        getListStudentsFromDatabase();
//
//        Button btnPushData = (Button) findViewById(R.id.btnPushData);
//        Button btnGetData = (Button) findViewById(R.id.btnGetData);
//        Button btnUpdateData = (Button) findViewById(R.id.btnUpdateData);
//        tvData = (TextView) findViewById(R.id.tvGetData);
//
//        btnPushData.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onClickPushData();
//            }
//        });
//
//        btnGetData.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onClickGetData();
//            }
//        });
//
//        btnUpdateData.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onClickUpdateData();
//            }
//        });
    }

    private void onClickAddStudent(Student student) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("list_users");

        String pathObject = String.valueOf(student.getId());
        myRef.child(pathObject).setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Data added successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initUi() {
        edtId = findViewById(R.id.edtId);
        edtName = findViewById(R.id.edtName);
        edtTeam = findViewById(R.id.edtTeam);
        btnAddUser = findViewById(R.id.btnAddStudent);
        btnAttendance = findViewById(R.id.btnAttendance);
        rcvStudents = findViewById(R.id.rcv_students);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvStudents.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvStudents.addItemDecoration(dividerItemDecoration);

        mListStudents = new ArrayList<>();
        mStudentAdapter = new StudentAdapter(mListStudents);

        rcvStudents.setAdapter(mStudentAdapter);
    }

    private void getListStudentsFromDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("list_users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mListStudents != null) {
                    mListStudents.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Student student = dataSnapshot.getValue(Student.class);
                    mListStudents.add(student);
                }
                mStudentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    private void onClickUpdateData() {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("list_users/absent");
//        int as = Integer.parseInt((String) tvData.getText());
//        myRef.setValue(as);
//
//    }
//
//    private void onClickPushData() {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("my_map");
//
//        Map<String,Boolean> map = new HashMap<>();
//        map.put("1",true);
//        map.put("2",true);
//        map.put("3",false);
//        map.put("4",true);
//
//
////        Student user = new Student(20182410,"Bui Van Dat",7,0);
//        myRef.setValue(map);
//    }
//
//    private void onClickGetData() {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("my_map");
//
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Map<String, Boolean> mapResult = new HashMap<>();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    String key = dataSnapshot.getKey();
//                    Boolean value = dataSnapshot.getValue(Boolean.class);
//
//                    mapResult.put(key,value);
//                }
//                tvData.setText(mapResult.toString());
////                Student student = snapshot.getValue(Student.class);
////                tvData.setText(student.toString());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    public void logoutAdmin(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}