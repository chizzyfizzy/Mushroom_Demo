package com.ppem.psu.mushroomdemo4;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ChartFragment extends Fragment {
    private List<Cell> cellList;
    private List<Count> countList, countsForCell;
    private Context context;
    private CellDAO cellDataSource;
    private CountsDAO countDataSource;
    private CountCellEntityDAO countCellDataSource;
    private Long roomId;
    private ChartAdapter chartAdapter;
    private GridView gridView;
    private Spinner countSpinner;
    private int chartCol;
    Count currentCount;

    public ChartFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        roomId = bundle.getLong("room");
        chartCol = bundle.getInt("column");
        String bed = bundle.getString("bed");
        context = getActivity();

        countDataSource = new CountsDAO(context);
        countDataSource.open();
        countList = countDataSource.getChartCounts(roomId);

        cellDataSource = new CellDAO(context);
        cellDataSource.open();
        cellList = cellDataSource.getCellsForRoomBed(roomId, "A");
        countsForCell = new ArrayList<Count>();

        countCellDataSource = new CountCellEntityDAO(context);
        countCellDataSource.open();


        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_fragment_a, container, false);
        chartAdapter = new ChartAdapter(context, cellList);
        gridView = (GridView) rootView.findViewById(R.id.chartGridView);
        gridView.setNumColumns(chartCol);
        gridView.setAdapter(chartAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cell selectedCell = chartAdapter.getItem(position);
                view.setBackgroundColor(Color.GREEN);
                chartAdapter.booleanArray.put(position, true);

               // TextView tempTV = (TextView) view.findViewById(R.id.countsAddedLabel);
                TextView tempTV = new TextView(context);
                LinearLayout tempL = (LinearLayout) view.findViewById(R.id.countAddLinearLayout);
                //If empty add first "Letter"
                if(tempTV.getText().toString().isEmpty()) {
                    tempTV.setText(Character.toString(currentCount.getCountName().charAt(0)));
                    tempL.addView(tempTV);
                }//If not empty add "-Letter"
                else{
                    String temp = tempTV.getText().toString();
                    tempTV.setText(temp + "-" + Character.toString(currentCount.getCountName().charAt(0)));
                }
                countCellDataSource.insertCountCellEntity(new CountCellEntity(selectedCell, currentCount), roomId);
                String tempCellLabel = selectedCell.getCellBed() + "-" + selectedCell.getCellColumn() + "-(" + selectedCell.getCellRow() + ")";
                Toast.makeText(context, "Added " + currentCount.getCountName() +" to square " + tempCellLabel, Toast.LENGTH_SHORT).show();
                countsForCell = countCellDataSource.getCountsForCell(selectedCell.getCellId());
                if(countsForCell == null){
                    System.out.println("Counts for Cell list empty");
                }
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
        //return inflater.inflate(R.layout.fragment_fragment_a, container, false);
    }


}
