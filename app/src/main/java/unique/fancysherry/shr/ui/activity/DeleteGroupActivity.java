package unique.fancysherry.shr.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.DeleteRequest;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.widget.ProgressWheel;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.SApplication;

public class DeleteGroupActivity extends AppCompatActivity {

  private String group_id;
  private String group_name;
  private Handler handler;
  private Runnable runnable;
  private Activity activity;
  @InjectView(R.id.delete_group_information)
  TextView delete_group_information;
  @InjectView(R.id.delete_group_name)
  TextView delete_group_name;
  @InjectView(R.id.delete_group_result)
  TextView delete_group_result;
  @InjectView(R.id.delete_spinner)
  ProgressWheel delete_spinner;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activity = this;
    handler = new Handler();
    runnable = new Runnable() {
      @Override
      public void run() {
        delete_spinner.stopSpinning();
        Toast.makeText(activity, "该组已被解散", Toast.LENGTH_LONG).show();
        Intent mIntent=new Intent(activity,MainActivity.class);
        startActivity(mIntent);
        finish();
      }
    };

    setContentView(R.layout.activity_delete_group);
    ButterKnife.inject(this);
    Bundle mBundle = getIntent().getExtras();
    group_id = mBundle.getString("group_id");
    group_name = mBundle.getString("group_name");
    initializeToolbar();
    initData();
  }


  public void initData()
  {
    delete_spinner.setText("长按确认 解散组");
    delete_spinner.setTextSize(30);
    delete_spinner.setRimWidth(30);
    delete_spinner.setCircleColor(0xeeeeee);
    delete_spinner.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        delete_spinner.startSpinning();
        deleteGroup();
        return false;
      }
    });

    delete_group_information.setText("143天前" + group_name + "组被创建，现在这里已经聚集了133个小伙伴，积累了234条分享经验");
    delete_group_name.setText("你确定要删除" + group_name + "组吗？");
    delete_group_result.setText("解散后组员仍可以在个人主页查看他们在" + group_name + "组Shr过的分享的源网页，但评论和组员数据将全部删除");
  }

  protected void initializeToolbar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
    }
    Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

    setSupportActionBar(mToolbar);
    getSupportActionBar().setTitle("成员管理");
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
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

  private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
  {
    StringBuilder result = new StringBuilder();
    boolean first = true;

    for (NameValuePair pair : params)
    {
      if (first)
        first = false;
      else
        result.append("&");

      result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
      result.append("=");
      result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
    }

    return result.toString();
  }


  public void deleteGroup()  {
//    DeleteRequest<GsonRequest.FormResult> group_delete_request =
//        new DeleteRequest<>(Request.Method.DELETE,
//            APIConstants.BASE_URL + "/group",
//            getHeader(), null,
//            GsonRequest.FormResult.class,
//            new Response.Listener<GsonRequest.FormResult>() {
//              @Override
//              public void onResponse(GsonRequest.FormResult result) {
//                handler.postDelayed(runnable, 2000);
//              }
//            }, new Response.ErrorListener() {
//              @Override
//              public void onErrorResponse(VolleyError pVolleyError) {
//                LogUtil.e("response error " + pVolleyError);
//              }
//            });
//    executeRequest(group_delete_request);
    new Thread(new Runnable() {
      @Override
      public void run() {
        URL url = null;
        try {
          url = new URL(APIConstants.BASE_URL + "/group");
        } catch (MalformedURLException exception) {
          exception.printStackTrace();
        }
        HttpURLConnection httpURLConnection = null;
        try {
          httpURLConnection = (HttpURLConnection) url.openConnection();
          httpURLConnection.setRequestProperty("Content-Type",
                  "application/x-www-form-urlencoded");
          httpURLConnection.setRequestProperty("Cookie", getCookie());
          httpURLConnection.setRequestProperty("User-Agent",
                  "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36");
          httpURLConnection.setRequestMethod("DELETE");
          httpURLConnection.setDoInput(true);
          httpURLConnection.setDoOutput(true);

          List<NameValuePair> params = new ArrayList<NameValuePair>();
          params.add(new BasicNameValuePair("group_id", group_id));

          OutputStream os = httpURLConnection.getOutputStream();
          BufferedWriter writer = new BufferedWriter(
                  new OutputStreamWriter(os, "UTF-8"));
          writer.write(getQuery(params));
          writer.flush();
          writer.close();
          os.close();


          LogUtil.e(String.valueOf(httpURLConnection.getResponseCode()));
          InputStream in = httpURLConnection.getInputStream();
          BufferedReader br = new BufferedReader(new InputStreamReader(in));
          String str = null;
          StringBuffer buffer = new StringBuffer();
          while((str = br.readLine())!=null){//BufferedReader特有功能，一次读取一行数据
            buffer.append(str);
          }
          in.close();
          br.close();
          LogUtil.e("message   "+buffer.toString());
          if(httpURLConnection.getResponseCode()==200)
            handler.postDelayed(runnable,2000);
        } catch (IOException exception) {
          exception.printStackTrace();
        } finally {
          if (httpURLConnection != null) {
            httpURLConnection.disconnect();
          }
        }
      }
    }).start();
  }

  public String getCookie() {

    UserBean currentUser = AccountManager.getInstance().getCurrentUser();
    if (currentUser != null && currentUser.getCookieHolder() != null) {
      currentUser.getCookieHolder().generateCookieString();
    }
    return currentUser.getCookieHolder().generateCookieString();
  }

  public Map<String, String> getHeader() {
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

  public void executeRequest(Request request) {
    SApplication.getRequestManager().executeRequest(request, this);
  }
  public Map<String, String> getParams() {
    Map<String, String> params = new HashMap<>();
    Log.e("delete request",group_id);
    params.put("group_id", group_id);
    return params;
  }
//do as get http,put params after url

}
