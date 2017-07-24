package com.ppem.psu.mushroomdemo4;

import android.graphics.Rect;
import android.support.annotation.IntRange;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Mitchell on 7/20/2017.
 */

public class ChartRecyclerDecoration extends RecyclerView.ItemDecoration {
    private int verticalSpaceHeight;

    public ChartRecyclerDecoration(@IntRange(from=0)int margin, @IntRange(from=0) int columns){

    }


    public ChartRecyclerDecoration(int vertSpaceHeight){
        this.verticalSpaceHeight = vertSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
        if(parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = verticalSpaceHeight;
        }
    }


}
