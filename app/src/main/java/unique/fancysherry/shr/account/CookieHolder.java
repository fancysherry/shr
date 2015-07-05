package unique.fancysherry.shr.account;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import unique.fancysherry.shr.util.LogUtil;

import android.text.TextUtils;


public class CookieHolder {

  public Map<String, String> currentCookie;

  public CookieHolder() {}

  public void saveCookie(Map<String, String> volleyTypeHeader) {
    Map<String, String> result = new HashMap<>();
    Set<String> keys = volleyTypeHeader.keySet();
    for (String key : keys) {
      if (!TextUtils.isEmpty(key) && key.contains(CookieUtil.COOKIE_PREFIX)) {
        result.put(key, volleyTypeHeader.get(key));
      }
    }
    this.currentCookie = result;
  }

  public String generateCookieString() {
    String result = "";
    for (Map.Entry<String, String> entry : currentCookie.entrySet()) {
      String cookie = entry.getValue();
      if (cookie.contains(";")) {
        String usefulPart = cookie.substring(0, cookie.indexOf(";")+1);
        result += usefulPart;
        result += " ";
      }
    }
    return result;
  }

  public void printCookie() {
    for (Map.Entry entry : currentCookie.entrySet()) {
      LogUtil.e("cookie " + entry.getKey() + "|" + entry.getValue());
    }
  }

}
