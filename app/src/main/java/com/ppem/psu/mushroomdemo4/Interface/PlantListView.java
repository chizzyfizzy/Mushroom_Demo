package com.ppem.psu.mushroomdemo4.Interface;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.opencsv.CSVWriter;
import com.ppem.psu.mushroomdemo4.DatabaseControllers.CountsDAO;
import com.ppem.psu.mushroomdemo4.DatabaseControllers.FarmDAO;
import com.ppem.psu.mushroomdemo4.DatabaseControllers.PlantDAO;
import com.ppem.psu.mushroomdemo4.DatabaseControllers.RoomDAO;
import com.ppem.psu.mushroomdemo4.Models.Count;
import com.ppem.psu.mushroomdemo4.Models.Farm;
import com.ppem.psu.mushroomdemo4.Models.Plant;
import com.ppem.psu.mushroomdemo4.Models.Room;
import com.ppem.psu.mushroomdemo4.R;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


//TODO List ---------------------------------------------
    //Add all CRUD for every table
    //Create dialog menu for each dialog option (maybe)
                //Farm & room dialog done.
    //Try an expandable list view with PLant-> room?
    //JSON
    //Add count TYPE table (water, temp, etc.,)
    //Get rid of useless menu options for each activity
    //Calendar and date functionalities. (I guess make a new count object for each date a room is edited?
    //A room cycle feature, room mushroom type
    //Weekly counts feature
    //A list of cells with specific counts for room. (Reverse of getting counts for cell function) For exporting data.
    //User Login to trace who edited what (save for amazon if I get there.)

    //RoomListView CRUD is currently bugged**********

    //Add some sort of tutorial/FAQ page on how to do things for now?
    //Probably create fragments out some views instead of just an activity except for charts?
//TODO List ----------------------------------------------- By Mitch, For Mitch


public class PlantListView extends AppCompatActivity {
    ListView plantLV, farmLV;
    private PlantDAO plantDataSource;
    //Get all database sources for Exporting data
    RoomDAO roomDataSource;
    CountsDAO countDataSource;
    PlantListViewAdapter pAdapter;
    FarmDAO farmDataSource;
    TextView farmName;
    private List<Plant> plantValues;
    private long farmId;
    private final String sharedPrefsName = "AppPreferences";
    private Farm farm;
    SharedPreferences prefs;
    FarmListAdapter fAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_list_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        farmDataSource = new FarmDAO(this);
        plantDataSource = new PlantDAO(this);
        roomDataSource = new RoomDAO(this);
        countDataSource = new CountsDAO(this);

    }

    @Override
    protected void onResume(){
        super.onResume();
        farmDataSource.open();
        plantDataSource.open();
        prefs = getSharedPreferences(sharedPrefsName, MODE_PRIVATE);
        farmId = prefs.getLong("FarmId", -1);
        farmName = (TextView) findViewById(R.id.farmNameText);
        if(farmId == -1){
            createFarmDialog();
        } else if(farmDataSource.getSpecificFarm(farmId) == null) {
            createFarmDialog();
        }
        else {
            farm = farmDataSource.getSpecificFarm(farmId);
            farmName.setText(farm.getFarmName());
        }
        plantValues = plantDataSource.getAllPlantsForFarm(farmId);
        pAdapter = new PlantListViewAdapter(this, plantValues);
        plantLV = (ListView) findViewById(R.id.plantListView);
        plantLV.setAdapter(pAdapter);
        //list item click handler
        plantLV = (ListView) findViewById(R.id.plantListView);
        registerForContextMenu(plantLV);
        plantLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Plant p = pAdapter.getItem(position);
                RoomListView room = new RoomListView();
                room.setPlantInfo(p);
                Intent openRoomListIntent = new Intent(PlantListView.this, RoomListView.class);
                startActivity(openRoomListIntent);

            }
        });

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        farmDataSource.close();
        plantDataSource.close();
    }

    //Context menu on long button click (User holds selection on list item)
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        if(v.getId() == R.id.plantListView){
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
        final Plant plant = plantValues.get(info.position);
        switch(item.getItemId()){
            case R.id.add:
                Toast.makeText(PlantListView.this, "Add Plant Option selected", Toast.LENGTH_SHORT).show();
                createPlantDialog();
                return true;

            case R.id.edit:
                Toast.makeText(PlantListView.this, "Edit Plant Option selected for " + plant.getPlantName(), Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(PlantListView.this);
                alertBuilder.setTitle("Editing Plant: " + plant.getPlantName());
                LinearLayout layout = new LinearLayout(PlantListView.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                final EditText pName = new EditText(PlantListView.this);
                pName.setText(plant.getPlantName());
                layout.addView(pName);
                final EditText pLabel = new EditText(PlantListView.this);
                if(plant.getPlantLabel() == null || plant.getPlantLabel().equals("")) {
                    pLabel.setHint("Add Optional Label");
                } else {pLabel.setText(plant.getPlantLabel()); }
                layout.addView(pLabel);
                alertBuilder.setView(layout);
                alertBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        plantValues.set(info.position, new Plant(plant.getPlantId(), pName.getText().toString(),  pLabel.getText().toString()));
                        pAdapter.notifyDataSetChanged();
                        plantDataSource.updatePlant(pName.getText().toString(), pLabel.getText().toString(), plant.getPlantId());
                    }
                });
                AlertDialog a = alertBuilder.create();
                a.show();

                return true;

            case R.id.delete:
                new AlertDialog.Builder(this)
                        .setTitle("Confirm")
                        .setMessage("Are you sure you want to delete " + plant.getPlantName() + "?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                plantDataSource.deletePlant(plant);
                                plantValues.remove(info.position);
                                pAdapter.notifyDataSetChanged();
                                Toast.makeText(PlantListView.this, "Deleted Plant " + plant.getPlantName(), Toast.LENGTH_SHORT).show();

                            }})
                        .setNegativeButton("Cancel", null).show();
                return true;

            default:
                return super.onContextItemSelected(item);
        }

    }


    //Settings menu creator and handler
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_plant_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == R.id.createFarm){
            createFarmDialog();
        }

        if(id == R.id.setCounts){
            Intent openFarmCountsIntent = new Intent(PlantListView.this, FarmCountsView.class);
            openFarmCountsIntent.putExtra("farm",farmId);
            startActivity(openFarmCountsIntent);
        }

        //TODO add confirmation message.
        if (id == R.id.deleteFarm) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Delete " + farm.getFarmName() + "?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            farmDataSource.deleteFarm(farm);
                            Toast.makeText(PlantListView.this, "Deleted farm " + farm.getFarmName(), Toast.LENGTH_SHORT).show();
                            if(farmDataSource.getAllFarms().size() > 0){
                                changeFarmDialog();
                            }
                            if(farmDataSource.getAllFarms().size() == 0){
                                createFarmDialog();
                            }
                            refreshData();
                        }})
                    .setNegativeButton("Cancel", null).show();
        }
        if (id == R.id.exportData) {
            try {
                roomDataSource.open();
                countDataSource.open();
                new ExportDatabaseCSVTask().execute("");
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        if(id == R.id.changeFarm){
            if(farmDataSource.getAllFarms().size() > 0){
                changeFarmDialog();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO Create custom dialog xml
    //Create Plant Menu Option Selected
    private void createPlantDialog(){
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(PlantListView.this);
        alertBuilder.setTitle("Enter a Name & a Label(Optional)");
        LinearLayout layout = new LinearLayout(PlantListView.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final TextView nameLabel = new TextView(PlantListView.this);
        String nLabel = "Name: ";
        nameLabel.setText(nLabel);
        layout.addView(nameLabel);
        final EditText pName = new EditText(PlantListView.this);
        layout.addView(pName);
        final TextView labelLabel = new TextView(PlantListView.this);
        String lLabel = "Label: ";
        labelLabel.setText(lLabel);
        layout.addView(labelLabel);
        final EditText pLabel = new EditText(PlantListView.this);
        layout.addView(pLabel);

        alertBuilder.setView(layout);
        alertBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!pName.getText().toString().isEmpty()) {
                    plantDataSource.createPlant(pName.getText().toString(), pLabel.getText().toString());
                    refreshData();
                } else {
                    Toast errorToast = Toast.makeText(PlantListView.this, "Error, please enter a name for the new plant", Toast.LENGTH_LONG);
                    errorToast.show();
                }
            }
        });

        AlertDialog a = alertBuilder.create();
        a.show();
    }

    private void refreshData(){
        farmName.setText(farm.getFarmName());
        farmId = farm.getFarmId();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("FarmId",farmId);
        editor.apply();
        plantValues = plantDataSource.getAllPlantsForFarm(farmId);
        pAdapter = new PlantListViewAdapter(this, plantValues);
        plantLV = (ListView) findViewById(R.id.plantListView);
        plantLV.setAdapter(pAdapter);
    }

    private void changeFarmDialog(){
        final Dialog dialog = new Dialog(PlantListView.this);
        dialog.setContentView(R.layout.change_farm_dialog);
        final List<Farm> allFarms = farmDataSource.getAllFarms();
        farmLV = (ListView) dialog.findViewById(R.id.farmListDialog) ;
        fAdapter = new FarmListAdapter(this, allFarms);
        farmLV.setAdapter(fAdapter);
        farmLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                farm = allFarms.get(position);
                refreshData();
            }
        });
        dialog.show();

    }

    private void createFarmDialog(){
        final Dialog dialog = new Dialog(PlantListView.this);
        dialog.setContentView(R.layout.create_farm_dialog);
        final EditText farmName = (EditText) dialog.findViewById(R.id.farmNameEditText);
        final EditText buildingName = (EditText) dialog.findViewById(R.id.buildingNameEditText);
        final EditText buildingNumber = (EditText) dialog.findViewById(R.id.buildingNumberEditText);
        final EditText roomName = (EditText) dialog.findViewById(R.id.roomNameEditText);
        final EditText farmDescr = (EditText) dialog.findViewById(R.id.farmDescrEditText);
        Button createBtn = (Button) dialog.findViewById(R.id.createFarmButton);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if any value is empty
                if(farmName.getText().toString().isEmpty() || buildingName.getText().toString().isEmpty()
                        || buildingNumber.getText().toString().isEmpty() || roomName.getText().toString().isEmpty()){
                    Toast errorToast = Toast.makeText(PlantListView.this, "Error: Not All Fields Filled. ", Toast.LENGTH_LONG);
                    errorToast.show();
                }
                else{
                    farm = farmDataSource.createFarm(farmName.getText().toString(), farmDescr.getText().toString());
                    for(int i = 1; i < Integer.parseInt(buildingNumber.getText().toString()) + 1; i++){
                        plantDataSource.createPlantsForFarm(buildingName.getText().toString() + " " + i, farm.getFarmId());
                    }
                    dialog.dismiss();
                    refreshData();
                    //TODO add room/farm/plant names to sharedpreferences. For now nothing happens with roomName.
                }
            }
        });

        Button cancelBtn = (Button) dialog.findViewById(R.id.cancelFarmButton);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //Exporting current farm data
    //Tried using openCSV jar, not compatable with sqlite.
    //Should probably make this a separate class
    private class ExportDatabaseCSVTask extends AsyncTask<String, Void, Boolean> { //TODO make this it's own class (problem with toasts needing activity context)
        // Storage Permissions
        private static final int REQUEST_EXTERNAL_STORAGE = 1;
        private  String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        String newLine[] = {" ", " "};
        String columnHeaders[] = {"Farm Info"};
        List<Plant> plantList = plantDataSource.getAllPlants();
        List<Room> roomList;
        List<Count> countList;
        SimpleDateFormat format = new SimpleDateFormat("MM-dd");
        Date date;
        String printDate;
        private final ProgressDialog dialog = new ProgressDialog(PlantListView.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting database...");
            this.dialog.show();
        }

        protected Boolean doInBackground(final String... args) {
            File dbFile = getDatabasePath("farm.db");
            System.out.println(dbFile);  // displays the data base path in your logcat
            File exportDir = new File(Environment.getExternalStorageDirectory(), "");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
                System.out.println("New export directory");
            }
            File file = new File(exportDir, "farmData.csv");
            try {
                verifyStoragePermissions(PlantListView.this); //REQUIRED FOR API 23+ !!!!!! WILL NOT WORK WITHOUT
                if(!file.exists()){
                    file.createNewFile();
                }
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                csvWrite.writeNext(columnHeaders);
                //Plant Loop
                for (Plant plant : plantList) { //TODO nested for loops for room & count data
                    //String pId = String.valueOf(plant.getPlantId());
                    String pName = plant.getPlantName();
                    String pLabel = plant.getPlantLabel();
                    String arrStr[] = {pName, pLabel};
                    csvWrite.writeNext(newLine);
                    csvWrite.writeNext(arrStr);//TODO After each .writeNext, appendTab, do for loop Rooms, repeat for counts
                    roomList = roomDataSource.getAllRoomsForPlant(plant.getPlantId());
                    //Room for Plant Loop
                    for(Room room : roomList) {
                        String roomString[] = {" ", room.getRoomName()};
                        csvWrite.writeNext(roomString);
                        countList = countDataSource.getAllCountsForRoom(room.getRoomId());
                        //Count for Room Loop
                        for(Count count : countList) {
                            //date = new Date(count.getCountDate());
                          //  printDate = format.format(date);
                            String countString[] = {/*printDate, */count.getCountName(), String.valueOf(count.getCountNumber())};
                            csvWrite.writeNext(countString);

                        }
                    }
                }

                csvWrite.close();

                return true;
            } catch (Exception e) {
                Log.e("PlantListView", e.getMessage(), e);
                return false;
            } /*catch (IOException e) {
                Log.e("PlantListView", e.getMessage(), e);
                return false;
            }Don't Neeed maybe?*/

        }

        protected void onPostExecute(final Boolean success) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            if (success) {
                Toast.makeText(PlantListView.this, "Export successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PlantListView.this, "Export failed", Toast.LENGTH_SHORT).show();
            }
        }


        private  void verifyStoragePermissions(Activity activity) {
            // Check if we have write permission
            int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }
        }
    }

}