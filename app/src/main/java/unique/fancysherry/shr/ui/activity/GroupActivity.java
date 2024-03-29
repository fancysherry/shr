package unique.fancysherry.shr.ui.activity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.Group;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.util.DateUtil;
import unique.fancysherry.shr.util.LogUtil;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;

public class GroupActivity extends BaseActivity {
  @InjectView(R.id.group_create_time)
  TextView group_create_time;
  @InjectView(R.id.group_header_share_intro)
  TextView group_shr_intro;
  @InjectView(R.id.group_layout_share_count)
  TextView group_layout_share_count;
  @InjectView(R.id.group_layout_member_count)
  TextView group_layout_member_count;
  @InjectView(R.id.group_create_name)
  TextView group_create_name;

  private TextView active_member_layout1_name;
  private SimpleDraweeView active_member_layout1_portrait;
  private TextView active_member_layout2_name;
  private SimpleDraweeView active_member_layout2_portrait;
  private TextView active_member_layout3_name;
  private SimpleDraweeView active_member_layout3_portrait;
  private TextView active_member_layout4_name;
  private SimpleDraweeView active_member_layout4_portrait;

  private Group group;
  private User user;
  private List<User> group_users = null;
  private String group_id;
  private String group_name;
  private Handler handler;
  private Runnable runnable;
  private Runnable runnable_user;
  private Runnable runnable_group_users;

  private Activity context;
  @InjectView(R.id.group_activity_toolbar)
  Toolbar mToolbar;

  private Bundle complete_bundle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_group);
    ButterKnife.inject(this);
    context = this;
    complete_bundle = getIntent().getExtras();
    group_id = complete_bundle.getString("group_id");
    group_name = complete_bundle.getString("group_name");
    getGroupData();
    initializeToolbar(mToolbar);
    handler = new Handler();
    runnable = new Runnable() {
      @Override
      public void run() {
        getUserData();// 检查是否为管理员
      }
    };

    runnable_user = new Runnable() {
      @Override
      public void run() {
        if (user.nickname.equals(group.admin.name))
          initView_manage();
        else
          initView_not_manage();
        getGroupUserData();
        initData();
      }
    };
    runnable_group_users = new Runnable() {
      @Override
      public void run() {
        initGroupUserData();
      }
    };
  }

  @OnClick({R.id.group_layout_share, R.id.group_layout_member})
  public void click(View mView) {
    switch (mView.getId()) {
      case R.id.group_layout_member:
        Intent intent_group_member = new Intent(context, GroupMemberActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putParcelable("group", group);
        intent_group_member.putExtras(mBundle);
        startActivity(intent_group_member);
        break;
      case R.id.group_layout_share:
        context.finish();
        break;
    }
  }

  private void initView_not_manage() {
    active_member_layout1_name = (TextView) findViewById(R.id.active_member_layout1_name_notmanage);
    active_member_layout2_name = (TextView) findViewById(R.id.active_member_layout2_name_notmanage);
    active_member_layout3_name = (TextView) findViewById(R.id.active_member_layout3_name_notmanage);
    active_member_layout4_name = (TextView) findViewById(R.id.active_member_layout4_name_notmanage);
    active_member_layout1_portrait =
        (SimpleDraweeView) findViewById(R.id.active_member_layout1_portrait_notmanage);
    active_member_layout2_portrait =
        (SimpleDraweeView) findViewById(R.id.active_member_layout2_portrait_notmanage);
    active_member_layout3_portrait =
        (SimpleDraweeView) findViewById(R.id.active_member_layout3_portrait_notmanage);
    active_member_layout4_portrait =
        (SimpleDraweeView) findViewById(R.id.active_member_layout4_portrait_notmanage);
    LinearLayout group_notmanage_framelayout_part2 =
        (LinearLayout) findViewById(R.id.group_notmanage_framelayout_part2);
    group_notmanage_framelayout_part2.setVisibility(View.VISIBLE);
  }

  private void initView_manage() {
    active_member_layout1_name = (TextView) findViewById(R.id.active_member_layout1_name);
    active_member_layout2_name = (TextView) findViewById(R.id.active_member_layout2_name);
    active_member_layout3_name = (TextView) findViewById(R.id.active_member_layout3_name);
    active_member_layout4_name = (TextView) findViewById(R.id.active_member_layout4_name);
    active_member_layout1_portrait =
        (SimpleDraweeView) findViewById(R.id.active_member_layout1_portrait);
    active_member_layout2_portrait =
        (SimpleDraweeView) findViewById(R.id.active_member_layout2_portrait);
    active_member_layout3_portrait =
        (SimpleDraweeView) findViewById(R.id.active_member_layout3_portrait);
    active_member_layout4_portrait =
        (SimpleDraweeView) findViewById(R.id.active_member_layout4_portrait);
    LinearLayout group_manage_framelayout_part1 =
        (LinearLayout) findViewById(R.id.group_manage_framelayout_part1);
    group_manage_framelayout_part1.setVisibility(View.VISIBLE);
    RelativeLayout manage_group_layout_manage =
        (RelativeLayout) findViewById(R.id.manage_group_layout_manage);
    RelativeLayout manage_group_layout_invite =
        (RelativeLayout) findViewById(R.id.manage_group_layout_invite);
    manage_group_layout_invite.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent mIntent = new Intent(context, InviteMemberActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("group_id", group_id);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
      }
    });

    manage_group_layout_manage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent mIntent = new Intent(context, GroupEditActivity.class);
        mIntent.putExtras(complete_bundle);
        startActivity(mIntent);
      }
    });
  }

  public void initData() {
    try {
      group_create_time.setText("创建于" + DateUtil.toDate(group.create_time));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    group_layout_share_count.setText(group.group_share_sum);
    group_layout_member_count.setText(String.valueOf(group.users.size()));
    group_create_name.setText(group_name);
    group_shr_intro.setText(group.group_intro);
  }

  public void initGroupUserData() {
    int size = group_users.size();
    List<User> temp = new ArrayList<>();
    temp.addAll(group_users);
    if (size == 0) {
      active_member_layout1_portrait.setVisibility(View.INVISIBLE);
      active_member_layout1_name.setVisibility(View.INVISIBLE);
      active_member_layout2_portrait.setVisibility(View.INVISIBLE);
      active_member_layout2_name.setVisibility(View.INVISIBLE);
      active_member_layout3_portrait.setVisibility(View.INVISIBLE);
      active_member_layout3_name.setVisibility(View.INVISIBLE);
      active_member_layout4_portrait.setVisibility(View.INVISIBLE);
      active_member_layout4_name.setVisibility(View.INVISIBLE);
    } else if (size == 1) {
      active_member_layout2_portrait.setVisibility(View.INVISIBLE);
      active_member_layout2_name.setVisibility(View.INVISIBLE);
      active_member_layout3_portrait.setVisibility(View.INVISIBLE);
      active_member_layout3_name.setVisibility(View.INVISIBLE);
      active_member_layout4_portrait.setVisibility(View.INVISIBLE);
      active_member_layout4_name.setVisibility(View.INVISIBLE);
      select_active(temp);
      active_member_layout1_name.setText(temp.get(0).name);
      active_member_layout1_portrait.setImageURI(Uri.parse(APIConstants.BASE_URL
          + temp.get(0).avatar));
    } else if (size == 2) {
      active_member_layout3_portrait.setVisibility(View.INVISIBLE);
      active_member_layout3_name.setVisibility(View.INVISIBLE);
      active_member_layout4_portrait.setVisibility(View.INVISIBLE);
      active_member_layout4_name.setVisibility(View.INVISIBLE);
      select_active(temp);
      active_member_layout1_name.setText(temp.get(0).name);
      active_member_layout1_portrait.setImageURI(Uri.parse(APIConstants.BASE_URL
          + temp.get(0).avatar));
      active_member_layout2_name.setText(temp.get(1).name);
      active_member_layout2_portrait.setImageURI(Uri.parse(APIConstants.BASE_URL
          + temp.get(1).avatar));
    } else if (size == 3) {
      active_member_layout4_portrait.setVisibility(View.INVISIBLE);
      active_member_layout4_name.setVisibility(View.INVISIBLE);
      select_active(temp);
      active_member_layout1_name.setText(temp.get(0).name);
      active_member_layout1_portrait.setImageURI(Uri.parse(APIConstants.BASE_URL
          + temp.get(0).avatar));
      active_member_layout2_name.setText(temp.get(1).name);
      active_member_layout2_portrait.setImageURI(Uri.parse(APIConstants.BASE_URL
          + temp.get(1).avatar));
      active_member_layout3_name.setText(temp.get(2).name);
      active_member_layout3_portrait.setImageURI(Uri.parse(APIConstants.BASE_URL
          + temp.get(2).avatar));

    } else if (size >= 4) {
      select_active(temp);
      active_member_layout1_name.setText(temp.get(0).name);
      active_member_layout1_portrait.setImageURI(Uri.parse(APIConstants.BASE_URL
          + temp.get(0).avatar));
      active_member_layout2_name.setText(temp.get(1).name);
      active_member_layout2_portrait.setImageURI(Uri.parse(APIConstants.BASE_URL
          + temp.get(1).avatar));
      active_member_layout3_name.setText(temp.get(2).name);
      active_member_layout3_portrait.setImageURI(Uri.parse(APIConstants.BASE_URL
          + temp.get(2).avatar));
      active_member_layout4_name.setText(temp.get(3).name);
      active_member_layout4_portrait.setImageURI(Uri.parse(APIConstants.BASE_URL
          + temp.get(3).avatar));
    }
  }

  private void select_active(List<User> primary_list) {
    Collections.sort(primary_list);
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

  @Override
  protected void onDestroy() {
    super.onDestroy();
    ButterKnife.reset(this);
  }

  public void getGroupData() {
    GsonRequest<Group> group_share_request =
        new GsonRequest<>(Request.Method.GET,
            APIConstants.BASE_URL + "/group/info?group_id=" + group_id,
            getHeader(), null,
            Group.class,
            new Response.Listener<Group>() {
              @Override
              public void onResponse(Group pGroup) {
                // shareAdapter.setData(shares.shares);
                // mShares = shares.shares;
                group = pGroup;
                handler.post(runnable);
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(group_share_request);
  }

  public void getGroupUserData() {
    GsonRequest<Group> group_share_request =
        new GsonRequest<>(Request.Method.GET,
            APIConstants.BASE_URL + "/group/users?group_id=" + group_id,
            getHeader(), null,
            Group.class,
            new Response.Listener<Group>() {
              @Override
              public void onResponse(Group pGroup) {
                group_users = pGroup.users;
                handler.post(runnable_group_users);
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(group_share_request);
  }

  public void getUserData() {
    GsonRequest<User> user_request =
        new GsonRequest<>(Request.Method.GET,
            APIConstants.BASE_URL + "/homepage",
            getHeader(), null,
            User.class,
            new Response.Listener<User>() {
              @Override
              public void onResponse(User pUser) {
                // shareAdapter.setData(shares.shares);
                // mShares = shares.shares;
                user = pUser;
                handler.post(runnable_user);
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(user_request);
  }
}
