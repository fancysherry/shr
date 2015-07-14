package unique.fancysherry.shr.util.system;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.IOUtils;

public class FileUtil {
  public static final String FOLDER_NAME = "shr";

  public static String getFilePath(String fileName) {
    String folderPath =
        Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + FOLDER_NAME;
    File folder = new File(folderPath);
    try {
      if (!folder.exists()) {
        folder.mkdir();
      }
      return folderPath + "/" + fileName;
    } catch (Exception e) {
      Log.e("create folder failed", "" + e);
      e.printStackTrace();
    }
    return "";
  }


  /**
   * Get the external storage path of the device
   *
   * @return The external storage path of the device.
   */
  public static String getAppRootDirectory(String appFolderName) {
    try {
      if (!Environment.getExternalStorageState().equals(
              Environment.MEDIA_MOUNTED)) {
        return null;
      }
    } catch (Exception e) {
      // Catch exception is trying to fix a crash inside of Environment.getExternalStorageState().
      e.printStackTrace();
      return null;
    }
    String rootDir = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/" + appFolderName + "/";
    File file = new File(rootDir);
    if (!file.exists()) {
      if (!file.mkdirs()) {
        return null;
      }
    }
    return rootDir;
  }

  /**
   * Get the external storage path of the device
   *
   * @return The external storage path of the device.
   */
  public static String getStorageRootDirectory() {
    try {
      if (!Environment.getExternalStorageState().equals(
              Environment.MEDIA_MOUNTED)) {
        return null;
      }
    } catch (Exception e) {
      // Catch exception is trying to fix a crash inside of Environment.getExternalStorageState().
      e.printStackTrace();
      return null;
    }
    String rootDir = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/";
    File file = new File(rootDir);
    if (!file.exists()) {
      if (!file.mkdirs()) {
        return null;
      }
    }
    return rootDir;
  }

  public static Uri saveImageToFile(Bitmap content, String path) {
    try {
      FileOutputStream outputStream =
              new FileOutputStream(path);
      content.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
      return Uri.fromFile(new File(path));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void saveTextFile(String filePath, String content) {
    File file = new File(filePath);
    try {
      FileOutputStream outputStream = new FileOutputStream(file);
      outputStream.write(content.getBytes());
      outputStream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String readAssetFile(Context context, String path) {
    try {
      InputStream inputStream = context.getAssets().open(path);
      return IOUtils.toString(inputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "";
  }
}
