package unique.fancysherry.shr.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import unique.fancysherry.shr.io.model.Share;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.fragment.BrowserFragment;
import unique.fancysherry.shr.ui.fragment.NotificationFragment;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.SApplication;

public class BrowserActivity extends ActionBarActivity {
  private String url;
  private String id;

  private ImageView content_back_bt;
  private ImageView gratitude_bt;
  private ImageView comment_bt;
  private Context context;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_browser);
    Bundle extras = getIntent().getExtras();
    id = extras.getString("id");
    context = this;

    content_back_bt = (ImageView) findViewById(R.id.webview_content_back_button);
    gratitude_bt = (ImageView) findViewById(R.id.webview_content_gratitude_button);
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



    GsonRequest<Share> share_request =
        new GsonRequest<>(Request.Method.GET, "http://104.236.46.64:8888/share", getHeader(),
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
