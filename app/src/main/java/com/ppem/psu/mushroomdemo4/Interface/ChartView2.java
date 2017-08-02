package com.ppem.psu.mushroomdemo4.Interface;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.ppem.psu.mushroomdemo4.DatabaseControllers.BedDAO;
import com.ppem.psu.mushroomdemo4.DatabaseControllers.CellDAO;
import com.ppem.psu.mushroomdemo4.DatabaseControllers.CountsDAO;
import com.ppem.psu.mushroomdemo4.Models.Bed;
import com.ppem.psu.mushroomdemo4.Models.Count;
import com.ppem.psu.mushroomdemo4.R;

import java.util.ArrayList;
import java.util.List;

public class ChartView2 extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private BedDAO bedDataSource;
    private CellDAO cellDataSource;
    private CountsDAO countsDataSource;
    private Toolbar toolbar;
    private long roomId;
    private List<Bed> bedList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_view2);
        Intent getIntent = getIntent();
        roomId = getIntent.getLongExtra("RoomId", -1);

        countsDataSource = new CountsDAO(this);
        cellDataSource = new CellDAO(this);
        bedDataSource = new BedDAO(this);

        countsDataSource.open();
        cellDataSource.open();
        bedDataSource.open();
        bedList = bedDataSource.getBedsForRoom(roomId);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        countsDataSource.close();
        cellDataSource.close();
        bedDataSource.close();
    }

    //Viewpager used for tabs/swiping
    private void setupViewPager(ViewPager viewPager) {
        //TODO Peaked room doesn't seem to add another column
        //TabPageAdapter adapter = new TabPageAdapter(getSupportFragmentManager());
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        //Create Bed Fragments. Pass Bed Info
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

        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        //On selected fragment, change current bed selection, return fragment from fragment list.
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                adapter.getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    //Fragnent Adapter
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

        //Make fragment list for getting the fragment on selection later.
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

    //Settings Menu
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
        if(id == R.id.editBeds) {
            //TODO Add functionality to CRUD individual Beds.
        }
        return super.onOptionsItemSelected(item);
    }



    @Override //On back key, close db handlers and close activity. Helps with memory leaks?
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
