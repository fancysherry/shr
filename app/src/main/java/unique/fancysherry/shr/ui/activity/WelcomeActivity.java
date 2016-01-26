package unique.fancysherry.shr.ui.activity;

import java.util.HashMap;
import java.util.Map;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountBean;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.request.LoginRequest;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.LocalConfig;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

import com.android.volley.Response;
import com.android.volley.VolleyError;

public class WelcomeActivity extends BaseActivity {
  public Activity activity;
  public LoginRequest<LoginRequest.FormResult> login_request;
  public String username;
  public String password;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome);
    ButterKnife.inject(this);
    activity = this;
  }

  @OnClick({R.id.welcome_activity_text})
  public void click(View mView) {
    switch (mView.getId()) {
      case R.id.welcome_activity_text:
        AccountBean mAccountBean = AccountManager.getInstance().getCurrentUser().mAccountBean;
        username = mAccountBean.username;
        password = mAccountBean.pwd;
        start();
        break;
    }
  }


  private void start() {
    Map<String, String> params = new HashMap<String, String>();
    params.put("email", username);
    params.put("password", password);
    login_request =
        new LoginRequest<>(APIConstants.BASE_URL + "/login", null,
            params, LoginRequest.FormResult.class,
            new Response.Listener<LoginRequest.FormResult>() {
              @Override
              public void onResponse(LoginRequest.FormResult result) {
                if (result.message.equals("success")) {
                  LogUtil.e("login success");
                  loginSuccessfully(result);
                }
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("error " + pVolleyError);
              }
            });
    executeRequest(login_request);
  }

  protected void loginSuccessfully(LoginRequest.FormResult model) {
    String sessionid = login_request.cookies;
    // 多账号
    // if (AccountManager.getInstance().getCurrentUser() == null) {
    AccountManager.getInstance().addAccount(new AccountBean(username, password));
    // }
    AccountManager.getInstance().getCurrentUser().getCookieHolder()
        .saveCookie(sessionid);
    LocalConfig.setFirstLaunch(false);
    Intent intent_enter_mainactivity = new Intent(activity, MainActivity.class);
    startActivity(intent_enter_mainactivity);
    finish();
  }
}
