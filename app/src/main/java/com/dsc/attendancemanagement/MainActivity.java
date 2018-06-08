package com.dsc.attendancemanagement;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.dsc.attendancemanagement.attendance.AttendanceFragment;
import com.dsc.attendancemanagement.edit.EditClassroomFragment;
import com.dsc.attendancemanagement.statistics.StatisticsFragment;

/**
 * Created by Developer Student Club
 * Varshit Ratna(lead)
 * Devaraj Akhil(Core team)
 */
public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int numberOfClassrooms = 0;
        Bundle args = getIntent().getExtras();
        if (args != null) {
            numberOfClassrooms = args.getInt("numberOfClassrooms");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabss);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
    }

    public class TabsPagerAdapter extends FragmentPagerAdapter {

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return EditClassroomFragment.newInstance();
                case 1:
                    return AttendanceFragment.newInstance();
                case 2:
                    return StatisticsFragment.newInstance();
                default:
                    return AttendanceFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            // get item count - equal to number of tabs
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] titles = getResources().getStringArray(R.array.main_page);
            return titles[position];
        }
    }
}
