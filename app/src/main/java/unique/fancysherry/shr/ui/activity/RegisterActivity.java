package unique.fancysherry.shr.ui.activity;

import java.util.HashMap;
import java.util.Map;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountBean;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.LocalConfig;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

public class RegisterActivity extends BaseActivity {
  private String username;
  private String password;
  private String email;
  private String verify_password;
  private Activity activity;
  @InjectView(R.id.register_email)
  EditText register_email;
  @InjectView(R.id.register_username)
  EditText register_username;
  @InjectView(R.id.register_password)
  EditText register_password;
  @InjectView(R.id.register_verify_password)
  EditText register_verify_password;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_register);
    ButterKnife.inject(this);
    activity = this;
  }

  @OnClick({R.id.register_activity_login_button, R.id.register_activity_register_button})
  public void click(View mView) {
    switch (mView.getId()) {
      case R.id.register_activity_login_button:
        Intent intent_login = new Intent(activity, LoginActivity.class);
        startActivity(intent_login);
        finish();
        break;
      case R.id.register_activity_register_button:
        username = register_username.getText().toString();
        password = register_password.getText().toString();
        email = register_email.getText().toString();
        verify_password = register_verify_password.getText().toString();
        if (email == null || email.equals(""))
          Toast.makeText(activity, "账户不能为空", Toast.LENGTH_SHORT).show();
        else if (username == null || username.equals(""))
          Toast.makeText(activity, "用户名不能为空", Toast.LENGTH_SHORT).show();
        else if (password == null || password.equals(""))
          Toast.makeText(activity, "密码不能为空", Toast.LENGTH_SHORT).show();
        else if (verify_password == null || !(verify_password.equals(password)))
          Toast.makeText(activity, "确认密码和输入的密码不相符", Toast.LENGTH_SHORT).show();
        start();
        break;
    }
  }

  private void start() {
    Map<String, String> params = new HashMap<String, String>();
    params.put("email", email);
    params.put("nickname", username);
    params.put("password", password);
    GsonRequest<GsonRequest.FormResult> register_request =
        new GsonRequest<>(Request.Method.POST, APIConstants.BASE_URL + "/register", null,
            params, GsonRequest.FormResult.class,
            new Response.Listener<GsonRequest.FormResult>() {
              @Override
              public void onResponse(GsonRequest.FormResult result) {
                if (result.message.equals("success")) {
                  LogUtil.e("register success");
                  registerSuccessfully();
                }
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("error " + pVolleyError);
              }
            });
    executeRequest(register_request);
  }

  protected void registerSuccessfully() {
    Toast.makeText(activity, "注册成功", Toast.LENGTH_SHORT).show();
    AccountManager.getInstance().addAccount(new AccountBean(email, password));
    LocalConfig.setFirstRegister(false);
    Intent intent_register_successful = new Intent(activity, WelcomeActivity.class);
    startActivity(intent_register_successful);
    finish();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    ButterKnife.reset(this);
  }

}
