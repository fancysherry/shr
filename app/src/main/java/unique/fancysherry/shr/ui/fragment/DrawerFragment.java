package unique.fancysherry.shr.ui.fragment;

import android.content.Intent;
import android.os.Handler;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.Group;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.activity.UserActivity;
import unique.fancysherry.shr.ui.adapter.recycleview.DrawItemAdapter;
import unique.fancysherry.shr.ui.widget.BadgeView;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.SApplication;
import butterknife.InjectView;


/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a
 * href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class DrawerFragment extends Fragment {

  /**
   * A pointer to the current callbacks instance (the Activity).
   */
  private NavigationDrawerCallbacks mCallbacks;

  @InjectView(R.id.drawer_header_name)
  TextView nickname;
  @InjectView(R.id.drawer_header_gratitude)
  TextView gratitude_num;
  @InjectView(R.id.drawer_header_portrait)
  CircleImageView portrait;
  @InjectView(R.id.drawer_add_group)
  TextView add_group_button;
  @InjectView(R.id.drawer_list)
  RecyclerView mDrawerRecyclerView;
  @InjectView(R.id.drawer_message_title)
  TextView drawer_message_title;
  @InjectView(R.id.drawer_at_title)
  TextView drawer_at_title;

  private DrawerLayout mDrawerLayout;
  private View mFragmentContainerView;
  private DrawItemAdapter drawItemAdapter;

  private User user;
  private Handler handler;
  private Runnable runnable;

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
      }
    };


    // // Read in the flag indicating whether or not the user has demonstrated awareness of the
    // // drawer. See PREF_USER_LEARNED_DRAWER for details.
    // SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
    // mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
    //
    // if (savedInstanceState != null) {
    // mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
    // item_max_size = savedInstanceState.getInt(ITEM_MAX_SIZE_VALUE);
    // group_name_list = savedInstanceState.getStringArrayList(ITEM_LIST);
    // mFromSavedInstanceState = true;
    // }


  }



  public void getUserData() {
    GsonRequest<User> group_share_request =
        new GsonRequest<>(Request.Method.GET,
            APIConstants.BASE_URL + "/homepage",
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
    ButterKnife.inject(this, view);
    drawer_message_title.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        selectItem("notification");
      }
    });

    drawer_at_title.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        selectItem("at_me");
      }
    });

    BadgeView mBadgeView_message = new BadgeView(getActivity());
    mBadgeView_message.setTargetView(drawer_message_title);
    mBadgeView_message.setBadgeMargin(0, 0, 8, 0);
    mBadgeView_message.setBadgeGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT | Gravity.END);
    mBadgeView_message.setBadgeCount(12);

    BadgeView mBadgeView_at = new BadgeView(getActivity());
    mBadgeView_at.setTargetView(drawer_at_title);
    mBadgeView_at.setBadgeMargin(0, 0, 8, 0);
    mBadgeView_at.setBadgeGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT | Gravity.END);
    mBadgeView_at.setBadgeCount(23);


    portrait.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent mIntent = new Intent(getActivity(), UserActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putParcelable("user", user);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
        mDrawerLayout.closeDrawers();
      }
    });

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
    initAdapter();
    return view;
  }

  public void initAdapter()
  {
    mDrawerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
        LinearLayoutManager.VERTICAL, false));
    drawItemAdapter = new DrawItemAdapter(getActivity());
    mDrawerRecyclerView.setAdapter(drawItemAdapter);
    drawItemAdapter.setOnItemClickListener(new DrawItemAdapter.OnRecyclerViewItemClickListener() {
      @Override
      public void onItemClick(View view, Group group) {
        selectItem(group.name);
      }
    });

  }

  // 重新获取数据
  public void refresh()
  {
    getUserData();
  }

  // 更新适配器数据
  public void refreshData(List<Group> groups)
  {
    drawItemAdapter.setData(groups, null);
  }


  private String getListTitle(int position)
  {
    // if (position == 0)
    // return getString(R.string.fragment_notification);
    // else if (2 <= position && position < item_max_size)
    // return group_name_list.get(position - 2);
    // else if (position == 1)
    // return getString(R.string.fragment_share_content);
    // else
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

  private void selectItem(String group_name) {
    if (mDrawerLayout != null) {
      mDrawerLayout.closeDrawer(mFragmentContainerView);
    }
    if (mCallbacks != null) {
      mCallbacks.onNavigationDrawerItemSelected(group_name);
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

  // @Override
  // public void onSaveInstanceState(Bundle outState) {
  // super.onSaveInstanceState(outState);
  // outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
  // outState.putInt(ITEM_MAX_SIZE_VALUE, item_max_size);
  // outState.putStringArrayList(ITEM_LIST, group_name_list);
  // }

  /**
   * Callbacks interface that all activities using this fragment must implement.
   */
  public static interface NavigationDrawerCallbacks {
    /**
     * Called when an item in the navigation drawer is selected.
     */
    void onNavigationDrawerItemSelected(String group_name);

    void onAddGroupListener();

    void onGetAllGroup(List<String> group_name_list);
  }


  public void executeRequest(Request request) {
    SApplication.getRequestManager().executeRequest(request, this);
  }
}
