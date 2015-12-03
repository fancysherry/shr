package unique.fancysherry.shr.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.UploadImage.SelectPicPopupWindow;
import unique.fancysherry.shr.io.model.Share;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.adapter.recycleview.DividerItemDecoration;
import unique.fancysherry.shr.ui.adapter.recycleview.GroupShareAdapter;
import unique.fancysherry.shr.ui.adapter.recycleview.UserItemDecoration;
import unique.fancysherry.shr.ui.adapter.recycleview.UserShareAdapter;
import unique.fancysherry.shr.ui.dialog.ConfirmDialog;
import unique.fancysherry.shr.ui.otto.BusProvider;
import unique.fancysherry.shr.ui.otto.DataChangeAction;
import unique.fancysherry.shr.ui.widget.BlacklistPopupWindow;
import unique.fancysherry.shr.ui.widget.TagGroup;
import unique.fancysherry.shr.util.DateUtil;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.SApplication;
import unique.fancysherry.shr.util.system.ResourceHelper;

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
  @InjectView(R.id.user_edit_icon)
  ImageView user_edit;
  private BlacklistPopupWindow menuWindow; // 自定义的头像编辑弹出框
  private UserShareAdapter userShareAdapter;
  private Activity context;
  private User mUser;
  private Toolbar mToolbar;
  private ArrayList<String> test_taggroup = new ArrayList<>();// todo 目前最大组的数量是20个
  private Handler handler;
  private Runnable runnable;
  private Runnable runnable_black;

  private String is_mine_id;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user);
    BusProvider.getInstance().register(this);
    ButterKnife.inject(this);
    context = this;
    initializeToolbar();

    handler = new Handler();
    runnable = new Runnable() {
      @Override
      public void run() {
        // LogUtil.e(AccountManager.getInstance().getCurrentUser().mAccountBean.username+ "");
        user_edit.setBackgroundResource(R.drawable.ic_more2x);
        user_edit.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            menuWindow = new BlacklistPopupWindow(context, itemsOnClick);
            menuWindow.showAtLocation(findViewById(R.id.parent_layout),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
          }
        });
        initData();
      }
    };
    runnable_black = new Runnable() {
      @Override
      public void run() {
        if (menuWindow.putblack.getText().equals("屏蔽用户")) {
          Toast.makeText(context, "拉黑", Toast.LENGTH_LONG).show();
          LogUtil.e("屏蔽");
          menuWindow.putblack.setText("取消屏蔽");
        } else if (menuWindow.putblack.getText().equals("取消屏蔽"))

        {
          LogUtil.e("取消屏蔽");
          Toast.makeText(context, "取消拉黑", Toast.LENGTH_LONG).show();
          menuWindow.putblack.setText("屏蔽用户");
        }
      }
    };


    if (getIntent().getParcelableExtra("user") != null) {
      user_edit.setBackgroundResource(R.drawable.icon_input_four);
      user_edit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent mIntent = new Intent(context, UserInformationResetActivity.class);
          mIntent.putExtra("user_id", mUser.id);
          mIntent.putExtra("user_avatar", mUser.avatar);
          startActivity(mIntent);
        }
      });
      mUser = getIntent().getParcelableExtra("user");
      initData();
    }
    else if (getIntent().getExtras().getString("user_id") != null)
    {
      is_mine_id = getIntent().getExtras().getString("user_id");
      getUserData(is_mine_id);
    }
  }

  // 为弹出窗口实现监听类
  private View.OnClickListener itemsOnClick = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      // 隐藏弹出窗口
      menuWindow.dismiss();

      switch (v.getId()) {
        case R.id.putblack://
          showDialog(ConfirmDialog.PUT_BLACKLIST_CONFIRM);
          // Toast.makeText(context, "拉黑", Toast.LENGTH_LONG).show();
          break;
        case R.id.cancelBtn:// 取消
          break;
        default:
          break;
      }
    }
  };

  void showDialog(String type) {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    Fragment prev = getSupportFragmentManager().findFragmentByTag("put_black_dialog");
    if (prev != null) {
      ft.remove(prev);
    }
    ft.addToBackStack(null);
    ConfirmDialog dialogFrag = ConfirmDialog.newInstance(type);
    dialogFrag.show(getSupportFragmentManager(), "put_black_dialog");
  }

  public void onDismissDialog() {
    if (menuWindow.putblack.getText().equals("屏蔽用户")) {
      put_blacklist();
    } else if (menuWindow.putblack.getText().equals("取消屏蔽")) {
      cancel_put_blacklist();
    }
    // TODO add your implementation.
  }

  public Map<String, String> getParams_black() {
    Map<String, String> params = new HashMap<>();
    params.put("blacked_user_id", is_mine_id);
    return params;
  }

  public Map<String, String> getParams_cancel_black() {
    Map<String, String> params = new HashMap<>();
    params.put("cancelled_user_id", is_mine_id);
    return params;
  }

  public void put_blacklist() {
    GsonRequest<GsonRequest.FormResult> put_blacklist_request =
        new GsonRequest<>(Request.Method.POST,
            APIConstants.BASE_URL + "/user/black",
            getHeader(), getParams_black(),
            GsonRequest.FormResult.class,
            new Response.Listener<GsonRequest.FormResult>() {
              @Override
              public void onResponse(GsonRequest.FormResult result) {
                if (result.message.equals("message")) {
                  handler.post(runnable_black);
                  LogUtil.e("log");
                }
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(put_blacklist_request);
  }

  public void cancel_put_blacklist() {
    GsonRequest<GsonRequest.FormResult> cancel_put_blacklist_request =
        new GsonRequest<>(Request.Method.POST,
            APIConstants.BASE_URL + "/user/cancel_black",
            getHeader(), getParams_cancel_black(),
            GsonRequest.FormResult.class,
            new Response.Listener<GsonRequest.FormResult>() {
              @Override
              public void onResponse(GsonRequest.FormResult result) {
                if (result.message.equals("message"))
                  handler.post(runnable_black);
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(cancel_put_blacklist_request);
  }


  public void getUserData(String user_id) {
    GsonRequest<User> user_request =
        new GsonRequest<>(Request.Method.GET,
            APIConstants.BASE_URL + "/homepage?uid=" + user_id,
            getHeader(), null,
            User.class,
            new Response.Listener<User>() {
              @Override
              public void onResponse(User pUser) {
                // shareAdapter.setData(shares.shares);
                // mShares = shares.shares;
                mUser = pUser;
                handler.post(runnable);
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(user_request);
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
                mUser = pUser;
                handler.post(runnable);
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

  public void executeRequest(Request request) {
    SApplication.getRequestManager().executeRequest(request, this);
  }

  // Resolve the given attribute of the current theme
  private int getAttributeColor(int resId) {
    TypedValue typedValue = new TypedValue();
    getTheme().resolveAttribute(resId, typedValue, true);
    int color = 0x000000;
    if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT
        && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
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
    mToolbar = (Toolbar) findViewById(R.id.user_activity_toolbar);
    // // 设置菜单及其点击监听
    // mToolbar.inflateMenu(R.menu.menu_user);
    // mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
    // @Override
    // public boolean onMenuItemClick(MenuItem item) {
    // switch (item.getItemId()) {
    // case R.id.action_settings:
    // break;
    // }
    // return true;
    // }
    // });

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
    // user_edit.setOnClickListener(new View.OnClickListener() {
    // @Override
    // public void onClick(View v) {
    // Intent mIntent = new Intent(context, UserInformationResetActivity.class);
    // mIntent.putExtra("user_id", mUser.id);
    // startActivity(mIntent);
    // }
    // });
    // mUser = getIntent().getParcelableExtra("user");
    shr_number.setText(String.valueOf(mUser.shares.size()));
    gratitude_number.setText(String.valueOf(mUser.gratitude_shares_sum));
    group_name.setText(mUser.groups.get(0).name);
    user_attend_time.setText(getTime(mUser.register_time));
    introduce.setText(mUser.brief);
    getSupportActionBar().setTitle(mUser.nickname);
    // Log.e("nickname", mUser.nickname);
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
        mIntent.putExtra(APIConstants.TYPE, APIConstants.SHARE_TYPE);
        startActivity(mIntent);
      }
    });


    if (mUser.groups.size() <= 20) {
      for (int i = 0; i < mUser.groups.size(); i++) {
        if (mUser.groups.get(i).name == null)
          break;
        test_taggroup.add(mUser.groups.get(i).name);
        // Log.e("groups index "+i, mUser.groups.get(i).name);
      }
      Log.e("test_taggroup", String.valueOf(test_taggroup.size()));
      Log.e("groups", String.valueOf(mUser.groups.size()));
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

  // 这个注解一定要有,表示订阅了TestAction,并且方法的用 public 修饰的.方法名可以随意取,重点是参数,它是根据你的参数进行判断
  @Subscribe
  public void onChangeAvatar(DataChangeAction dataChangeAction) {
    if (dataChangeAction.getStr().equals(DataChangeAction.CHANGE_AVATAR))
    {
      getUserData();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    BusProvider.getInstance().unregister(this);
    ButterKnife.reset(this);
  }


}
