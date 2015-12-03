package unique.fancysherry.shr.ui.activity;

import android.app.Activity;
import android.content.Intent;
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

import java.text.ParseException;
import java.util.HashMap;
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
import unique.fancysherry.shr.util.system.ResourceHelper;

public class GroupActivity extends AppCompatActivity {
  @InjectView(R.id.group_create_time)
  TextView group_create_time;
  @InjectView(R.id.group_header_share_number)
  TextView group_shr_num;
  @InjectView(R.id.group_invite_button)
  ImageView group_invite_bt;
  @InjectView(R.id.group_invite_text)
  TextView group_invite_text;
  @InjectView(R.id.group_manage_text)
  TextView group_manage_text;
  @InjectView(R.id.group_manage_button)
  ImageView group_manage_bt;
  @InjectView(R.id.group_activity_group_members_list)
  RecyclerView group_member_list;
  @InjectView(R.id.activity_group_toolbar_title)
  TextView activity_group_toolbar_title;
  @InjectView(R.id.manage_group_layout)
  LinearLayout manage_group_layout;

  private Group group;
  private User user;
  private String group_id;
  private String group_name;
  private Handler handler;
  private Runnable runnable;
  private Runnable runnable_user;


  private MemberAdapter manageAdapter;
  private Activity context;
  private Toolbar mToolbar;

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


    group_invite_text.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent mIntent = new Intent(context, InviteMemberActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("group_id", group_id);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
      }
    });

    group_manage_text.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent mIntent = new Intent(context, GroupEditActivity.class);
        mIntent.putExtras(complete_bundle);
        startActivity(mIntent);
      }
    });

    group_invite_bt.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent mIntent = new Intent(context, InviteMemberActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("group_id", group_id);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);

      }
    });

    group_manage_bt.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent mIntent = new Intent(context, GroupEditActivity.class);
        mIntent.putExtras(complete_bundle);
        startActivity(mIntent);
      }
    });

    getGroupData();
    initializeToolbar();
    initAdapter();
    initData();

    handler = new Handler();
    runnable = new Runnable() {
      @Override
      public void run() {
        getUserData();//检查是否为管理员
        try {
          group_create_time.setText("创建于" + DateUtil.toDate(group.create_time));
        } catch (ParseException e) {
          e.printStackTrace();
        }
        // group_shr_num.setText("共" + String.valueOf(group.shares.size()) + "条Shr");
        manageAdapter.setData(group.users);
      }
    };

    runnable_user = new Runnable() {
      @Override
      public void run() {
        if (user.nickname.equals(group.admin.name))
          manage_group_layout.setVisibility(View.VISIBLE);
        else
          manage_group_layout.setVisibility(View.INVISIBLE);
      }
    };


  }

  public void initData()
  {
    activity_group_toolbar_title.setText(group_name);
  }


//  // Resolve the given attribute of the current theme
//  private int getAttributeColor(int resId) {
//    TypedValue typedValue = new TypedValue();
//    getTheme().resolveAttribute(resId, typedValue, true);
//    int color = 0x000000;
//    if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
//      // resId is a color
//      color = typedValue.data;
//    } else {
//      // resId is not a color
//    }
//    return color;
//  }

  protected void initializeToolbar() {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//      getWindow().setStatusBarColor(getAttributeColor(R.attr.colorPrimaryDark));
//    }
    mToolbar = (Toolbar) findViewById(R.id.group_activity_toolbar);
    setSupportActionBar(mToolbar);
    getSupportActionBar().setTitle("");
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);

  }

  // private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener()
  // {
  // @Override
  // public boolean onMenuItemClick(MenuItem menuItem) {
  // switch (menuItem.getItemId()) {
  // case android.R.id.home:
  // finish();
  // }
  // return true;
  // }
  // };

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
                        user=pUser;
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
