package hva.flashdiscount.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import hva.flashdiscount.fragment.DiscountListFragment;
import hva.flashdiscount.fragment.MapViewFragment;

/**
 * Created by Laptop_Ezra on 12-10-2016.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                MapViewFragment tab2 = new MapViewFragment();
                return tab2;
            case 1:
                DiscountListFragment tab1 = new DiscountListFragment();
                return tab1;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}