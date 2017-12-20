package tk.xiangjianpeng.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tk.xiangjianpeng.musicapp.Fragment.HistoryListFragment;
import tk.xiangjianpeng.musicapp.Fragment.MusicListFragment;

/**
 * Created by user on 2017/12/18.
 */

public class MenuActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView fragmentTitle1, fragmentTitle2, titleBottomLine;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private List<Fragment> fragmentList = new ArrayList<>();
    // screenWidth表示屏幕宽度
    private int screenWidth, bottomLineWidth;
    private RelativeLayout menutab_Layout;

    public static final int FRAGMENT_COUNT = 2;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);
        getBottomLineWidth();
        fragmentTitle1=(TextView)findViewById(R.id.fragment1);
        fragmentTitle1.setOnClickListener(this);
        fragmentTitle2=(TextView)findViewById(R.id.fragment2);
        fragmentTitle2.setOnClickListener(this);
        titleBottomLine=(TextView)findViewById(R.id.fragmentTitle);
        fragmentList.add(new MusicListFragment());
        fragmentList.add(new HistoryListFragment());
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new PageChangeListener());
        menutab_Layout= (RelativeLayout) findViewById(R.id.menutab_layout);
        menutab_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, UiActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fragment1:
                viewPager.setCurrentItem(0);
                break;
            case R.id.fragment2:
                viewPager.setCurrentItem(1);
                break;
        }

    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            titleBottomLine.setX(position * bottomLineWidth + positionOffsetPixels
                    / FRAGMENT_COUNT);
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private class MenuListItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        }
    }

    private void getBottomLineWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        bottomLineWidth = screenWidth / FRAGMENT_COUNT;
    }
}
