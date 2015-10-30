package unique.fancysherry.shr.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.Share;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.ui.adapter.recycleview.DividerItemDecoration;
import unique.fancysherry.shr.ui.adapter.recycleview.GroupShareAdapter;
import unique.fancysherry.shr.ui.adapter.recycleview.UserItemDecoration;
import unique.fancysherry.shr.ui.adapter.recycleview.UserShareAdapter;
import unique.fancysherry.shr.ui.widget.TagGroup;

public class UserActivity extends AppCompatActivity {
  @InjectView(R.id.user_portrait)
  SimpleDraweeView imageview_portrait;
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
  @InjectView(R.id.activity_user_taggroup)
  TagGroup tagGroup;

  private UserShareAdapter userShareAdapter;
  private Activity context;
  private User mUser;
  private Toolbar mToolbar;
  private ArrayList<String> test_taggroup = new ArrayList<>();// todo 目前最大组的数量是20个

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user);
    ButterKnife.inject(this);
    context = this;
    initializeToolbar();
    initData();
  }

  protected void initializeToolbar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
    }
    mToolbar = (Toolbar) findViewById(R.id.user_activity_toolbar);

    setSupportActionBar(mToolbar);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    // mToolbar.setOnMenuItemClickListener(onMenuItemClick);

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

  // private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener()
  // {
  // @Override
  // public boolean onMenuItemClick(MenuItem menuItem) {
  // switch (menuItem.getItemId()) {
  // case android.R.id.home:
  // Log.e("home_button", "onclick");
  // context.finish();
  // break;
  // case R.id.action_edit:
  // Intent mIntent = new Intent(context, UserInformationResetActivity.class);
  // mIntent.putExtra("user_id", mUser.id);
  // startActivity(mIntent);
  // break;
  //
  // case R.id.action_settings:
  // break;
  // }
  // return true;
  // }
  // };
  //
  // @Override
  // public boolean onCreateOptionsMenu(Menu menu) {
  // // Inflate the menu; this adds items to the action bar if it is present.
  // getMenuInflater().inflate(R.menu.menu_user, menu);
  // return true;
  // }


  private void initData()
  {
    mUser = getIntent().getParcelableExtra("user");
    shr_number.setText(String.valueOf(mUser.shares.size()));
    gratitude_number.setText(mUser.gratitude_shares_sum);
    group_name.setText(mUser.groups.get(0).name);
    user_attend_time.setText(getTime(mUser.register_time));
    introduce.setText(mUser.brief);
    getSupportActionBar().setTitle(mUser.nickname);
    // Log.e("niclname", mUser.nickname);
    imageview_portrait.setImageURI(Uri.parse(APIConstants.BASE_URL + mUser.avatar));


    shr_list.setLayoutManager(new LinearLayoutManager(this,
        LinearLayoutManager.VERTICAL, false));
    userShareAdapter = new UserShareAdapter(this);
    userShareAdapter.setData(mUser.shares);
    shr_list.setAdapter(userShareAdapter);
    shr_list.addItemDecoration(new UserItemDecoration());
    userShareAdapter.setOnItemClickListener(new UserShareAdapter.OnRecyclerViewItemClickListener() {
      @Override
      public void onItemClick(View view, Share data) {
        Intent mIntent = new Intent(context, BrowserActivity.class);
        mIntent.putExtra("id", data.id);
        startActivity(mIntent);
      }
    });


    if (mUser.groups.size() <= 20) {
      for (int i = 0; i < mUser.groups.size(); i++) {
        test_taggroup.add(mUser.groups.get(i).name);
      }
      tagGroup.setTags(test_taggroup);
      tagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
        @Override
        public void onTagClick(String tag) {
          if (tag.equals("..."))
            tagGroup.setAllTags(test_taggroup);
          else if (tag.equals("<-"))
            tagGroup.setTags(test_taggroup);
        }
      });
    }
    else {
      Toast.makeText(this, "你创建的组超过了20个", Toast.LENGTH_LONG).show();
    }

  }

  private String getTime(String time)
  {
    Pattern pattern = Pattern.compile("[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]");
    Matcher matcher = pattern.matcher(time);
    if (matcher.find()) {
      return matcher.group(0) + "加入";
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
