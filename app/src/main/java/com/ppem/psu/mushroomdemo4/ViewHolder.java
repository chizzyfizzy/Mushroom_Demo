package com.ppem.psu.mushroomdemo4;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Mitchell on 7/20/2017.
 */

public class ViewHolder extends RecyclerView.ViewHolder {
    TextView cellColumn, cellRow, countsAdded;
    int position;

    public ViewHolder(View view){
        super(view);
        this.cellColumn = (TextView) view.findViewById(R.id.cellColumnText);
        this.cellRow = (TextView) view.findViewById(R.id.cellRowText);
        this.countsAdded = (TextView) view.findViewById(R.id.countsAddedLabel);
    }
}
