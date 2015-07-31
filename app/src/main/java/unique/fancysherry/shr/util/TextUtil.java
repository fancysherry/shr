package unique.fancysherry.shr.util;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

import android.graphics.Paint;
import android.text.TextUtils;


public class TextUtil {

  private TextUtil() {}

  public static String removeEmptyCharacters(String text) {
    if (text != null) {
      text = text.replaceAll("\\s*", "");
    } else {
      text = "";
    }
    return text;
  }

  public static String parseUrl(String url) {
    if (TextUtils.isEmpty(url)) {
      url = "";
    } else {
      if (!url.startsWith("http")) {
        url = "http://" + url;
      }
    }
    return url;
  }

  public static float getTextWidth(Paint paint, String text, float textSize) {
    if (TextUtils.isEmpty(text)) {
      return 0;
    }
    paint.setTextSize(textSize);
    float[] widths = new float[text.length()];
    paint.getTextWidths(text, widths);
    float totalWidth = 0;
    for (int i = 0; i < widths.length; i++) {
      totalWidth += widths[i];
    }
    return totalWidth;
  }


  public static String getEncodedGETUrl(String url, Map<String, String> params)
  {
    if (params == null) {
      return url;
    }
    ArrayList<Map.Entry<String, String>> entryArrayList = new ArrayList<>();
    entryArrayList.addAll(params.entrySet());
    for (int i = 0; i < entryArrayList.size(); i++) {
      Map.Entry<String, String> entry = entryArrayList.get(i);
      String key = entry.getKey();
      String value = entry.getValue();
      try {
        value = URLEncoder.encode(value, "UTF-8");
      } catch (Exception e) {
        e.printStackTrace();
      }
      try {
        key = URLEncoder.encode(key, "UTF-8");
      } catch (Exception e) {
        e.printStackTrace();
      }
      if (i == 0) {
        url += ("?" + key + "=" + value);
      } else {
        url += ("&" + key + "=" + value);
      }
    }
    return url;
  }

}
