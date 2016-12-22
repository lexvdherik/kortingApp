package hva.flashdiscount.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import hva.flashdiscount.R;
import hva.flashdiscount.layout.RoundNetworkImageView;
import hva.flashdiscount.model.User;
import hva.flashdiscount.utils.LoginSingleton;
import hva.flashdiscount.utils.VolleySingleton;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    private static final String TAG = "MD/NavDrawerFrag";

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    // The title which was previously held in the Toolbar
    private CharSequence mPrevTitle = null;
    private boolean mSelectedPosChanged = false;
    private View.OnClickListener mOriginalListener;
    private LoginSingleton loginSingleton;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginSingleton = LoginSingleton.getInstance(getActivity());

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());


        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        // Set up the drawer's data first
        mDrawerListView.setAdapter(new DrawerAdapter(getActivity()));
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
//        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                toolbar,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                // Reset the title on the toolbar, if applicable
                if (!mSelectedPosChanged && mPrevTitle != null) {
                    actionBar.setTitle(mPrevTitle);
                    mPrevTitle = null;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                Log.e(TAG, "ondraweropennn");
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }
                Log.e(TAG, "dfsdfds = " + String.valueOf(loginSingleton.loggedIn()));
                if (loginSingleton.loggedIn() && loginSingleton.loginExpired()) {
                    Log.w(TAG, "login expired");
                    User user = loginSingleton.silentLogin();
                    if (user != null) {
                        LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.nav_header);

                        ImageLoader mImageLoader = VolleySingleton.getInstance(getActivity()).getImageLoader();
                        RoundNetworkImageView image = (RoundNetworkImageView) layout.findViewById(R.id.profile_picture);
                        if (image != null) {
                            image.setImageUrl(user.getPicture().toString(), mImageLoader);
                        }

                        ((TextView) layout.findViewById(R.id.naam)).setText(user.getName());
                        ((TextView) layout.findViewById(R.id.email)).setText(user.getEmail());
                    }
                } else if (loginSingleton.loggedIn() && !loginSingleton.loginExpired()) {
                    Log.w(TAG, "load everything from sharedpref");
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.nav_header);

                    ImageLoader mImageLoader = VolleySingleton.getInstance(getActivity()).getImageLoader();
                    ((RoundNetworkImageView) layout.findViewById(R.id.profile_picture)).setImageUrl(sharedPref.getString("picture", ""), mImageLoader);
                    ((TextView) layout.findViewById(R.id.naam)).setText(sharedPref.getString("name", ""));
                    ((TextView) layout.findViewById(R.id.email)).setText(sharedPref.getString("email", ""));

                }


                // Save the title in the action bar
//                mPrevTitle = actionBar.getTitle();
                // Now make the actionBar use the "context's" title, as per guidelines
//                actionBar.setTitle(R.string.app_name);
                // Reset the position change status
                mSelectedPosChanged = false;



                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
                               @Override
                               public void run() {
                                   mDrawerToggle.syncState();
                               }
                           }

        );

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mOriginalListener = mDrawerToggle.getToolbarNavigationClickListener();
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(mFragmentContainerView);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
    }

    public void syncState() {
        mDrawerToggle.syncState();
    }

    // Tells the toolbar+drawer to switch to the up button or switch back to the normal drawer
    public void toggleDrawerUse(boolean useDrawer, View.OnClickListener listener) {
        // Enable/Disable the icon being used by the drawer
        mDrawerToggle.setDrawerIndicatorEnabled(useDrawer);

        // Either use the original drawer/home button listener, or use the provided one as requested
        if (useDrawer) {
            mDrawerToggle.setToolbarNavigationClickListener(mOriginalListener);
        } else {
            mDrawerToggle.setToolbarNavigationClickListener(listener);
        }
        // TODO: Enable/Disable the drawer even being able to open/close
    }

    // Sets the position only in the drawer
    public void setSelectedItem(int position) {
        mCurrentSelectedPosition = position;
        mSelectedPosChanged = true;

        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    private void selectItem(int position) {
        setSelectedItem(position);
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    class DrawerAdapter extends BaseAdapter {
        Context mContext;
        String[] titles;

        DrawerAdapter(Context context) {
            mContext = context;

            titles = mContext.getResources().getStringArray(R.array.drawer_items);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = null;
            ViewHolder holder;

            if (position == 0) {
                LayoutInflater inflater =
                        (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.nav_header, parent, false);
                TextView tx = (TextView) row.findViewById(R.id.naam);
                tx.setText("tony is gay");
                //holder = new ViewHolder();
                // holder.text = (TextView) row.findViewById(R.id.naam);

                return row;
            } else {


                if (convertView == null) {
                    // Then gotta set up this row for the first time
                    LayoutInflater inflater =
                            (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row = inflater.inflate(R.layout.list_nav_item, parent, false);

                    // Create a ViewHolder to save all the different parts of the row
                    holder = new ViewHolder();
                    holder.text = (TextView) row.findViewById(R.id.text);

                    // Make the row reuse the ViewHolder
                    row.setTag(holder);
                } else { // Otherwise, use the recycled view
                    row = convertView;
                    holder = (ViewHolder) row.getTag();
                }

                // Set this view's content
                holder.text.setText(titles[position]);

                return row;
            }
        }

        class ViewHolder {
            public TextView text;
        }
    }
}
