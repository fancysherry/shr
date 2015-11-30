package unique.fancysherry.shr.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.Share;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.fragment.BrowserFragment;
import unique.fancysherry.shr.ui.fragment.NotificationFragment;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.SApplication;

public class BrowserActivity extends AppCompatActivity {
  private String url;
  private String id;

  private ImageView content_back_bt;
  private ImageView gratitude_bt;
  private ImageView comment_bt;
  private Context context;

  private String share_type = APIConstants.SHARE_TYPE;// share or inbox share

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_browser);
    Bundle extras = getIntent().getExtras();
    id = extras.getString("id");
    share_type = extras.getString(APIConstants.TYPE);
    context = this;

    // content_back_bt = (ImageView) findViewById(R.id.webview_content_back_button);
    // gratitude_bt = (ImageView) findViewById(R.id.webview_content_gratitude_button);
    comment_bt = (ImageView) findViewById(R.id.webview_content_commment_button);

    comment_bt.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        LogUtil.e("turn to comment activity");
        Intent mIntent = new Intent(context, CommentActivity.class);
        mIntent.putExtra("share_id", id);
        startActivity(mIntent);
      }
    });


    initializeToolbar();

    if (share_type.equals(APIConstants.INBOX_SHARE_TYPE)) {

      GsonRequest<Share> inbox_share_request =
          new GsonRequest<>(Request.Method.GET, APIConstants.BASE_URL
              + "/inbox_share?inbox_share_id=" + id, getHeader(),
              null, Share.class,
              new Response.Listener<Share>() {
                @Override
                public void onResponse(Share share) {
                  FragmentManager fragmentManager = getSupportFragmentManager();
                  fragmentManager.beginTransaction()
                      .replace(R.id.webview_content, BrowserFragment.newInstance(share.url))
                      .commit();


                }
              }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError pVolleyError) {
                  LogUtil.e("response error " + pVolleyError);
                }
              });

      executeRequest(inbox_share_request);
    }
    else if (share_type.equals(APIConstants.SHARE_TYPE)) {
      GsonRequest<Share> share_request =
          new GsonRequest<>(Request.Method.GET, APIConstants.BASE_URL + "/share", getHeader(),
              getParams(), Share.class,
              new Response.Listener<Share>() {
                @Override
                public void onResponse(Share share) {
                  FragmentManager fragmentManager = getSupportFragmentManager();
                  fragmentManager.beginTransaction()
                      .replace(R.id.webview_content, BrowserFragment.newInstance(share.url))
                      .commit();


                }
              }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError pVolleyError) {
                  LogUtil.e("response error " + pVolleyError);
                }
              });

      executeRequest(share_request);
    }

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
    Toolbar mToolbar = (Toolbar) findViewById(R.id.browser_activity_toolbar);

    setSupportActionBar(mToolbar);
    getSupportActionBar().setTitle("");
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    mToolbar.setOnMenuItemClickListener(onMenuItemClick);

  }

  private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
      switch (menuItem.getItemId()) {
        case android.R.id.home:
          finish();
      }
      return true;
    }
  };

  public Map<String, String> getHeader()
  {
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


  public Map<String, String> getParams()
  {
    Map<String, String> params = new HashMap<String, String>();
    params.put("share_id", id);
    return params;
  }


  public void executeRequest(Request request) {
    SApplication.getRequestManager().executeRequest(request, this);
  }

}
