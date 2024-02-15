package ch.zhaw.init.orwell_a.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import ch.zhaw.init.orwell_a.ui.fragments.AudioFragment;
import ch.zhaw.init.orwell_a.ui.fragments.ImageFragment;
import ch.zhaw.init.orwell_a.ui.fragments.LocationFragment;
import ch.zhaw.init.orwell_a.ui.fragments.SpyFragment;

public class TabAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private String[] tabTitles;


    public TabAdapter(FragmentManager fm, int behavior, int numOfTabs, String[] tabTitles) {
        super(fm, behavior);
        this.mNumOfTabs = numOfTabs;
        this.tabTitles = tabTitles;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new ImageFragment();
            case 2:
                return new AudioFragment();
            case 3:
                return new LocationFragment();
            default:
                return new SpyFragment();
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
