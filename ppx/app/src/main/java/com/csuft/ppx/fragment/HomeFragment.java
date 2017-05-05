package com.csuft.ppx.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.csuft.ppx.R;
import com.csuft.ppx.activity.ParkActivity;
import com.csuft.ppx.activity.SearchActivity;


public class HomeFragment extends Fragment implements View.OnClickListener{
    private Button butstop;
    private Button butleave;
    private Button butselpark;
    private ImageView bar;
    private DrawerLayout drawerLayout;
    private LinearLayout registerLin;
    private LinearLayout loginLin;
    private Toolbar toolbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initWidget(rootView);
        return rootView;
    }
    private void initWidget(View view){
        butstop = (Button)view.findViewById(R.id.butstop);
        butleave = (Button)view.findViewById(R.id.butleave);
        butselpark = (Button)view.findViewById(R.id.butselpark);
        butselpark.setOnClickListener(this);
        butstop.setOnClickListener(this);
        butleave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.butstop:
                break;
            case R.id.butleave:
                Intent intent2 = new Intent(getActivity(), ParkActivity.class);
                startActivity(intent2);
                break;
            case R.id.butselpark:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                break;
        }
    }
}
