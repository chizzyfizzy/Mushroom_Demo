package com.ppem.psu.mushroomdemo4.Interface;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.ppem.psu.mushroomdemo4.DatabaseControllers.BedDAO;
import com.ppem.psu.mushroomdemo4.DatabaseControllers.CellDAO;
import com.ppem.psu.mushroomdemo4.DatabaseControllers.CountsDAO;
import com.ppem.psu.mushroomdemo4.DatabaseControllers.PlantDAO;
import com.ppem.psu.mushroomdemo4.DatabaseControllers.RoomDAO;
import com.ppem.psu.mushroomdemo4.Models.Bed;
import com.ppem.psu.mushroomdemo4.Models.Count;
import com.ppem.psu.mushroomdemo4.Models.Plant;
import com.ppem.psu.mushroomdemo4.Models.Room;
import com.ppem.psu.mushroomdemo4.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RoomListView extends AppCompatActivity {
    ListView roomLV;
    TextView pName, pLabel;
    private long thePlantId;
    private String thePlantName, thePlantLabel;
    private RoomListViewAdapter rAdapter;
    private List<Room> roomValues;
    private Date date;
    private RoomDAO roomDataSource;
    private PlantDAO plantDataSource;
    private CellDAO cellDataSource;
    private CountsDAO countDataSource;
    private BedDAO bedDataSource;
    private int updatedRoom = 1;
    private Room room;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        plantDataSource = new PlantDAO(this);
        roomDataSource = new RoomDAO(this);
        cellDataSource = new CellDAO(this);
        countDataSource = new CountsDAO(this);
        bedDataSource = new BedDAO(this);

        intent = getIntent();
        thePlantName = intent.getStringExtra("Plant Name");
        thePlantLabel = intent.getStringExtra("Plant Label");
        thePlantId = intent.getLongExtra("Plant Id", 0);
    }

    @Override
    protected void onResume(){
        super.onResume();
        plantDataSource.open();
        roomDataSource.open();
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
                room = rAdapter.getItem(position);
                Intent openCountListIntent = new Intent(RoomListView.this, CountView.class);
                openCountListIntent.putExtra("Plant Name",thePlantName);
                openCountListIntent.putExtra("Room Name", room.getRoomName());
                openCountListIntent.putExtra("Room ID", room.getRoomId());
                openCountListIntent.putExtra("Room Date", getDateTime());
                startActivityForResult(openCountListIntent, 0);
            }
        });

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        plantDataSource.close();
        roomDataSource.close();
        cellDataSource.close();
        countDataSource.close();
        bedDataSource.close();
    }

    //Fills list with room items
    public void populateListView(){
        roomValues = roomDataSource.getAllRoomsForPlant(thePlantId);
        rAdapter = new RoomListViewAdapter(this, roomValues);
        roomLV = (ListView) findViewById(R.id.roomListView);
        roomLV.setAdapter(rAdapter);
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
        switch(item.getItemId()){
            case R.id.add:
                Toast.makeText(RoomListView.this, "Add Room Option selected", Toast.LENGTH_SHORT).show();

                return true;

            case R.id.edit:
                Toast.makeText(RoomListView.this, "Edit Room Option selected for " + room.getRoomName() + " " + room.getRoomLabel(), Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RoomListView.this);
                alertBuilder.setTitle("Editing " + room.getRoomName());
                LinearLayout layout = new LinearLayout(RoomListView.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                final EditText rName = new EditText(RoomListView.this);
                rName.setText(room.getRoomName());
                layout.addView(rName);
                final EditText rLabel = new EditText(RoomListView.this);
                if(room.getRoomLabel() == null || room.getRoomLabel().equals("")) {
                    rLabel.setHint("Add Optional Label");
                } else {rLabel.setText(room.getRoomLabel()); }
                layout.addView(rLabel);
                alertBuilder.setView(layout);
                alertBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        roomDataSource.updateRoom(rName.getText().toString(), rLabel.getText().toString(), room.getRoomId(), thePlantId);
                        roomValues.set(info.position, new Room(room.getRoomId(), rName.getText().toString(),  rLabel.getText().toString()));
                        //rAdapter.notifyDataSetChanged();
                        populateListView();
                    }
                });
                AlertDialog a = alertBuilder.create();
                a.show();
              /*  Toast.makeText(RoomListView.this, "Edit Room Option selected for " + room.getRoomName(), Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RoomListView.this);
                alertBuilder.setTitle("Editing Room: " + room.getRoomName());
                LinearLayout layout = new LinearLayout(RoomListView.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                final EditText rName = new EditText(RoomListView.this);
                rName.setText(room.getRoomName());
                layout.addView(rName);
                final EditText rLabel = new EditText(RoomListView.this);
                if(room.getRoomLabel() == null) {
                    rLabel.setHint("Add Optional Label");
                } else {rLabel.setText(room.getRoomLabel()); }
                layout.addView(rLabel);
                alertBuilder.setView(layout);
                alertBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        roomValues.set(info.position, new Room(rName.getText().toString(), rLabel.getText().toString()));
                        rAdapter.notifyDataSetChanged();
                        roomDataSource.updateRoom(rName.getText().toString(), rLabel.getText().toString(), room.getRoomId(), thePlantId);
                        //populateListView();
                    }
                });
                AlertDialog a = alertBuilder.create();
                a.show();*/

                return true;
            case R.id.delete:
                new AlertDialog.Builder(this)
                        .setTitle("Confirm")
                        .setMessage("Are you sure you want to delete " + room.getRoomName() + "?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                roomDataSource.deleteRoom(room);
                                Toast.makeText(RoomListView.this, "Deleted Room " + room.getRoomName(), Toast.LENGTH_SHORT).show();
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

        //create a new room handler
        if(id == R.id.createRoom){
            createRoomsDialog();
        }
        //Delete all rooms for plant
        if(id == R.id.deleteRoomsForPlant){
            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Delete all rooms for " + thePlantName + "?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            roomDataSource.deleteAllRoomsForPlant(thePlantId);
                            Toast.makeText(RoomListView.this, "Deleted all rooms for " + thePlantName, Toast.LENGTH_SHORT).show();
                        }})
                    .setNegativeButton("Cancel", null).show();
            return true;

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
        final Dialog dialog = new Dialog(RoomListView.this);
        dialog.setContentView(R.layout.create_room_dialog);
        final TextView plantName = (TextView) dialog.findViewById(R.id.createRoomPlantName);
        plantName.setText("Creating rooms for " + thePlantName);
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
                    Toast errorToast = Toast.makeText(RoomListView.this, "Error: Not All Fields Filled. ", Toast.LENGTH_LONG);
                    errorToast.show();
                }
                else{
                    Toast waitToast = Toast.makeText(RoomListView.this, "Please wait a few seconds for the Application to set up.", Toast.LENGTH_LONG);
                    waitToast.show();
                    //Check if counts have been made. If not then create the default counts.
                    if(tempCountList.size() == 0){
                        createDefaultCounts();
                        tempCountList = countDataSource.getDistinctCounts();
                    }
                    //Create # of Rooms Loop
                    for (int i = 1; i < Integer.parseInt(roomNumber.getText().toString()) + 1; i++){
                        Room room = roomDataSource.createRoom(roomName.getText().toString() + " " + i, thePlantId);
                        //Add distinct counts to room loop
                        for(int k = 0; k < tempCountList.size(); k++) {
                            countDataSource.createCount(tempCountList.get(k), room.getRoomId());
                        }
                        //Create 4 default beds loop - Chart Increment to add A/B/C/D
                        char bedIncrement = 'A';
                        for(int j = 0; j < 4; j++){
                            //Add another level to bed B+C if room is peaked
                            if(roomPeak.isChecked() && (j == 1 || j == 2)){
                                bLevels += 1;
                            }
                            Bed bed = bedDataSource.createBed(new Bed("Bed " + String.valueOf(bedIncrement), bLevels, bSqaures), room.getRoomId());
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
                    dialog.dismiss();
                    populateListView();
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

    //If counts are updated, change the last edit date for the room
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            roomDataSource.updateRoomDate(room.getRoomId(), getDateTime());
            populateListView();
        }
    }

    private String getDateTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void createDefaultCounts(){
        countDataSource.createDefaultCount("Sciarid", false);
        countDataSource.createDefaultCount("Phorrid", false);
        countDataSource.createDefaultCount("Green Mold", true);
        countDataSource.createDefaultCount("Cobweb", true);
        countDataSource.createDefaultCount("Syzygites", true);
        countDataSource.createDefaultCount("Blotch", true);
        countDataSource.createDefaultCount("Bubble", true);
    }

}
