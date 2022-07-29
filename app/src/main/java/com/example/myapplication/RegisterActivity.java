package com.example.myapplication;

import static com.example.myapplication.LoginActivity.mAuth;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailEdit, passEdit, fullName, day, month, year;
    private ProgressBar progressBar;
    private Button buttonRegister;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEdit = findViewById(R.id.email);
        passEdit = findViewById(R.id.password);
        buttonRegister = findViewById(R.id.btnregis);
        progressBar = findViewById(R.id.progress_bar);
        fullName = findViewById(R.id.fullName);
        day = findViewById(R.id.day);
        month = findViewById(R.id.month);
        year = findViewById(R.id.year);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {
        String email, pass, name, day, month, year;

        email = emailEdit.getText().toString();
        pass = passEdit.getText().toString();
        name = fullName.getText().toString();
        day = this.day.getText().toString();
        month = this.month.getText().toString();
        year = this.year.getText().toString();

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please enter your name!!",Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(day) || TextUtils.isEmpty(month) || TextUtils.isEmpty(year) ){
            Toast.makeText(this,"Please enter your age!!",Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter your email!!",Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Please enter valid email!!",Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(pass)){
            Toast.makeText(this,"Please enter your password!!",Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.length() < 6){
            passEdit.setError("Min password length should be 6 characters");
            passEdit.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Create user on Firebase Authentication service
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener (
                this,
                new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    // If create User on Firebase successfully, then send
                    // verification email to User's email address
                    FirebaseUser createdUser = mAuth.getCurrentUser();
                    if (createdUser != null) createdUser.sendEmailVerification();

                    // Show the toast notification to user
                    // to remind user to check verification email
                    Toast.makeText(RegisterActivity.this,
                            "Registered Successfully!\nPlease check your email to verify account before you log in!",
                            Toast.LENGTH_LONG
                    ).show();

                    // And save the registration information to Firebase database
                    User user = new User(name, day, month, year, email);
                    FirebaseDatabase.getInstance()
                            .getReference("Users")
                            .child(mAuth.getCurrentUser().getUid())
                            .setValue(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        // If save information to Firebase database
                                        // successfully, then move to LoginActivity
                                        Intent intent = new Intent (
                                                RegisterActivity.this,
                                                LoginActivity.class
                                        );
                                        startActivity(intent);
                                        progressBar.setVisibility(View.GONE);

                                    } else {

                                        // If failed to save information to Firebase database
                                        // then show toast notification to user
                                        Toast.makeText(
                                                RegisterActivity.this,
                                                "Failed to Register!",
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                                }
                            });
                } else {

                    // If failed to create User on Firebase Authentication Service
                    // then show toast notification to user
                    Toast.makeText (
                            RegisterActivity.this,
                            "Failed to Register!",
                            Toast.LENGTH_LONG
                    ).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

        }).addOnFailureListener(new OnFailureListener() {

            // If failed to create User on Firebase Authentication Service
            // then show toast notification to user
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(
                        RegisterActivity.this,
                        "Failed to register!",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
