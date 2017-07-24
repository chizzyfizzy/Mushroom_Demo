package com.ppem.psu.mushroomdemo4;

import android.content.Context;

import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitchell on 7/17/2017.
 */

public class ChartAdapter extends ArrayAdapter<Cell> {
    List<Cell> cellList;
    SparseBooleanArray booleanArray = new SparseBooleanArray();
    private ChartRecyclerViewAdapter.ItemClickListener mClickListener;

    public ChartAdapter(Context context, List<Cell> cells){
        super(context, 0, cells);
        cellList = cells;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Cell cell = getItem(position);
        ChartViewHolder viewHolder;


        if(view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.grid_chart_item, null);
        }

        if(cell != null){
            viewHolder = new ChartViewHolder();
            viewHolder.cellColumn = (TextView) view.findViewById(R.id.cellColumnText);
            viewHolder.cellRow = (TextView) view.findViewById(R.id.cellRowText);
            //viewHolder.countsAdded = (TextView) view.findViewById(R.id.countsAddedLabel);
            view.setTag(viewHolder);
        }
        else{viewHolder = (ChartViewHolder) view.getTag();}


        if(booleanArray.get(position)){
            view.setBackgroundColor(Color.GREEN);
        }
        else{
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        viewHolder.cellColumn.setText(String.valueOf(cell.getCellColumn()) + "-");
        viewHolder.cellRow.setText("(" + String.valueOf(cell.getCellRow()) + ")");
        //viewHolder.countsAdded.setText("");



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


