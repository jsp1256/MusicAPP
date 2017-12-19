package tk.xiangjianpeng.musicapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tk.xiangjianpeng.musicapp.Fragment.MusicListFragment;

/**
 * Created by user on 2017/12/18.
 */

public class MenuActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView fragmentTitle1, fragmentTitle2, titleBottomLine;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private List<Fragment> fragmentList = new ArrayList<>();
    // screenWidth表示屏幕宽度
    private int screenWidth, bottomLineWidth;

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
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageSelected(int position) {

            }

            public void onPageScrolled(int item, float percent, int offset) {
                titleBottomLine.setX(item * bottomLineWidth + offset
                        / FRAGMENT_COUNT);
            }

            public void onPageScrollStateChanged(int position) {

            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

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
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section4).toUpperCase(l);
            }
            return null;
        }
    }

    private void getBottomLineWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        bottomLineWidth = screenWidth / FRAGMENT_COUNT;

    }
}
