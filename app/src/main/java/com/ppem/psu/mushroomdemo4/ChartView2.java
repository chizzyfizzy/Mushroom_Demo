package com.ppem.psu.mushroomdemo4;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class ChartView2 extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ChartDAO chartDataSource;
    private CellDAO cellDataSource;
    private Toolbar toolbar;
    private long roomId;
    private Chart chart;
    private int chartCol, chartRow;
    Bundle bundleA, bundleB, bundleC, bundleD;

    //TODO Add Bed Peak Level to creating cells method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_view2);
        Intent getIntent = getIntent();
        roomId = getIntent.getLongExtra("RoomId", -1);


        chartDataSource = new ChartDAO(this);
        chartDataSource.open();
        cellDataSource = new CellDAO(this);
        cellDataSource.open();
        chart = chartDataSource.getChartForRoom(roomId);
        chartCol = chart.getColNum();
        chartRow = chart.getRowNum();



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        //Keeps cells highlight after switching tabs
        viewPager.setOffscreenPageLimit(4);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        if(chart == null){
            createChartDialog();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        //TODO Peaked room doesn't seem to add another column
        bundleA = new Bundle();
        bundleA.putLong("room",roomId);
        bundleA.putInt("column",chartCol);
        bundleA.putString("bed", "A");
        TabPageAdapter adapter = new TabPageAdapter(getSupportFragmentManager());

        char bedIncrement = 'A';
        Bundle bundle = new Bundle();
        bundle.putLong("room",roomId);
        for(int i = 0; i < 4; i++){
            if((chart.getBedPeak()) && (i == 1 || i == 2)) {
                bundle.putInt("column", chartCol + 1);
            }
            else {
                bundle.putInt("column", chartCol);
            }
            bundle.putString("bed", String.valueOf(bedIncrement));
            ChartFragment fragment = new ChartFragment();
            fragment.setArguments(bundle);
            adapter.addFragment(fragment, "BED " + String.valueOf(bedIncrement));
            bedIncrement++;
        }

        /*
        ChartFragment fragA = new ChartFragment();
        fragA.setArguments(bundleA);
        adapter.addFragment(fragA, "BED A");

        fragA = new ChartFragment();
        bundleA.putString("bed", "B");
        fragA.setArguments(bundleA);
        adapter.addFragment(fragA, "BED B");
*/



        viewPager.setAdapter(adapter);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chart_view, menu);
        return true;
    }

    @Override //TODO Create & Edit Chart functions
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.createChart) {  //TODO Check if chart is already made.
/*            if(adapter.getCount() > 0) {
                Toast errorToast = Toast.makeText(ChartView.this, "You cannot create a new chart while one is already set for this room." + "\n" +
                        " Either edit or delete the current chart.", Toast.LENGTH_LONG);
                errorToast.show();
            }*/
            //else {
            createChartDialog();
            //}
        }
        if(id == R.id.editChart) {

        }
        if(id == R.id.deleteAllCharts){
            chartDataSource.deleteAllCharts();
            cellDataSource.deleteCellsForRoom(roomId);
        }

        return super.onOptionsItemSelected(item);
    }






    private void createChartDialog(){
        final Dialog dialog = new Dialog(ChartView2.this);
        dialog.setContentView(R.layout.chart_view_dialog);
        final CheckBox bedPeak = (CheckBox) dialog.findViewById(R.id.bedPeakCheckBox);
        final EditText colNumText = (EditText) dialog.findViewById(R.id.bedNumEditText);
        final EditText rowNumText = (EditText) dialog.findViewById(R.id.numRowsEditText);
        final CheckBox copyToAllCheckBox = (CheckBox) dialog.findViewById(R.id.copyToAllCheckBox);
        Button createBtn = (Button) dialog.findViewById(R.id.createChartButton);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(colNumText.getText().toString().isEmpty() || rowNumText.getText().toString().isEmpty()) {
                    Toast errorToast = Toast.makeText(ChartView2.this, "Error: Not All Fields Filled. ", Toast.LENGTH_LONG);
                    errorToast.show();
                }
                else{
                    boolean bedPeakCheck = bedPeak.isChecked();
                    int colNum = Integer.parseInt(colNumText.getText().toString());
                    int rowNum = Integer.parseInt(rowNumText.getText().toString());
                    //Box NOT checked
                    if(!copyToAllCheckBox.isChecked()) {
                        if(roomId > -1) {
                          //  chartDataSource.createChart(colNum, bedPeakCheck, rowNum, roomId);
                            createCellList(colNum, rowNum, roomId);

                        }
                        dialog.dismiss();
                    }
                    //Box IS checked
                    else if(copyToAllCheckBox.isChecked()){
                       // chartDataSource.createChartAllRooms(colNum, bedPeakCheck, rowNum);

                        dialog.dismiss();
                    }
                }
            }
        });
        Button cancelBtn = (Button) dialog.findViewById(R.id.cancelDialogButton);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void createCellList(int col, int row, long roomId){
        char bedIncrement = 'A';
        for (int n = 0; n < 4; n++) {
            for (int i = 1; i < row + 1; i++) {
                for (int j = 1; j < col + 1; j++) {
                    if (j * row + i >= col) {
                        System.out.println(j + " " + i);
                        //label = String.valueOf(j) + "(" + String.valueOf(i) + ")";
                         cellDataSource.createCell(String.valueOf(bedIncrement), j, i, roomId);
                    } else {
                        //label = String.valueOf(j) + "(" + String.valueOf(i * rowNum + i);
                        System.out.println(j + " " + i);
                        cellDataSource.createCell(String.valueOf(bedIncrement), j, i, roomId);
                    }
                }
            }
            bedIncrement++;
        }
    }

}
