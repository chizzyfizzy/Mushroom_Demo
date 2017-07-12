package com.ppem.psu.mushroomdemo4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Created by Mitchell on 6/19/2017.
 */

//Adapter to add a room item to listview
public class RoomListViewAdapter extends ArrayAdapter<Room> {
    public RoomListViewAdapter(Context context, List<Room> rooms){
        super(context, 0, rooms);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Room room = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.room_list_view_item, parent, false);
        }

        TextView rName = (TextView) convertView.findViewById(R.id.roomNameTextView);

        rName.setText(room.getRoomName());

        return convertView;
    }
}
