package unique.fancysherry.shr.ui.activity;

import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import unique.fancysherry.shr.R;

public class DeleteGroupActivity extends AppCompatActivity {

  private String group_id;
  private String group_name;

  @InjectView(R.id.delete_group_information)
  TextView delete_group_information;
  @InjectView(R.id.delete_group_name)
  TextView delete_group_name;
  @InjectView(R.id.delete_group_result)
  TextView delete_group_result;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_delete_group);
    ButterKnife.inject(this);
    Bundle mBundle = getIntent().getExtras();
    group_id = mBundle.getString("group_id");
    group_name = mBundle.getString("group_name");
    initializeToolbar();
    initData();
  }


  public void initData()
  {
    delete_group_information.setText("143天前" + group_name + "组被创建，现在这里已经聚集了133个小伙伴，积累了234条分享经验");
    delete_group_name.setText("你确定要删除" + group_name + "组吗？");
    delete_group_result.setText("解散后组员仍可以在个人主页查看他们在" + group_name + "组Shr过的分享的源网页，但评论和组员数据将全部删除");
  }

  protected void initializeToolbar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
    }
    Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

    setSupportActionBar(mToolbar);
    getSupportActionBar().setTitle("成员管理");
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
//    mToolbar.setOnMenuItemClickListener(onMenuItemClick);

  }

//  @Override
//  public boolean onOptionsItemSelected(MenuItem item) {
//    // Handle action bar item clicks here. The action bar will
//    // automatically handle clicks on the Home/Up button, so long
//    // as you specify a parent activity in AndroidManifest.xml.
//    int id = item.getItemId();
//    if (id == android.R.id.home) {
//      finish();
//    }
//    return super.onOptionsItemSelected(item);
//  }


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
