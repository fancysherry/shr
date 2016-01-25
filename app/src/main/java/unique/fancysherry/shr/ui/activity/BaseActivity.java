package unique.fancysherry.shr.ui.activity;

import java.util.HashMap;
import java.util.Map;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.util.UrlFromString;
import unique.fancysherry.shr.util.config.SApplication;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;

/**
 * Created by fancysherry on 15-11-29.
 */
public class BaseActivity extends AppCompatActivity {
  private String text_from_clipboard = null;
  private ClipboardManager clipboard = null;
  public String extract_url;

  protected void initializeToolbar(Toolbar mToolbar) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      getWindow().setStatusBarColor(getAttributeColor(R.attr.colorPrimaryDark));
    }
    setSupportActionBar(mToolbar);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setTitle("");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    // mToolbar.setOnMenuItemClickListener(onMenuItemClick);
  }

  // Resolve the given attribute of the current theme
  private int getAttributeColor(int resId) {
    TypedValue typedValue = new TypedValue();
    getTheme().resolveAttribute(resId, typedValue, true);
    int color = 0x000000;
    if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT
        && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
      // resId is a color
      color = typedValue.data;
    } else {
      // resId is not a color
    }
    return color;
  }

  public Map<String, String> getHeader() {
    Map<String, String> headers = new HashMap<String, String>();
    UserBean currentUser = AccountManager.getInstance().getCurrentUser();
    if (currentUser != null && currentUser.getCookieHolder() != null) {
      currentUser.getCookieHolder().generateCookieString();
      headers.put("Cookie", currentUser.getCookieHolder().generateCookieString());
    }
    headers
        .put(
            "User-Agent",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36");
    return headers;
  }

  public void executeRequest(Request request) {
    SApplication.getRequestManager().executeRequest(request, this);
  }

  protected boolean checkShare() {
    checkClipboard();
    if (text_from_clipboard != null) {
      if (UrlFromString.pullLinks(text_from_clipboard) != null) {
        extract_url = UrlFromString.pullLinks(text_from_clipboard);
        // showMyDialog(Gravity.BOTTOM);
        return true;
      } else
        return false;
    } else
      return false;
  }

  /**
   * 往Clip中放入数据
   * 因为我share后会刷新activity，如果数据不清理，会导致一直弹窗
   */
  protected void clearClipboard() {
    // 类型一:text
    clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    ClipData textCd = ClipData.newPlainText("kkk", "unique shr");
    clipboard.setPrimaryClip(textCd);
  }

  /**
   * 从Clip中取数据
   */
  protected void checkClipboard() {
    clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    ClipData.Item item = null;

    // 无数据时直接返回
    if (!clipboard.hasPrimaryClip()) {
      Toast.makeText(getApplicationContext(), "剪贴板中无数据", Toast.LENGTH_SHORT).show();
      return;
    }

    // 如果是文本信息
    if (clipboard.getPrimaryClipDescription().hasMimeType(
        ClipDescription.MIMETYPE_TEXT_PLAIN)) {
      ClipData cdText = clipboard.getPrimaryClip();
      item = cdText.getItemAt(0);
      // 此处是TEXT文本信息
      if (item.getText() == null) {
        Toast.makeText(getApplicationContext(), "剪贴板中无内容", Toast.LENGTH_SHORT).show();
        // return;
      } else {
        Toast.makeText(getApplicationContext(), item.getText(), Toast.LENGTH_SHORT).show();
        text_from_clipboard = item.getText().toString();
      }
    }
  }

}
