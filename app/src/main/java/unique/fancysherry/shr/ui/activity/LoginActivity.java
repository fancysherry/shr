package unique.fancysherry.shr.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountBean;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.io.request.LoginRequest;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.LocalConfig;
import unique.fancysherry.shr.util.config.SApplication;

public class LoginActivity extends AppCompatActivity {
  @InjectView(R.id.login_button)
  Button login_button;
  @InjectView(R.id.login_username)
  EditText login_username;
  @InjectView(R.id.login_password)
  EditText login_password;
  private String return_message;
  private Context context;
  private LoginRequest<LoginRequest.FormResult> login_request;
  private String sessionid;
  private String username;
  private String password;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LogUtil.e("login_start");
    context = this;
    if (LocalConfig.isFirstLaunch()) {
      /* set it to be full screen */
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
          WindowManager.LayoutParams.FLAG_FULLSCREEN);
      setContentView(R.layout.activity_login);
      ButterKnife.inject(this);
      login_button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          username = login_username.getText().toString();
          password = login_password.getText().toString();
          if (username == null)
            Toast.makeText(context, "用户名不能为空", Toast.LENGTH_LONG).show();
          else if (password == null)
            Toast.makeText(context, "密码不能为空", Toast.LENGTH_LONG).show();
          else {
            start();
          }
        }
      });
    }
    else {
      AccountBean mAccountBean = AccountManager.getInstance().getCurrentUser().mAccountBean;
      username = mAccountBean.username;
      password = mAccountBean.pwd;
      start();
    }
  }

  private void start() {
    login_request =
        new LoginRequest<>(APIConstants.BASE_URL + "/login", null,
            getParams_login(), LoginRequest.FormResult.class,
            new Response.Listener<LoginRequest.FormResult>() {
              @Override
              public void onResponse(LoginRequest.FormResult result) {
                return_message = result.message;
                if (return_message.equals("success")) {
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
    sessionid = login_request.cookies;
    // 多账号
    // if (AccountManager.getInstance().getCurrentUser() == null) {
    AccountManager.getInstance().addAccount(new AccountBean(username, password));
    // }
    AccountManager.getInstance().getCurrentUser().getCookieHolder()
        .saveCookie(sessionid);
    LocalConfig.setFirstLaunch(false);
    Intent mIntent = new Intent(context, MainActivity.class);
    startActivity(mIntent);
    finish();
  }

  public Map<String, String> getParams_login()
  {
    // username = "longchen@hustunique.com";
    // password = "hustunique";
    Map<String, String> params = new HashMap<String, String>();
    params.put("email", username);
    params.put("password", password);
    return params;
  }

  public void executeRequest(Request request) {
    SApplication.getRequestManager().executeRequest(request, this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    ButterKnife.reset(this);
  }
}
