package com.ppem.psu.mushroomdemo4;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ChartView extends AppCompatActivity {
    //TODO Make it a static tab-layout instead of current dynamic with for loop (Did not realize EVERY room had 4 beds.)  ---Maybe not because of bedPeak variable---
    private ChartDAO chartDataSource;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private long roomId;
    ViewPagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_view);
        chartDataSource = new ChartDAO(this);
        chartDataSource.open();

        Intent getIntent = getIntent();
        roomId = getIntent.getLongExtra("RoomId", -1);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateTabs();

    }

    public void populateTabs(){

        viewPager = (ViewPager) findViewById(R.id.myViewPager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Chart chart = chartDataSource.getChartForRoom(roomId);
        char bedIncrement = 'A';
        //Populate if room is peaked
        if(chart.getBedPeak()){
            for(int i = 0; i< 4; i++) {
                adapter.addFrag(ChartFragment.newInstance(chart.getColNum() + 1, chart.getRowNum()), "BED " + (bedIncrement += i));
            }
        }

        else{
            for(int i = 0; i< 4; i++) {
                adapter.addFrag(ChartFragment.newInstance(chart.getColNum(), chart.getRowNum()), "BED " + (bedIncrement += i));
            }
        }


        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
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
            viewPager.removeAllViews();
            populateTabs();
        }
        return super.onOptionsItemSelected(item);
    }


    private void createChartDialog(){
        final Dialog dialog = new Dialog(ChartView.this);
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
                    Toast errorToast = Toast.makeText(ChartView.this, "Error: Not All Fields Filled. ", Toast.LENGTH_LONG);
                    errorToast.show();
                }
                else{
                    boolean bedPeakCheck = bedPeak.isChecked();
                    int colNum = Integer.parseInt(colNumText.getText().toString());
                    int rowNum = Integer.parseInt(rowNumText.getText().toString());
                    //Box NOT checked
                    if(!copyToAllCheckBox.isChecked()) {
                        if(roomId > -1) {
                            chartDataSource.createChart(colNum, bedPeakCheck, rowNum, roomId);
                            viewPager.removeAllViews();
                            populateTabs();
                            populateTabs();
                        }
                        dialog.dismiss();
                    }
                    //Box IS checked
                    else if(copyToAllCheckBox.isChecked()){
                        chartDataSource.createChartAllRooms(colNum, bedPeakCheck, rowNum);
                        viewPager.removeAllViews();
                        populateTabs();
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

}
