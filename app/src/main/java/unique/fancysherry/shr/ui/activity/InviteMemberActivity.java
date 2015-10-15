package unique.fancysherry.shr.ui.activity;

import android.os.Build;
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

  protected void initializeToolbar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
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
