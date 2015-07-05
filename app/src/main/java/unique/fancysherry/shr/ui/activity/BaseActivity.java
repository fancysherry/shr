/*
 * Copyright (C) 2014 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package unique.fancysherry.shr.ui.activity;

import android.app.ActivityOptions;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.system.ResourceHelper;

/**
 * Abstract activity with toolbar, navigation drawer and cast support. Needs to be extended by
 * any activity that wants to be shown as a top level activity.
 *
 * The requirements for a subclass is to call {@link #initializeToolbar()} on onCreate, after
 * setContentView() is called and have three mandatory layout elements:
 * a {@link Toolbar} with id 'toolbar',
 * a {@link DrawerLayout} with id 'drawerLayout' and
 * a {@link ListView} with id 'drawerList'.
 * 在抽象类中抽离出了toolbar和drawertoggle的逻辑
 */
public abstract class BaseActivity extends ActionBarActivity {



  private Toolbar mToolbar;
  private ActionBarDrawerToggle mDrawerToggle;
  private DrawerLayout mDrawerLayout;
  private ListView mDrawerList;

  private boolean mToolbarInitialized;

  private int mItemToOpenWhenDrawerCloses = -1;



  private DrawerLayout.DrawerListener mDrawerListener = new DrawerLayout.DrawerListener() {
    @Override
    public void onDrawerClosed(View drawerView) {
      if (mDrawerToggle != null) mDrawerToggle.onDrawerClosed(drawerView);
      int position = mItemToOpenWhenDrawerCloses;
      if (position >= 0) {
        Bundle extras = ActivityOptions.makeCustomAnimation(
            BaseActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();

      }
    }

    @Override
    public void onDrawerStateChanged(int newState) {
      if (mDrawerToggle != null) mDrawerToggle.onDrawerStateChanged(newState);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
      if (mDrawerToggle != null) mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
    }

    @Override
    public void onDrawerOpened(View drawerView) {
      if (mDrawerToggle != null) mDrawerToggle.onDrawerOpened(drawerView);
      getSupportActionBar().setTitle(R.string.app_name);
    }
  };

  private FragmentManager.OnBackStackChangedListener mBackStackChangedListener =
      new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
          updateDrawerToggle();
        }
      };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LogUtil.d("Activity onCreate");

  }

  @Override
  protected void onStart() {
    super.onStart();
    if (!mToolbarInitialized) {
      throw new IllegalStateException("You must run super.initializeToolbar at " +
          "the end of your onCreate method");
    }
  }


  // 使默认的图标变为三条杆
  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    if (mDrawerToggle != null) {
      mDrawerToggle.syncState();
    }
  }

  @Override
  public void onResume() {
    super.onResume();

    // Whenever the fragment back stack changes, we may need to update the
    // action bar toggle: only top level screens show the hamburger-like icon, inner
    // screens - either Activities or fragments - show the "Up" icon instead.
    getFragmentManager().addOnBackStackChangedListener(mBackStackChangedListener);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (mDrawerToggle != null) {
      mDrawerToggle.onConfigurationChanged(newConfig);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    getFragmentManager().removeOnBackStackChangedListener(mBackStackChangedListener);
  }


  @Override
  public void onBackPressed() {
    // If the drawer is open, back will close it
    if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
      mDrawerLayout.closeDrawers();
      return;
    }
    // Otherwise, it may return to the previous fragment stack
    FragmentManager fragmentManager = getFragmentManager();
    if (fragmentManager.getBackStackEntryCount() > 0) {
      fragmentManager.popBackStack();
    } else {
      // Lastly, it will rely on the system behavior for back
      super.onBackPressed();
    }
  }

  @Override
  public void setTitle(CharSequence title) {
    super.setTitle(title);
    mToolbar.setTitle(title);
  }

  protected abstract void initView();

  protected void initializeToolbar() {
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    if (mToolbar == null) {
      throw new IllegalStateException("Layout is required to include a Toolbar with id " +
          "'toolbar'");
    }

    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
    if (mDrawerLayout != null) {
      mDrawerList = (ListView) findViewById(R.id.drawer_list);
      if (mDrawerList == null) {
        throw new IllegalStateException("A layout with a drawerLayout is required to" +
            "include a ListView with id 'drawerList'");
      }

      // Create an ActionBarDrawerToggle that will handle opening/closing of the drawer:
      mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
          mToolbar, R.string.open_content_drawer, R.string.close_content_drawer);
      mDrawerLayout.setDrawerListener(mDrawerListener);
      mDrawerLayout.setStatusBarBackgroundColor(
          ResourceHelper.getThemeColor(this, R.attr.colorPrimary, android.R.color.black));
      setSupportActionBar(mToolbar);
      updateDrawerToggle();
    } else {
      setSupportActionBar(mToolbar);
    }

    mToolbarInitialized = true;
  }



  protected void updateDrawerToggle() {
    if (mDrawerToggle == null) {
      return;
    }
    boolean isRoot = getFragmentManager().getBackStackEntryCount() == 0;
    mDrawerToggle.setDrawerIndicatorEnabled(isRoot);
    getSupportActionBar().setDisplayShowHomeEnabled(!isRoot);
    getSupportActionBar().setDisplayHomeAsUpEnabled(!isRoot);
    getSupportActionBar().setHomeButtonEnabled(!isRoot);
    if (isRoot) {
      mDrawerToggle.syncState();
    }
  }

  /**
   * Shows the Cast First Time User experience to the user (an overlay that explains what is
   * the Cast icon)
   */
  // private void showFtu() {
  // Menu menu = mToolbar.getMenu();
  // View view = menu.findItem(R.id.media_route_menu_item).getActionView();
  // if (view != null && view instanceof MediaRouteButton) {
  // new ShowcaseView.Builder(this)
  // .setTarget(new ViewTarget(view))
  // .setContentTitle(R.string.touch_to_cast)
  // .hideOnTouchOutside()
  // .build();
  // }
  // }
}
