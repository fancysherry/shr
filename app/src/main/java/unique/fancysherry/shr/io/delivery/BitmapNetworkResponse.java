package unique.fancysherry.shr.io.delivery;

import java.util.Collections;

import android.graphics.Bitmap;

import com.android.volley.NetworkResponse;

/**
 * Created by suanmiao on 15/1/26.
 */
public class BitmapNetworkResponse extends NetworkResponse {
  private Bitmap result;

  public BitmapNetworkResponse(Bitmap result) {
    super(new byte[] {}, Collections.<String, String>emptyMap());
    this.result = result;
  }

  public Bitmap getResult() {
    return result;
  }
}
