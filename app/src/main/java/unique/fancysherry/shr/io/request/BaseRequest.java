package unique.fancysherry.shr.io.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.Map;

import unique.fancysherry.shr.io.IVolleyActionDelivery;
import unique.fancysherry.shr.io.Util.PhotoLoader;

/**
 * Created by fancysherry on 15-7-11.
 */
public class BaseRequest<T> extends Request<T> {
  private IVolleyActionDelivery<T> volleyActionDelivery;
  private Map<String, String> headers;
  private Map<String, String> params;
  private PhotoLoader mPhotoLoader;



  public BaseRequest(int method, String url, Response.ErrorListener listener) {
    super(method, url, listener);
  }

  @Override
  protected Response<T> parseNetworkResponse(NetworkResponse pNetworkResponse) {
    return volleyActionDelivery.parseNetworkResponse(pNetworkResponse);
  }

  @Override
  protected void deliverResponse(T pT) {

  }

  @Override
  public void deliverError(VolleyError error) {
    super.deliverError(error);
  }


  public PhotoLoader getPhoto() {
    return mPhotoLoader;
  }
}
