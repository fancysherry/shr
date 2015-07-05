package unique.fancysherry.shr.util.config;


import android.app.Application;
import android.content.Context;

import java.io.File;



/**
 * Created by suanmiao on 14-10-31.
 */
public class SApplication extends Application {
  public static Context context;

  public static final String appFolderName = "shr";

  @Override
  public void onCreate() {
    super.onCreate();
    init();
  }

  private void init() {
    context = getApplicationContext();
  }

  public static Context getAppContext() {
    return context;
  }

}
