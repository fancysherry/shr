package unique.fancysherry.shr.ui.fragment;

import java.util.HashMap;
import java.util.Map;

import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.ui.otto.BusProvider;
import unique.fancysherry.shr.util.config.SApplication;

import android.support.v4.app.Fragment;

import com.android.volley.Request;

/**
 * Created by fancysherry on 1/25/16.
 */
public class BaseFragment extends Fragment {
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
}
