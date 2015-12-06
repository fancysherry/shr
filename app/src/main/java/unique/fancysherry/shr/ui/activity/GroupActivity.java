package unique.fancysherry.shr.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.Group;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.adapter.recycleview.MemberAdapter;
import unique.fancysherry.shr.util.DateUtil;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.SApplication;
import unique.fancysherry.shr.util.system.DensityUtils;
import unique.fancysherry.shr.util.system.ResourceHelper;

public class GroupActivity extends AppCompatActivity {
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
  RecyclerView group_member_list;

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


  private MemberAdapter manageAdapter;
  private Activity context;
  private Toolbar mToolbar;

  private Bundle complete_bundle;

  // @Override
  // public void onWindowFocusChanged(boolean hasFocus) {
  // super.onWindowFocusChanged(hasFocus);
  // if (hasFocus) {
  // context.getWindow().getDecorView().setSystemUiVisibility(
  // View.SYSTEM_UI_FLAG_LAYOUT_STABLE
  // | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
  // | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
  // | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
  // | View.SYSTEM_UI_FLAG_FULLSCREEN
  // | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
  // }
  // }

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
    initializeToolbar();
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
    group_member_list =
        (RecyclerView) findViewById(R.id.group_activity_group_members_list_notmanage);
    initAdapter();
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
    group_member_list = (RecyclerView) findViewById(R.id.group_activity_group_members_list);
    LinearLayout manage_group_layout_manage =
        (LinearLayout) findViewById(R.id.manage_group_layout_manage);
    LinearLayout manage_group_layout_invite =
        (LinearLayout) findViewById(R.id.manage_group_layout_invite);
    initAdapter();
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
    manageAdapter.setData(group_users);
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
    }
    else if (size == 1) {
      active_member_layout2_portrait.setVisibility(View.INVISIBLE);
      active_member_layout2_name.setVisibility(View.INVISIBLE);
      active_member_layout3_portrait.setVisibility(View.INVISIBLE);
      active_member_layout3_name.setVisibility(View.INVISIBLE);
      active_member_layout4_portrait.setVisibility(View.INVISIBLE);
      active_member_layout4_name.setVisibility(View.INVISIBLE);
      temp = select_active(1, temp);
      active_member_layout1_name.setText(temp.get(0).name);
      active_member_layout1_portrait.setImageURI(Uri.parse(APIConstants.BASE_URL
          + temp.get(0).avatar));
    }
    else if (size == 2) {
      active_member_layout3_portrait.setVisibility(View.INVISIBLE);
      active_member_layout3_name.setVisibility(View.INVISIBLE);
      active_member_layout4_portrait.setVisibility(View.INVISIBLE);
      active_member_layout4_name.setVisibility(View.INVISIBLE);
      temp = select_active(2, temp);
      active_member_layout1_name.setText(temp.get(0).name);
      active_member_layout1_portrait.setImageURI(Uri.parse(APIConstants.BASE_URL
          + temp.get(0).avatar));
      active_member_layout2_name.setText(temp.get(1).name);
      active_member_layout2_portrait.setImageURI(Uri.parse(APIConstants.BASE_URL
          + temp.get(1).avatar));
    }
    else if (size == 3) {
      active_member_layout4_portrait.setVisibility(View.INVISIBLE);
      active_member_layout4_name.setVisibility(View.INVISIBLE);
      temp = select_active(3, temp);
      active_member_layout1_name.setText(temp.get(0).name);
      active_member_layout1_portrait.setImageURI(Uri.parse(APIConstants.BASE_URL
          + temp.get(0).avatar));
      active_member_layout2_name.setText(temp.get(1).name);
      active_member_layout2_portrait.setImageURI(Uri.parse(APIConstants.BASE_URL
          + temp.get(1).avatar));
      active_member_layout3_name.setText(temp.get(2).name);
      active_member_layout3_portrait.setImageURI(Uri.parse(APIConstants.BASE_URL
          + temp.get(2).avatar));

    }
    else if (size >= 4)
    {
      temp = select_active(4, temp);
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

  private List<User> select_active(int mount, List<User> primary_list)
  {
    User first;
    User second;
    User third;
    User fourth;
    int flag = 0;
    List<User> select_list = new ArrayList<>();
    if (mount == 1) {
      first = primary_list.get(0);
      for (int i = 0; i < primary_list.size(); i++) {
        if (primary_list.get(i).share_sum > first.share_sum) {
          first = primary_list.get(i);
          flag = i;
        }
      }
      primary_list.remove(flag);
      select_list.add(first);

    }
    else if (mount == 2) {
      first = primary_list.get(0);
      for (int i = 0; i < primary_list.size(); i++) {
        if (primary_list.get(i).share_sum > first.share_sum) {
          first = primary_list.get(i);
          flag = i;
        }
      }
      primary_list.remove(flag);
      second = primary_list.get(0);
      for (int i = 0; i < primary_list.size(); i++) {
        if (primary_list.get(i).share_sum > second.share_sum) {
          second = primary_list.get(i);
          flag = i;
        }
      }
      primary_list.remove(flag);
      select_list.add(first);
      select_list.add(second);

    }
    else if (mount == 3) {
      first = primary_list.get(0);
      for (int i = 0; i < primary_list.size(); i++) {
        if (primary_list.get(i).share_sum > first.share_sum) {
          first = primary_list.get(i);
          flag = i;
        }
      }
      primary_list.remove(flag);
      second = primary_list.get(0);
      for (int i = 0; i < primary_list.size(); i++) {
        if (primary_list.get(i).share_sum > second.share_sum) {
          second = primary_list.get(i);
          flag = i;
        }
      }
      primary_list.remove(flag);
      third = primary_list.get(0);
      for (int i = 0; i < primary_list.size(); i++) {
        if (primary_list.get(i).share_sum > third.share_sum) {
          third = primary_list.get(i);
          flag = i;
        }
      }
//      primary_list.remove(flag);
      select_list.add(first);
      select_list.add(second);
      select_list.add(third);
    }
    else if (mount == 4) {
      first = primary_list.get(0);
      for (int i = 0; i < primary_list.size(); i++) {
        if (primary_list.get(i).share_sum > first.share_sum) {
          first = primary_list.get(i);
          flag = i;
        }
      }
      primary_list.remove(flag);
      second = primary_list.get(0);
      for (int i = 0; i < primary_list.size(); i++) {
        if (primary_list.get(i).share_sum > second.share_sum) {
          second = primary_list.get(i);
          flag = i;
        }
      }
      primary_list.remove(flag);
      third = primary_list.get(0);
      for (int i = 0; i < primary_list.size(); i++) {
        if (primary_list.get(i).share_sum > third.share_sum) {
          third = primary_list.get(i);
          flag = i;
        }
      }
      primary_list.remove(flag);
      fourth = primary_list.get(0);
      for (int i = 0; i < primary_list.size(); i++) {
        if (primary_list.get(i).share_sum > fourth.share_sum) {
          fourth = primary_list.get(i);
          flag = i;
        }
      }
//      primary_list.remove(flag);
      select_list.add(first);
      select_list.add(second);
      select_list.add(third);
      select_list.add(fourth);
    }
    return select_list;
  }


  protected void initializeToolbar() {
    mToolbar = (Toolbar) findViewById(R.id.group_activity_toolbar);
    setSupportActionBar(mToolbar);
    getSupportActionBar().setTitle("");
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

  private void initAdapter() {
    int viewHeight = DensityUtils.dp2px(this, 60) * group.users.size();
    group_member_list.getLayoutParams().height = viewHeight;
    manageAdapter = new MemberAdapter(this);
    group_member_list.setLayoutManager(new LinearLayoutManager(this,
        LinearLayoutManager.VERTICAL, false));
    group_member_list.setAdapter(manageAdapter);
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


  public Map<String, String> getHeader() {
    Map<String, String> headers = new HashMap<String, String>();
    UserBean currentUser = AccountManager.getInstance().getCurrentUser();
    if (currentUser != null && currentUser.getCookieHolder() != null) {
      currentUser.getCookieHolder().generateCookieString();
      headers.put("Cookie", currentUser.getCookieHolder().generateCookieString());
    }

    headers
        .put(
            "User-Agent",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36");
    return headers;
  }


  public Map<String, String> getParams() {
    Map<String, String> params = new HashMap<String, String>();
    params.put("group_id", group_id);
    return params;
  }


  public void executeRequest(Request request) {
    SApplication.getRequestManager().executeRequest(request, this);
  }


}
