package unique.fancysherry.shr.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountBean;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.io.request.LoginRequest;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.SApplication;

public class LoginActivity extends AppCompatActivity {
  private String return_message;
  private Context context;
  private String sessionid;
  private LoginRequest<LoginRequest.FormResult> login_request;

  private String username;
  private String password;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    LogUtil.e("login_start");
    context = this;

    start();

  }


  private void start()
  {

    login_request =
        new LoginRequest<>(APIConstants.BASE_URL+"/login", null,
            getParams_login(), LoginRequest.FormResult.class,
            new Response.Listener<LoginRequest.FormResult>() {
              @Override
              public void onResponse(LoginRequest.FormResult result) {
                return_message = result.message;
                if (return_message.equals("success"))
                {
                  LogUtil.e("login success");
                  loginSuccessfully(result);
                }

              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });

    executeRequest(login_request);

  }



  protected void loginSuccessfully(LoginRequest.FormResult model) {

    sessionid = login_request.cookies;
    if (AccountManager.getInstance().getCurrentUser() == null) {
      AccountManager.getInstance().addAccount(new AccountBean(username, password));
    }
    AccountManager.getInstance().getCurrentUser().getCookieHolder()
        .saveCookie(sessionid);


    Intent mIntent = new Intent(context, MainActivity.class);
    startActivity(mIntent);
  }


  public Map<String, String> getHeader()
  {
    Map<String, String> headers = new HashMap<String, String>();
    headers.put("", "");
    headers.put("", "");

    return headers;
  }


  public Map<String, String> getParams_login()
  {
    username = "123@123.com";
    password = "123";
    Map<String, String> params = new HashMap<String, String>();
    params.put("email", "123@123.com");
    params.put("password", "123");
    return params;
  }

  public void executeRequest(Request request) {
    SApplication.getRequestManager().executeRequest(request, this);
  }



}
