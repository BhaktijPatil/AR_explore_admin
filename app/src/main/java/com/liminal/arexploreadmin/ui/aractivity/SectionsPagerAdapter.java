package com.liminal.arexploreadmin.ui.aractivity;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.liminal.arexploreadmin.R;

// FragmentPagerAdapter returns the fragment corresponding to one of the tabs.
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.ar_activity_tab_1, R.string.ar_activity_tab_2};
    private final Context mContext;
    private final int TAB_COUNT = 2;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // Function to instantiate fragment for a given tab
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0: return new AddActivityFragment();
            case 1: return new ModifyActivityFragment();
        }
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    // Function to decide number of tabs
    @Override
    public int getCount() {
        return TAB_COUNT;
    }
}