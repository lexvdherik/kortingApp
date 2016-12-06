package hva.flashdiscount.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import hva.flashdiscount.fragment.DiscountListFragment;
import hva.flashdiscount.fragment.MapViewFragment;

/**
 * Return tabs to display.
 *
 * @author Laptop_Ezra
 * @since 12-10-2016
 */

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = TabPagerAdapter.class.getSimpleName();

    private int mNumOfTabs;

    public TabPagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new MapViewFragment();
            case 1:
                return new DiscountListFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}