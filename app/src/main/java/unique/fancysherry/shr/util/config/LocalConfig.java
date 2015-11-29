package unique.fancysherry.shr.util.config;


import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by fancysherry on 15-7-02.
 */
public class LocalConfig {

  private static final int SHAREDPREF_OPEN_MODE = Context.MODE_MULTI_PROCESS;
  /**
   * app state ,include:
   * 1.is first launch?
   * 2.settings in setting page;
   */
  private static final String SHAREDPREF_APP_STATE = "app_state";

  private static final String KEY_FIRST_LAUNCH = "first_launch";

  /**
   * user config, include:
   * 1.user token
   * 2.personalized setting for user;
   */
  private static final String SHAREDPREF_USER_CONFIG = "user_config";

  private static final String KEY_USER_ACCOUNT = "user_account";
  private static final String KEY_USER_INDEX = "user_index";
  private static final String KEY_TRAFFIC_SAVE_MODE = "traffic_save_mode";


  private static SharedPreferences getAppStateSharedpref() {
    return SApplication.getAppContext().getSharedPreferences(SHAREDPREF_APP_STATE,
        SHAREDPREF_OPEN_MODE);
  }

  private static SharedPreferences getUserConfigSharedpref() {
    return SApplication.getAppContext().getSharedPreferences(SHAREDPREF_USER_CONFIG,
        SHAREDPREF_OPEN_MODE);
  }

  public static boolean setFirstLaunch(boolean firstLaunch) {
    SharedPreferences.Editor editor = getAppStateSharedpref().edit();
    editor.putBoolean(KEY_FIRST_LAUNCH, firstLaunch);
    return editor.commit();
  }

  public static boolean isFirstLaunch()
  {
    SharedPreferences userConfig = getUserConfigSharedpref();
    return userConfig.getBoolean(KEY_USER_ACCOUNT, true);
  }

  public static String getUserAccountString() {
    SharedPreferences userConfig = getUserConfigSharedpref();
    return userConfig.getString(KEY_USER_ACCOUNT, "");
  }

  public static boolean putUserAccountString(String content) {
    SharedPreferences.Editor editor = getUserConfigSharedpref().edit();
    editor.putString(KEY_USER_ACCOUNT, content);
    return editor.commit();
  }

  public static int getUserIndex() {
    SharedPreferences userConfig = getUserConfigSharedpref();
    return userConfig.getInt(KEY_USER_INDEX, -1);
  }

  public static boolean putUserIndex(int content) {
    SharedPreferences.Editor editor = getUserConfigSharedpref().edit();
    editor.putInt(KEY_USER_INDEX, content);
    return editor.commit();
  }

  public static boolean getTrafficSaveMode() {
    SharedPreferences userConfig = getUserConfigSharedpref();
    return userConfig.getBoolean(KEY_TRAFFIC_SAVE_MODE, false);
  }

  public static boolean putTrafficSaveMode(boolean switchOn) {
    SharedPreferences.Editor editor = getUserConfigSharedpref().edit();
    editor.putBoolean(KEY_TRAFFIC_SAVE_MODE, switchOn);
    return editor.commit();
  }

}
