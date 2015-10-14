package unique.fancysherry.shr.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.model.Group;
import unique.fancysherry.shr.ui.fragment.DrawerFragment;
import unique.fancysherry.shr.ui.fragment.GroupFragment;
import unique.fancysherry.shr.ui.fragment.NewGroupFragment;
import unique.fancysherry.shr.ui.fragment.NotificationFragment;
import unique.fancysherry.shr.ui.fragment.ShareContentFragment;
import unique.fancysherry.shr.util.LogUtil;


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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = this;
    setContentView(R.layout.activity_main);
    initView();
    initializeToolbar();
    getSupportActionBar().setTitle("消息");
    mToolbar.setOnMenuItemClickListener(onMenuItemClick);
  }

  private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
      switch (menuItem.getItemId()) {
        case R.id.action_edit:
          Intent mIntent = new Intent(context, GroupActivity.class);
          mIntent.putExtra("group_id", now_group_id);
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
  }



  @Override
  public void onNavigationDrawerItemSelected(String group_name) {
    // update the main content by replacing fragments
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
}
