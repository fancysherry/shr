package unique.fancysherry.shr.ui.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.fragment.DrawerFragment;
import unique.fancysherry.shr.ui.fragment.NewGroupFragment;
import unique.fancysherry.shr.ui.fragment.NotificationFragment;
import unique.fancysherry.shr.ui.fragment.ShareContentFragment;
import unique.fancysherry.shr.ui.widget.Dialog.DialogPlus;
import unique.fancysherry.shr.ui.widget.Dialog.Holder;
import unique.fancysherry.shr.ui.widget.Dialog.OnClickListener;
import unique.fancysherry.shr.ui.widget.Dialog.OnDismissListener;
import unique.fancysherry.shr.ui.widget.Dialog.ViewHolder;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.UrlFromString;
import unique.fancysherry.shr.util.UrlUtil;
import unique.fancysherry.shr.util.config.SApplication;


public class MainActivity extends BaseActivity
    implements DrawerFragment.NavigationDrawerCallbacks,
    NewGroupFragment.OnNewGroupListener, ShareContentFragment.OnGetGroupIdListener {

  public FragmentManager fragmentManager;

  /**
   * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
   */
  private DrawerFragment mDrawerFragment;

  private Activity activity;

  private String now_group_id;

  private String now_group_name;
  private String text_from_clipboard = null;

  private User user;
  private Handler handler;
  private Runnable runnable;
  private ClipboardManager clipboard = null;

  private EditText dialog_intro_input;

  private String extract_url;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activity = this;
    setContentView(R.layout.activity_main);
    initView();
    initializeToolbar();
    getSupportActionBar().setTitle("消息");
    mToolbar.setOnMenuItemClickListener(onMenuItemClick);


    // 默认加载第一个组的信息
    handler = new Handler();
    runnable = new Runnable() {
      @Override
      public void run() {
        fragmentManager
            .beginTransaction()
            .replace(R.id.container,
                ShareContentFragment.newInstance(user.groups.get(0).name))
            .commit();
      }
    };
  }

  @Override
  public void onResume() {
    super.onResume();
    checkShare();
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
        case R.id.action_edit:
          Intent mIntent = new Intent(activity, GroupActivity.class);
          Bundle mBundle = new Bundle();
          mBundle.putString("group_id", now_group_id);
          mBundle.putString("group_name", now_group_name);
          mIntent.putExtras(mBundle);
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
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }



  @Override
  protected void initView() {
    mDrawerFragment = (DrawerFragment)
        getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

    mDrawerFragment.setUp(
        R.id.navigation_drawer,
        (DrawerLayout) findViewById(R.id.drawerLayout));

    fragmentManager = getSupportFragmentManager();
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
    }
    else if (group_name.equals("at_me")) {
      fragmentManager
          .beginTransaction()
          .replace(R.id.container,
              ShareContentFragment.newInstance("at_me"))
          .commit();
    }
    else {
      fragmentManager
          .beginTransaction()
          .replace(R.id.container,
              ShareContentFragment.newInstance(group_name))
          .commit();
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
    LogUtil.e(currentUser.getCookieHolder().generateCookieString());
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


  private boolean checkShare()
  {
    checkClipboard();
    if (text_from_clipboard != null) {
      if (UrlFromString.pullLinks(text_from_clipboard) != null)
      {
        extract_url = UrlFromString.pullLinks(text_from_clipboard);
        showMyDialog(Gravity.BOTTOM);
        return true;
      } else
        return false;
    }
    else
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

    /**
     * 往ClipboardManager中可放的数据类型有三种:
     * 因为大家都知道,就算是电脑,Ctrl+c也不可能在同一时间里即
     * 从C盘剪贴,又从D般剪贴,所以小马只写一种简单的信息进去,
     * 另外两种写在注释中
     */
    /**
     *
     //类型二:URI
     * Uri copyUri = Uri.parse(CONTACTS + COPY_PATH + "/" + "XiaoMa");
     * ClipData clipUri = ClipData.newUri(getContentResolver(),"URI",copyUri);
     * clipboard.setPrimaryClip(clipUri);
     *
     */
    // //类型三:Intent
    // //试下在Intent剪贴时使用Bundle传值进去
    // clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
    // Intent appIntent = new Intent();
    // Bundle bundle = new Bundle();
    // bundle.putInt("xiaoma", 3344258);
    // bundle.putInt("yatou", 3344179);
    // appIntent.putExtra("XiaoMaGuo", bundle);
    // appIntent.setClass(ClipBoardDemoActivity.this, ReceiverClip.class);
    // ClipData clipIntent = ClipData.newIntent("Intent",appIntent);
    // clipboard.setPrimaryClip(clipIntent);
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

    // 如果是INTENT
    // } else if (clipboard.getPrimaryClipDescription().hasMimeType(
    // ClipDescription.MIMETYPE_TEXT_INTENT)) {
    // //此处是INTENT
    // item = clipboard.getPrimaryClip().getItemAt(0);
    // Intent intent = item.getIntent();
    // startActivity(intent);
    // //........
    //
    // //如果是URI
    // } else if (clipboard.getPrimaryClipDescription().hasMimeType(
    // ClipDescription.MIMETYPE_TEXT_URILIST)) {
    // //此处是URI内容
    // ContentResolver cr = getContentResolver();
    // ClipData cdUri = clipboard.getPrimaryClip();
    // item = cdUri.getItemAt(0);
    // Uri uri = item.getUri();
    // if(uri != null){
    // String mimeType = cr.getType(uri);
    // if (mimeType != null) {
    // if (mimeType.equals(MIME_TYPE_CONTACT)) {
    // Cursor pasteCursor = cr.query(uri, null, null, null, null);
    // if (pasteCursor != null) {
    // if (pasteCursor.moveToFirst()) {
    // //此处对数据进行操作就可以了,前提是有权限
    // }
    // }
    // pasteCursor.close();
    // }
    // }
    // }
    // }
  }

  public void showMyDialog(int gravity) {
    Holder holder = new ViewHolder(R.layout.dialog_shr_content);
    LayoutInflater mLayoutInflater = this.getLayoutInflater();
    View diaglog_view = mLayoutInflater.inflate(R.layout.dialog_shr_content, null);
    dialog_intro_input =
        (EditText) diaglog_view.findViewById(R.id.dialog_shr_content_intro);
    // tagGroup = (TagGroup) diaglog_view.findViewById(R.id.user_groups_tagGroup);
    // getUserData();

    OnClickListener clickListener = new OnClickListener() {
      @Override
      public void onClick(DialogPlus dialog, View view) {
        switch (view.getId()) {
          case R.id.dialog_shr_content_tagview1:
            if (checkShare())
              post_share_url("诶哟");
            else
              Toast.makeText(activity, "请复制包含url的有效链接", Toast.LENGTH_LONG);
            break;
          case R.id.dialog_shr_content_tagview2:
            if (checkShare())
              post_share_url("测试");
            else
              Toast.makeText(activity, "请复制包含url的有效链接", Toast.LENGTH_LONG);
            break;
          case R.id.dialog_shr_content_tagview3:
            if (checkShare())
              post_share_url("inbox_share");
            else
              Toast.makeText(activity, "请复制包含url的有效链接", Toast.LENGTH_LONG);
            break;

        }
        dialog.dismiss();
      }
    };

    OnDismissListener dismissListener = new OnDismissListener() {
      @Override
      public void onDismiss(DialogPlus dialog) {}
    };
    showOnlyContentDialog(holder, gravity, dismissListener, clickListener);
  }

  private void showOnlyContentDialog(Holder holder, int gravity,
      OnDismissListener dismissListener, OnClickListener clickListener
      ) {
    final DialogPlus dialog = DialogPlus.newDialog(activity)
        .setContentHolder(holder)
        .setGravity(gravity)
        .setOnDismissListener(dismissListener)
        .setCancelable(true)
        .setOnClickListener(clickListener)
        .create();
    dialog.show();
  }

  public void post_share_url(String group_name) {
    GsonRequest<GsonRequest.FormResult> group_share_url_request =
        new GsonRequest<>(Request.Method.POST,
            APIConstants.BASE_URL + "/share",
            getHeader(), getParams_share(group_name),
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

  public Map<String, String> getParams_share(String group_name) {
    // String url = "http://stackoverflow.com/questions/8126299/android-share-browser-url-to-app";
    String intro = dialog_intro_input.getText().toString();
    Map<String, String> params = new HashMap<>();
    params.put("url", extract_url);
    params.put("comment", intro);
    params.put("groups", group_name);
    return params;
  }

}
