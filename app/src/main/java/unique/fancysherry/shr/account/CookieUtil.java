package unique.fancysherry.shr.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;

import android.text.TextUtils;

/**
 * Created by suanmiao on 15/3/3.
 */
public class CookieUtil {

  public static final String COOKIE_PREFIX = "Set-Cookie";

  public static List<BasicNameValuePair> getClippedHeader(Map<String, String> header) {
    List<BasicNameValuePair> result = new ArrayList<>();
    Set<String> keys = header.keySet();
    for (String key : keys) {
      if (!TextUtils.isEmpty(key) && key.contains(COOKIE_PREFIX)) {
        key = COOKIE_PREFIX;
      }
      result.add(new BasicNameValuePair(key, header.get(key)));
    }
    return result;
  }

  public static Map<String, String> getVolleyTypeHeader(Header[] headers) {
    Map<String, String> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    for (int i = 0; i < headers.length; i++) {
      String name = headers[i].getName();
      String value = headers[i].getValue();
      if (!TextUtils.isEmpty(name) && name.contains(CookieUtil.COOKIE_PREFIX)) {
        name += i;
      }
      result.put(name, value);
    }
    return result;
  }

}
