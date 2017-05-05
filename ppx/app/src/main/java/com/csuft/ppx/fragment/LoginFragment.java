package com.csuft.ppx.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csuft.ppx.R;

/**
 * Created by ling on 2017/5/5.
 */

public class LoginFragment extends Fragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        initWidget(rootView);
        return rootView;
    }
    private void initWidget(View view){

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
