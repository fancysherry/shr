package unique.fancysherry.shr.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.ButterKnife;
import butterknife.InjectView;
import unique.fancysherry.shr.R;
import unique.fancysherry.shr.ui.activity.BrowserActivity;

/**
 * Created by Dsnc on 6/28/14.
 */
public class BrowserFragment extends Fragment {

  public static final String KEY_URL = "key_url";

  private String url;

  @InjectView(R.id.webview)
  WebView mWebview;

  public static BrowserFragment newInstance(String url) {
    BrowserFragment fragment = new BrowserFragment();
    Bundle args = new Bundle();
    args.putString(KEY_URL, url);
    fragment.setArguments(args);
    return fragment;
  }

  public BrowserFragment() {}

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      url = getArguments().getString(KEY_URL);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_browser, container, false);
    ButterKnife.inject(this, view);

    initWebView();

    return view;
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

    StringBuilder builder = new StringBuilder("<html>");
    builder.append("<head>");
    builder.append("<link rel=stylesheet href='css/style.css'>");
    builder.append("</head>");
    builder.append("");
    builder.append("</html>");


    mWebview.setScrollbarFadingEnabled(true);
    mWebview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
    mWebview.setMapTrackballToArrowKeys(false);

    WebSettings settings = mWebview.getSettings();
    settings.setJavaScriptEnabled(true);
    settings.setBuiltInZoomControls(true);
    settings.setDomStorageEnabled(true);


    mWebview.setWebViewClient(new MyWebViewClient());
    mWebview.setWebChromeClient(new MyWebChromeClient());

     mWebview.loadUrl(url);
//    mWebview.loadDataWithBaseURL("file:///android_asset/", builder.toString(), "text/html",
//        "UTF-8", "");
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
