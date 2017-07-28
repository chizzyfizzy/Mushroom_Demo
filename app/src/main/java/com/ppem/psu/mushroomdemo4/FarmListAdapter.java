package com.ppem.psu.mushroomdemo4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mitchell on 7/25/2017.
 */

public class FarmListAdapter extends ArrayAdapter<Farm> {
    public FarmListAdapter(Context context, List<Farm> farms){
        super(context, 0, farms);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Farm farm = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.farm_list_item, parent, false);
        }

        TextView fName = (TextView) convertView.findViewById(R.id.farmNameListItem);
        TextView fDescr = (TextView) convertView.findViewById(R.id.farmDescListItem);


        fName.setText(farm.getFarmName());
        fDescr.setText(farm.getFarmDescription());


        return convertView;
    }
}
