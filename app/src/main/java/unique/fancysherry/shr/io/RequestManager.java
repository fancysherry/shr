package unique.fancysherry.shr.io;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.DiskBasedCache;

import java.io.File;

import unique.fancysherry.shr.io.cache.CacheManager;
import unique.fancysherry.shr.io.request.BaseRequest;
import unique.fancysherry.shr.io.request.PhotoRequest;
import unique.fancysherry.shr.util.config.SApplication;
import unique.fancysherry.shr.util.system.FileUtil;

/**
 * Created by fancysherry on 15-7-11.
 */
public class RequestManager {

  private CacheManager cacheManager;
  private String diskBitmapCacheDir;
  private String diskHTTPCacheDir;


  private RequestQueue requestQueue; // volley 的请求队列
  private static final String TAG_PREFIX = "/";



  public RequestManager(Context context, String appFolderName) {
    initCommon(appFolderName);
    initVolley(context);
  }


  private void initCommon(String appFolderName) {
    diskBitmapCacheDir = FileUtil.getAppRootDirectory(appFolderName) +
        "/cache";
    diskHTTPCacheDir = FileUtil.getAppRootDirectory(appFolderName) +
        "/httpCache";

    cacheManager =
        new CacheManager(diskBitmapCacheDir,
            SApplication.getAppContext());
  }


  private void initVolley(Context context) {
    requestQueue =
        new RequestQueue(new DiskBasedCache(new File(diskHTTPCacheDir)), new CommonNetwork());
    requestQueue.start();
  }

  public void setVolleyRequestQueue(RequestQueue requestQueue) {
    if (this.requestQueue != null) {
      this.requestQueue.stop();
    }
    this.requestQueue = requestQueue;
    requestQueue.start();
  }


  public CacheManager getCacheManager()
  {
    return cacheManager;
  }


  /*****
   * execute request
   */


  public <T> void executeRequest(Request<T> request, Object tag)
  {
    if (request instanceof PhotoRequest)
    {
      requestQueue.add(request);
    }

    else
    {
      requestQueue.add(request);
    }
  }

  private RequestFinishListener mRequestFinishListener = new RequestFinishListener() {
    @Override
    public void onFinish(String hashTag) {
      // runningRequest.remove(hashTag);
    }
  };


  public interface RequestFinishListener {
    public void onFinish(String hashTag);
  }

  public void cancelRequest(Object tag)
  {

  }


}
