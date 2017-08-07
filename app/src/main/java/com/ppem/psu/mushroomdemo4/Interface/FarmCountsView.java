package com.ppem.psu.mushroomdemo4.Interface;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ppem.psu.mushroomdemo4.DatabaseControllers.CountsDAO;
import com.ppem.psu.mushroomdemo4.DatabaseControllers.RoomDAO;
import com.ppem.psu.mushroomdemo4.Models.Count;
import com.ppem.psu.mushroomdemo4.Models.Room;
import com.ppem.psu.mushroomdemo4.R;

import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;

public class FarmCountsView extends AppCompatActivity {
    RoomDAO roomDataSource;
    List<Room> roomList;
    CountsDAO countDataSource;
    private long farmId;
    Button addC, updateC, deleteC, countColorBtn, cellColorBtn, backgroundColorBtn;
    TextView exampleCell, exampleCount;
    EditText countName;
    CheckBox inChart;
    ListView countLV;
    List<Count> countList;
    FarmCountsListAdapter adapter;
    Count count;
    private int index;
    int countColor, cellColor, backgrndColor;
    private final String sharedPrefsName = "AppPreferences";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;



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
        prefs = getSharedPreferences(sharedPrefsName, MODE_PRIVATE);
        backgrndColor = prefs.getInt("Background Color", Color.BLACK);
        cellColor = prefs.getInt("Cell Label Color", Color.CYAN);
        countColor = prefs.getInt("Count Color", Color.GREEN);

        countList = countDataSource.getDistinctCounts();
        if(countList.size() == 0){
            createDefaultCounts();
        }//TODO fix booleans
        countList = countDataSource.getDistinctCounts();
        adapter = new FarmCountsListAdapter(this, countList);
        countLV = (ListView) findViewById(R.id.farmCountsListView);
        countLV.setAdapter(adapter);

        exampleCell = (TextView) findViewById(R.id.cellLabelExampleText);
        exampleCell.setBackgroundColor(backgrndColor);
        exampleCell.setTextColor(cellColor);
        exampleCount = (TextView) findViewById(R.id.countExampleText);
        exampleCount.setBackgroundColor(backgrndColor);
        exampleCount.setTextColor(countColor);

        countName = (EditText) findViewById(R.id.newCountEditText);
        inChart = (CheckBox) findViewById(R.id.farmChartCountCheckBox);
        addC = (Button) findViewById(R.id.addNewCountButton);
        updateC = (Button) findViewById(R.id.updateCountButton);
        deleteC = (Button) findViewById(R.id.deleteCountButton);
        countColorBtn = (Button) findViewById(R.id.changeCountTextColor);
        cellColorBtn = (Button) findViewById(R.id.changeCellLabelColorBtn);
        backgroundColorBtn = (Button) findViewById(R.id.changeBackgroundColorBtn);


        countLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                index = position;
                count = countList.get(position);
                countName.setText(count.getCountName());
                inChart.setChecked(count.isInChart());
                exampleCount.setText(count.getCountName().substring(0,2));
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

        countColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCountColorDialog();
            }
        });

        cellColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCellColorDialog();
            }
        });

        backgroundColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBackgroundColorDialog();
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

    private void changeCountColorDialog(){

        final AmbilWarnaDialog dialog = new AmbilWarnaDialog(FarmCountsView.this, countColor, true, new AmbilWarnaDialog.OnAmbilWarnaListener() {


            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                Toast.makeText(getApplicationContext(), "Color Updated", Toast.LENGTH_SHORT).show();
                countColor = color;
                exampleCount.setTextColor(countColor);
                editor = prefs.edit();
                editor.putInt("Count Color",countColor);
                editor.apply();
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }
        });
        dialog.show();

    }


    private void changeCellColorDialog(){

        final AmbilWarnaDialog dialog = new AmbilWarnaDialog(FarmCountsView.this, cellColor, true, new AmbilWarnaDialog.OnAmbilWarnaListener() {


            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                Toast.makeText(getApplicationContext(), "Color Updated", Toast.LENGTH_SHORT).show();
                cellColor = color;
                exampleCell.setTextColor(cellColor);
                editor = prefs.edit();
                editor.putInt("Cell Label Color",cellColor);
                editor.apply();
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }
        });
        dialog.show();

    }

    private void changeBackgroundColorDialog(){

        final AmbilWarnaDialog dialog = new AmbilWarnaDialog(FarmCountsView.this, backgrndColor, true, new AmbilWarnaDialog.OnAmbilWarnaListener() {


            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                Toast.makeText(getApplicationContext(), "Color Updated", Toast.LENGTH_SHORT).show();
                backgrndColor = color;
                exampleCell.setBackgroundColor(backgrndColor);
                exampleCount.setBackgroundColor(backgrndColor);
                editor = prefs.edit();
                editor.putInt("Background Color",backgrndColor);
                editor.apply();
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }
        });
        dialog.show();

    }


}
