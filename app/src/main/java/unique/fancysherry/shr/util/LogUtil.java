package unique.fancysherry.shr.util;

import android.util.Log;


public class LogUtil {
  private static final String TAG = "shr";

  public static void e(String message) {
    Log.e(TAG, message);
  }

  public static void d(String message) {
    Log.d(TAG, message);
  }

}
