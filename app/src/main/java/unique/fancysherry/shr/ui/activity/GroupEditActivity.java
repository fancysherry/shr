package unique.fancysherry.shr.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import unique.fancysherry.shr.R;

public class GroupEditActivity extends BaseActivity {
  @InjectView(R.id.change_group_name_layout_content)
  TextView change_group_name_edittext;
  @InjectView(R.id.change_group_introduce_layout_content)
  EditText change_group_introduce_edittext;
  @InjectView(R.id.manage_group_member_layout)
  RelativeLayout manage_group_member_layout;
  @InjectView(R.id.change_group_manager_layout)
  RelativeLayout change_group_manager_layout;
  @InjectView(R.id.delete_group_btn)
  TextView delete_group_btn;
  @InjectView(R.id.group_edit_toolbar)
  Toolbar mToolbar;

  private Context context;
  private String group_id;
  private String group_name;
  private Bundle complete_bundle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_group_edit);
    ButterKnife.inject(this);
    complete_bundle = getIntent().getExtras();
    group_id = complete_bundle.getString("group_id");
    group_name = complete_bundle.getString("group_name");
    context = this;
    initializeToolbar(mToolbar);
  }

  @OnClick({R.id.manage_group_member_layout, R.id.change_group_manager_layout,
      R.id.delete_group_btn})
  public void click(View mView) {
    switch (mView.getId()) {
      case R.id.manage_group_member_layout:
        Intent mIntent = new Intent(context, GroupMemberDeleteActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("group_id", group_id);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
        break;
      case R.id.change_group_manager_layout:
        Intent mIntent_change = new Intent(context, GroupMemberDeleteActivity.class);
        Bundle mBundle_change = new Bundle();
        mBundle_change.putString("group_id", group_id);
        mIntent_change.putExtras(mBundle_change);
        startActivity(mIntent_change);
        break;
      case R.id.delete_group_btn:
        Intent mIntent_delete = new Intent(context, DeleteGroupActivity.class);
        mIntent_delete.putExtras(complete_bundle);
        startActivity(mIntent_delete);
        break;
    }
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
