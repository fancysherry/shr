package unique.fancysherry.shr.ui.activity;

import java.util.HashMap;
import java.util.Map;

import unique.fancysherry.shr.account.AccountBean;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.request.LoginRequest;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.LocalConfig;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Response;
import com.android.volley.VolleyError;

public class LauncherActivity extends BaseActivity {
  public String username;
  public String password;
  private LoginRequest<LoginRequest.FormResult> login_request;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (LocalConfig.isFirstRegister()) {
      Intent intent_register = new Intent(this, RegisterActivity.class);
      startActivity(intent_register);
      finish();
    } else if (LocalConfig.isFirstLaunch()) {
      Intent intent_login = new Intent(this, LoginActivity.class);
      startActivity(intent_login);
      finish();
    } else {
      AccountBean mAccountBean = AccountManager.getInstance().getCurrentUser().mAccountBean;
      username = mAccountBean.username;
      password = mAccountBean.pwd;
      start();
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
    Intent mIntent = new Intent(this, MainActivity.class);
    startActivity(mIntent);
    finish();
  }

}
