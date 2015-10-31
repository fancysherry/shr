package unique.fancysherry.shr.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.fragment.DrawerFragment;
import unique.fancysherry.shr.ui.fragment.NewGroupFragment;
import unique.fancysherry.shr.ui.fragment.NotificationFragment;
import unique.fancysherry.shr.ui.fragment.ShareContentFragment;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.SApplication;


public class MainActivity extends BaseActivity
    implements DrawerFragment.NavigationDrawerCallbacks,
    NewGroupFragment.OnNewGroupListener, ShareContentFragment.OnGetGroupIdListener {

  public FragmentManager fragmentManager;

  /**
   * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
   */
  private DrawerFragment mDrawerFragment;

  private Context context;

  private String now_group_id;

  private String now_group_name;

  private User user;
  private Handler handler;
  private Runnable runnable;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = this;
    setContentView(R.layout.activity_main);
    initView();
    initializeToolbar();
    getSupportActionBar().setTitle("消息");
    mToolbar.setOnMenuItemClickListener(onMenuItemClick);


    // 默认加载第一个组的信息
    handler = new Handler();
    runnable = new Runnable() {
      @Override
      public void run() {
        fragmentManager
            .beginTransaction()
            .replace(R.id.container,
                ShareContentFragment.newInstance(user.groups.get(0).name))
            .commit();
      }
    };
  }

    @Override
    protected void onRestart(){
        super.onRestart();
        getUserData();
    }

  private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
      switch (menuItem.getItemId()) {
        case R.id.action_edit:
          Intent mIntent = new Intent(context, GroupActivity.class);
          Bundle mBundle = new Bundle();
          mBundle.putString("group_id", now_group_id);
          mBundle.putString("group_name", now_group_name);
          mIntent.putExtras(mBundle);
          startActivity(mIntent);
          break;

        case R.id.action_settings:
          break;
      }
      return true;
    }
  };


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }



  @Override
  protected void initView() {
    mDrawerFragment = (DrawerFragment)
        getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

    mDrawerFragment.setUp(
        R.id.navigation_drawer,
        (DrawerLayout) findViewById(R.id.drawerLayout));

    fragmentManager = getSupportFragmentManager();
    getUserData();
  }



  @Override
  public void onNavigationDrawerItemSelected(String group_name) {
    // update the main content by replacing fragments
    if (fragmentManager == null)
      fragmentManager = getSupportFragmentManager();

    if (group_name.equals("notification")) {
      fragmentManager.beginTransaction()
          .replace(R.id.container, new NotificationFragment())
          .commit();
    }
    else if (group_name.equals("at_me")) {
      fragmentManager
          .beginTransaction()
          .replace(R.id.container,
              ShareContentFragment.newInstance("at_me"))
          .commit();
    }
    else {
      fragmentManager
          .beginTransaction()
          .replace(R.id.container,
              ShareContentFragment.newInstance(group_name))
          .commit();
    }

  }

  @Override
  public void onAddGroupListener() {
    if (fragmentManager == null)
      fragmentManager = getSupportFragmentManager();
    fragmentManager
        .beginTransaction()
        .replace(R.id.container,
            new NewGroupFragment())
        .commit();

  }

  @Override
  public void onGetAllGroup(List<String> group_name_list) {
    // this.group_name_list = group_name_list;
  }


  @Override
  public void OnNewGroupFinish(String group_name) {
    mDrawerFragment.refresh();
    fragmentManager
        .beginTransaction()
        .replace(R.id.container,
            ShareContentFragment.newInstance(group_name))
        .commit();

  }

  @Override
  public void OnGetGroupId(String id) {
    now_group_id = id;
  }


  @Override
  public void OnGetGroupName(String name) {
    now_group_name = name;
    getSupportActionBar().setTitle(name);
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


  public void executeRequest(Request request) {
    SApplication.getRequestManager().executeRequest(request, this);
  }
}
