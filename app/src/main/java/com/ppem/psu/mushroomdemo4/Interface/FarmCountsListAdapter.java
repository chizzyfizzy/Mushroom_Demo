package com.ppem.psu.mushroomdemo4.Interface;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ppem.psu.mushroomdemo4.Models.Count;
import com.ppem.psu.mushroomdemo4.R;

import java.util.List;

/**
 * Created by Mitchell on 7/24/2017.
 */

public class FarmCountsListAdapter extends ArrayAdapter<Count> {
    public FarmCountsListAdapter(Context context, List<Count> counts){
        super(context, 0, counts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Count count = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.farm_count_list_item, parent, false);
        }

        TextView cName = (TextView) convertView.findViewById(R.id.countNameListItem);
        TextView cChart = (TextView) convertView.findViewById(R.id.chartCountListItem);

        cName.setText(count.getCountName());
        if(count.isInChart()){
            cChart.setText("Used in Disease Chart");
        } else { cChart.setText("");}

        return convertView;
    }

}
