package unique.fancysherry.shr.io;

import android.graphics.Bitmap;
import android.net.http.AndroidHttpClient;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpClientStack;

import java.io.IOException;

import unique.fancysherry.shr.io.cache.AbstractMMBean;
import unique.fancysherry.shr.io.cache.BaseMMBean;
import unique.fancysherry.shr.io.cache.CacheManager;
import unique.fancysherry.shr.io.request.BaseRequest;
import unique.fancysherry.shr.util.config.SApplication;

/**
 * Created by fancysherry on 15-7-11.
 */
public class CommonNetwork extends BasicNetwork {

  static String userAgent = "volley/0";

  public CommonNetwork() {
    super(new HttpClientStack(AndroidHttpClient.newInstance(userAgent))); // deprecated
  }


  @Override
  public NetworkResponse performRequest(Request<?> request) throws VolleyError {
    if (request instanceof BaseRequest) {
      BaseRequest photoFakeVolleyRequest = (BaseRequest) request;
      // Photo.Option loadOption = photoFakeVolleyRequest.getPhoto().getLoadOption();
      // if (loadOption == null) {
      // loadOption = new Photo.Option();
      // }
      // if (loadOption.loadSource == Photo.LoadSource.ONLY_FROM_CACHE
      // || loadOption.loadSource == Photo.LoadSource.BOTH) {
      // Bitmap cachedImage = getImageFromCache(photoFakeVolleyRequest);
      // if (cachedImage != null
      // || loadOption.loadSource == Photo.LoadSource.ONLY_FROM_CACHE) {
      // return new BitmapNetworkResponse(cachedImage);
      // }
      // }
    }
    return super.performRequest(request);
  }

  private Bitmap getImageFromCache(BaseRequest pBaseRequest) {
    try {
      CacheManager mCacheManager = SApplication.getRequestManager().getCacheManager();
      AbstractMMBean bean = mCacheManager.get(pBaseRequest.getPhoto().getCacheKey());
      if (bean != null && bean.getDataType() == AbstractMMBean.TYPE_BITMAP) {
        return ((BaseMMBean) bean).getDataBitmap();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }



}
