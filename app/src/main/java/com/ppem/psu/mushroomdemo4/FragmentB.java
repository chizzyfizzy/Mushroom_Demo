package com.ppem.psu.mushroomdemo4;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class FragmentB extends Fragment implements ChartRecyclerViewAdapter.ItemClickListener {
    private List<Cell> cellList;
    private List<Count> countList;
    private Context context;
    private CellDAO cellDataSource;
    private CountsDAO countDataSource;
    private long roomId;
    private ChartRecyclerViewAdapter chartAdapter;
    private RecyclerView gridView;
    private int chartCol;
    private Spinner countSpinner;
    Count currentCount;

    public FragmentB(){
        //Required Empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        roomId = bundle.getLong("room");
        chartCol = bundle.getInt("column");

        context = getActivity();

        countDataSource = new CountsDAO(context);
        countDataSource.open();
        countList = countDataSource.getChartCounts(roomId);

        cellDataSource = new CellDAO(context);
        cellDataSource.open();
        cellList = cellDataSource.getCellsForRoomBed(roomId, "B");


        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_fragment_b, container, false);
        gridView = (RecyclerView) rootView.findViewById(R.id.chartGridView);
        gridView.setLayoutManager(new GridLayoutManager(context, chartCol));
        //This should edit spacing but I don't believe it does anything right now. To change spacing edit the RelativeLayout in grid_chart_item.xml
       // gridView.addItemDecoration(new ChartRecyclerDecoration(15));
        chartAdapter = new ChartRecyclerViewAdapter(context, cellList);
        chartAdapter.setClickListener(this);
        gridView.setAdapter(chartAdapter);




        CountSpinnerAdapter spinnerAdapter = new CountSpinnerAdapter(context, R.layout.count_spinner_item, countList);
        spinnerAdapter.setDropDownViewResource(R.layout.count_spinner_item);
        countSpinner = (Spinner) rootView.findViewById(R.id.countSpinner);
        countSpinner.setAdapter(spinnerAdapter);
        if(countList.size() > 0) {
            currentCount = countList.get(0);
        }
        countSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCount = countList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return rootView;
    }

    @Override
    public void onItemClick(View view, int position) {
        final ChartViewHolder holder = (ChartViewHolder) view.getTag();
        view.setBackgroundColor(Color.GREEN);

        TextView tempTV = (TextView) view.findViewById(R.id.countsAddedLabel);
        if(tempTV.getText().toString().isEmpty()) {
            tempTV.setText(Character.toString(currentCount.getCountName().charAt(0)));
        }
        else{
            String temp = tempTV.getText().toString();
            tempTV.setText(temp + "-" + Character.toString(currentCount.getCountName().charAt(0)));
        }
        //TODO add selected count+cell to COUNT+CELLS table
        //TODO add boolean to cell table to check if selected for count? (BUt what if multiple count?)
        Cell tempCell = chartAdapter.getItem(position);
        String tempCellLabel = tempCell.getCellBed() + "-" + tempCell.getCellColumn() + "-(" + tempCell.getCellRow() + ")";
        Toast.makeText(context, "Added " + currentCount.getCountName() +" to square " + tempCellLabel, Toast.LENGTH_SHORT).show();
    }
}
