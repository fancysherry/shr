package unique.fancysherry.shr.ui.fragment;

import android.content.Intent;
import android.os.Handler;
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
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.io.model.Group;
import unique.fancysherry.shr.io.model.ShareList;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.activity.UserActivity;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.SApplication;

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

  private static final String ITEM_MAX_SIZE_VALUE = "ITEM_MAX_SIZE_VALUE";
  private static final String ITEM_LIST = "ITEM_LIST";

  private ArrayList<String> group_name_list = new ArrayList<String>();



  private DrawerLayout mDrawerLayout;
  private ListView mDrawerListView;
  private View mFragmentContainerView;
  private SimpleAdapter simpleAdapter;
  private TextView add_group_button;
  private CircleImageView portrait;
  private TextView nickname;
  private TextView gratitude_num;


  private int mCurrentSelectedPosition = 0;
  private boolean mFromSavedInstanceState;
  private boolean mUserLearnedDrawer;

  private User user;
  private Handler handler;
  private Runnable runnable;


  private int item_max_size = 2;
  private List<Map<String, Object>> mData;

  public DrawerFragment() {}

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getUserData();

    handler = new Handler();
    runnable = new Runnable() {
      @Override
      public void run() {
        nickname.setText(user.nickname);
        gratitude_num.setText("感谢数  " + user.gratitude_shares_sum);
        refreshData(user.groups);
        mCallbacks.onGetAllGroup(group_name_list);
      }
    };


    // Read in the flag indicating whether or not the user has demonstrated awareness of the
    // drawer. See PREF_USER_LEARNED_DRAWER for details.
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
    mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

    if (savedInstanceState != null) {
      mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
      item_max_size = savedInstanceState.getInt(ITEM_MAX_SIZE_VALUE);
      group_name_list = savedInstanceState.getStringArrayList(ITEM_LIST);
      mFromSavedInstanceState = true;
    }

    // Select either the default item (0) or the last selected item.
    selectItem(mCurrentSelectedPosition);
  }



  public void getUserData() {
    GsonRequest<User> group_share_request =
        new GsonRequest<>(Request.Method.GET,
            "http://104.236.46.64:8888/homepage",
            getHeader(), null,
            User.class,
            new Response.Listener<User>() {
              @Override
              public void onResponse(User pUser) {
                user = pUser;
                handler.post(runnable);

              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(group_share_request);
  }

  public Map<String, String> getHeader() {
    Map<String, String> headers = new HashMap<String, String>();
    UserBean currentUser = AccountManager.getInstance().getCurrentUser();
    if (currentUser != null && currentUser.getCookieHolder() != null) {
      currentUser.getCookieHolder().generateCookieString();
      headers.put("Cookie", currentUser.getCookieHolder().generateCookieString());
    }

    headers
        .put(
            "User-Agent",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36");
    return headers;
  }



  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(
        R.layout.navigation_drawer, container, false);

    nickname = (TextView) view.findViewById(R.id.drawer_header_name);
    gratitude_num = (TextView) view.findViewById(R.id.drawer_header_gratitude);

    portrait = (CircleImageView) view.findViewById(R.id.drawer_header_portrait);
    portrait.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent mIntent = new Intent(getActivity(), UserActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putParcelable("user", user);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
      }
    });

    add_group_button = (TextView) view.findViewById(R.id.drawer_add_group);
    add_group_button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mDrawerLayout != null) {
          mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
          mCallbacks.onAddGroupListener();
        }
      }
    });

    mDrawerListView = (ListView) view.findViewById(R.id.drawer_list);
    mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
      }
    });
    simpleAdapter = new SimpleAdapter(
        getActivity(),
        getDatas(),
        R.layout.drawer_list_item,
        new String[] {"image", "title", "text"},
        new int[] {R.id.drawer_item_icon, R.id.drawer_item_title}
        );
    mDrawerListView.setAdapter(simpleAdapter);
    mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

    return view;
  }

  private List<Map<String, Object>> getDatas()
  {
    mData = new ArrayList<Map<String, Object>>();;
    for (int i = 0; i < item_max_size; i++) {
      Map<String, Object> item = new HashMap<String, Object>();
      item.put("image", R.mipmap.ic_launcher);
      item.put("title", getListTitle(i));
      mData.add(item);
    }

    return mData;
  }

  public void refreshData(String new_group_name)
  {
    // item_max_size++;
    // group_name_list.add(new_group_name);
    //
    // Map<String, Object> item = new HashMap<String, Object>();
    // item.put("image", R.mipmap.ic_launcher);
    // item.put("title", new_group_name);
    // mData.add(item);
    //
    //
    // simpleAdapter.notifyDataSetChanged();
    // mDrawerListView.setAdapter(simpleAdapter);
    // LogUtil.e("list_size   " + String.valueOf(group_name_list.size()));
  }

  public void refreshData(List<Group> groups)
  {
    item_max_size = +groups.size();
    for (int i = 0; i < groups.size(); i++)
      group_name_list.add(groups.get(i).name);


//    LogUtil.e("group_name_list_size   +++++++++"+String.valueOf(group_name_list.size()));
//    for (int i = 0; i < group_name_list.size(); i++)
//    LogUtil.e("aaa" +group_name_list.get(i));

    for (int i = 2; i < item_max_size; i++) {
      Map<String, Object> item = new HashMap<String, Object>();
      item.put("image", R.mipmap.ic_launcher);
      item.put("title", getListTitle(i));
      mData.add(item);
    }
    simpleAdapter.notifyDataSetChanged();

  }


  private String getListTitle(int position)
  {
    if (position == 0)
      return getString(R.string.fragment_notification);
    else if (2 <= position && position < item_max_size)
      return group_name_list.get(position - 2);
    else if (position == 1)
      return getString(R.string.fragment_unique);
    else
      return null;
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
    outState.putInt(ITEM_MAX_SIZE_VALUE, item_max_size);
    outState.putStringArrayList(ITEM_LIST, group_name_list);
  }

  /**
   * Callbacks interface that all activities using this fragment must implement.
   */
  public static interface NavigationDrawerCallbacks {
    /**
     * Called when an item in the navigation drawer is selected.
     */
    void onNavigationDrawerItemSelected(int position);

    void onAddGroupListener();

    void onGetAllGroup(List<String> group_name_list);
  }


  public void executeRequest(Request request) {
    SApplication.getRequestManager().executeRequest(request, this);
  }
}
