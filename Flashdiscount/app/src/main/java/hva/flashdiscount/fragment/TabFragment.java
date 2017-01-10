package hva.flashdiscount.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import hva.flashdiscount.R;
import hva.flashdiscount.adapter.CategoryAdapter;
import hva.flashdiscount.adapter.TabPagerAdapter;


public class TabFragment extends Fragment {

    private static final String TAG = TabFragment.class.getSimpleName();
    private int tabPosition;
    private SharedPreferences sharedPref;
    private AlertDialog dialog;

    public TabFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        tabPosition = sharedPref.getInt("tab_position", 0);

    }

    public void showFilter() {
        Log.e(TAG, "showFilter: " + "SHOWING FILTER");
        if (dialog == null) {
            View tabView = getActivity().findViewById(R.id.tab_relative_layout);
            TabLayout tabLayout = (TabLayout) tabView.findViewById(R.id.tab_layout);
            final TabPagerAdapter adapter = new TabPagerAdapter(getFragmentManager(), tabLayout.getTabCount());
            final MapViewFragment mapViewFragment = (MapViewFragment) adapter.getItem(0);


            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View checkBoxView = inflater.inflate(R.layout.marker_selection, null);

            mapViewFragment.categoryAdapter = new CategoryAdapter(getContext(), R.layout.category_list_child, mapViewFragment.categories, mapViewFragment);

            ListView categoryListView = (ListView) checkBoxView.findViewById(R.id.listview_categories);
            categoryListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
            categoryListView.setAdapter(mapViewFragment.categoryAdapter);
            builder.setView(checkBoxView);

            CheckBox buses = (CheckBox) checkBoxView.findViewById(R.id.checkBox1);
            //trains = (CheckBox) list.findViewById(R.id.checkBox2);
            Button okButton = (Button) checkBoxView.findViewById(R.id.okButton);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mapViewFragment.displaySelectedMarkers();
                }
            });
//            Button cancelButton = (Button) checkBoxView.findViewById(R.id.cancelButton);
//            cancelButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog.dismiss();
//                }
//            });
            dialog = builder.create();
        }
        dialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View tabView = inflater.inflate(R.layout.fragment_tab, container, false);

        TabLayout tabLayout = (TabLayout) tabView.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Map"));
        tabLayout.addTab(tabLayout.newTab().setText("List"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) tabView.findViewById(R.id.pager);
        final TabPagerAdapter adapter = new TabPagerAdapter(getFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.clearOnTabSelectedListeners();
        viewPager.setCurrentItem(tabPosition);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("tab_position", tab.getPosition());
                editor.apply();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return tabView;
    }

}
