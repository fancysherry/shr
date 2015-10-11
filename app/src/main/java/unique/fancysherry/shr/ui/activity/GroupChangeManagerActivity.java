package unique.fancysherry.shr.ui.activity;

import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import unique.fancysherry.shr.R;
import unique.fancysherry.shr.ui.adapter.recycleview.GroupManageAdapter;

public class GroupChangeManagerActivity extends AppCompatActivity {

  @InjectView(R.id.group_manage_list)
  RecyclerView group_manage_list;
  private String group_id;

  private GroupManageAdapter groupManageAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_group_change_manager);
    ButterKnife.inject(this);
    initAdapter();
    Bundle mBundle = getIntent().getExtras();
    group_id = mBundle.getString("group_id");
    initializeToolbar();

  }

  private void initAdapter()
  {
    groupManageAdapter = new GroupManageAdapter(this);
    group_manage_list.setLayoutManager(new LinearLayoutManager(this,
        LinearLayoutManager.VERTICAL, false));
    group_manage_list.setAdapter(groupManageAdapter);
  }

  protected void initializeToolbar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
    }
    Toolbar mToolbar = (Toolbar) findViewById(R.id.group_manage_activity_toolbar);
    mToolbar.setTitle("");
    setSupportActionBar(mToolbar);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);

  }


  private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
      switch (menuItem.getItemId()) {
        case android.R.id.home:
          finish();
      }
      return true;
    }
  };
}
