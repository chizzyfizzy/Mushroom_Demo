package com.ppem.psu.mushroomdemo4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_counts_view);
        Intent intent = getIntent();
        farmId = intent.getLongExtra("farm", 0);
        countDataSource = new CountsDAO(this);
        countDataSource.open();
        countList = countDataSource.getDistinctCounts();
        if(countList == null){
            countDataSource.createDefaultCount("Fly 1", 0);
            countDataSource.createDefaultCount("Fly 2", 0);
            countDataSource.createDefaultCount("Green Mold", 1);
            countDataSource.createDefaultCount("Cobweb", 1);
            countDataSource.createDefaultCount("Syzygites", 1);
            countDataSource.createDefaultCount("Blotch", 1);
        }
        populateListVieW();

        countName = (EditText) findViewById(R.id.newCountEditText);
        addC = (Button) findViewById(R.id.addNewCountButton);
        updateC = (Button) findViewById(R.id.updateCountButton);
        deleteC = (Button) findViewById(R.id.deleteCountButton);
    }

    private void populateListVieW(){
        countList = countDataSource.getDistinctCounts();
        adapter = new FarmCountsListAdapter(this, countList);
        countLV = (ListView) findViewById(R.id.farmCountsListView);
        countLV.setAdapter(adapter);

    }
}
