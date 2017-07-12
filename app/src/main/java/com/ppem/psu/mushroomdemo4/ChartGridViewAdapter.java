package com.ppem.psu.mushroomdemo4;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitchell on 6/29/2017.
 */

public class ChartGridViewAdapter extends BaseAdapter {
    List<String> result;
    Context context;


    private static LayoutInflater inflater=null;
    public ChartGridViewAdapter(Context ctx, List<String> valueList) {
        // TODO Auto-generated constructor stub
        result=valueList;
        context=ctx;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        TextView cell;
    }
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder=new Holder();

        View rowView;
        rowView = inflater.inflate(R.layout.grid_chart_item, null);
        holder.cell=(TextView) rowView.findViewById(R.id.bedGridTextItem);
        holder.cell.setText(result.get(position).toString());
        rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "You Clicked "+result.get(position).toString(), Toast.LENGTH_LONG).show();
                holder.cell.setBackgroundColor(Color.GREEN);
                DatabaseHelper.selectedCells.add(result.get(position).toString());
                System.out.println(result.get(position).toString());

            }
        });
        return rowView;
    }
}
