package unique.fancysherry.shr.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.Share;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.adapter.recycleview.UserItemDecoration;
import unique.fancysherry.shr.ui.adapter.recycleview.UserShareAdapter;
import unique.fancysherry.shr.ui.dialog.ConfirmDialog;
import unique.fancysherry.shr.ui.otto.BusProvider;
import unique.fancysherry.shr.ui.otto.DataChangeAction;
import unique.fancysherry.shr.ui.widget.BlacklistPopupWindow;
import unique.fancysherry.shr.ui.widget.TagGroup;
import unique.fancysherry.shr.util.DateUtil;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.system.DensityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.otto.Subscribe;

public class UserActivity extends BaseActivity {
  @InjectView(R.id.user_portrait)
  SimpleDraweeView imageview_portrait;
  @InjectView(R.id.shr_number)
  TextView shr_number;
  @InjectView(R.id.user_nickname)
  TextView user_nickname;
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
  @InjectView(R.id.user_activity_toolbar)
  Toolbar mToolbar;
  private BlacklistPopupWindow menuWindow; // 自定义的头像编辑弹出框
  private UserShareAdapter userShareAdapter;
  private Activity context;
  private User mUser;
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
    initializeToolbar(mToolbar);

    handler = new Handler();
    runnable = new Runnable() {
      @Override
      public void run() {
        user_edit.setBackgroundResource(R.drawable.ic_more);
        user_edit.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            menuWindow =
                new BlacklistPopupWindow(inflater.inflate(R.layout.layout_dialog_putblacklist,
                    null, false),
                    itemsOnClick);
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
          // menuWindow.change_text("取消屏蔽");
          ((TextView) menuWindow.getContentView().findViewById(R.id.putblack)).setText("取消屏蔽");
        } else if (menuWindow.putblack.getText().equals("取消屏蔽"))

        {
          Toast.makeText(context, "取消拉黑", Toast.LENGTH_LONG).show();
          // menuWindow.change_text("屏蔽用户");
          ((TextView) menuWindow.getContentView().findViewById(R.id.putblack)).setText("屏蔽用户");
        }
      }
    };


    if (getIntent().getParcelableExtra("user") != null) {
      user_edit.setBackgroundResource(R.drawable.icon_edit_white);
      user_edit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent mIntent = new Intent(context, UserInformationResetActivity.class);
          mIntent.putExtra("user_id", mUser.id);
          mIntent.putExtra("user_avatar", mUser.avatar);
          mIntent.putExtra("user_intro", mUser.brief);
          mIntent.putExtra("user_name", mUser.nickname);
          // mIntent.putExtra("user_email", mUser.email);
          startActivity(mIntent);
        }
      });
      mUser = getIntent().getParcelableExtra("user");
      initData();
    } else if (getIntent().getExtras().getString("user_id") != null) {
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

  public void put_blacklist() {
    Map<String, String> params = new HashMap<>();
    params.put("blacked_user_id", is_mine_id);

    GsonRequest<GsonRequest.FormResult> put_blacklist_request =
        new GsonRequest<>(Request.Method.POST,
            APIConstants.BASE_URL + "/user/black",
            getHeader(), params,
            GsonRequest.FormResult.class,
            new Response.Listener<GsonRequest.FormResult>() {
              @Override
              public void onResponse(GsonRequest.FormResult result) {
                if (result.message.equals("success")) {
                  handler.post(runnable_black);
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
    Map<String, String> params = new HashMap<>();
    params.put("cancelled_user_id", is_mine_id);

    GsonRequest<GsonRequest.FormResult> cancel_put_blacklist_request =
        new GsonRequest<>(Request.Method.POST,
            APIConstants.BASE_URL + "/user/cancel_black",
            getHeader(), params,
            GsonRequest.FormResult.class,
            new Response.Listener<GsonRequest.FormResult>() {
              @Override
              public void onResponse(GsonRequest.FormResult result) {
                if (result.message.equals("success"))
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

  public void initAdapter() {
    int viewHeight = (DensityUtils.dp2px(this, 112) + 50) * mUser.shares.size()+100;
    shr_list.getLayoutParams().height = viewHeight;
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
  }

  private void initData() {
    shr_number.setText(String.valueOf(mUser.shares.size()) + " 分享");
    gratitude_number.setText(String.valueOf(mUser.gratitude_shares_sum) + " 感谢");
    group_name.setText(mUser.groups.get(0).name);
    user_attend_time.setText(DateUtil.getTime4(mUser.register_time));
    introduce.setText(mUser.brief);
    imageview_portrait.setImageURI(Uri.parse(APIConstants.BASE_URL + mUser.avatar));
    user_nickname.setText(mUser.nickname);
    initAdapter();


    if (mUser.groups.size() <= 20) {
      for (int i = 0; i < mUser.groups.size(); i++) {
        if (mUser.groups.get(i).name == null)
          break;
        test_taggroup.add(mUser.groups.get(i).name);
        // Log.e("groups index "+i, mUser.groups.get(i).name);
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
    } else {
      Toast.makeText(this, "你创建的组超过了20个", Toast.LENGTH_LONG).show();
    }
  }

  // 这个注解一定要有,表示订阅了TestAction,并且方法的用 public 修饰的.方法名可以随意取,重点是参数,它是根据你的参数进行判断
  @Subscribe
  public void onChangeAvatar(DataChangeAction dataChangeAction) {
    if (dataChangeAction.getStr().equals(DataChangeAction.CHANGE_AVATAR)) {
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
