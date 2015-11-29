package unique.fancysherry.shr.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by fancysherry on 15-10-17.
 */
public class IconLoad {
  public static Bitmap myBitmap = null;

  public static void load(ImageView imageView, final String url_src)
  {
    // new Thread(new Runnable() {
    // @Override
    // public void run() {
    // try {
    // URL url = new URL(url_src);
    // HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    // connection.setDoInput(true);
    // connection.connect();
    // InputStream input = connection.getInputStream();
    // BitmapFactory.Options options = new BitmapFactory.Options();
    // myBitmap = BitmapFactory.decodeStream(input, null, options);
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }
    // }).start();

    new Thread(new Runnable() {
      @Override
      public void run() {
        OkHttpClient client = new OkHttpClient();
        Request mRequest = new Request.Builder().url(url_src).build();
        try {
          Response response = client.newCall(mRequest).execute();
          myBitmap = BitmapFactory.decodeStream(response.body().byteStream());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }).start();


    while (true)
    {
      if (myBitmap != null)
      {
        imageView.setImageBitmap(myBitmap);
        break;
      }
    }

  }
}
