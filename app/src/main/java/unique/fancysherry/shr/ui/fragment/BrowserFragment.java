package unique.fancysherry.shr.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.Group;
import unique.fancysherry.shr.io.model.InboxShare;
import unique.fancysherry.shr.io.model.Share;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.activity.CommentActivity;
import unique.fancysherry.shr.ui.activity.MainActivity;
import unique.fancysherry.shr.ui.dialog.ShrDialog;
import unique.fancysherry.shr.ui.otto.BusProvider;
import unique.fancysherry.shr.ui.otto.ForwardUrlAction;
import unique.fancysherry.shr.util.LogUtil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.otto.Subscribe;

/**
 * Created by Dsnc on 6/28/14.
 */
public class BrowserFragment extends BaseFragment {

  public static final String KEY_URL = "key_url";
  public static final String SHARE_TYPE = "share_type";
  public static final String SHARE_ID = "share_id";

  private String url;
  private String share_type;
  private String share_id;
  private String html;
  private String group_id;// inboxshare to share

  private Handler handler;
  private Runnable runnable;
  private Runnable runnable_refresh_data;
  private Runnable runnable_thank;
  private Runnable runnable_get_group_id;
  private Runnable runnable_load_webview_data;
  private Share share;
  private User mUser;
  private ArrayList<String> test_taggroup = new ArrayList<>();
  private GsonRequest<GsonRequest.FormResult> group_inboxshare_url_request;
  @InjectView(R.id.webview)
  WebView mWebview;
  @InjectView(R.id.webview_top_layout)
  RelativeLayout webview_top_layout;

  @InjectView(R.id.webview_bottom_layout)
  RelativeLayout webview_bottom_layout;
  @InjectView(R.id.share_count)
  TextView share_count;
  @InjectView(R.id.like_count)
  TextView like_count;
  @InjectView(R.id.comment_count)
  TextView comment_count;
  @InjectView(R.id.webview_bottom_comment_button)
  ImageView webview_bottom_comment_button;
  @InjectView(R.id.webview_bottom_like_button)
  ImageView webview_bottom_like_button;
  @InjectView(R.id.webview_bottom_dismiss_button)
  ImageView webview_bottom_dismiss_button;
  @InjectView(R.id.webview_top_share_button)
  ImageView webview_top_share_button;
  @InjectView(R.id.webview_top_primary_button)
  ImageView webview_top_primary_button;


  public static BrowserFragment newInstance(String url, String share_type, String share_id) {
    BrowserFragment fragment = new BrowserFragment();
    Bundle args = new Bundle();
    args.putString(KEY_URL, url);
    args.putString(SHARE_TYPE, share_type);
    args.putString(SHARE_ID, share_id);
    fragment.setArguments(args);
    return fragment;
  }

  public BrowserFragment() {}

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    BusProvider.getInstance().register(this);
    if (getArguments() != null) {
      url = getArguments().getString(KEY_URL);
      share_type = getArguments().getString(SHARE_TYPE);
      share_id = getArguments().getString(SHARE_ID);
    }

    handler = new Handler();
    runnable_refresh_data = new Runnable() {
      @Override
      public void run() {
        like_count.setText(share.gratitude_sum);
        comment_count.setText(share.comment_sum);
        // if (html == null) {
        // html=share.content;
        // mWebview.loadData(html, "text/html", "UTF-8");
        // }
      }
    };

    runnable_get_group_id = new Runnable() {
      @Override
      public void run() {
        executeRequest(group_inboxshare_url_request);
      }
    };
    runnable = new Runnable() {
      @Override
      public void run() {
        start_dialog();
      }
    };
    runnable_load_webview_data = new Runnable() {
      @Override
      public void run() {
        if (html != null)
          mWebview.loadData(html, "text/html;charset=utf-8", null);
      }
    };

    runnable_thank = new Runnable() {
      @Override
      public void run() {
        if (webview_bottom_like_button.getTag().toString().equals("like"))
          webview_bottom_like_button.setTag("cancel like");
        else if (webview_bottom_like_button.getTag().toString().equals("cancel like"))
          webview_bottom_like_button.setTag("like");
      }
    };
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_browser, container, false);
    ButterKnife.inject(this, view);
    if (share_type.equals(APIConstants.INBOX_SHARE_TYPE)) {
      webview_bottom_layout.setVisibility(View.INVISIBLE);
    } else {
      webview_bottom_comment_button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          refreshData();
          Intent mIntent = new Intent(getActivity(), CommentActivity.class);
          mIntent.putExtra("share_id", share_id);
          startActivity(mIntent);
        }
      });
    }

    initWebView();
    refreshData();
    loadContentData();
    return view;
  }

  @OnClick({R.id.webview_top_primary_button, R.id.webview_top_share_button,
      R.id.webview_bottom_dismiss_button, R.id.webview_bottom_like_button})
  public void click(View mView) {
    switch (mView.getId()) {
      case R.id.webview_top_primary_button:
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
        break;
      case R.id.webview_top_share_button:
        getUserData();
        break;
      case R.id.webview_bottom_dismiss_button:
        getActivity().finish();
        break;
      case R.id.webview_bottom_like_button:
        if (webview_bottom_like_button.getTag().toString().equals("like"))
          thank_request();
        else if (webview_bottom_like_button.getTag().toString().equals("cancel like"))
          cancel_thank_request();
        refreshData();
        break;
    }
  }

  private void initWebView() {
    mWebview.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebview.canGoBack()) {
          mWebview.goBack();
          return true;
        }
        return false;
      }
    });

    mWebview.setScrollbarFadingEnabled(true);
    mWebview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
    mWebview.setMapTrackballToArrowKeys(false);

    WebSettings settings = mWebview.getSettings();
    settings.setJavaScriptEnabled(true);
    settings.setBuiltInZoomControls(true);
    settings.setDomStorageEnabled(true);


    mWebview.setWebViewClient(new MyWebViewClient());
    mWebview.setWebChromeClient(new MyWebChromeClient());

    // Runnable mRunnable = new Runnable() {
    // @Override
    // public void run() {
    // try {
    // URL mURL = new URL(url);
    // Readability readability = new Readability(mURL, 5000); // URL
    // readability.init();
    // html = readability.outerHtml();
    // LogUtil.e(html);
    // } catch (MalformedURLException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // handler.post(runnable_load_webview_data);
    // }
    // };
    // new Thread(mRunnable).start();


    // mWebview.loadUrl(url);
    // mWebview.loadDataWithBaseURL("file:///android_asset/", builder.toString(), "text/html",
    // "UTF-8", "");
  }

  public void refresh() {
    mWebview.reload();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.reset(this);
  }

  @Override
  public void onResume() {
    super.onResume();
    mWebview.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
    mWebview.onPause();
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

  public void getGroupId(String group_name) {
    GsonRequest<Group> group_id_request =
        new GsonRequest<>(Request.Method.GET,
            APIConstants.BASE_URL + "/group?group_name=" + group_name,
            getHeader(), null,
            Group.class,
            new Response.Listener<Group>() {
              @Override
              public void onResponse(Group pGroup) {
                group_id = pGroup.group_id;
                handler.post(runnable_get_group_id);
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(group_id_request);
  }

  public void refreshData() {
    GsonRequest<Share> share_request =
        new GsonRequest<>(Request.Method.GET, APIConstants.BASE_URL + "/share", getHeader(),
            getParams(), Share.class,
            new Response.Listener<Share>() {
              @Override
              public void onResponse(Share pshare) {
                share = pshare;
                handler.post(runnable_refresh_data);
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(share_request);
  }

  public void loadContentData() {
    if (share_type.equals(APIConstants.SHARE_TYPE)) {
      GsonRequest<Share> share_request =
          new GsonRequest<>(Request.Method.GET, APIConstants.BASE_URL + "/share", getHeader(),
              getParams(), Share.class,
              new Response.Listener<Share>() {
                @Override
                public void onResponse(Share pshare) {
                  html = pshare.content;
                  handler.post(runnable_load_webview_data);
                }
              }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError pVolleyError) {
                  LogUtil.e("response error " + pVolleyError);
                }
              });
      executeRequest(share_request);
    } else if (share_type.equals(APIConstants.INBOX_SHARE_TYPE)) {
      if (share_type.equals(APIConstants.INBOX_SHARE_TYPE)) {
        GsonRequest<InboxShare> inbox_share_request =
            new GsonRequest<>(Request.Method.GET, APIConstants.BASE_URL
                + "/inbox_share?inbox_share_id=" + share_id, getHeader(),
                null, InboxShare.class,
                new Response.Listener<InboxShare>() {
                  @Override
                  public void onResponse(InboxShare pshare) {
                    html = pshare.content;
                    handler.post(runnable_load_webview_data);
                  }
                }, new Response.ErrorListener() {
                  @Override
                  public void onErrorResponse(VolleyError pVolleyError) {
                    LogUtil.e("response error " + pVolleyError);
                  }
                });
        executeRequest(inbox_share_request);
      }
    }
  }

  public void thank_request() {
    GsonRequest<GsonRequest.FormResult> thank_request =
        new GsonRequest<>(Request.Method.POST, APIConstants.BASE_URL + "/gratitude", getHeader(),
            getParams(), GsonRequest.FormResult.class,
            new Response.Listener<GsonRequest.FormResult>() {
              @Override
              public void onResponse(GsonRequest.FormResult result) {
                if (result.message.equals("success"))
                  handler.post(runnable_thank);
                else if (result.reason != null)
                  Toast.makeText(getActivity(), result.reason, Toast.LENGTH_LONG).show();
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(thank_request);
  }

  public void cancel_thank_request() {
    GsonRequest<GsonRequest.FormResult> cancel_thank_request =
        new GsonRequest<>(Request.Method.DELETE, APIConstants.BASE_URL + "/gratitude", getHeader(),
            getParams(), GsonRequest.FormResult.class,
            new Response.Listener<GsonRequest.FormResult>() {
              @Override
              public void onResponse(GsonRequest.FormResult result) {
                if (result.message.equals("success"))
                  handler.post(runnable_thank);
                else if (result.reason != null)
                  Toast.makeText(getActivity(), result.reason, Toast.LENGTH_LONG).show();
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(cancel_thank_request);
  }

  public void start_dialog() {
    // LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
    // View diaglog_view = mLayoutInflater.inflate(R.layout.dialog_shr_content_test, null);
    // tagGroup = (TagGroup) diaglog_view.findViewById(R.id.user_groups_tagGroup);
    for (int i = 0; i < mUser.groups.size(); i++) {
      test_taggroup.add(mUser.groups.get(i).name);
      LogUtil.e("test");
    }
    /**
     * 为了不重复显示dialog，在显示对话框之前移除正在显示的对话框。
     */
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    Fragment fragment = getFragmentManager().findFragmentByTag("ForwardShrDialog");
    if (null != fragment) {
      ft.remove(fragment);
    }
    ShrDialog dialogFragment = ShrDialog.newInstance(test_taggroup, APIConstants.SHARE_FORWARD);
    dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ShrDialog);
    dialogFragment.show(ft, "ForwardShrDialog");
  }

  public void post_share_url(String group_name, String comment) {
    if (share_type.equals(APIConstants.SHARE_TYPE)) {
      GsonRequest<GsonRequest.FormResult> group_share_url_request =
          new GsonRequest<>(Request.Method.POST,
              APIConstants.BASE_URL + "/share/forward",
              getHeader(), getParams_share(group_name, comment),
              GsonRequest.FormResult.class,
              new Response.Listener<GsonRequest.FormResult>() {
                @Override
                public void onResponse(GsonRequest.FormResult result) {
                  if (result.message.equals("success")) {
                    Toast.makeText(getActivity(), result.message, Toast.LENGTH_LONG).show();
                    Intent mIntent = new Intent(getActivity(), MainActivity.class);
                    startActivity(mIntent);
                  }
                }
              }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError pVolleyError) {
                  LogUtil.e("response error " + pVolleyError);
                }
              });
      executeRequest(group_share_url_request);
    } else if (share_type.equals(APIConstants.INBOX_SHARE_TYPE)) {
      if (group_name.equals("@me")) {
        Toast.makeText(getActivity(), "shr已经在inboxshare中", Toast.LENGTH_SHORT).show();
      } else {
        group_inboxshare_url_request =
            new GsonRequest<>(Request.Method.PUT,
                APIConstants.BASE_URL + "/inbox_share",
                getHeader(), getParams_inboxshare(group_name, comment),
                GsonRequest.FormResult.class,
                new Response.Listener<GsonRequest.FormResult>() {
                  @Override
                  public void onResponse(GsonRequest.FormResult result) {
                    if (result.message.equals("success")) {
                      Toast.makeText(getActivity(), result.message, Toast.LENGTH_LONG).show();
                      Intent mIntent = new Intent(getActivity(), MainActivity.class);
                      startActivity(mIntent);
                    }
                  }
                }, new Response.ErrorListener() {
                  @Override
                  public void onErrorResponse(VolleyError pVolleyError) {
                    LogUtil.e("response error " + pVolleyError);
                  }
                });
        getGroupId(group_name);
        // executeRequest(group_inboxshare_url_request);
      }
    }
  }

  public Map<String, String> getParams_share(String group_name, String comment) {
    // String url = "http://stackoverflow.com/questions/8126299/android-share-browser-url-to-app";
    Map<String, String> params = new HashMap<>();
    params.put("share_id", share_id);
    if (comment != null) {
      params.put("comment", comment);
    }
    if (!group_name.equals("@me"))
      params.put("groups", group_name);
    return params;
  }

  public Map<String, String> getParams_inboxshare(String group_name, String comment) {
    // String url = "http://stackoverflow.com/questions/8126299/android-share-browser-url-to-app";
    Map<String, String> params = new HashMap<>();
    params.put("inbox_share_id", share_id);
    params.put("group_id", group_id);
    if (comment != null) {
      params.put("comment", comment);
    }
    return params;
  }

  public Map<String, String> getParams() {
    Map<String, String> params = new HashMap<String, String>();
    params.put("share_id", share_id);
    return params;
  }

  @Subscribe
  public void onShareGroupName(ForwardUrlAction forwardUrlAction) {
    // 这里更新视图或者后台操作,从TestAction获取传递参数.
    if (forwardUrlAction.getGroup_name() != null) {
      post_share_url(forwardUrlAction.getGroup_name(), forwardUrlAction.getComment());
    }
  }

  @Override
  public void onDestroy() {
    BusProvider.getInstance().unregister(this);
    super.onDestroy();
  }


  private class MyWebViewClient extends WebViewClient {

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
      super.onPageStarted(view, url, favicon);
      if (null == getActivity()) {
        return;
      }
      // ((BrowserActivity) getActivity()).showProgressBar();
    }

    @Override
    public void onPageFinished(WebView view, String url) {
      super.onPageFinished(view, url);
      if (null == getActivity()) {
        return;
      }
      // ((BrowserActivity) getActivity()).hideProgressBar();
    }
  }

  private class MyWebChromeClient extends WebChromeClient {
    @Override
    public void onReceivedTitle(WebView view, String sTitle) {
      super.onReceivedTitle(view, sTitle);
      if (sTitle != null && sTitle.length() > 0) {
        if (getActivity() == null) {
          return;
        }
        if (!TextUtils.isEmpty(view.getTitle())) {
          // ((BrowserActivity) getActivity()).setToolTitle(view.getTitle());
        }
      }
    }
  }
}
