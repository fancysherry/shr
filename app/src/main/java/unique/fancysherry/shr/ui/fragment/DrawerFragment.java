package unique.fancysherry.shr.ui.fragment;

import java.util.List;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.Group;
import unique.fancysherry.shr.io.model.Notify;
import unique.fancysherry.shr.io.model.NotifyList;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.activity.UserActivity;
import unique.fancysherry.shr.ui.adapter.recycleview.DrawItemAdapter;
import unique.fancysherry.shr.ui.otto.BusProvider;
import unique.fancysherry.shr.ui.otto.DataChangeAction;
import unique.fancysherry.shr.ui.widget.BadgeView;
import unique.fancysherry.shr.util.LogUtil;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.otto.Subscribe;


/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a
 * href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class DrawerFragment extends BaseFragment {
  /**
   * A pointer to the current callbacks instance (the Activity).
   */
  private NavigationDrawerCallbacks mCallbacks;

  @InjectView(R.id.drawer_header_name)
  TextView nickname;
  @InjectView(R.id.drawer_header_gratitude)
  TextView gratitude_num;
  @InjectView(R.id.drawer_header_portrait)
  SimpleDraweeView portrait;
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
  private List<Notify> notifyList;
  private Runnable runnable_get_invite_list;
  private int notify_invite_size = 0;
  private BadgeView mBadgeView_notify_invite;

  public DrawerFragment() {}

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    BusProvider.getInstance().register(this);
    getUserData();

    handler = new Handler();
    runnable = new Runnable() {
      @Override
      public void run() {
        nickname.setText(user.nickname);
        gratitude_num.setText("感谢数  " + user.gratitude_shares_sum);
        portrait.setImageURI(Uri.parse(APIConstants.BASE_URL + user.avatar));
        refreshData(user.groups);
      }
    };
    runnable_get_invite_list = new Runnable() {
      @Override
      public void run() {
        if (notifyList != null) {
          for (int i = 0; i < notifyList.size(); i++) {
            if (notifyList.get(i).notify_type.equals(Notify.INVITE))
              notify_invite_size++;
          }
        }
        mBadgeView_notify_invite.setBadgeCount(notify_invite_size);
        LogUtil.e("notify_invite_size  " + notify_invite_size);
        notify_invite_size = 0;

      }
    };
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

    mBadgeView_notify_invite = new BadgeView(getActivity());
    mBadgeView_notify_invite.setTargetView(drawer_message_title);
    mBadgeView_notify_invite.setBadgeMargin(0, 0, 8, 0);
    mBadgeView_notify_invite.setBadgeGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT | Gravity.END);
    mBadgeView_notify_invite.setBadgeCount(0);

    BadgeView mBadgeView_at = new BadgeView(getActivity());
    mBadgeView_at.setTargetView(drawer_at_title);
    mBadgeView_at.setBadgeMargin(0, 0, 8, 0);
    mBadgeView_at.setBadgeGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT | Gravity.END);
    mBadgeView_at.setBadgeCount(0);

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
    initAdapter();
    return view;
  }

  public void initAdapter() {
    mDrawerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
        LinearLayoutManager.VERTICAL, false));
    drawItemAdapter = new DrawItemAdapter(getActivity());
    mDrawerRecyclerView.setAdapter(drawItemAdapter);
    drawItemAdapter.setOnItemClickListener(new DrawItemAdapter.OnRecyclerViewItemClickListener() {
      @Override
      public void onItemClick(View view, Group group) {
        if (group.name != null)
          selectItem(group.name);
        else {
          if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
          }
          if (mCallbacks != null) {
            mCallbacks.onAddGroupListener();
          }
        }
      }
    });

  }

  // 重新获取数据
  public void refresh() {
    getUserData();
  }

  // 更新适配器数据
  public void refreshData(List<Group> groups) {
    drawItemAdapter.setData(groups, null);
  }

  private String getListTitle(int position) {
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

  public void get_invite_list() {
    GsonRequest<NotifyList> group_share_request =
        new GsonRequest<>(Request.Method.GET,
            APIConstants.BASE_URL + "/user/notify",
            getHeader(), null,
            NotifyList.class,
            new Response.Listener<NotifyList>() {
              @Override
              public void onResponse(NotifyList pNotifyList) {
                notifyList = pNotifyList.notifies;
                handler.post(runnable_get_invite_list);
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(group_share_request);
  }

  /**
   * Callbacks interface that all activities using this fragment must implement.
   */
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

  // public void onSaveInstanceState(Bundle outState) {
  public static interface NavigationDrawerCallbacks {
    /**
     * Called when an item in the navigation drawer is selected.
     */
    void onNavigationDrawerItemSelected(String group_name);

    void onAddGroupListener();

    void onGetAllGroup(List<String> group_name_list);
  }

  // 这个注解一定要有,表示订阅了TestAction,并且方法的用 public 修饰的.方法名可以随意取,重点是参数,它是根据你的参数进行判断
  @Subscribe
  public void onChangeData(DataChangeAction dataChangeAction) {
    // 这里更新视图或者后台操作,从TestAction获取传递参数.
    if (dataChangeAction.getStr().equals(DataChangeAction.CHANGE_AVATAR)
        || dataChangeAction.getStr().equals(DataChangeAction.DELETE_GROUP)) {
      getUserData();
    }
    if (dataChangeAction.getStr().equals(DataChangeAction.MESSAGE_COUNT)) {
      get_invite_list();
    }
  }

  @Override
  public void onDestroy() {
    BusProvider.getInstance().unregister(this);
    super.onDestroy();
  }


}
