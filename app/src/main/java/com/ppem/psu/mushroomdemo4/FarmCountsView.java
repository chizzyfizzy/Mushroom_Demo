package com.ppem.psu.mushroomdemo4;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class FarmCountsView extends AppCompatActivity {
    RoomDAO roomDataSource;
    CountsDAO countDataSource;
    PlantListViewAdapter pAdapter;
    FarmDAO farmDataSource;
    private long farmId;
    Button addC, updateC, deleteC;
    EditText countName;
    CheckBox inChart;
    ListView countLV;
    List<Count> countList;
    FarmCountsListAdapter adapter;
    Count count;
    private int index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_counts_view);
        Intent intent = getIntent();
        farmId = intent.getLongExtra("farm", 0);
        countDataSource = new CountsDAO(this);
        countDataSource.open();
        countList = countDataSource.getDistinctCounts();
        if(countList.size() == 0){
            countDataSource.createDefaultCount("Fly 1", 0);
            countDataSource.createDefaultCount("Fly 2", 0);
            countDataSource.createDefaultCount("Green Mold", 1);
            countDataSource.createDefaultCount("Cobweb", 1);
            countDataSource.createDefaultCount("Syzygites", 1);
            countDataSource.createDefaultCount("Blotch", 1);
        }
        countList = countDataSource.getDistinctCounts();
        adapter = new FarmCountsListAdapter(this, countList);
        countLV = (ListView) findViewById(R.id.farmCountsListView);
        countLV.setAdapter(adapter);

        countName = (EditText) findViewById(R.id.newCountEditText);
        inChart = (CheckBox) findViewById(R.id.farmChartCountCheckBox);
        addC = (Button) findViewById(R.id.addNewCountButton);
        updateC = (Button) findViewById(R.id.updateCountButton);
        deleteC = (Button) findViewById(R.id.deleteCountButton);


        countLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                index = position;
                count = countList.get(position);
                countName.setText(count.getCountName());
            }
        });


        addC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countName.getText().toString() == "") {
                    Toast.makeText(FarmCountsView.this, "No name has been entered.", Toast.LENGTH_SHORT).show();
                }
                else{
                    int chartBool = 0;
                    if(inChart.isChecked()){
                        chartBool = 1;
                        inChart.toggle();
                    }
                    countList.add(new Count(countName.getText().toString(), inChart.isChecked()));
                    adapter.notifyDataSetChanged();
                    countDataSource.createCountAllRooms(countName.getText().toString(), chartBool);
                    countName.setText("");
                }
            }
        });

        updateC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int chartBool = 0;
                if(inChart.isChecked()){
                    chartBool = 1;
                }
                if(count == null){
                    Toast.makeText(FarmCountsView.this, "No Count selected.", Toast.LENGTH_SHORT).show();
                }
                else if(countName.getText().toString() == ""){
                    Toast.makeText(FarmCountsView.this, "No name has been entered.", Toast.LENGTH_SHORT).show();
                }
                else if((count.getCountName() == countName.getText().toString()) && (count.isInChart() == inChart.isChecked())){
                    Toast.makeText(FarmCountsView.this, "Selected count hasn't changed.", Toast.LENGTH_SHORT).show();
                }
                else{
                    countList.set(index, new Count(countName.getText().toString(), inChart.isChecked()));
                    adapter.notifyDataSetChanged();
                    countDataSource.updateCount(countName.getText().toString(), chartBool, count.getCountId());
                }
            }
        });

        deleteC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count == null){
                    Toast.makeText(FarmCountsView.this, "No Count selected.", Toast.LENGTH_SHORT).show();
                }
                else{
                    new AlertDialog.Builder(FarmCountsView.this)
                            .setTitle("Confirm")
                            .setMessage("Are you sure you want to delete " + count.getCountName() + "?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    countDataSource.deleteCount(count);
                                    Toast.makeText(FarmCountsView.this, "Deleted Count " + count.getCountName(), Toast.LENGTH_SHORT).show();
                                }})
                            .setNegativeButton("Cancel", null).show();
                }
            }
        });

    }


}
