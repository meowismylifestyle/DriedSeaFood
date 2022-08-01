package com.example.myapplication;

import static com.example.myapplication.LoginActivity.mAuth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailEdit, passEdit, fullName, birthdayTextEdit;
    private ProgressBar progressBar;
    private Button buttonRegister;

    final Calendar birthdayCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEdit = findViewById(R.id.email);
        passEdit = findViewById(R.id.password);
        buttonRegister = findViewById(R.id.btnregis);
        progressBar = findViewById(R.id.progress_bar);
        fullName = findViewById(R.id.fullName);
        birthdayTextEdit = findViewById(R.id.birthday_EditText);

        DatePickerDialog.OnDateSetListener birthdayPicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                birthdayCalendar.set(Calendar.YEAR, year);
                birthdayCalendar.set(Calendar.MONTH, month);
                birthdayCalendar.set(Calendar.DAY_OF_MONTH, day);

                // Update birthday edit text
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                birthdayTextEdit.setText(dateFormat.format(birthdayCalendar.getTime()));
            }
        };

        birthdayTextEdit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new DatePickerDialog(
                                RegisterActivity.this,
                                birthdayPicker,
                                birthdayCalendar.get(Calendar.YEAR),
                                birthdayCalendar.get(Calendar.MONTH),
                                birthdayCalendar.get(Calendar.DAY_OF_MONTH)
                        ).show();
                    }
                }
        );

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {
        String email, pass, name, day, month, year, birthday;
        day = month = year = "";

        email = emailEdit.getText().toString().replaceAll("\\s+", "");
        pass = passEdit.getText().toString().replaceAll("\\s+", "");
        name = fullName.getText().toString();

        birthday = birthdayTextEdit.getText().toString();
        String[] dateParts = birthday.split("/");

        boolean validBirthday = false;
        if (dateParts.length == 3) {
            day = dateParts[0];
            month = dateParts[1];
            year = dateParts[2];
            validBirthday = true;
        }

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, getResources().getString(R.string.please_enter_your_name), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validBirthday){
            Toast.makeText(this, getResources().getString(R.string.please_enter_your_birthday), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, getResources().getString(R.string.please_enter_your_email), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, getResources().getString(R.string.please_enter_valid_email),Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(pass)){
            Toast.makeText(this, getResources().getString(R.string.please_enter_your_password),Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.length() < 6){
            passEdit.setError(getString(R.string.min_password_length_is_6));
            passEdit.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Create user on Firebase Authentication service
        String finalDay = day;
        String finalMonth = month;
        String finalYear = year;
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
                            getResources().getString(R.string.registered_successfully),
                            Toast.LENGTH_LONG
                    ).show();

                    // And save the registration information to Firebase database
                    User user = new User(name, finalDay, finalMonth, finalYear, email);
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
                                                getResources().getString(R.string.failed_to_register),
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
                            getResources().getString(R.string.failed_to_register),
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
                        getResources().getString(R.string.failed_to_register),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
