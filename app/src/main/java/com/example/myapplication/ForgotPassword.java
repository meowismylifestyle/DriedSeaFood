package com.example.myapplication;

import static com.example.myapplication.LoginActivity.mAuth;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPassword extends AppCompatActivity {
    private EditText emailEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEdt = findViewById(R.id.email_forgot);
        Button resetPass = findViewById(R.id.resetPassword);

        resetPass.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword(){
        String email = emailEdt.getText().toString().trim();

        if(email.isEmpty()){
            emailEdt.setError(getString(R.string.email_is_required));
            emailEdt.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEdt.setError(getString(R.string.provide_valid_email));
            emailEdt.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(ForgotPassword.this,
                        getResources().getString(R.string.check_your_email_to_reset_password),
                        Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(ForgotPassword.this,
                        getResources().getString(R.string.try_again),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}
