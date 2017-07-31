package com.ppem.psu.mushroomdemo4.Interface;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ppem.psu.mushroomdemo4.Models.Farm;
import com.ppem.psu.mushroomdemo4.R;

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
        TextView fDescription = (TextView) convertView.findViewById(R.id.farmDescListItem);


        fName.setText(farm.getFarmName());
        fDescription.setText(farm.getFarmDescription());


        return convertView;
    }
}
