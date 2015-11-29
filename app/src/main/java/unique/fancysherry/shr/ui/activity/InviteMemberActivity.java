package unique.fancysherry.shr.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.WindowManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import unique.fancysherry.shr.R;
import unique.fancysherry.shr.ui.adapter.recycleview.InvitedMemberAdapter;

public class InviteMemberActivity extends AppCompatActivity {
  @InjectView(R.id.group_invited_list)
  RecyclerView group_invited_list;

  private InvitedMemberAdapter invitedMemberAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_invite_member);
    ButterKnife.inject(this);
    initAdapter();
    initializeToolbar();
  }

  private void initAdapter()
  {
    invitedMemberAdapter = new InvitedMemberAdapter(this);
    group_invited_list.setLayoutManager(new LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false));
    group_invited_list.setAdapter(invitedMemberAdapter);
  }

  // Resolve the given attribute of the current theme
  private int getAttributeColor(int resId) {
    TypedValue typedValue = new TypedValue();
    getTheme().resolveAttribute(resId, typedValue, true);
    int color = 0x000000;
    if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
      // resId is a color
      color = typedValue.data;
    } else {
      // resId is not a color
    }
    return color;
  }

  protected void initializeToolbar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      getWindow().setStatusBarColor(getAttributeColor(R.attr.colorPrimaryDark));
    }
    Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(mToolbar);
    getSupportActionBar().setTitle("邀请成员");
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);

  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == android.R.id.home) {
      finish();
    }
    return super.onOptionsItemSelected(item);
  }

}
