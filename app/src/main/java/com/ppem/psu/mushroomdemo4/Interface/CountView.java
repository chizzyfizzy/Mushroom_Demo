package com.ppem.psu.mushroomdemo4.Interface;

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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.ppem.psu.mushroomdemo4.DatabaseControllers.CountsDAO;
import com.ppem.psu.mushroomdemo4.DatabaseControllers.RoomDAO;
import com.ppem.psu.mushroomdemo4.Models.Count;
import com.ppem.psu.mushroomdemo4.Models.Room;
import com.ppem.psu.mushroomdemo4.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CountView extends AppCompatActivity {

    private TextView pName, rName, dateText;
    private Button saveBtn, openChartButton, roomComment;
    private CountsDAO countDataSource;
    List<EditText> allCountsET;
    List<TextView> allCountNamesET;
    TableLayout tableLayout;
    TableRow tableRow;
    TextView[] countNameTextArray;
    EditText[] countNumTextArray;
    TableRow[] rowArray;
    String theRoomName;
    long theRoomId;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_list_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Prevents Keyboard opening automatically when screen opens
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        intent = getIntent();
        String sentPName = intent.getStringExtra("Plant Name");
        theRoomName = intent.getStringExtra("Room Name");
        theRoomId = intent.getLongExtra("Room ID", 0);

        countDataSource = new CountsDAO(this);
        countDataSource.open();
        allCountsET = new ArrayList<>();
        allCountNamesET = new ArrayList<>();
        pName = (TextView) findViewById(R.id.plantNameCountText);
        rName = (TextView) findViewById(R.id.roomNameCountText);
        dateText = (TextView) findViewById(R.id.countDateTextView);
        pName.setText(sentPName);
        rName.setText(theRoomName);
        dateText.setText(intent.getStringExtra("Room Date"));
        saveBtn = (Button) findViewById(R.id.saveChangesButton);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCounts();
            }
        });

        openChartButton = (Button) findViewById(R.id.openChartButton);
        openChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openRoomChart = new Intent(CountView.this, ChartView2.class);
                openRoomChart.putExtra("RoomId",theRoomId);
                startActivity(openRoomChart);
            }
        });

        roomComment = (Button) findViewById(R.id.commentButton);
        roomComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentDialog();
            }
        });

        //Create table layout for counter list & populate lsit view
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        populateListView();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        countDataSource.close();
    }

    @Override
    protected void onResume(){
        super.onResume();
        populateListView();
    }


    //Populate list with created variables in form of table layout.
    //Easiest way I found for creating a list wtih editable texts for entering data.
    //Also clears the list to prevent duplicates when recalling
    public void populateListView(){
        //Clear table rows so there are no duplicates
        tableLayout.removeAllViews();
        //Add column-titles row
        tableRow = new TableRow(this);
        tableRow.setId(TableRow.NO_ID);
        tableRow.setBackgroundColor(Color.argb(255,63,81,181));
        tableRow.setLayoutParams(new Toolbar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        TextView column_name = new TextView(this);
        column_name.setText("        Name        "); //If these are changed to strings from AndroidResouce, the spaces are lost and view is ruined.
        column_name.setTextSize(30);
        tableRow.addView(column_name);
        TextView column_count = new TextView(this);
        column_count.setText("       #       ");
        column_count.setTextSize(30);
        tableRow.addView(column_count);
        tableLayout.addView(tableRow);

        //Adding Counts
        List<Count> countValues = countDataSource.getAllCountsForRoom(theRoomId);
        int size = countValues.size();
        countNameTextArray = new TextView[size];
        countNumTextArray = new EditText[size];
        rowArray = new TableRow[size];

        //Loop to add all counts to view
        for(int i =0; i < size; i++){
            //create new table row
            String countName = countValues.get(i).getCountName();
            int countId = (int) countValues.get(i).getCountId();
            rowArray[i] = new TableRow(getApplicationContext());
            rowArray[i].setId(countId); //Set Id of row for same as countId. For updating counts.

            //create and set countName TextView
            countNameTextArray[i] = new TextView(getApplicationContext());
            countNameTextArray[i].setId(countId);
            countNameTextArray[i].setText(countName);
            countNameTextArray[i].setTextSize(30);
            countNameTextArray[i].setTextColor(Color.BLACK);
            countNameTextArray[i].setGravity(Gravity.CENTER);

            //Add textName view to row
            rowArray[i].addView(countNameTextArray[i]);

            //create and set count number edittext view
            countNumTextArray[i] = new EditText(getApplicationContext());
            countNumTextArray[i].setId(countId);
            countNumTextArray[i].setInputType(InputType.TYPE_CLASS_NUMBER);
            countNumTextArray[i].setTextSize(30);
            countNumTextArray[i].setTextColor(Color.BLACK);
            countNumTextArray[i].setPadding(0,0,0,20);
            countNumTextArray[i].setGravity(Gravity.CENTER_HORIZONTAL);
            //Gets value for count from previous data entry (if there was any) or else error.
            if(countValues.get(i).getCountNumber() != 0) {
                countNumTextArray[i].setText(String.valueOf(countValues.get(i).getCountNumber()));
            } //Add edit text view to row
            rowArray[i].addView(countNumTextArray[i]);
            rowArray[i].setPadding(5,0,0,100);
            //Add row to table layout
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //Create Count handler
        if(id == R.id.createCount){
            createNewCount();
        }
        //Delete counts (not just values for counts)
        if(id == R.id.deleteCounts) {
            //Add confirmation message
            countDataSource.deleteAllCounts();
            populateListView();
        }
        //Remove values of counts(reset)
        if(id == R.id.resetCounts){
            countDataSource.resetCounts();
        }
        return super.onOptionsItemSelected(item);
    }


    //Loop through all counts and update counts
    public void updateCounts(){
        for(int i = 1; i<tableLayout.getChildCount();i++){
            TableRow tempRow = (TableRow) tableLayout.getChildAt(i);//Gets row i in tablelayout
            EditText tempET = (EditText) tempRow.getChildAt(1); //Gets edit text in the row layout
            long countId = tempRow.getId();
            String tempString = tempET.getText().toString();
            if(!tempString.equals("")) {
                int countNum = Integer.parseInt(tempString);
                if (countDataSource.updateCountValue(countId, countNum)) {
                    Toast updatedToast = Toast.makeText(CountView.this, "Counts Updated Successfully.", Toast.LENGTH_LONG);
                    updatedToast.show();
                }
            }
            //For Activity Result: Since updating counts, return that the room has been updated to change the LastEdit date of the room
            setResult(1, intent);
            //Closes the activity.
            finish();
        }
    }


    //Quick way to add a new count instead of going all the way back to the farm-counts-view
    private void createNewCount(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(CountView.this);
        alertBuilder.setTitle("Enter a Name For New Count: ");
        LinearLayout layout = new LinearLayout(CountView.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText cName = new EditText(CountView.this);
        layout.addView(cName);
        final CheckBox isChartCount = new CheckBox(CountView.this);
        isChartCount.setText(R.string.ChartBooleanQuestion);
        layout.addView(isChartCount);
        final CheckBox copyToAllRooms = new CheckBox(CountView.this);
        copyToAllRooms.setText(R.string.CopyCountAllRoomsQuestion);
        layout.addView(copyToAllRooms);

        alertBuilder.setView(layout);
        alertBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Check if name is empty
                if(!cName.getText().toString().isEmpty()) {
                    //Copy All Box NOT Checked
                    if(!copyToAllRooms.isChecked()) {
                        countDataSource.createCount(new Count(cName.getText().toString(), isChartCount.isChecked()), theRoomId);

                        System.out.println("Adding Count to This Room");
                        //Refresh view
                        populateListView();
                    }
                    //Copy All Box IS Checked
                    else if(copyToAllRooms.isChecked()){
                            countDataSource.createCountAllRooms(new Count(cName.getText().toString(), isChartCount.isChecked()));
                        System.out.println("Adding Count to All Rooms and Plants");
                        //Refresh view
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

    //Dialog to edit comment for room
    private void commentDialog(){
        final RoomDAO roomData = new RoomDAO(CountView.this);
        roomData.open();
        Room room = roomData.getRoom(theRoomId);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(CountView.this);
        alertBuilder.setTitle("Leave a Comment: ");
        LinearLayout layout = new LinearLayout(CountView.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText comment = new EditText(CountView.this);
        comment.setText(room.getRoomComment());
        layout.addView(comment);
        alertBuilder.setView(layout);
        alertBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                roomData.updateRoomComment(comment.getText().toString(), theRoomId);
                roomData.close();
            }
        });
        AlertDialog a = alertBuilder.create();
        a.show();
    }



}
