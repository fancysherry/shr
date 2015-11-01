package unique.fancysherry.shr.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.widget.Dialog.DialogPlus;
import unique.fancysherry.shr.ui.widget.Dialog.Holder;
import unique.fancysherry.shr.ui.widget.Dialog.OnClickListener;
import unique.fancysherry.shr.ui.widget.Dialog.OnDismissListener;
import unique.fancysherry.shr.ui.widget.Dialog.ViewHolder;
import unique.fancysherry.shr.ui.widget.TagGroup;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.SApplication;
import unique.fancysherry.shr.util.UrlFromString;

public class ShareUrlActivity extends AppCompatActivity {
  private Activity activity;
  private User mUser;
  private TagGroup tagGroup;
  private ArrayList<String> test_taggroup = new ArrayList<>();

  private Handler handler;
  private Runnable runnable;

  private EditText dialog_intro_input;
  private String extract_url;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    handler = new Handler();
    runnable = new Runnable() {
      @Override
      public void run() {
        // initData();
      }
    };


    activity = this;
    setContentView(R.layout.activity_share_url);
    initView();
    showMyDialog(Gravity.CENTER);
    // Get intent, action and MIME type
    Intent intent = getIntent();
    String action = intent.getAction();
    String type = intent.getType();

    if (Intent.ACTION_SEND.equals(action) && type != null) {
      if ("text/plain".equals(type)) {
        handleSendText(intent); // Handle text being sent
      }
    }
  }

  public void initView()
  {

  }

  public void initData()
  {
    // if (mUser.groups.size() <= 20) {
    // for (int i = 0; i < mUser.groups.size(); i++) {
    // test_taggroup.add(mUser.groups.get(i).name);
    // }
    // tagGroup.setTagsDailog(test_taggroup);
    // // tagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
    // // @Override
    // // public void onTagClick(String tag) {
    // // if (tag.equals("..."))
    // // tagGroup.setAllTags(test_taggroup);
    // // else if (tag.equals("<-"))
    // // tagGroup.setTags(test_taggroup);
    // // }
    // // });
    // }
    // else
    // {
    // Toast.makeText(this, "你创建的组超过了20个", Toast.LENGTH_LONG).show();
    // }
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
            post_share_url("诶哟");
            break;
          case R.id.dialog_shr_content_tagview2:
            post_share_url("测试");
            break;
          case R.id.dialog_shr_content_tagview3:
            post_share_url("inbox_share");
            break;

        }
        dialog.dismiss();
      }
    };


    // OnClickListener clickListener = new OnClickListener() {
    // @Override
    // public void onClick(DialogPlus dialog, View view) {
    // switch (view.getId()) {
    //
    // case R.id.user_groups_tagGroup:
    //
    // // post_share_url();
    // tagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
    // @Override
    // public void onTagClick(String tag) {
    // if (tag.equals("..."))
    // tagGroup.setAllTags(test_taggroup);
    // else if (tag.equals("<-"))
    // tagGroup.setTags(test_taggroup);
    // else
    // {
    // Toast.makeText(activity, "We're glad that you love it" + tag, Toast.LENGTH_LONG)
    // .show();
    // }
    // }
    // });
    // break;
    //
    // }
    // dialog.dismiss();
    // }
    // };

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

  public Map<String, String> getHeader() {
    Map<String, String> headers = new HashMap<String, String>();
    UserBean currentUser = AccountManager.getInstance().getCurrentUser();
    if (currentUser != null && currentUser.getCookieHolder() != null) {
      currentUser.getCookieHolder().generateCookieString();
      headers.put("Cookie", currentUser.getCookieHolder().generateCookieString());
    }
    // headers.put("Content-Type","multipart/form-data; boundary=----WebKitFormBoundaryB7M2bdU0wHyT6UkF");
    headers.put("Accept-Encoding", "gzip, deflate");
    headers
        .put(
            "User-Agent",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36");
    return headers;
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
    if (!group_name.equals("inbox_share"))
      params.put("groups", group_name);
    return params;
  }

  public void executeRequest(Request request) {
    SApplication.getRequestManager().executeRequest(request, this);
  }



}
