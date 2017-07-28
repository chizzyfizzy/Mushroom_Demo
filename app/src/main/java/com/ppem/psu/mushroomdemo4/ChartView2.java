package com.ppem.psu.mushroomdemo4;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ChartView2 extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ChartDAO chartDataSource;
    private BedDAO bedDataSource;
    private CellDAO cellDataSource;
    private CountsDAO countsDataSource;
    private Toolbar toolbar;
    private long roomId;
    private Chart chart;
    private int chartCol, chartRow;
    private List<Bed> bedList;
    public Bed bed;
    public List<Count> countList;
    Bundle bundleA, bundleB, bundleC, bundleD;

    private FragmentPagerAdapter smartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_view2);
        Intent getIntent = getIntent();
        roomId = getIntent.getLongExtra("RoomId", -1);


        chartDataSource = new ChartDAO(this);
        chartDataSource.open();
        countsDataSource = new CountsDAO(this);
        countsDataSource.open();
        countList = countsDataSource.getChartCounts(roomId);
        cellDataSource = new CellDAO(this);
        cellDataSource.open();
        bedDataSource = new BedDAO(this);
        bedDataSource.open();
        bedList = bedDataSource.getBedsForRoom(roomId);
        chart = chartDataSource.getChartForRoom(roomId);
        chartCol = chart.getColNum();
        chartRow = chart.getRowNum();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        //Keeps cells highlight after switching tabs
        setupViewPager(viewPager);
        //viewPager.setOffscreenPageLimit(4);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
            chartDataSource.close();
            countsDataSource.close();
            cellDataSource.close();
            bedDataSource.close();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setupViewPager(ViewPager viewPager) {
        //TODO Peaked room doesn't seem to add another column
        //TabPageAdapter adapter = new TabPageAdapter(getSupportFragmentManager());
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for(int i = 0; i < bedList.size(); i++){
            ChartFragment frag = new ChartFragment();
            Bundle b = new Bundle();
            b.putLong("room", roomId);
            b.putInt("level",bedList.get(i).getBedLevels());
            b.putLong("bed", bedList.get(i).getBedId());
            b.putString("name",bedList.get(i).getBedName());
            frag.setArguments(b);
            adapter.addFragment(frag, bedList.get(i).getBedName());
        }
/*
        BedA fragA = new BedA();
        bundleA = new Bundle();
        bundleA.putLong("room",roomId);
        bundleA.putInt("column",bedList.get(0).getBedLevels());
        bundleA.putLong("bed", bedList.get(0).getBedId());
        fragA.setArguments(bundleA);
        adapter.addFragment(fragA, "BED A");

        BedB fragB = new BedB();
        bundleB = new Bundle();
        bundleB.putLong("room", roomId);
        bundleB.putLong("bed", bedList.get(1).getBedId());
        bundleB.putInt("column", bedList.get(1).getBedLevels());
        fragB.setArguments(bundleB);
        adapter.addFragment(fragB, "BED B");

        BedC fragC = new BedC();
        bundleC = new Bundle();
        bundleC.putLong("room", roomId);
        bundleC.putLong("bed", bedList.get(2).getBedId());
        bundleC.putInt("column", bedList.get(2).getBedLevels());
        fragC.setArguments(bundleC);
        adapter.addFragment(fragC, "BED C");

        BedD fragD = new BedD();
        bundleD = new Bundle();
        bundleD.putLong("room", roomId);
        bundleD.putLong("bed", bedList.get(3).getBedId());
        bundleD.putInt("column", bedList.get(3).getBedLevels());
        fragD.setArguments(bundleD);
        adapter.addFragment(fragD, "BED D");
*/

        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bed = bedList.get(position);
                adapter.getItem(position).updateChart();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<ChartFragment> fragList = new ArrayList<>();
        private final List<String> fragTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }


        @Override
        public ChartFragment getItem(int position) {
            return fragList.get(position);
        }

        @Override
        public int getCount() {
            return fragList.size();
        }

        public void addFragment(ChartFragment fragment, String title) {
            fragList.add(fragment);
            fragTitleList.add(title);
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragTitleList.get(position);
        }
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
           // cellDataSource.deleteCellsForRoom(roomId);
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
