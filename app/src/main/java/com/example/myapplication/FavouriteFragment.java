package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FavouriteFragment extends Fragment {
    private View view;
    private TextView hello;

    public FavouriteFragment(){
        super(R.layout.hello);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);

        hello = view.findViewById(R.id.hello);
        hello.setText(LoginActivity.currentUser.getFavouriteFishes().toString());

        return view;
    }
}
