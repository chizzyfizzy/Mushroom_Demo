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
 * Created by Mitchell on 7/18/2017.
 */

public class CountListviewAdapter extends ArrayAdapter<Count> {
    public CountListviewAdapter(Context context, List<Count> counts){
        super(context, 0, counts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Count count = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.room_list_view_item, parent, false);
        }

        TextView rName = (TextView) convertView.findViewById(R.id.roomNameTextView);



        return convertView;
    }
}
