package unique.fancysherry.shr.ui.fragment;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unique.fancysherry.shr.R;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a
 * href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class DrawerFragment extends Fragment {

  /**
   * Remember the position of the selected item.
   */
  private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

  /**
   * Per the design guidelines, you should show the drawer on launch until the user manually
   * expands it. This shared preference tracks this.
   */
  private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

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

  public DrawerFragment() {}

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Read in the flag indicating whether or not the user has demonstrated awareness of the
    // drawer. See PREF_USER_LEARNED_DRAWER for details.
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
    mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

    if (savedInstanceState != null) {
      mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
      mFromSavedInstanceState = true;
    }

    // Select either the default item (0) or the last selected item.
    selectItem(mCurrentSelectedPosition);
  }



  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(
        R.layout.navigation_drawer, container, false);

    mDrawerListView = (ListView) view.findViewById(R.id.drawer_list);
    mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
      }
    });
    mDrawerListView.setAdapter(new SimpleAdapter(
        getActivity(),
        getDatas(),
        R.layout.drawer_list_item,
        new String[] {"image", "title", "text"},
        new int[] {R.id.drawer_item_icon, R.id.drawer_item_title}
        ));
    mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

    return view;
  }


  private List<Map<String, Object>> getDatas()
  {
    List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();;
    for (int i = 0; i < 3; i++) {
      Map<String, Object> item = new HashMap<String, Object>();
      item.put("image", R.mipmap.ic_launcher);
      item.put("title", getListTitle(i));
      mData.add(item);
    }

    return mData;
  }

  private String getListTitle(int position)
  {
    if (position == 0)
      return getString(R.string.fragment_notification);
    else if (position == 1)
      return getString(getGroup());
    else if (position == 2)
      return getString(R.string.fragment_unique);
    else
      return null;
  }

  private int getGroup()
  {
    return R.string.fragment_group_android;
  }

  /**
   * Users of this fragment must call this method to set up the navigation drawer interactions.
   *
   * @param fragmentId The android:id of this fragment in its activity's layout.
   * @param drawerLayout The DrawerLayout containing this fragment's UI.
   */
  public void setUp(int fragmentId, DrawerLayout drawerLayout) {
    mFragmentContainerView = getActivity().findViewById(fragmentId);
    mDrawerLayout = drawerLayout;

    // set a custom shadow that overlays the main content when the drawer opens
    mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    mDrawerLayout.setStatusBarBackground(R.color.drawer_header_background);
    // set up the drawer's list view with items and click listener

  }

  private void selectItem(int position) {
    mCurrentSelectedPosition = position;
    if (mDrawerListView != null) {
      mDrawerListView.setItemChecked(position, true);
    }
    if (mDrawerLayout != null) {
      mDrawerLayout.closeDrawer(mFragmentContainerView);
    }
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

  /**
   * Callbacks interface that all activities using this fragment must implement.
   */
  public static interface NavigationDrawerCallbacks {
    /**
     * Called when an item in the navigation drawer is selected.
     */
    void onNavigationDrawerItemSelected(int position);
  }
}
