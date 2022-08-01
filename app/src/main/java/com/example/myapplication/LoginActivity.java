package com.example.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    EditText emailEdit, passEdit;
    Button buttonLogin;
    TextView forgetPassword, buttonRegister;
    ProgressBar progressBar;
    ImageButton buttonLanguage;

    public static FirebaseAuth mAuth;
    public static FirebaseUser firebaseUser;
    public static DatabaseReference currentUserReference;
    public static User currentUser;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        LocaleHelper.onCreate(LoginActivity.this.getBaseContext());
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.app_name));
        }

        mAuth = FirebaseAuth.getInstance();

        emailEdit = findViewById(R.id.email);
        passEdit = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btnlogin);
        buttonRegister = findViewById(R.id.btnregis);
        progressBar = findViewById(R.id.progress_bar);
        forgetPassword = findViewById(R.id.forgotpassword);
        buttonLanguage = findViewById(R.id.language_button);

        buttonLogin.setOnClickListener(v -> login());
        buttonRegister.setOnClickListener(v -> register());
        forgetPassword.setOnClickListener(v -> forgetPassword());
        buttonLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocaleHelper.showChangeLanguageDialog(
                        LoginActivity.this, LoginActivity.this
                );
            }
        });
    }

    private void register() {
        Intent i =new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(i);
    }

    private void forgetPassword(){
        Intent i = new Intent(LoginActivity.this,ForgotPassword.class);
        startActivity(i);
    }

    private void login() {
        String email,pass;
        email = emailEdit.getText().toString().replaceAll("\\s+", "");
        pass = passEdit.getText().toString().replaceAll("\\s+", "");

        // Check text edit of email address
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this,
                    getResources().getString(R.string.please_enter_email),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Check text edit of password
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this,
                    getResources().getString(R.string.please_enter_password),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Try sign-in with user-typed email and password
        mAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        firebaseUser = LoginActivity.mAuth.getCurrentUser();

                        // Check if account is verified via email
                        if (firebaseUser != null && firebaseUser.isEmailVerified()) {

                            // If account is verified, get the database reference
                            // and retrieve user's information
                            Toast.makeText(
                                    getApplicationContext(),
                                    getResources().getString(R.string.login_successfully),
                                    Toast.LENGTH_SHORT
                            ).show();

                            // Get database reference
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            currentUserReference = database
                                    .getReference("Users")
                                    .child(firebaseUser.getUid());

                            // Retrieve user's information
                            currentUserReference
                                    .addListenerForSingleValueEvent(
                                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    LoginActivity.currentUser = snapshot.getValue(User.class);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.d("LoginActivity", "get data failed");
                                }
                            });

                            // Move to MainActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            ActivityCompat.finishAffinity(LoginActivity.this);

                        } else {

                            // If account is not verified, show the Toast message to user
                            Toast.makeText(
                                    getApplicationContext(),
                                    getResources().getString(R.string.you_havent_verify_your_account),
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    } else {

                        // If sign-in unsuccessfully, show the Toast notification
                        Toast.makeText(
                                getApplicationContext(),
                                getResources().getString(R.string.cant_login),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }
}
