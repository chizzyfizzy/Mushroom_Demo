package com.ppem.psu.mushroomdemo4;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitchell on 7/20/2017.
 */

public class ChartRecyclerViewAdapter extends RecyclerView.Adapter<ChartRecyclerViewAdapter.ViewHolder> {
    private List<Cell> cellList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;


    // data is passed into the constructor
    public ChartRecyclerViewAdapter(Context context, List<Cell> cells) {
        this.mInflater = LayoutInflater.from(context);
        this.cellList = cells;
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.grid_chart_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Cell cell = cellList.get(position);
        holder.cellColumn.setText(String.valueOf(cell.getCellColumn()) + "-");
        holder.cellRow.setText(String.valueOf("(" + cell.getCellRow()) + ")");
        holder.countsAdded.setText("");
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return cellList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView cellColumn, cellRow, countsAdded;

        public ViewHolder(View itemView) {
            super(itemView);
            cellColumn = (TextView) itemView.findViewById(R.id.cellColumnText);
            cellRow = (TextView) itemView.findViewById(R.id.cellRowText);
            countsAdded = (TextView) itemView.findViewById(R.id.countsAddedLabel);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Cell getItem(int position) {
        return cellList.get(position);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
