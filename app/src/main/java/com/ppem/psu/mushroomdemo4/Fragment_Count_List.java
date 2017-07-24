package com.ppem.psu.mushroomdemo4;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;


public class Fragment_Count_List extends Fragment {

    public Fragment_Count_List() {
        // Required empty public constructor
    }


    public static Fragment_Count_List newInstance(String param1, String param2) {
        Fragment_Count_List fragment = new Fragment_Count_List();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return container;
    }


}
