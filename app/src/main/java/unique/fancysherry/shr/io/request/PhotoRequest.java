package unique.fancysherry.shr.io.request;

import com.android.volley.Request;
import com.android.volley.Response;

import unique.fancysherry.shr.io.IVolleyActionDelivery;
import unique.fancysherry.shr.io.delivery.PhotoActionDelivery;
import unique.fancysherry.shr.io.Util.PhotoLoader;

/**
 * Created by fancysherry on 15-7-13.
 */
public class PhotoRequest<T> extends BaseRequest<T> {

  private PhotoActionDelivery mPhotoActionDelivery;
  private PhotoLoader photoLoader;


  public PhotoRequest(PhotoLoader pLoader, String url, Response.ErrorListener listener) {
    super(Request.Method.GET, url, listener);
    this.photoLoader = pLoader;
    this.mPhotoActionDelivery = new PhotoActionDelivery(photoLoader);
  }


  public IVolleyActionDelivery<PhotoLoader> getActionDelivery() {
    return mPhotoActionDelivery;
  }
}
