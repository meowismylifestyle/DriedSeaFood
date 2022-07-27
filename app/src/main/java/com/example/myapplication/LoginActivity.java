package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {
    private EditText emailedit,passedit;
    private Button btnlogin;
    private TextView forget_pass, btnregis;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();

        emailedit=findViewById(R.id.email);
        passedit=findViewById(R.id.password);
        btnlogin=findViewById(R.id.btnlogin);
        btnregis=findViewById(R.id.btnregis);
        progressBar = findViewById(R.id.progress_bar);
        forget_pass = findViewById(R.id.forgotpassword);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        btnregis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        forget_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetpass();
            }
        });
    }
    private void register() {
        Intent i =new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(i);
    }

    private void forgetpass(){
        Intent i = new Intent(LoginActivity.this,ForgotPassword.class);
        startActivity(i);
    }

    private void login() {
        String email,pass;
        email=emailedit.getText().toString();
        pass=passedit.getText().toString();


        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please Enter Email!!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pass)){
            Toast.makeText(this,"Please Enter Password!!",Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user.isEmailVerified()){
                    Toast.makeText(getApplicationContext(),"Login Successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent =new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    progressBar.setVisibility(View.GONE);
                    }else {
                        Toast.makeText(getApplicationContext(),"Can't Loginn!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Can't Loginn!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
