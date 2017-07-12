package com.ppem.psu.mushroomdemo4;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitchell on 6/29/2017.
 */

public class ChartFragment extends Fragment{
    public static int columns, rows;
    public static List<String> cellList;


    public ChartFragment() {
    }

    public static ChartFragment newInstance(int colNum, int rowNum) {
        ChartFragment fragment = new ChartFragment();
        columns = colNum;
        rows = rowNum;
        cellList = new ArrayList<>();
        //TODO Create for loops to populate gridview based on columns and rows.
        for(int i = 1; i < rowNum + 1; i++) {
            for(int j = 1; j < colNum + 1; j++){
                if(j*rowNum+i >= colNum) {
                    cellList.add(String.valueOf(j) + "(" + String.valueOf(i) + ")");
                }
                else{
                    cellList.add(String.valueOf(j) + "(" + String.valueOf(i*rowNum + i));
                }
            }
        }
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chart, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.chartGridView);
        Button saveButton = (Button) rootView.findViewById(R.id.saveGridDataButton);
        gridView.setNumColumns(columns);
        gridView.setAdapter(new ChartGridViewAdapter(getContext(), cellList));
        return rootView;
    }
}
