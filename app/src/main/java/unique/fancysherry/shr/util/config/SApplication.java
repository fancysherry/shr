package unique.fancysherry.shr.util.config;


import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.DiskBasedCache;

import java.io.File;

import unique.fancysherry.shr.io.CommonNetwork;
import unique.fancysherry.shr.io.RequestManager;
import unique.fancysherry.shr.util.system.FileUtil;


/**
 * Created by suanmiao on 14-10-31.
 */
public class SApplication extends Application {
  public static Context context;

  private static RequestManager sRequestManager;

  public static final String appFolderName = "shr";

  @Override
  public void onCreate() {
    super.onCreate();
    init();
  }

  private void init() {
    context = getApplicationContext();
    sRequestManager = initRequestManager();
  }

  public static Context getAppContext() {
    return context;
  }


  private RequestManager initRequestManager() {
    RequestManager requestManager = new RequestManager(this, appFolderName);
    String diskHTTPCacheDir = FileUtil.getAppRootDirectory(appFolderName) +
            "/httpCache";
    RequestQueue requestQueue =
            new RequestQueue(new DiskBasedCache(new File(diskHTTPCacheDir)), new CommonNetwork());
    requestManager.setVolleyRequestQueue(requestQueue);
    return requestManager;
  }

  public static RequestManager getRequestManager() {
    return sRequestManager;
  }


}
