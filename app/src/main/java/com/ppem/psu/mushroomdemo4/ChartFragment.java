package com.ppem.psu.mushroomdemo4;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ChartFragment extends Fragment {
    private long bedId;
    public Bed bed;


    private List<Cell> cellList;
    private List<Count> countList, countsForCell;
    private List<CountCellEntity> countCellEntityList;
    private Context context;
    private CellDAO cellDataSource;
    private CountCellEntityDAO countCellDataSource;
    private Long roomId;
    private ChartAdapter chartAdapter;
    private GridView gridView;
    private Spinner countSpinner;
    private int chartCol;
    Count currentCount;
    ChartView2 activity;
    String bedName;

    public ChartFragment(){

    }

    public static ChartFragment newInstance(int page, String title, long roomId, long bedId){
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putInt("page",page);
        args.putString("title",title);
        args.putLong("room",roomId);
        args.putLong("bed",bedId);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        roomId = args.getLong("room");
        bedId = args.getLong("bed");
        chartCol = args.getInt("level");
        bedName = args.getString("name");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (ChartView2)getActivity();
        context = getActivity();
        countList = activity.countList;

        cellDataSource = new CellDAO(context);
        cellDataSource.open();
        cellList = new ArrayList<>();
        cellList = cellDataSource.getCellsForBed(bedId);

        countCellDataSource = new CountCellEntityDAO(context);
        countCellDataSource.open();
        countCellEntityList = new ArrayList<>();
        countCellEntityList = countCellDataSource.getAllCountCellsBed(bedId);
        countsForCell = new ArrayList<>();
        for(int i = 0; i < cellList.size(); i++){
            countsForCell = countCellDataSource.getDistinctCountsForCell(cellList.get(i).getCellId());
            cellList.get(i).setCountListInCell(countsForCell);
        }


        Toast.makeText(context, "Bed " + String.valueOf(bedId),Toast.LENGTH_SHORT).show();
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.chart_fragment, container, false);
        chartAdapter = new ChartAdapter(context, cellList);
        gridView = (GridView) rootView.findViewById(R.id.chartGridView);
        gridView.setNumColumns(chartCol);
        gridView.setAdapter(chartAdapter);
        gridView.setGravity(Gravity.TOP);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cell selectedCell = cellList.get(position);
                view.setBackgroundColor(Color.GREEN);
                TextView tempTV = (TextView) view.findViewById(R.id.countsAddedLabel);
                String currentCountsString = tempTV.getText().toString();
                if(currentCountsString.equals("")) {
                    tempTV.setText(String.valueOf(currentCount.getCountName().charAt(0)));
                    currentCountsString = String.valueOf(currentCount.getCountName().charAt(0));;
                } else{
                    currentCountsString = currentCountsString + "-" + String.valueOf(currentCount.getCountName().charAt(0));
                    tempTV.setText(currentCountsString);
                }
                chartAdapter.countsForCellString[position] = currentCountsString;
                countCellDataSource.insertCountCellEntity(new CountCellEntity(selectedCell, currentCount), bedId);
                selectedCell.addCountListInCell(currentCount);
                String tempCellLabel = bedId + "-" + selectedCell.getCellColumn() + "-(" + selectedCell.getCellRow() + ")";
                Toast.makeText(context, "Added " + currentCount.getCountName() +" to square " + tempCellLabel, Toast.LENGTH_SHORT).show();
            }
        });

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

    public void updateChart(){
        bedId = activity.bed.getBedId();
        cellList = cellDataSource.getCellsForBed(bedId);
        chartAdapter.notifyDataSetChanged();
    }

}
