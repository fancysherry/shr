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

  private List<String> group_name_list = new ArrayList<String>();
  private static final String ITEM_LIST = "ITEM_LIST";
  public FragmentManager fragmentManager;

  /**
   * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
   */
  private DrawerFragment mDrawerFragment;

  private Context context;

  private String now_group_id;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    // DrawerLayout child = (DrawerLayout) inflater.inflate(R.layout.activity_main, null);
    // ((ViewGroup) child.getParent()).removeView(child);
    context = this;
    setContentView(R.layout.activity_main);
    initView();
    initializeToolbar();
    mToolbar.setOnMenuItemClickListener(onMenuItemClick);

    // if (savedInstanceState != null) {
    // group_name_list = savedInstanceState.getStringArrayList(ITEM_LIST);
    // }

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
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }



  @Override
  protected void initView() {
    mDrawerFragment = (DrawerFragment)
        getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

    // Set up the drawer.
    mDrawerFragment.setUp(
        R.id.navigation_drawer,
        (DrawerLayout) findViewById(R.id.drawerLayout));
  }



  @Override
  public void onNavigationDrawerItemSelected(int position) {
    // update the main content by replacing fragments
    fragmentManager = getSupportFragmentManager();
    switch (position) {
      case 0:
        fragmentManager.beginTransaction()
            .replace(R.id.container, new NotificationFragment())
            .commit();
        break;
      case 1:
        fragmentManager
            .beginTransaction()
            .replace(R.id.container,
                ShareContentFragment.newInstance("unique"))
            .commit();
        break;
      default:
        fragmentManager
            .beginTransaction()
            .replace(R.id.container,
                ShareContentFragment.newInstance(group_name_list.get(position - 2)))
            .commit();
        break;


    }
  }

  @Override
  public void onAddGroupListener() {
    fragmentManager
        .beginTransaction()
        .replace(R.id.container,
            new NewGroupFragment())
        .commit();

  }

  @Override
  public void onGetAllGroup(List<String> group_name_list) {
    this.group_name_list = group_name_list;
  }


  @Override
  public void OnNewGroupFinish(String group_name) {
    mDrawerFragment.refreshData(group_name);

    group_name_list.add(group_name);
    LogUtil.e("group_name--------------------------------------------------------" + group_name);
    fragmentManager
        .beginTransaction()
        .replace(R.id.container,
            ShareContentFragment.newInstance(group_name))
        .commit();

  }

  // @Override
  // public void onSaveInstanceState(Bundle outState) {
  // super.onSaveInstanceState(outState);
  // outState.putStringArrayList(ITEM_LIST, group_name_list);
  // }

  @Override
  public void OnGetGroupId(String id) {
    now_group_id = id;
  }
}
