package unique.fancysherry.shr.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

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
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.ui.otto.BusProvider;
import unique.fancysherry.shr.ui.otto.DataChangeAction;
import unique.fancysherry.shr.ui.widget.ProgressWheel;
import unique.fancysherry.shr.util.LogUtil;

public class DeleteGroupActivity extends BaseActivity {

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
  @InjectView(R.id.delete_group_toolbar)
  Toolbar mToolbar;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activity = this;

    /**
     * otto 的bus只能在主线程启动
     */
    handler = new Handler();
    runnable = new Runnable() {
      @Override
      public void run() {
        DataChangeAction mDataChangeAction = new DataChangeAction();
        mDataChangeAction.setStr(DataChangeAction.DELETE_GROUP);
        BusProvider.getInstance().post(mDataChangeAction);
        delete_spinner.stopSpinning();
        Toast.makeText(activity, "该组已被解散", Toast.LENGTH_LONG).show();
        Intent mIntent = new Intent(activity, MainActivity.class);
        startActivity(mIntent);
        finish();
      }
    };

    setContentView(R.layout.activity_delete_group);
    ButterKnife.inject(this);
    Bundle mBundle = getIntent().getExtras();
    group_id = mBundle.getString("group_id");
    group_name = mBundle.getString("group_name");
    initializeToolbar(mToolbar);
    initData();
  }


  public void initData() {
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


  /****
   * mcxiaoke的volley版本有个bug，执行delete方法的参数会被清空，
   * 所以这里使用了最原始的请求方法，后续的版本可能会使用okhttp来替代
   */
  public void deleteGroup() {
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
          httpURLConnection
              .setRequestProperty(
                  "User-Agent",
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
          InputStream in = httpURLConnection.getInputStream();
          BufferedReader br = new BufferedReader(new InputStreamReader(in));
          String str = null;
          StringBuffer buffer = new StringBuffer();
          while ((str = br.readLine()) != null) {// BufferedReader特有功能，一次读取一行数据
            buffer.append(str);
          }
          in.close();
          br.close();
          LogUtil.e("message   " + buffer.toString());
          if (httpURLConnection.getResponseCode() == 200) {

            handler.postDelayed(runnable, 2000);
          }
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


}
