package unique.fancysherry.shr.util;

import android.util.Log;


public class LogUtil {
  private static final String TAG = "shr";
  private static final String LOG_PREFIX = "shr";
  private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
  private static final int MAX_LOG_TAG_LENGTH = 23;

  public static String makeLogTag(String str) {
    if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
      return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
    }

    return LOG_PREFIX + str;
  }

  /**
   * Don't use this when obfuscating class names!
   */
  public static String makeLogTag(Class cls) {
    return makeLogTag(cls.getSimpleName());
  }



  public static void e(String message) {
    Log.e(TAG, message);
  }

  public static void d(String message) {
    Log.d(TAG, message);
  }

}
