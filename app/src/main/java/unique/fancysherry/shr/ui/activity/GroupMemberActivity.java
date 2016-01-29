package unique.fancysherry.shr.ui.activity;

import java.util.List;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.model.Group;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.ui.adapter.recycleview.MemberAdapter;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GroupMemberActivity extends BaseActivity {
  @InjectView(R.id.group_member_activity_group_member_list)
  RecyclerView group_member_list;
  @InjectView(R.id.group_member_activity_toolbar)
  Toolbar mToolbar;
  private MemberAdapter memberAdapter;
  public List<User> members;
  public Group group;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_group_member);
    ButterKnife.inject(this);
    group = getIntent().getExtras().getParcelable("group");
    members = group.users;
    initializeToolbar(mToolbar);
    initAdapter();
  }

  private void initAdapter() {
    group_member_list.setLayoutManager(new LinearLayoutManager(this,
        LinearLayoutManager.VERTICAL, false));
    memberAdapter = new MemberAdapter(this);
    group_member_list.setAdapter(memberAdapter);
    memberAdapter.setData(members);
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
