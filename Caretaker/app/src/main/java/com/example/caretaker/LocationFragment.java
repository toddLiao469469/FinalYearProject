package com.example.caretaker;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocationFragment extends Fragment {
    Bundle bundle;
    String username;
    String user;
    String name;
    private static LocationAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    public void setArguments(@Nullable Bundle args) {
        this.bundle = args;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        username = bundle.getString("username");
        user = bundle.getString("user");
        name = bundle.getString("name");
        ((AppCompatActivity)getContext()).getSupportActionBar().setTitle(name+"的地點紀錄");
        final ListView listView = (ListView)view.findViewById(R.id.listView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                adapter = new LocationAdapter(getActivity(), username, user);
                listView.setAdapter(adapter);

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });


        final LocationAdapter adapter = new LocationAdapter(getActivity(), username,user);
        listView.setAdapter(adapter);
        return view;
    }


}
