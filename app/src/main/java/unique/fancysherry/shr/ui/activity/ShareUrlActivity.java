package unique.fancysherry.shr.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountBean;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.io.request.LoginRequest;
import unique.fancysherry.shr.ui.otto.BusProvider;
import unique.fancysherry.shr.ui.otto.ShareUrlAction;
import unique.fancysherry.shr.ui.dialog.ShrDialog;
import unique.fancysherry.shr.ui.widget.TagGroup;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.LocalConfig;
import unique.fancysherry.shr.util.config.SApplication;
import unique.fancysherry.shr.util.UrlFromString;

public class ShareUrlActivity extends BaseActivity {
  private Activity activity;
  private User mUser;
  private TagGroup tagGroup;
  private ArrayList<String> test_taggroup = new ArrayList<>();

  private Handler handler;
  private Runnable runnable;

  private String extract_url;

  private String sessionid;
  private String username;
  private String password;
  private String group_name;
  private String comment;
  private LoginRequest<LoginRequest.FormResult> login_request;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    BusProvider.getInstance().register(this);
    activity = this;
    setContentView(R.layout.activity_share_url);
    handler = new Handler();
    runnable = new Runnable() {
      @Override
      public void run() {
        start_dialog();
      }
    };
    initView();
    // showMyDialog(Gravity.CENTER);
    // Get intent, action and MIME type
    Intent intent = getIntent();
    String action = intent.getAction();
    String type = intent.getType();

    if (Intent.ACTION_SEND.equals(action) && type != null) {
      if ("text/plain".equals(type)) {
        handleSendText(intent); // Handle text being sent
      }
    }
    start();

  }

  public void initView() {

  }

  private void start() {
    AccountBean mAccountBean = AccountManager.getInstance().getCurrentUser().mAccountBean;
    username = mAccountBean.username;
    password = mAccountBean.pwd;
    if (username != null && password != null) {
      login_request =
          new LoginRequest<>(APIConstants.BASE_URL + "/login", null,
              getParams_login(), LoginRequest.FormResult.class,
              new Response.Listener<LoginRequest.FormResult>() {
                @Override
                public void onResponse(LoginRequest.FormResult result) {
                  if (result.message.equals("success")) {
                    loginSuccessfully(result);
                  }
                }
              }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError pVolleyError) {
                  LogUtil.e("error " + pVolleyError);
                }
              });
      executeRequest(login_request);
    }
    // todo intent
    else
      Toast.makeText(this, "从未在该设备上登陆过shr，请先登陆", Toast.LENGTH_SHORT).show();
  }



  protected void loginSuccessfully(LoginRequest.FormResult model) {
    sessionid = login_request.cookies;
    AccountManager.getInstance().getCurrentUser().getCookieHolder()
        .saveCookie(sessionid);
    LocalConfig.setFirstLaunch(false);
    getUserData();// 获取个人所在组的信息，结束后启动dialog

  }

  public Map<String, String> getParams_login()
  {
    // username = "longchen@hustunique.com";
    // password = "hustunique";
    Map<String, String> params = new HashMap<String, String>();
    params.put("email", username);
    params.put("password", password);
    return params;
  }

  public void start_dialog()
  {
    // LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
    // View diaglog_view = mLayoutInflater.inflate(R.layout.dialog_shr_content, null);
    // tagGroup = (TagGroup) diaglog_view.findViewById(R.id.user_groups_tagGroup);
    for (int i = 0; i < mUser.groups.size(); i++) {
      test_taggroup.add(mUser.groups.get(i).name);
      LogUtil.e("test");
    }
    /**
     * 为了不重复显示dialog，在显示对话框之前移除正在显示的对话框。
     */
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    Fragment fragment = getSupportFragmentManager().findFragmentByTag("ShrDialog");
    if (null != fragment) {
      ft.remove(fragment);
    }
    ShrDialog dialogFragment = ShrDialog.newInstance(test_taggroup, APIConstants.SHARE_OUT,"这是一条分享的标题");
    dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ShrDialog);
    dialogFragment.show(ft, "ShrDialog");
    // tagGroup.setTagsDailog(test_taggroup);
    // LogUtil.e("tagGroup:"+tagGroup.getChildCount());
    // tagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
    // @Override
    // public void onTagClick(String tag) {
    // if (tag.equals("..."))
    // tagGroup.setAllTagsDailog(test_taggroup);
    // else if (tag.equals("<-"))
    // tagGroup.setTagsDailog(test_taggroup);
    // }
    // });
    // showDialog(Gravity.CENTER,diaglog_view);
  }

  public void handleSendText(Intent intent) {
    String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
    // String sharedTitle = intent.getStringExtra(Intent.EXTRA_TITLE);
    if (sharedText != null) {
      // Update UI to reflect text being shared
      Log.e("shareText", sharedText);
      // Log.e("shareTitle", sharedTitle);
      extract_url = UrlFromString.pullLinks(sharedText);
      if (extract_url == null)
      {
        Toast.makeText(this, "您分享的网页不包含有效网址，请重新分享", Toast.LENGTH_LONG).show();
      }
      else
      {
        Toast.makeText(this, extract_url, Toast.LENGTH_LONG).show();
      }
    }
  }

  public void getUserData() {
    GsonRequest<User> group_share_request =
        new GsonRequest<>(Request.Method.GET,
            APIConstants.BASE_URL + "/homepage",
            getHeader(), null,
            User.class,
            new Response.Listener<User>() {
              @Override
              public void onResponse(User pUser) {
                mUser = pUser;
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

  public void post_share_url(String group_name, String comment) {
    GsonRequest<GsonRequest.FormResult> group_share_url_request =
        new GsonRequest<>(Request.Method.POST,
            APIConstants.BASE_URL + "/share",
            getHeader(), getParams_share(group_name, comment),
            GsonRequest.FormResult.class,
            new Response.Listener<GsonRequest.FormResult>() {
              @Override
              public void onResponse(GsonRequest.FormResult pGroup) {
                if (pGroup.message.equals("success"))
                  Toast.makeText(activity, "share a page successful", Toast.LENGTH_LONG)
                      .show();
                Log.e("message", pGroup.message);
                Intent mIntent = new Intent(activity, MainActivity.class);
                startActivity(mIntent);
                finish();
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(group_share_url_request);
  }

  public Map<String, String> getParams_share(String group_name, String comment) {
    // String url = "http://stackoverflow.com/questions/8126299/android-share-browser-url-to-app";
    Map<String, String> params = new HashMap<>();
    params.put("url", extract_url);
    if (comment != null) {
      params.put("comment", comment);
    }
    if (!group_name.equals("@me"))
      params.put("groups", group_name);
    return params;
  }


  @Subscribe
  public void onShareGroupName(ShareUrlAction shareUrlAction) {
    // 这里更新视图或者后台操作,从TestAction获取传递参数.
    if (shareUrlAction.getGroup_name() != null) {
      group_name = shareUrlAction.getGroup_name();
      comment = shareUrlAction.getComment();
      post_share_url(group_name, comment);
    }
  }

  @Override
  public void onDestroy() {
    BusProvider.getInstance().unregister(this);
    super.onDestroy();
  }

}
