package com.ppem.psu.mushroomdemo4.Interface;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ppem.psu.mushroomdemo4.Models.Count;

import java.util.List;

/**
 * Created by Mitchell on 7/19/2017.
 */

public class CountSpinnerAdapter extends ArrayAdapter<Count> {

    // Your sent context
    private Context context;
    // Your custom values for the spinner (User)
    private List<Count> countValues;



    public CountSpinnerAdapter(Context context, int textViewResourceId,
                       List<Count> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.countValues = values;
    }

    public int getCount(){
        return countValues.size();
    }

    public Count getItem(int position){
        return countValues.get(position);
    }

    public long getItemId(int position){
        return position;
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object
        label.setTextSize(20);
        label.setGravity(Gravity.RIGHT);
        label.setText(countValues.get(position).getCountName());

        // And finally return your dynamic (or custom) view for each spinner item*/
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {

        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setTextSize(30);
        label.setPadding(0,20,20,0);
        label.setGravity(Gravity.CENTER);
        label.setText(countValues.get(position).getCountName());


        return label;
    }
}
