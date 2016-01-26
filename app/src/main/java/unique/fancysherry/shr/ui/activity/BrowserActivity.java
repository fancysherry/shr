package unique.fancysherry.shr.ui.activity;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.Share;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.fragment.BrowserFragment;
import unique.fancysherry.shr.util.LogUtil;

public class BrowserActivity extends BaseActivity {
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
                      .replace(R.id.webview_content,
                          BrowserFragment.newInstance(share.url, APIConstants.INBOX_SHARE_TYPE, id,share.title))
                      .commit();


                }
              }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError pVolleyError) {
                  LogUtil.e("response error " + pVolleyError);
                }
              });
      executeRequest(inbox_share_request);
    } else if (share_type.equals(APIConstants.SHARE_TYPE)) {
      Map<String, String> params = new HashMap<String, String>();
      params.put("share_id", id);
      GsonRequest<Share> share_request =
          new GsonRequest<>(Request.Method.GET, APIConstants.BASE_URL + "/share", getHeader(),
              params, Share.class,
              new Response.Listener<Share>() {
                @Override
                public void onResponse(Share share) {
                  FragmentManager fragmentManager = getSupportFragmentManager();
                  fragmentManager.beginTransaction()
                      .replace(R.id.webview_content,
                          BrowserFragment.newInstance(share.url, APIConstants.SHARE_TYPE, id,share.title))
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


}
