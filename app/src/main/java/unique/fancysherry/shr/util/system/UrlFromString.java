package unique.fancysherry.shr.util.system;

import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fancysherry on 15-10-29.
 */
public class UrlFromString {

  public static String pullLinks(String text) {
    // ArrayList links = new ArrayList();

    String regex =
        "\\(?\\b(https://|http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(text);
      String urlStr=null;
    if(m.find()) {
        urlStr = m.group();
        if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
            urlStr = urlStr.substring(1, urlStr.length() - 1);
        }
    }
    Log.e("###########url", urlStr);
    return urlStr;
  }
}
