package com.example.caretaker;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

public class AssesusFragment extends Fragment {


    public AssesusFragment() {
        // Required empty public constructor
    }
    @Override
    public void onDestroyView() {
        ((AppCompatActivity) getContext()).getSupportActionBar().setTitle("看護資料");
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_assesus, container, false);
        ((AppCompatActivity) getContext()).getSupportActionBar().setTitle("評價我們");
        RatingBar ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#ea9c4d"), PorterDuff.Mode.SRC_ATOP);

        return v;
    }
}


