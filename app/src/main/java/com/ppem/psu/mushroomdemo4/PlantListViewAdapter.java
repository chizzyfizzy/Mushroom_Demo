package com.ppem.psu.mushroomdemo4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mitchell on 6/19/2017.
 */

//An adapter to add a plant item to listviews
public class PlantListViewAdapter extends ArrayAdapter<Plant> {
    public PlantListViewAdapter(Context context, List<Plant> plants){
        super(context, 0, plants);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Plant plant = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.plant_list_view_item, parent, false);
        }

        TextView pName = (TextView) convertView.findViewById(R.id.plantNameListItem);
        TextView pLabel = (TextView) convertView.findViewById(R.id.plantLabelListItem);

        pName.setText(plant.getPlantName());
        pLabel.setText(plant.getPlantLabel());

        return convertView;
    }


}
