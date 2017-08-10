package com.ppem.psu.mushroomdemo4.Interface;

import android.content.Context;

import android.content.SharedPreferences;
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

import static android.content.Context.MODE_PRIVATE;

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
    private final String sharedPrefsName = "AppPreferences";
    SharedPreferences prefs;

    public ChartAdapter(Context context, List<Cell> cells){
        super(context, 0, cells);
        this.context = context;
        this.cellList = cells;
        this.countsForCellString = new String[cells.size()];
        prefs = context.getSharedPreferences(sharedPrefsName, MODE_PRIVATE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Cell cell = cellList.get(position);
        ChartViewHolder viewHolder;
        items = cellList.size();


        //This is suppose to prevent recycling of a view, but still does.
        if(view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.grid_chart_item, null);

            //For some reason this makes the gridview look nice
            //It started as an attempt to make the whole gridview fit the screen with no scrolling to avoid having the added count-textviews from appearing in random places.
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
        if(cell.getCountListInCell().size() > 0) {
            String tempString = "";
            for (int i = 0; i < cell.getCountListInCell().size(); i++) { //Gets first 2 letters of each count added, add to one string.
                Count count = cell.getCountListInCell().get(i);
                tempString += count.getCountName().substring(0, 2);
                countsForCellString[position] = tempString; //Saves the string to the cell position to prevent recycling the view on scroll/swiping
            }
            booleanArray.put(position, true);//Prevents recycling the highlighted view on scroll/swiping
        }
        else{
            countsForCellString[position] = "";
            booleanArray.put(position, false);
        }

        //Set the text counts text at postion
        viewHolder.countsAdded.setText(countsForCellString[position]);
        viewHolder.countsAdded.setTextColor(prefs.getInt("Count Color",Color.GREEN));

        //boolean array required to set background color of cells that have counts or not.
        //If boolean at position == true, used sharedPreferences colors.
        if(booleanArray.get(position)){
            view.setBackgroundColor(prefs.getInt("Background Color",0xffffff00));
            viewHolder.cellColumn.setTextColor(prefs.getInt("Cell Label Color",Color.CYAN));
            viewHolder.cellRow.setTextColor(prefs.getInt("Cell Label Color", Color.CYAN));
        }
        else{ //If boolean at position == false, keep it default.
            view.setBackgroundColor(Color.TRANSPARENT);
            viewHolder.cellColumn.setTextColor(Color.BLACK);
            viewHolder.cellRow.setTextColor(Color.BLACK);
        }

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


