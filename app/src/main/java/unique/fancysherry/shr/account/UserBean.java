package unique.fancysherry.shr.account;


import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;

import unique.fancysherry.shr.io.model.User;

public class UserBean {
  public AccountBean mAccountBean;

  private CookieHolder mCookieHolder;
  private String token;
  private User user_profile;

  public UserBean(AccountBean accountBean) {
    this.mAccountBean = accountBean;
    this.mCookieHolder = new CookieHolder();
  }

  public CookieHolder getCookieHolder() {
    return mCookieHolder;
  }

  public void setAccountBean(AccountBean mAccountBean) {
    this.mAccountBean = mAccountBean;
  }

  public AccountBean getAccountBean() {
    return mAccountBean;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  public void updateToken() {
    loginBackground(mAccountBean.username, mAccountBean.pwd);
  }

  private void loginBackground(final String username, final String passwordEncrypted) {

  }

  public User getUser_profile() {
    return user_profile;
  }

  public void setUser_profile(User pUser_profile) {
    user_profile = pUser_profile;
  }
}
