package com.kondenko.pocketwaka.screens.fragments.leaders;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kondenko.pocketwaka.R;

public class FragmentLeaders extends Fragment {


    public FragmentLeaders() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaders, container, false);
    }

}
