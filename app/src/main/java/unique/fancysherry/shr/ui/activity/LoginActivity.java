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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.android.volley.Response;
import com.android.volley.VolleyError;

public class LoginActivity extends BaseActivity {
  @InjectView(R.id.login_activity_login_button)
  Button login_button;
  @InjectView(R.id.login_username)
  EditText login_username;
  @InjectView(R.id.login_password)
  EditText login_password;
  private Context context;
  private LoginRequest<LoginRequest.FormResult> login_request;
  private String username;
  private String password;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LogUtil.e("login_start");
    context = this;
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_login);
    ButterKnife.inject(this);
    if (LocalConfig.isFirstLaunch()&&(!LocalConfig.isFirstRegister())) {
      AccountBean mAccountBean = AccountManager.getInstance().getCurrentUser().mAccountBean;
      username = mAccountBean.username;
      login_username.setText(username);
    }
  }

  @OnClick({R.id.login_activity_login_button, R.id.login_activity_register_button,
      R.id.login_activity_forget_password_button})
  public void click(View mView) {
    switch (mView.getId()) {
      case R.id.login_activity_login_button:
        username = login_username.getText().toString();
        password = login_password.getText().toString();
        if (username == null || username.equals(""))
          Toast.makeText(context, "用户名不能为空", Toast.LENGTH_SHORT).show();
        else if (password == null || password.equals(""))
          Toast.makeText(context, "密码不能为空", Toast.LENGTH_SHORT).show();
        else
          start();
        break;
      case R.id.login_activity_register_button:
        Intent intent_register = new Intent(context, RegisterActivity.class);
        startActivity(intent_register);
        finish();
        break;
      case R.id.login_activity_forget_password_button:
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
                  runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      loginSuccessfully();
                    }
                  });
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

  protected void loginSuccessfully() {
    AccountManager.getInstance()
        .addAccount(new AccountBean(username, password));
    AccountManager.getInstance().getCurrentUser().getCookieHolder()
        .saveCookie(login_request.cookies);
    LocalConfig.setFirstLaunch(false);
    Intent mIntent = new Intent(context, MainActivity.class);
    startActivity(mIntent);
    finish();

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    ButterKnife.reset(this);
  }
}
