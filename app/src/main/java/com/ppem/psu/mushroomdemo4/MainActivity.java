package com.ppem.psu.mushroomdemo4;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


//TODO List ---------------------------------------------
    //On first creating app, ask FARM NAME(add to dbhelper) -> Number of PLANTS -> NAME OF PLANTS (HOUSES/PLANTS/BUILDINGS) -> NAME OF ROOMS(ROOM/Double/Single)
                //Does ALL except ask on first time opening app (doesn't matter too much)
    //Add option to add label (organic/mushroom type/ etc) to any plant or room
                //DONE
    //Add all CRUD for every table
    //Add Bed Table in DatabaseHelper
    //Create dialog menu for each dialog option (maybe)
                //Farm & room dialog done.
    //Try an expandable list view with PLant-> room?
    //Add dates to counts
    //CRUD gridview cells with counts.
        //Currently changes background color. Inserts new count-cell entity. Nothing else.
        //Selected counts can be added to cell. Figure out how to handle cell+count data (cell's have multiple counts at once, and vice-versa).
        //Figure out how to display multiple counts in one cell. (String array[] = (cellPosition, String counts))????
    //Put all hard coded strings into strings value list
    //JSON
    //Add count TYPE table (number, string, boolean, temp, date
    //When creating a new chart(or any object for boolean), have the DAO convert the boolean to an int if inserting to SQLITE. (Doesn't matter if just switching data storage.)
    //FarmCountsView activity needs to update counts in the rooms after add/update/delete somehow, if rooms are already set up.
    //Get rid of a lot of menu options for each activity
    //Add some sort of tutorial/FAQ page on how to do things for now?
    //CODE: add comments to explain things. Make code more consistent. Make it look nicer basically.
    //More constructors for model classes
    //Probably create fragments out some views instead of just an activity except for charts?
    //Farm List Dialog for when user wants to switch to another farm.
    //Should databases open/close for each individual transaction?
    //If another person reads this. I'd probably create your own chart-view (gridview) setup so that:
        //A) you know what the f I was trying to do.
        //B) you might find/know a better way.
//TODO List ----------------------------------------------- By Mitch, For Mitch


public class MainActivity extends AppCompatActivity {
    ListView plantLV;

    private PlantDAO plantDataSource;
    //Get database sources for Exporting data
    RoomDAO roomDataSource;
    CountsDAO countDataSource;
    PlantListViewAdapter pAdapter;
    FarmDAO farmDataSource;
    TextView farmName;
    private List<Plant> plantValues;
    private long farmId;
    private long sharedPrefFarmId = 1;
    private final String sharedPrefName = "AppPreferences";
    private Farm farm;
    SharedPreferences prefs;
    FarmListAdapter fAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        prefs = getSharedPreferences(sharedPrefName, MODE_PRIVATE);
        farmId = prefs.getLong("FarmId", -1);
        if(farmId == -1){
            createFarmDialog();
        } else if(farmDataSource.getSpecificFarm(farmId) == null) {
            createFarmDialog();
        }
        else {
            Farm farm = farmDataSource.getSpecificFarm(farmId);
            farmName = (TextView) findViewById(R.id.farmNameText);
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
                RoomView room = new RoomView();
                room.setPlantInfo(p);
                Intent openRoomListIntent = new Intent(MainActivity.this, RoomView.class);
                startActivity(openRoomListIntent);

            }
        });

    }

    @Override
    protected void onPause(){
        super.onPause();
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
                Toast.makeText(MainActivity.this, "Add Plant Option selected", Toast.LENGTH_SHORT).show();
                createPlantDialog();
                return true;

            case R.id.edit:
                Toast.makeText(MainActivity.this, "Edit Plant Option selected for " + plant.getPlantName(), Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                alertBuilder.setTitle("Editing Plant: " + plant.getPlantName());
                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                final EditText pName = new EditText(MainActivity.this);
                pName.setText(plant.getPlantName());
                layout.addView(pName);
                final EditText pLabel = new EditText(MainActivity.this);
                if(plant.getPlantLabel() == "") {
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
                                Toast.makeText(MainActivity.this, "Deleted Plant " + plant.getPlantName(), Toast.LENGTH_SHORT).show();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == R.id.setFarm){
            createFarmDialog();
        }

        if(id == R.id.setCounts){
            Intent openFarmCountsIntent = new Intent(MainActivity.this, FarmCountsView.class);
            openFarmCountsIntent.putExtra("farm",farmId);
            startActivity(openFarmCountsIntent);
        }

        //TODO add confirmation message.
        if (id == R.id.deleteData) {
            plantDataSource.deleteAllPlants();
            pAdapter.notifyDataSetChanged();
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
            changeFarmDialog();
            refreshView();
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO Create custom dialog xml
    //Create Plant Menu Option Selected
    private void createPlantDialog(){
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setTitle("Enter a Name & a Label(Optional)");
        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final TextView nameLabel = new TextView(MainActivity.this);
        String nLabel = "Name: ";
        nameLabel.setText(nLabel);
        layout.addView(nameLabel);
        final EditText pName = new EditText(MainActivity.this);
        layout.addView(pName);
        final TextView labelLabel = new TextView(MainActivity.this);
        String lLabel = "Label: ";
        labelLabel.setText(lLabel);
        layout.addView(labelLabel);
        final EditText pLabel = new EditText(MainActivity.this);
        layout.addView(pLabel);

        alertBuilder.setView(layout);
        alertBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!pName.getText().toString().isEmpty()) {
                    plantDataSource.createPlant(pName.getText().toString(), pLabel.getText().toString());
                    //populateListView();
                } else {
                    Toast errorToast = Toast.makeText(MainActivity.this, "Error, please enter a name for the new plant", Toast.LENGTH_LONG);
                    errorToast.show();
                }
            }
        });

        AlertDialog a = alertBuilder.create();
        a.show();
    }

    private void changeFarmDialog(){
        //setContentView(R.layout.change_farm_dialog);
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.change_farm_dialog);
        final List<Farm> allFarms = farmDataSource.getAllFarms();
        ListView farmLV = (ListView) findViewById(R.id.farmListDialog) ;
        fAdapter = new FarmListAdapter(this, allFarms);
        farmLV.setAdapter(fAdapter);

        farmLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                farm = allFarms.get(position);
                farmId = farm.getFarmId();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("FarmId",farmId);
                editor.apply();
                plantValues = plantDataSource.getAllPlantsForFarm(farmId);
                pAdapter.notifyDataSetChanged();



                dialog.dismiss();
            }
        });
        dialog.show();
        refreshView();
    }

    private void createFarmDialog(){
        final Dialog dialog = new Dialog(MainActivity.this);
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
                    Toast errorToast = Toast.makeText(MainActivity.this, "Error: Not All Fields Filled. ", Toast.LENGTH_LONG);
                    errorToast.show();
                }
                else{
                    farm = farmDataSource.createFarm(farmName.getText().toString(), farmDescr.getText().toString());
                    farmId = farm.getFarmId();
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong("FarmId",farmId);
                    editor.apply();
                    farmName.setText(farmName.getText().toString());
                    for(int i = 1; i < Integer.parseInt(buildingNumber.getText().toString()) + 1; i++){
                        plantDataSource.createPlantsForFarm(buildingName.getText().toString() + " " + i, farm.getFarmId());
                    }
                    dialog.dismiss();
                    refreshView();
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

    private void refreshView(){
        plantValues = plantDataSource.getAllPlantsForFarm(farmId);
        pAdapter.notifyDataSetChanged();
    }

    //Tried using openCSV jar, not compatable with sqlite.
    //Try to make this a separate class
    private class ExportDatabaseCSVTask extends AsyncTask<String, Void, Boolean> { //TODO make this it's own class (problem with toasts needing activity context)
        // Storage Permissions
        private static final int REQUEST_EXTERNAL_STORAGE = 1;
        private  String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        String newLine[] = {" ", " "};
        String columnHeaders[] = {"Farm Info"};
        SQLiteDatabase database = null;
        List<Plant> plantList = plantDataSource.getAllPlants();
        List<Room> roomList;
        List<Count> countList;
        SimpleDateFormat format = new SimpleDateFormat("MM-dd");
        Date date;
        String printDate;
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

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
                verifyStoragePermissions(MainActivity.this); //REQUIRED FOR API 23+ !!!!!! WILL NOT WORK WITHOUT
                if(!file.exists()){
                    file.createNewFile();
                }
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                csvWrite.writeNext(columnHeaders);
                //Plant Loop
                for (Plant plant : plantList) { //TODO nested for loops for room & count data?
                    //String pId = String.valueOf(plant.getPlantId());
                    String pName = plant.getPlantName();
                    String pLabel = plant.getPlantLabel();
                    String arrStr[] = {pName, pLabel};
                    csvWrite.writeNext(newLine);
                    csvWrite.writeNext(arrStr);//TODO After each .writeNext, appendTab, do for loop Rooms, repeat for counts?
                    roomList = roomDataSource.getAllRoomsForPlant(plant.getPlantId());
                    //Room for Plant Loop
                    for(Room room : roomList) {
                        String roomString[] = {" ", room.getRoomName()};
                        csvWrite.writeNext(roomString);
                        countList = countDataSource.getAllCountsForRoom(room.getRoomId());
                        //Count for Room Loop
                        for(Count count : countList) {
                            date = new Date(count.getCountDate());
                            printDate = format.format(date);
                            String countString[] = {printDate, count.getCountName(), String.valueOf(count.getCountNumber())};
                            csvWrite.writeNext(countString);

                        }
                    }
                }

                csvWrite.close();

                return true;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
                return false;
            } /*catch (IOException e) {
                Log.e("MainActivity", e.getMessage(), e);
                return false;
            }Don't Neeed maybe?*/

        }

        protected void onPostExecute(final Boolean success) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            if (success) {
                Toast.makeText(MainActivity.this, "Export successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Export failed", Toast.LENGTH_SHORT).show();
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