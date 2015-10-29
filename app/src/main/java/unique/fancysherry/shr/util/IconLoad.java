package unique.fancysherry.shr.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by fancysherry on 15-10-17.
 */
public class IconLoad {
  public static void load(ImageView imageview, String url_src)
  {
    try {
      URL url = new URL(url_src);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setDoInput(true);
      connection.connect();
      InputStream input = connection.getInputStream();
      BitmapFactory.Options options = new BitmapFactory.Options();
      Bitmap myBitmap = BitmapFactory.decodeStream(input, null, options);
      imageview.setImageBitmap(myBitmap);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
