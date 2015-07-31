package unique.fancysherry.shr.account;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import unique.fancysherry.shr.util.LogUtil;

import android.text.TextUtils;


public class CookieHolder {

//  public Map<String, String> currentCookie;
  
  private String sessionid;

  public CookieHolder() {}

  public void saveCookie(String sessionid) {
//    Map<String, String> result = new HashMap<>();
//    Set<String> keys = volleyTypeHeader.keySet();
//    for (String key : keys) {
//      if (!TextUtils.isEmpty(key) && key.contains(CookieUtil.COOKIE_PREFIX)) {
//        result.put(key, volleyTypeHeader.get(key));
//      }
//    }
//    this.currentCookie = result;
    this.sessionid=sessionid;
  }

  public String generateCookieString() {
//    String result = "";
//    for (Map.Entry<String, String> entry : currentCookie.entrySet()) {
//      String cookie = entry.getValue();
//      if (cookie.contains(";")) {
//        String usefulPart = cookie.substring(0, cookie.indexOf(";")+1);
//        result += usefulPart;
//        result += " ";
//      }
//    }
    return sessionid;
  }

  public void printCookie() {
      LogUtil.e("cookie " +sessionid);
  }

}
