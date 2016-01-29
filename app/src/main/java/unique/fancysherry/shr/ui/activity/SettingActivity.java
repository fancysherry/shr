package unique.fancysherry.shr.ui.activity;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.LocalConfig;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

public class SettingActivity extends BaseActivity {
  @InjectView(R.id.toolbar_setting)
  Toolbar mToolbar;
  private Activity activity;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting);
    ButterKnife.inject(this);
    initializeToolbar(mToolbar);
    activity = this;
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

  @OnClick({R.id.setting_logout_layout, R.id.setting_use_introduce_layout,
      R.id.setting_version_informaton_layout, R.id.setting_develop_information_layout})
  public void click(View mView) {
    switch (mView.getId()) {
      case R.id.setting_logout_layout:
        logout();
        break;
      case R.id.setting_use_introduce_layout:
        break;
      case R.id.setting_version_informaton_layout:
        break;
      case R.id.setting_develop_information_layout:
        break;
    }
  }

  public void logout() {
    GsonRequest<GsonRequest.FormResult> logout_request =
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
    executeRequest(logout_request);
  }

}
