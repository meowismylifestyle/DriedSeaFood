package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavouriteFragment extends Fragment {
    View view;
    RecyclerView recyclerViewFavouriteFish;
    FishAdapter fishAdapter;
    FragmentActivity activity;
    Context context;


    public FavouriteFragment(){
        super(R.layout.activity_favourite);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this.getActivity();
        if (activity == null)
            Log.d("FavouriteFragment", "activity is null");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);

        if (view != null) {
            context = view.getContext();
            recyclerViewFavouriteFish = view.findViewById(R.id.rcv_fav_fish);
        } else
            Log.d("SearchFragment", "onCreateView: view is null");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerViewFavouriteFish.setLayoutManager(linearLayoutManager);

        fishAdapter = new FishAdapter(LoginActivity.currentUser.getFavouriteFishItem());
        recyclerViewFavouriteFish.setAdapter(fishAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration (
                view.getContext(), DividerItemDecoration.VERTICAL
        );
        recyclerViewFavouriteFish.addItemDecoration(itemDecoration);

        return view;
    }
}
