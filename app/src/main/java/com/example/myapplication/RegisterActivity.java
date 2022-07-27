package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailedit,passedit,fullname,Day,Month,Year;
    private ProgressBar progressBar;
    private Button btnregis;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth=FirebaseAuth.getInstance();

        emailedit=findViewById(R.id.email);
        passedit=findViewById(R.id.password);
        btnregis=findViewById(R.id.btnregis);
        progressBar = findViewById(R.id.progress_bar);
        fullname = findViewById(R.id.fullName);
        Day = findViewById(R.id.day);
        Month = findViewById(R.id.month);
        Year = findViewById(R.id.year);
        btnregis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {
        String email,pass,name,day,month,year;
        email=emailedit.getText().toString();
        pass=passedit.getText().toString();
        name = fullname.getText().toString();
        day = Day.getText().toString();
        month = Month.getText().toString();
        year = Year.getText().toString();
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please enter your name!!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(day) || TextUtils.isEmpty(month) || TextUtils.isEmpty(year) ){
            Toast.makeText(this,"Please enter your age!!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter your email!!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Please enter valid email!!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pass)){
            Toast.makeText(this,"Please enter your password!!",Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.length() < 6){
            passedit.setError("Min password length should be 6 characters");
            passedit.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    User user = new User(name,day,month,year,email);
                    mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                                        user1.sendEmailVerification();
                                        Toast.makeText(RegisterActivity.this,"Check your email to verify your account!", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(RegisterActivity.this,"Email not Existed", Toast.LENGTH_LONG).show();
                                }
                        }
                    });
                    FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                            Toast.makeText(RegisterActivity.this, "Registered successfully!!", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            progressBar.setVisibility(View.GONE);

                                    }else {
                                        Toast.makeText(RegisterActivity.this,"Failed to Registerer!!",Toast.LENGTH_LONG).show();
                                    }
                                }
                                });
                            }else {
                    Toast.makeText(RegisterActivity.this,"Failed to Registerer!!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
