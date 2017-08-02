package com.ppem.psu.mushroomdemo4.Interface;

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

import com.ppem.psu.mushroomdemo4.DatabaseControllers.CountsDAO;
import com.ppem.psu.mushroomdemo4.DatabaseControllers.FarmDAO;
import com.ppem.psu.mushroomdemo4.DatabaseControllers.RoomDAO;
import com.ppem.psu.mushroomdemo4.Models.Count;
import com.ppem.psu.mushroomdemo4.Models.Room;
import com.ppem.psu.mushroomdemo4.R;

import java.util.List;

public class FarmCountsView extends AppCompatActivity {
    RoomDAO roomDataSource;
    List<Room> roomList;
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
        roomDataSource = new RoomDAO(this);
        roomDataSource.open();
        roomList = roomDataSource.getAllRooms();
        countDataSource = new CountsDAO(this);
        countDataSource.open();

        countList = countDataSource.getDistinctCounts();
        if(countList.size() == 0){
            createDefaultCounts();
        }//TODO fix booleans
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
                inChart.setChecked(count.isInChart());
            }
        });


        addC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countName.getText().toString().equals("")) {
                    Toast.makeText(FarmCountsView.this, "No name has been entered.", Toast.LENGTH_SHORT).show();
                }
                else{
                    countList.add(new Count(countName.getText().toString(), inChart.isChecked()));
                    adapter.notifyDataSetChanged();
                    for(int i = 0; i < roomList.size(); i++){
                        countDataSource.createCount(new Count(countName.getText().toString(), inChart.isChecked()), roomList.get(i).getRoomId());
                    }
                    clearSelection();
                }
            }
        });

        updateC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count == null){
                    Toast.makeText(FarmCountsView.this, "No Count selected.", Toast.LENGTH_SHORT).show();
                }
                else if(countName.getText().toString().equals("") || countName.getText().toString().equals(" ")){
                    Toast.makeText(FarmCountsView.this, "No name has been entered.", Toast.LENGTH_SHORT).show();
                }
                else if((count.getCountName().equals(countName.getText().toString())) && (count.isInChart() == inChart.isChecked())){
                    Toast.makeText(FarmCountsView.this, "Selected count hasn't changed.", Toast.LENGTH_SHORT).show();
                }
                else{
                    countDataSource.updateCountWithName(count.getCountName(), countName.getText().toString(), inChart.isChecked());
                    countList.set(index, new Count(countName.getText().toString(), inChart.isChecked()));
                    adapter.notifyDataSetChanged();
                    Toast.makeText(FarmCountsView.this, "Count Updated ", Toast.LENGTH_SHORT).show();
                    clearSelection();
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
                                    countList.remove(index);
                                    adapter.notifyDataSetChanged();
                                    countDataSource.deleteCountWithName(count);
                                    Toast.makeText(FarmCountsView.this, "Deleted Count " + count.getCountName(), Toast.LENGTH_SHORT).show();
                                    clearSelection();
                                }})
                            .setNegativeButton("Cancel", null).show();

                }

            }
        });

    }

    private void clearSelection(){
        index = 0;
        count = null;
        countName.setText("");
        inChart.setChecked(false);
    }

    private void createDefaultCounts(){
        countDataSource.createDefaultCount("Fly 1", false);
        countDataSource.createDefaultCount("Fly 2", false);
        countDataSource.createDefaultCount("Green Mold", true);
        countDataSource.createDefaultCount("Cobweb", true);
        countDataSource.createDefaultCount("Syzygites", true);
        countDataSource.createDefaultCount("Blotch", true);
    }


}
