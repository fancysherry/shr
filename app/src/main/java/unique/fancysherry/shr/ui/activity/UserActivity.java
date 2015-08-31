package unique.fancysherry.shr.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.model.Share;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.ui.adapter.recycleview.ShareAdapter;

public class UserActivity extends ActionbarCastActivity {
  @InjectView(R.id.user_portrait)
  CircleImageView imageview_portrait;
  @InjectView(R.id.shr_number)
  TextView shr_number;
  @InjectView(R.id.gratitude_number)
  TextView gratitude_number;
  @InjectView(R.id.user_group)
  TextView group_name;
  @InjectView(R.id.user_add_time)
  TextView user_attend_time;
  @InjectView(R.id.user_introduce)
  TextView introduce;
  @InjectView(R.id.user_shr_history)
  RecyclerView shr_list;

  private ShareAdapter shareAdapter;
  private Context context;
  private User mUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user);
    ButterKnife.inject(this);
    context = this;
    initData();
  }

  @Override
  protected void initializeToolbar() {
    mToolbar = (Toolbar) findViewById(R.id.user_activity_toolbar);
    mToolbar.setTitle("我");
    mToolbar.getBackground().setAlpha(0);
    setSupportActionBar(mToolbar);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    mToolbar.setNavigationIcon(R.drawable.ic_launcher);
    mToolbar.setOnMenuItemClickListener(onMenuItemClick);

  }

  private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
      switch (menuItem.getItemId()) {
        case R.id.action_edit:
          Intent mIntent = new Intent(context, UserInformationResetActivity.class);
          mIntent.putExtra("user_id", mUser.id);
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
    getMenuInflater().inflate(R.menu.menu_user, menu);
    return true;
  }


  private void initData()
  {
    mUser = getIntent().getParcelableExtra("user");
    shr_number.setText(String.valueOf(mUser.shares.size()));
    gratitude_number.setText(mUser.gratitude_shares_sum);
    group_name.setText(mUser.groups.get(0).group_name);
    user_attend_time.setText(getTime(mUser.register_time));
    introduce.setText(mUser.brief);


    shr_list.setLayoutManager(new LinearLayoutManager(this,
        LinearLayoutManager.VERTICAL, false));
    shareAdapter = new ShareAdapter(this);
    shareAdapter.setData(mUser.shares);
    shr_list.setAdapter(shareAdapter);
    shareAdapter.setOnItemClickListener(new ShareAdapter.OnRecyclerViewItemClickListener() {
      @Override
      public void onItemClick(View view, Share data) {
        Intent mIntent = new Intent(context, BrowserActivity.class);
        mIntent.putExtra("id", data.id);
        startActivity(mIntent);
      }
    });


  }

  private String getTime(String time)
  {
    Pattern pattern = Pattern.compile("[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]");
    Matcher matcher = pattern.matcher(time);
    if (matcher.find()) {
      return matcher.group(0)+"加入";
    }
    else
      return null;
  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
    ButterKnife.reset(this);
  }


}
