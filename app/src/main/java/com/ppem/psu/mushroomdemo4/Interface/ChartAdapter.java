package com.ppem.psu.mushroomdemo4.Interface;

import android.content.Context;

import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.ppem.psu.mushroomdemo4.Models.Cell;
import com.ppem.psu.mushroomdemo4.Models.Count;
import com.ppem.psu.mushroomdemo4.R;

import java.util.List;

/**
 * Created by Mitchell on 7/17/2017.
 */

public class ChartAdapter extends ArrayAdapter<Cell> {
    List<Cell> cellList;
    SparseBooleanArray booleanArray = new SparseBooleanArray();
    String[] countsForCellString;
    private Context context;
    int totalHeight = 0;
    int items;
    int rows = 0;

    public ChartAdapter(Context context, List<Cell> cells){
        super(context, 0, cells);
        this.context = context;
        this.cellList = cells;
        this.countsForCellString = new String[cells.size()];
        //this.countsForCell = countsForCell;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Cell cell = cellList.get(position);
        ChartViewHolder viewHolder;
        items = cellList.size();
        String addCountToCellString = "";
        // countsForCellString = new String[cellList.size()];

        for(int i = 0;  i < cell.getCountListInCell().size();i++){
            addCountToCellString += cell.getCountListInCell().get(i).getCountName().charAt(0);
        }


        if(countsForCellString[position] == null) {
            countsForCellString[position] = (addCountToCellString);
        }

        if(view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.grid_chart_item, null);

            //For some reason this makes the grid cells nicer looking.
            //It was an attempt to make the whole gridview fit the screen with no scrolling to avoid having the added count-textviews from appearing in random places.
            view.measure(0, 0);
            totalHeight = view.getMeasuredHeight() + 50;
            float x = 1;
            if( items > 0){
                x = items/5;
                this.rows = (int) (x + 1);
                totalHeight *= rows;
            }
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                    android.view.ViewGroup.LayoutParams.FILL_PARENT,
                    totalHeight/rows);
            view.setLayoutParams(param);
        }

        if(cell != null){
            viewHolder = new ChartViewHolder();
            viewHolder.cellColumn = (TextView) view.findViewById(R.id.cellColumnText);
            viewHolder.cellRow = (TextView) view.findViewById(R.id.cellRowText);
            viewHolder.countsAdded = (TextView) view.findViewById(R.id.countsAddedLabel);
            view.setTag(viewHolder);
        }
        else{viewHolder = (ChartViewHolder) view.getTag();}


        viewHolder.cellColumn.setText(String.valueOf(cell.getCellColumn()) + "-");
        viewHolder.cellRow.setText("(" + String.valueOf(cell.getCellRow()) + ")");

        //This adds counts to cells that have been selected (should get selected ones from last edit DATE)
        String tempString = "";
        if(cell.getCountListInCell().size() > 0) {
            for (int i = 0; i < cell.getCountListInCell().size(); i++) {
                Count count = cell.getCountListInCell().get(i);
                tempString = tempString + String.valueOf(count.getCountName().charAt(0));
                countsForCellString[position] = tempString;

            }
            //viewHolder.countsAdded.setText(tempString);
            view.setBackgroundColor(Color.GREEN);
            booleanArray.put(position, true);
        }

        if(countsForCellString[position] != null) {
            viewHolder.countsAdded.setText(countsForCellString[position]);
        } else{
            viewHolder.countsAdded.setText("");
        }

        if(booleanArray.get(position)){
            view.setBackgroundColor(Color.GREEN);
        }
        else{view.setBackgroundColor(Color.TRANSPARENT);}




        return view;
    }

    @Override
    public Cell getItem(int position){
        return cellList.get(position);
    }

}

class ChartViewHolder {
     TextView cellColumn, cellRow, countsAdded;
     int position;
}


