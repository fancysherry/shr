package unique.fancysherry.shr.util.system;


import java.io.File;

import android.os.Environment;
import android.util.Log;

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
}
