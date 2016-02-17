package unique.fancysherry.shr.io.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import unique.fancysherry.shr.io.model.Share;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.TextUtil;

/**
 * Created by fancysherry on 15-7-14.
 */
public class GsonRequest<T> extends Request<T> {
  private final Response.Listener<T> mListener;

  private Gson mGson;

  private Class<T> mClass;


  private Map<String, String> headers;
  private Map<String, String> params;

  public GsonRequest(int method, String url, Map<String, String> headers,
      Map<String, String> params, Class<T> clazz, Response.Listener<T> listener,
      Response.ErrorListener errorListener) {
    super(method, url, errorListener);
    mGson = new Gson();
    mClass = clazz;
    mListener = listener;
    this.headers=headers;
    this.params=params;
  }



  @Override
  protected Response<T> parseNetworkResponse(NetworkResponse response) {
    try {
      String jsonString = new String(response.data,
          HttpHeaderParser.parseCharset(response.headers));
      LogUtil.e(jsonString+ "--------------------------------   shr");
      return Response.success(mGson.fromJson(jsonString, mClass),
          HttpHeaderParser.parseCacheHeaders(response));
    } catch (UnsupportedEncodingException e) {
      return Response.error(new ParseError(e));
    }
  }

  @Override
  protected void deliverResponse(T response) {
    mListener.onResponse(response);
  }

  @Override
  public Map<String, String> getHeaders() throws AuthFailureError {
    if (headers != null) {
      return headers;
    } else {
      return super.getHeaders();
    }
  }

  /**
   *
   * @return params for POST request
   * @throws AuthFailureError
   */
  @Override
  protected Map<String, String> getParams() throws AuthFailureError {
    if (this.params != null) {
      return this.params;
    } else {
      return super.getParams();
    }
  }

  @Override
  public String getUrl() {
    if (getMethod() == Method.GET) {
      return TextUtil.getEncodedGETUrl(super.getUrl(), this.params);
    } else {
      return super.getUrl();
    }
  }


  public static class FormResult
  {
    public String message;
    public String cookie;
    public String key;
    public String reason;
    public List<String> share_id_list;
  }


}
