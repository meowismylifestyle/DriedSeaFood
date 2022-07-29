package com.example.myapplication;

import static com.example.myapplication.LoginActivity.currentUser;
import static com.example.myapplication.LoginActivity.currentUserReference;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UserFragment extends Fragment {

    Button updateButton;
    TextInputEditText fullnameView, emailView, birthdayView;

    private View view;
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);

        if (view != null) {
            fullnameView = view.findViewById(R.id.fullname_user);
            emailView = view.findViewById(R.id.email_user);
            birthdayView = view.findViewById(R.id.birthday_user);
            updateButton = view.findViewById(R.id.user_update_button);

            fullnameView.setText(currentUser.getFullName());
            emailView.setText(currentUser.getEmail());
            birthdayView.setText(currentUser.getBirthday());

            // Disable editing email in this screen
            emailView.setFocusable(false);

            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickUpdateButton();
                }
            });
        }

        return view;
    }

    void onClickUpdateButton() {
        currentUser.setFullName(Objects.requireNonNull(fullnameView.getText()).toString());
        currentUser.setEmail(Objects.requireNonNull(emailView.getText()).toString());
        currentUser.setBirthday(Objects.requireNonNull(birthdayView.getText()).toString());
        currentUserReference.setValue(currentUser);
    }

    public UserFragment(){
        super(R.layout.activity_user);;
    }
}

