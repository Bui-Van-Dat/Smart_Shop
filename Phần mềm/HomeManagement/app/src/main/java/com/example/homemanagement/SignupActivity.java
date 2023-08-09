package com.example.homemanagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword, edtName, edtPhone;
    TextView btnCreateAccount;
    ProgressDialog progressDialog;
    FirebaseFirestore fStore;
    FirebaseAuth mAuth;
    FirebaseUser fUser;
    boolean valid = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initUi();
        initListener();
    }

    private void initListener() {
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignup();
            }
        });
    }

    private void onClickSignup() {
        checkField(edtEmail);
        checkField(edtPassword);
        checkField(edtName);
        checkField(edtPhone);

//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//
//        String strEmail = edtEmail.getText().toString().trim();
//        String strPassword = edtPassword.getText().toString().trim();
//        progressDialog.show();
//        mAuth.createUserWithEmailAndPassword(strEmail, strPassword)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        progressDialog.dismiss();
//                        if (task.isSuccessful()) {
////                                FirebaseUser user = mAuth.getCurrentUser();
////                                DocumentReference df = fStore.collection("Users").document(user.getUid());
////                                Map<String, Object> userInfo = new HashMap<>();
////                                userInfo.put("FullName", edtName.getText().toString());
////                                userInfo.put("UserEmail", edtEmail.getText().toString());
////                                userInfo.put("PhoneNumber", edtPhone.getText().toString());
////                                userInfo.put("Pass", edtPassword.getText().toString());
////                                userInfo.put("isUser", "1");
////                                df.set(userInfo);
//
//                            // Sign in success, update UI with the signed-in user's information
//                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
//                            startActivity(intent);
//                            finishAffinity();
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Toast.makeText(SignupActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

        if (valid) {
            mAuth = FirebaseAuth.getInstance();
            fStore = FirebaseFirestore.getInstance();

            String strEmail = edtEmail.getText().toString().trim();
            String strPassword = edtPassword.getText().toString().trim();
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(strEmail, strPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                fUser = mAuth.getCurrentUser();
                                DocumentReference df = fStore.collection("Users").document(fUser.getUid());
                                Map<String, Object> userInfo = new HashMap<>();
                                userInfo.put("FullName", edtName.getText().toString());
                                userInfo.put("UserEmail", edtEmail.getText().toString());
                                userInfo.put("PhoneNumber", edtPhone.getText().toString());
                                userInfo.put("Pass", edtPassword.getText().toString());
                                userInfo.put("isUser", "1");
                                df.set(userInfo);

                                // Sign in success, update UI with the signed-in user's information
                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                startActivity(intent);
                                finishAffinity();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignupActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void initUi() {
        edtEmail = (EditText) findViewById(R.id.registerEmail);
        edtPassword = (EditText) findViewById(R.id.registerPassword);
        edtName = (EditText) findViewById(R.id.registerName);
        edtPhone = (EditText) findViewById(R.id.registerPhone);
        btnCreateAccount = (TextView) findViewById(R.id.registerBtn);
        progressDialog = new ProgressDialog(this);

    }

    public boolean checkField(EditText textField) {
        if (textField.getText().toString().isEmpty()) {
            textField.setError("Error");
            valid = false;
        } else {
            valid = true;
        }

        return valid;
    }
}