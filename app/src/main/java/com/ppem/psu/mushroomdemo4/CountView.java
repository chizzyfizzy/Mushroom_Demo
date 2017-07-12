package com.ppem.psu.mushroomdemo4;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CountView extends AppCompatActivity {

    private TextView pName, rName, dateText;
    private Button saveBtn;
    private CountsDAO countDataSource;
    LinearLayout L2;
    LinearLayout L1;
    RelativeLayout RL1;
    List<EditText> allCountsET;
    List<TextView> allCountNamesET;
    TableLayout tableLayout;
    TableRow tableRow;
    TextView[] countNameTextArray;
    EditText[] countNumTextArray;
    TableRow[] rowArray;
    //Only way to pass or keep room name/id for this activity
    static String theRoomName;
    static long theRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_list_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Prevents Keyboard opening automatically when screen opens
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //Define variables & simple get plant name for label
        Intent intent = getIntent();
        String sentPName = intent.getStringExtra("Plant Name");
        countDataSource = new CountsDAO(this);
        countDataSource.open();
        allCountsET = new ArrayList<EditText>();
        allCountNamesET = new ArrayList<TextView>();
        pName = (TextView) findViewById(R.id.plantNameCountText);
        rName = (TextView) findViewById(R.id.roomNameCountText);
        dateText = (TextView) findViewById(R.id.countDateTextView);
        pName.setText(sentPName);
        rName.setText(theRoomName);
        dateText.setText(getDateTime());
        saveBtn = (Button) findViewById(R.id.saveChangesButton);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCounts();
            }
        });

        //Create table layout for counter list & populate lsit view
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        populateListView();


    }


    //Populate list with created variables in form of nested layouts.
    //Also clears the list to prevent duplicates
    public void populateListView(){
        //Clear table rows so there are no duplicates
        tableLayout.removeAllViews();
        //Add column row
        tableRow = new TableRow(this);
        tableRow.setId(TableRow.NO_ID);
        tableRow.setBackgroundColor(Color.argb(255,63,81,181));
        tableRow.setLayoutParams(new Toolbar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        TextView column_name = new TextView(this);
        column_name.setText("        Type        ");
        column_name.setTextSize(30);
        tableRow.addView(column_name);
        TextView column_count = new TextView(this);
        column_count.setText("       #       ");
        column_count.setTextSize(30);

        tableRow.addView(column_count);
        tableLayout.addView(tableRow);
        List<Count> countValues = countDataSource.getAllCountsForRoom(theRoomId);
        int size = countValues.size();
        countNameTextArray = new TextView[size];
        countNumTextArray = new EditText[size];
        rowArray = new TableRow[size];

        for(int i =0; i < size; i++){
            String countName = countValues.get(i).getCountName();
            int countId = (int) countValues.get(i).getCountId();
            rowArray[i] = new TableRow(getApplicationContext());
            rowArray[i].setId(countId);

            countNameTextArray[i] = new TextView(getApplicationContext());
            countNameTextArray[i].setId(countId);
            countNameTextArray[i].setText(countName);
            countNameTextArray[i].setTextSize(30);
            countNameTextArray[i].setTextColor(Color.BLACK);
            countNameTextArray[i].setGravity(Gravity.CENTER);

            rowArray[i].addView(countNameTextArray[i]);

            countNumTextArray[i] = new EditText(getApplicationContext());
            countNumTextArray[i].setId(countId);
            countNumTextArray[i].setInputType(InputType.TYPE_CLASS_NUMBER);
            countNumTextArray[i].setTextSize(30);
            countNumTextArray[i].setTextColor(Color.BLACK);
            countNumTextArray[i].setPadding(0,0,0,20);
            countNumTextArray[i].setGravity(Gravity.CENTER_HORIZONTAL);
            if(countValues.get(i).getCountNumber() != 0) {
                countNumTextArray[i].setText(String.valueOf(countValues.get(i).getCountNumber()));
            }
            rowArray[i].addView(countNumTextArray[i]);
            rowArray[i].setPadding(5,0,0,100);

            tableLayout.addView(rowArray[i]);
        }

    }



    //Settings Menu Creator & Handler (3 dots top right of screen)

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_count_view, menu);
        return true;
    }

    @Override //TODO Delete Count Values, Delete All Counts for All Rooms
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //Create Count handler
        if(id == R.id.createCount){
            createNewCount();
        }
        //Delete count data handler
        if(id == R.id.deleteCounts) {
            //Add confirmation message
            countDataSource.deleteAllCounts();
            populateListView();
        }
        if(id == R.id.roomCharts){
            Intent openRoomChart = new Intent(CountView.this, ChartView.class);
            openRoomChart.putExtra("RoomId",theRoomId);
            startActivity(openRoomChart);
        }
        if(id == R.id.resetCounts){
            countDataSource.resetCounts();
        }
        if(id == R.id.printList){
            for(int i = 0; i < DatabaseHelper.selectedCells.size(); i++){
                System.out.println(DatabaseHelper.selectedCells.get(i));
            }
        }
        return super.onOptionsItemSelected(item);
    }


    //Gets passed room info for labels and inserting measure for given room.
    public void setRoomInfo(Room room){
        theRoomName = room.getRoomName();
        theRoomId = room.getRoomId();
    }


    public void updateCounts(){
        for(int i = 1; i<tableLayout.getChildCount();i++){
            TableRow tempRow = (TableRow) tableLayout.getChildAt(i);
            EditText tempET = (EditText) tempRow.getChildAt(1);
            long countId = tempRow.getId();
            String tempString = tempET.getText().toString();
            if(!tempString.isEmpty()) {
                int countNum = Integer.parseInt(tempString);
                boolean updated = countDataSource.updateCounts(theRoomId, countId, countNum);
                if(updated){
                    Toast updatedToast  = Toast.makeText(CountView.this, "Counts Updated Successfully.", Toast.LENGTH_LONG);
                    updatedToast.show();
                }
            }
        }
    }


    //TODO Create custom dialog xml
    private void createNewCount(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(CountView.this);
        alertBuilder.setTitle("Enter a Name For New Count: ");
        LinearLayout layout = new LinearLayout(CountView.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText cName = new EditText(CountView.this);
        layout.addView(cName);
        final CheckBox copyToAllRooms = new CheckBox(CountView.this);
        copyToAllRooms.setText("Copy To All Rooms and Plants");
        layout.addView(copyToAllRooms);
        alertBuilder.setView(layout);
        alertBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!cName.getText().toString().isEmpty()) {
                    //Box NOT Checked
                    if(!copyToAllRooms.isChecked()) {
                        countDataSource.createCount(cName.getText().toString(),theRoomId);
                        System.out.println("Adding Count to This Room");
                        populateListView();
                    }
                    //Box IS Checked
                    else if(copyToAllRooms.isChecked()){
                        countDataSource.createCountAllRooms(cName.getText().toString());
                        System.out.println("Adding Count to All Rooms and Plants");
                        populateListView();
                    }
                }else{
                    Toast errorToast = Toast.makeText(CountView.this, "Error, please enter a name for the new measure", Toast.LENGTH_LONG);
                    errorToast.show();
                }
            }
        });
        AlertDialog a = alertBuilder.create();
        a.show();
    }


    private String getDateTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


}
