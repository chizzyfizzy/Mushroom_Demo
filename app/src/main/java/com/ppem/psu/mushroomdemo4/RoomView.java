package com.ppem.psu.mushroomdemo4;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.CursorJoiner;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class RoomView extends AppCompatActivity {
    ListView roomLV;
    TextView pName, pLabel;
    static long thePlantId;
    static String thePlantName, thePlantLabel;
    private RoomListViewAdapter rAdapter;
    private List<Room> roomValues;

    private RoomDAO roomDataSource;
    private PlantDAO plantDataSource;
    private ChartDAO chartDataSource;
    private CellDAO cellDataSource;
    private CountsDAO countDataSource;
    private BedDAO bedDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        plantDataSource = new PlantDAO(this);
        roomDataSource = new RoomDAO(this);
        chartDataSource = new ChartDAO(this);
        cellDataSource = new CellDAO(this);
        countDataSource = new CountsDAO(this);
        bedDataSource = new BedDAO(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        plantDataSource.open();
        roomDataSource.open();
        chartDataSource.open();
        cellDataSource.open();
        countDataSource.open();
        bedDataSource.open();

        pName = (TextView) findViewById(R.id.plantNameTextRoomList);
        pLabel = (TextView) findViewById(R.id.plantLabelTextROomListView);
        pName.setText(thePlantName);
        pLabel.setText(thePlantLabel);

        populateListView();

        //List item click handler
        roomLV = (ListView) findViewById(R.id.roomListView);
        registerForContextMenu(roomLV);
        roomLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Room r = rAdapter.getItem(position);
                CountView room = new CountView();
                room.setRoomInfo(r);
                Intent openCountListIntent = new Intent(RoomView.this, CountView.class);
                openCountListIntent.putExtra("Plant Name",thePlantName);
                startActivity(openCountListIntent);
            }
        });

    }

    @Override
    protected void onPause(){
        super.onPause();
        plantDataSource.close();
        roomDataSource.close();
        chartDataSource.close();
        cellDataSource.close();
        countDataSource.close();
        bedDataSource.close();
    }

    //Fills list with room items
    public void populateListView(){
        roomValues = roomDataSource.getAllRoomsForPlant(thePlantId);

        if(roomValues != null) {
            rAdapter = new RoomListViewAdapter(this, roomValues);
            roomLV = (ListView) findViewById(R.id.roomListView);
            roomLV.setAdapter(rAdapter);
        }
    }

    //Context menu on long button click (User holds selection on list item)
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        if(v.getId() == R.id.roomListView){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            //menu.setHeaderTitle(plantValues.get(info.position).getPlantName());
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.crud_menu, menu);
        }
    }

    //Handles context menu selection
    @Override
    public boolean onContextItemSelected(MenuItem item){
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        final Room room = roomValues.get(info.position);
        int menuItemIndex = item.getItemId();
        switch(item.getItemId()){
            case R.id.add:
                Toast.makeText(RoomView.this, "Add Room Option selected", Toast.LENGTH_SHORT).show();

                return true;

            case R.id.edit:
                Toast.makeText(RoomView.this, "Edit Room Option selected for " + room.getRoomName(), Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RoomView.this);
                alertBuilder.setTitle("Editing Room: " + room.getRoomName());
                LinearLayout layout = new LinearLayout(RoomView.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                final EditText rName = new EditText(RoomView.this);
                rName.setText(room.getRoomName());
                layout.addView(rName);
                final EditText rLabel = new EditText(RoomView.this);
                if(room.getRoomLabel() == "") {
                    rLabel.setHint("Add Optional Label");
                } else {rLabel.setText(room.getRoomLabel()); }
                layout.addView(rLabel);
                alertBuilder.setView(layout);
                alertBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        roomDataSource.updateRoom(rName.getText().toString(), rLabel.getText().toString(), room.getRoomId(), thePlantId);
                    }
                });
                AlertDialog a = alertBuilder.create();
                a.show();

                return true;
            case R.id.delete:
                new AlertDialog.Builder(this)
                        .setTitle("Confirm")
                        .setMessage("Are you sure you want to delete " + room.getRoomName() + "?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                roomDataSource.deleteRoom(room);
                                Toast.makeText(RoomView.this, "Deleted Room " + room.getRoomName(), Toast.LENGTH_SHORT).show();
                            }})
                        .setNegativeButton("Cancel", null).show();
                return true;

            default:
                return super.onContextItemSelected(item);
        }

    }


    //Settings menu creator & handler
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_room_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //TODO Create custom dialog xml
        //create a new room handler
        if(id == R.id.createRoom){
            createRoomsDialog();
        }

        //TODO add delete option with confirmation message
        if(id == R.id.deleteRoomsForPlant){
            roomDataSource.deleteAllRoomsForPlant(thePlantId);
        }

        return super.onOptionsItemSelected(item);
    }


    //gets and sets plant info that was selected from previous screen.
    public void setPlantInfo(Plant thePlant){
        thePlantId = thePlant.getPlantId();
        thePlantName = thePlant.getPlantName();
        thePlantLabel = thePlant.getPlantLabel();
    }

    private void createRoomsDialog(){
        final Dialog dialog = new Dialog(RoomView.this);
        dialog.setContentView(R.layout.create_room_dialog);
        final EditText roomName = (EditText) dialog.findViewById(R.id.roomNameEditText);
        final EditText roomNumber = (EditText) dialog.findViewById(R.id.roomNumberEditText);
        final EditText roomBedLevels = (EditText) dialog.findViewById(R.id.roomBedsEditTExt);
        final EditText roomSquares = (EditText) dialog.findViewById(R.id.roomSquaresEditText);
        final CheckBox roomPeak = (CheckBox) dialog.findViewById(R.id.roomPeakCheckBox);

        Button createBtn = (Button) dialog.findViewById(R.id.createRoomButton);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int bLevels = Integer.parseInt(roomBedLevels.getText().toString());
                int bSqaures = Integer.parseInt(roomSquares.getText().toString());
                List<Count> tempCountList = countDataSource.getDistinctCounts();

                if(roomName.getText().toString().isEmpty() || roomBedLevels.getText().toString().isEmpty()
                        || roomSquares.getText().toString().isEmpty() || roomNumber.getText().toString().isEmpty()){
                    Toast errorToast = Toast.makeText(RoomView.this, "Error: Not All Fields Filled. ", Toast.LENGTH_LONG);
                    errorToast.show();
                }
                else{
                    Toast waitToast = Toast.makeText(RoomView.this, "Please wait a few seconds for the Application to set up.", Toast.LENGTH_LONG);
                    waitToast.show();
                    //Create # of Rooms Loop
                    for (int i = 1; i < Integer.parseInt(roomNumber.getText().toString()) + 1; i++){
                        Room room = roomDataSource.createRoom(roomName.getText().toString() + " " + i, thePlantId);
                        //Create 4 default beds loop - Chart Increment to add A/B/C/D
                        char bedIncrement = 'A';
                        for(int j = 0; j < 4; j++){
                            //Add another level to bed C+D if room is peaked
                            if(roomPeak.isChecked() && (j == 1 || j == 2)){
                                bLevels += 1;
                            }
                            Bed bed = bedDataSource.createBed("Bed " + String.valueOf(bedIncrement), bLevels, bSqaures, room.getRoomId());
                            //Create cells for bed: Fills grid horizontally so it needs a magical loop to fill it vertically.
                            for (int r = 1; r < bSqaures + 1; r++) {
                                for (int c = 1; c < bLevels + 1; c++) {
                                    if (c * bSqaures + r >= bLevels) {
                                        cellDataSource.createCellForBed(c, r, bed.getBedId());
                                    } else {
                                        cellDataSource.createCellForBed(c, r, bed.getBedId());
                                    }
                                }
                            }
                            bLevels = Integer.parseInt(roomBedLevels.getText().toString()); //Reset Bed levels incase it was added for peak.
                            bedIncrement++;
                        }
                    }
                    //Should just include in loop, and make it Create Count with roomId
                    //Add counts that are already set to the room.
                    for(int j = 0; j < tempCountList.size(); j++) {
                        int chartBool = 0;
                        if(tempCountList.get(j).isInChart()){
                            chartBool = 1;
                        }
                        countDataSource.createCountAllRooms(tempCountList.get(j).getCountName(), chartBool); //Should change to just pass the count.
                    }
                    populateListView();
                    dialog.dismiss();
                    //TODO add room/farm/plant names to sharedpreferences. For now nothing happens with roomName.
                }

            }
        });

        Button cancelBtn = (Button) dialog.findViewById(R.id.cancelRoomButton);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }




}
