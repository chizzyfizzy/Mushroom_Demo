package com.ppem.psu.mushroomdemo4.Interface;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ppem.psu.mushroomdemo4.DatabaseControllers.CellDAO;
import com.ppem.psu.mushroomdemo4.DatabaseControllers.CountCellEntityDAO;
import com.ppem.psu.mushroomdemo4.DatabaseControllers.CountsDAO;
import com.ppem.psu.mushroomdemo4.Models.Cell;
import com.ppem.psu.mushroomdemo4.Models.Count;
import com.ppem.psu.mushroomdemo4.Models.CountCellEntity;
import com.ppem.psu.mushroomdemo4.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class ChartFragment extends Fragment {
    private long bedId;
    private List<Cell> cellList;
    private List<Count> spinnerCountList, countsForCell;
    private Context context;
    private CellDAO cellDataSource;
    private CountsDAO countDataSource;
    private CountCellEntityDAO countCellDataSource;
    private Long roomId;
    private ChartAdapter chartAdapter;
    private int chartCol;
    Count currentCount;
    ChartView2 activity;
    String bedName;


    public ChartFragment(){
        //Required fragment constructor
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

        countDataSource = new CountsDAO(context);
        countDataSource.open();
        spinnerCountList = countDataSource.getChartCounts(roomId);

        cellDataSource = new CellDAO(context);
        cellDataSource.open();
        cellList = new ArrayList<>();
        cellList = cellDataSource.getCellsForBed(bedId);

        countCellDataSource = new CountCellEntityDAO(context);
        countCellDataSource.open();
        countsForCell = new ArrayList<>();
        for(int i = 0; i < cellList.size(); i++){ //Loop that gets counts that were added to the cell. Sets a list of counts for the cell that are added to the view in the adapter.
            countsForCell = countCellDataSource.getDistinctCountsForCell(cellList.get(i).getCellId());
            cellList.get(i).setCountListInCell(countsForCell);
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.chart_fragment, container, false);
        chartAdapter = new ChartAdapter(context, cellList);
        GridView chartGV = (GridView) rootView.findViewById(R.id.chartGridView);
        chartGV.setNumColumns(chartCol);
        chartGV.setAdapter(chartAdapter);
        chartGV.setGravity(Gravity.TOP);
        chartGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cell selectedCell = cellList.get(position);
                //If cell already has counts. Search and find any matching counts to delete it. Otherwise add count.
                if(selectedCell.getCountListInCell().size() > 0){
                    if(!deleteCountFromCell(selectedCell)){
                        addCountToCell(selectedCell);
                    }
                }
                else{addCountToCell(selectedCell);}
            }
        });

        CountSpinnerAdapter spinnerAdapter = new CountSpinnerAdapter(context, R.layout.count_spinner_item, spinnerCountList);
        spinnerAdapter.setDropDownViewResource(R.layout.count_spinner_item);
        Spinner countSpinner = (Spinner) rootView.findViewById(R.id.countSpinner);
        countSpinner.setAdapter(spinnerAdapter);
        if(spinnerCountList.size() > 0) {
            currentCount = spinnerCountList.get(0);
        }
        countSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    currentCount = spinnerCountList.get(position);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        return rootView;
    }


    private void addCountToCell(Cell selectedCell){
        countCellDataSource.insertCountCellEntity(new CountCellEntity(selectedCell, currentCount), bedId);
        currentCount.setCountNumber(currentCount.getCountNumber() + 1);
        countDataSource.updateCountValue(currentCount.getCountId(), currentCount.getCountNumber());
        selectedCell.addCountListInCell(currentCount);
        chartAdapter.notifyDataSetChanged();
        Toast.makeText(context, "Added " + currentCount.getCountName() + " to square "
                + bedName
                + "-" + selectedCell.getCellColumn()
                + "(" + selectedCell.getCellRow() + ")", Toast.LENGTH_SHORT).show();
    }

    private boolean deleteCountFromCell(Cell selectedCell){
        for(int i = 0; i < selectedCell.getCountListInCell().size(); i++){
            if(currentCount.getCountId() == selectedCell.getCountListInCell().get(i).getCountId()){
                countCellDataSource.deleteCountCell(selectedCell.getCountListInCell().get(i).getCountId(), selectedCell.getCellId(),bedId);
                currentCount.setCountNumber(currentCount.getCountNumber() -1 );
                countDataSource.updateCountValue(currentCount.getCountId(), currentCount.getCountNumber());
                selectedCell.getCountListInCell().remove(i);
                chartAdapter.notifyDataSetChanged();
                Toast.makeText(context, "Deleted " + currentCount.getCountName() + " from square "
                        + bedName
                        + "-" + selectedCell.getCellColumn()
                        + "(" + selectedCell.getCellRow() + ")", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }


}
