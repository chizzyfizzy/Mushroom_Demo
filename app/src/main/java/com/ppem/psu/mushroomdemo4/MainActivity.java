package com.ppem.psu.mushroomdemo4;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


//TODO Ask for farm name and keep it with shared preferences? Or Farm Database?


public class MainActivity extends AppCompatActivity {
    ListView plantLV;

    private PlantDAO plantDataSource;
    //Get database sources for Exporting data
    RoomDAO roomDataSource;
    CountsDAO countDataSource;
    PlantListViewAdapter pAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        roomDataSource = new RoomDAO(this);
        countDataSource = new CountsDAO(this);


        plantDataSource = new PlantDAO(this);
        plantDataSource.open();
        populateListView();

        //list item click handler
        plantLV = (ListView) findViewById(R.id.plantListView);
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

        //Create new plant handler
        //TODO could autogenerate plants by asking for 'Name', and autoincrement numbers. Could update labels later(making it optional for user)
        if (id == R.id.createPlant) {
            createPlantDialog();

        }

        //TODO add confirmation message.
        if (id == R.id.deleteData) {
            plantDataSource.deleteAllPlants();
            populateListView();
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
        return super.onOptionsItemSelected(item);
    }


    //populates list view with data from sqlite
    private void populateListView() {

        List<Plant> plantValues = plantDataSource.getAllPlants();

        //final PlantListViewAdapter pAdapter = new PlantListViewAdapter(this, plantValues);
        pAdapter = new PlantListViewAdapter(this, plantValues);

        plantLV = (ListView) findViewById(R.id.plantListView);
        plantLV.setAdapter(pAdapter);
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
                    populateListView();
                } else {
                    Toast errorToast = Toast.makeText(MainActivity.this, "Error, please enter a name for the new plant", Toast.LENGTH_LONG);
                    errorToast.show();
                }
            }
        });

        AlertDialog a = alertBuilder.create();
        a.show();
    }




    //Tried using openCSV jar, not compatable with sqlite.
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
              //  Cursor curCSV = myDatabase.rawQuery("select * from " + Table_Name, null);
              //  csvWrite.writeNext(curCSV.getColumnNames());
              //  while (curCSV.moveToNext()) {
               //     String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2)};
                //    csvWrite.writeNext(arrStr);
               // }
                csvWrite.close();

                return true;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
                return false;
            } /*catch (IOException e) {
                Log.e("MainActivity", e.getMessage(), e);
                return false;
            }*/

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