package com.ppem.psu.mushroomdemo4;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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



    private RoomDAO roomDataSource;
    private PlantDAO plantDataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        plantDataSource = new PlantDAO(this);
        plantDataSource.open();

        roomDataSource = new RoomDAO(this);
        roomDataSource.open();


        pName = (TextView) findViewById(R.id.plantNameTextRoomList);
        pLabel = (TextView) findViewById(R.id.plantLabelTextROomListView);
        pName.setText(thePlantName);
        pLabel.setText(thePlantLabel);


        populateListView();

        //List item click handler
        roomLV = (ListView) findViewById(R.id.roomListView);
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

    //Fills list with room items
    public void populateListView(){
        List<Room> roomValues = roomDataSource.getAllRoomsForPlant(thePlantId);

        if(roomValues != null) {
            rAdapter = new RoomListViewAdapter(this, roomValues);
            roomLV = (ListView) findViewById(R.id.roomListView);
            roomLV.setAdapter(rAdapter);
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
            createRoomDialog();
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

    private void createRoomDialog(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RoomView.this);
        alertBuilder.setTitle("Enter Room Name");
        LinearLayout layout = new LinearLayout(RoomView.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText rName = new EditText(RoomView.this);
        layout.addView(rName);
        alertBuilder.setView(layout);

        alertBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(rName.getText() != null) {
                    roomDataSource.createRoom(rName.getText().toString(), thePlantId);
                    populateListView();
                }else{
                    Toast errorToast = Toast.makeText(RoomView.this, "Error, please enter a name for the new room", Toast.LENGTH_LONG);
                    errorToast.show();
                }
            }
        });
        AlertDialog a = alertBuilder.create();
        a.show();
        populateListView();
    }



}
