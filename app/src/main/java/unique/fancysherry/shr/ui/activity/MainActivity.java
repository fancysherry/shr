package unique.fancysherry.shr.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.ui.fragment.DrawerFragment;
import unique.fancysherry.shr.ui.fragment.GroupFragment;
import unique.fancysherry.shr.ui.fragment.NotificationFragment;
import unique.fancysherry.shr.ui.fragment.UniqueFragment;


public class MainActivity extends BaseActivity
    implements DrawerFragment.NavigationDrawerCallbacks {

  /**
   * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
   */
  private DrawerFragment mDrawerFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    // DrawerLayout child = (DrawerLayout) inflater.inflate(R.layout.activity_main, null);
    // ((ViewGroup) child.getParent()).removeView(child);
    setContentView(R.layout.activity_main);
    initView();
    initializeToolbar();
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
    FragmentManager fragmentManager = getSupportFragmentManager();
    switch (position) {
      case 0:
        fragmentManager.beginTransaction()
            .replace(R.id.container, new NotificationFragment())
            .commit();
        break;
      case 1:
        fragmentManager.beginTransaction()
            .replace(R.id.container, new GroupFragment())
            .commit();
        break;
      case 2:
        fragmentManager.beginTransaction()
            .replace(R.id.container, new UniqueFragment())
            .commit();
        break;


    }
  }



}
