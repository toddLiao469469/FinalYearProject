package com.example.caretaker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class HeartbeatFragment extends Fragment {

    Bundle bundle;
    String username;
    String user;
    String name;
    private static HeartbeatAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void setArguments(Bundle args) {
        this.bundle = args;
    }

    @Override
    public void onDestroyView() {
        ((AppCompatActivity)getContext()).getSupportActionBar().setTitle("看護資料");
        super.onDestroyView();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_heartbeat, container, false);
        final ListView listView = (ListView) view.findViewById(R.id.listView);
        username = bundle.getString("username");
        user = bundle.getString("user");
        name = bundle.getString("name");
        ((AppCompatActivity)getContext()).getSupportActionBar().setTitle(name+"的心跳紀錄");
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                adapter = new HeartbeatAdapter(getActivity(), username, user);
                listView.setAdapter(adapter);

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });




        adapter = new HeartbeatAdapter(getActivity(), username, user);
        listView.setAdapter(adapter);
        return view;
    }


}
