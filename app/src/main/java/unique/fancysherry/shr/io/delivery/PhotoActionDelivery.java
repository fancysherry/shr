package unique.fancysherry.shr.io.delivery;


import unique.fancysherry.shr.io.cache.AbstractMMBean;
import unique.fancysherry.shr.io.cache.BaseMMBean;
import unique.fancysherry.shr.io.Util.PhotoLoader;

import android.text.TextUtils;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * Created by suanmiao on 15/1/24.
 */
public class PhotoActionDelivery extends BaseCachePhotoActionDelivery {
  private PhotoLoader photo;
  protected static final String KEY_CONTENT_LENGTH = "Content-Length";

  public PhotoActionDelivery(PhotoLoader photo) {
    this.photo = photo;
  }

  @Override
  public Response<PhotoLoader> parseNetworkResponse(NetworkResponse response) {
    try {
      if (response instanceof BitmapNetworkResponse) {
        BitmapNetworkResponse bitmapNetworkResponse = (BitmapNetworkResponse) response;
        photo.setContent(new BaseMMBean(bitmapNetworkResponse.getResult()));
        return Response.success(photo, HttpHeaderParser.parseCacheHeaders(response));
      } else {
        /**
         * get content length
         */
        String contentLength = response.headers.get(KEY_CONTENT_LENGTH);
        if (!TextUtils.isEmpty(contentLength)) {
          photo.setContentLength(Integer.parseInt(contentLength));
        }
        response.headers.get(KEY_CONTENT_LENGTH);
        AbstractMMBean bean =
            getCacheManager().getBeanGenerator().constructMMBeanFromNetworkData(photo,response.data);
        getCacheManager().put(photo.getCacheKey(), bean, true);
        photo.setContent(bean);
        getCacheManager().put(photo.getCacheKey(), bean, true);
        return Response.success(photo, HttpHeaderParser.parseCacheHeaders(response));
      }
    } catch (Exception e) {
      return Response.error(new ParseError());
    }

  }
}
