package unique.fancysherry.shr.ui.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.fragment.DrawerFragment;
import unique.fancysherry.shr.ui.fragment.InboxShareFragment;
import unique.fancysherry.shr.ui.fragment.NewGroupFragment;
import unique.fancysherry.shr.ui.fragment.NotificationFragment;
import unique.fancysherry.shr.ui.fragment.ShareContentFragment;
import unique.fancysherry.shr.ui.otto.BusProvider;
import unique.fancysherry.shr.ui.otto.DataChangeAction;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.UrlFromString;
import unique.fancysherry.shr.util.config.LocalConfig;
import unique.fancysherry.shr.util.config.SApplication;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.otto.Subscribe;


public class MainActivity extends BaseMainActivity
    implements
      DrawerFragment.NavigationDrawerCallbacks,
      NewGroupFragment.OnNewGroupListener,
      ShareContentFragment.OnGetGroupIdListener {
  public FragmentManager fragmentManager;
  /**
   * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
   */
  private DrawerFragment mDrawerFragment;

  private Activity activity;

  private String now_group_id;
  private String now_group_name;

  private String text_from_clipboard = null;
  private ClipboardManager clipboard = null;

  private User user;
  private Handler handler;
  private Runnable runnable;

  private EditText dialog_intro_input;
  private ImageView group_intro;

  private String extract_url;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activity = this;
    setContentView(R.layout.activity_main);
    BusProvider.getInstance().register(this);
    initView();
    initializeToolbar();
    getSupportActionBar().setTitle("消息");
    mToolbar.setOnMenuItemClickListener(onMenuItemClick);


    // 默认加载第一个组的信息
    handler = new Handler();
    runnable = new Runnable() {
      @Override
      public void run() {
        // LogUtil.e("first load ");
        // LogUtil.e("first load "+user.groups.get(0).name);
        // fragmentManager
        // .beginTransaction()
        // .replace(R.id.container,
        // ShareContentFragment.newInstance(user.groups.get(0).name))
        // .commit();
      }
    };
  }

  @Override
  public void onResume() {
    super.onResume();
    // checkShare();
    // getUserData();
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    getUserData();
  }

  private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
      switch (menuItem.getItemId()) {
        // case R.id.action_edit:
        // Intent mIntent = new Intent(activity, GroupActivity.class);
        // Bundle mBundle = new Bundle();
        // mBundle.putString("group_id", now_group_id);
        // mBundle.putString("group_name", now_group_name);
        // mIntent.putExtras(mBundle);
        // startActivity(mIntent);
        // break;

        case R.id.action_settings:
          exit_shr();
          break;
      }
      return true;
    }
  };


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }



  @Override
  protected void initView() {
    group_intro = (ImageView) findViewById(R.id.group_intro);
    group_intro.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent mIntent = new Intent(activity, GroupActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("group_id", now_group_id);
        mBundle.putString("group_name", now_group_name);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
      }
    });


    mDrawerFragment =
        (DrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

    mDrawerFragment.setUp(
        R.id.navigation_drawer,
        (DrawerLayout) findViewById(R.id.drawerLayout));

    fragmentManager = getSupportFragmentManager();

    // default load ar_me
    fragmentManager
        .beginTransaction()
        .replace(R.id.container,
            InboxShareFragment.newInstance())
        .commit();

    group_intro.setVisibility(View.INVISIBLE);
    getUserData();
  }



  @Override
  public void onNavigationDrawerItemSelected(String group_name) {
    // update the main content by replacing fragments
    if (fragmentManager == null)
      fragmentManager = getSupportFragmentManager();

    if (group_name.equals("notification")) {
      fragmentManager.beginTransaction()
          .replace(R.id.container, new NotificationFragment())
          .commit();
      group_intro.setVisibility(View.INVISIBLE);
    } else if (group_name.equals("at_me")) {
      fragmentManager
          .beginTransaction()
          .replace(R.id.container,
              InboxShareFragment.newInstance())
          .commit();
      group_intro.setVisibility(View.INVISIBLE);
    } else {
      fragmentManager
          .beginTransaction()
          .replace(R.id.container,
              ShareContentFragment.newInstance(group_name))
          .commit();
      group_intro.setVisibility(View.VISIBLE);
    }

  }

  @Override
  public void onAddGroupListener() {
    if (fragmentManager == null)
      fragmentManager = getSupportFragmentManager();
    fragmentManager
        .beginTransaction()
        .replace(R.id.container,
            new NewGroupFragment())
        .commit();

  }

  @Override
  public void onGetAllGroup(List<String> group_name_list) {
    // this.group_name_list = group_name_list;
  }


  @Override
  public void OnNewGroupFinish(String group_name) {
    mDrawerFragment.refresh();
    fragmentManager
        .beginTransaction()
        .replace(R.id.container,
            ShareContentFragment.newInstance(group_name))
        .commit();
    group_intro.setVisibility(View.VISIBLE);
  }

  @Override
  public void OnGetGroupId(String id) {
    now_group_id = id;
  }


  @Override
  public void OnGetGroupName(String name) {
    now_group_name = name;
    getSupportActionBar().setTitle(name);
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
                user = pUser;
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

  public Map<String, String> getHeader() {
    Map<String, String> headers = new HashMap<String, String>();
    UserBean currentUser = AccountManager.getInstance().getCurrentUser();
    if (currentUser != null && currentUser.getCookieHolder() != null) {
      currentUser.getCookieHolder().generateCookieString();
      headers.put("Cookie", currentUser.getCookieHolder().generateCookieString());
    }
    headers.put("Accept-Encoding", "gzip, deflate");
    headers
        .put(
            "User-Agent",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36");
    return headers;
  }


  public void executeRequest(Request request) {
    SApplication.getRequestManager().executeRequest(request, this);
  }


  private boolean checkShare() {
    checkClipboard();
    if (text_from_clipboard != null) {
      if (UrlFromString.pullLinks(text_from_clipboard) != null) {
        extract_url = UrlFromString.pullLinks(text_from_clipboard);
        // showMyDialog(Gravity.BOTTOM);
        return true;
      } else
        return false;
    } else
      return false;
  }

  /**
   * 往Clip中放入数据
   * 因为我share后会刷新activity，如果数据不清理，会导致一直弹窗
   */
  private void clearClipboard() {
    // 类型一:text
    clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    ClipData textCd = ClipData.newPlainText("kkk", "unique shr");
    clipboard.setPrimaryClip(textCd);
  }

  /**
   * 从Clip中取数据
   */
  private void checkClipboard() {
    clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    ClipData.Item item = null;

    // 无数据时直接返回
    if (!clipboard.hasPrimaryClip()) {
      Toast.makeText(getApplicationContext(), "剪贴板中无数据", Toast.LENGTH_SHORT).show();
      return;
    }

    // 如果是文本信息
    if (clipboard.getPrimaryClipDescription().hasMimeType(
        ClipDescription.MIMETYPE_TEXT_PLAIN)) {
      ClipData cdText = clipboard.getPrimaryClip();
      item = cdText.getItemAt(0);
      // 此处是TEXT文本信息
      if (item.getText() == null) {
        Toast.makeText(getApplicationContext(), "剪贴板中无内容", Toast.LENGTH_SHORT).show();
        // return;
      } else {
        Toast.makeText(getApplicationContext(), item.getText(), Toast.LENGTH_SHORT).show();
        text_from_clipboard = item.getText().toString();
      }
    }
  }


  public void exit_shr() {
    GsonRequest<GsonRequest.FormResult> group_share_url_request =
        new GsonRequest<>(Request.Method.GET,
            APIConstants.BASE_URL + "/logout",
            getHeader(), null,
            GsonRequest.FormResult.class,
            new Response.Listener<GsonRequest.FormResult>() {
              @Override
              public void onResponse(GsonRequest.FormResult result) {
                if (result.message.equals("success"))
                  Toast.makeText(activity, "exit success", Toast.LENGTH_LONG)
                      .show();
                Log.e("message", result.message);
                LocalConfig.setFirstLaunch(true);
                activity.finish();
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(group_share_url_request);
  }

  public void post_share_url(String group_name) {
    JSONArray mJSONArray = new JSONArray();
    // String url = "http://stackoverflow.com/questions/8126299/android-share-browser-url-to-app";
    String intro = dialog_intro_input.getText().toString();
    Map<String, String> params = new HashMap<>();
    params.put("url", extract_url);
    params.put("comment", intro);
    if (!group_name.equals("inbox_share"))
      params.put("groups", group_name);

    GsonRequest<GsonRequest.FormResult> group_share_url_request =
        new GsonRequest<>(Request.Method.POST,
            APIConstants.BASE_URL + "/share",
            getHeader(), params,
            GsonRequest.FormResult.class,
            new Response.Listener<GsonRequest.FormResult>() {
              @Override
              public void onResponse(GsonRequest.FormResult pGroup) {
                if (pGroup.message.equals("success"))
                  Toast.makeText(activity, "share a page successful", Toast.LENGTH_LONG)
                      .show();
                Log.e("message", pGroup.message);
                clearClipboard();
                Intent mIntent = new Intent(activity, MainActivity.class);
                startActivity(mIntent);
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(group_share_url_request);
  }

  // 这个注解一定要有,表示订阅了TestAction,并且方法的用 public 修饰的.方法名可以随意取,重点是参数,它是根据你的参数进行判断
  @Subscribe
  public void onChangeData(DataChangeAction dataChangeAction) {
    // 这里更新视图或者后台操作,从TestAction获取传递参数.
    if (dataChangeAction.getStr().equals(DataChangeAction.CHANGE_AVATAR)
        || dataChangeAction.getStr().equals(DataChangeAction.DELETE_GROUP)) {
      fragmentManager
          .beginTransaction()
          .replace(R.id.container,
              InboxShareFragment.newInstance())
          .commitAllowingStateLoss();
    }
  }

  @Override
  public void onDestroy() {
    BusProvider.getInstance().unregister(this);
    super.onDestroy();
  }

}
