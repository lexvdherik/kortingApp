//package hva.flashdiscount.adapter;
//
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//
//import hva.flashdiscount.fragment.DiscountListFragment;
//import hva.flashdiscount.fragment.MapViewFragment;
//
///**
// * Created by chrisvanderheijden on 10/10/2016.
// */
//
//public static class PagerAdapter extends FragmentPagerAdapter {
//
//    private static int NUM_ITEMS = 2;
//
//    public PagerAdapter(FragmentManager fragmentManager) {
//        super(fragmentManager);
//    }
//
//    @Override
//    public Fragment getItem(int position) {
//        switch(position){
//            case 0:
//        //        return DiscountListFragment.instantiate();
//                return MapViewFragment.newInstance(0,"");
//
//            default: return null;
//        }
//
//    }
//
//    @Override
//    public int getCount() {
//        return NUM_ITEMS;
//    }
//
//    @Override
//    public CharSequence getTitlePage(int position){
//           return "Page" + position;
//    }
//}
