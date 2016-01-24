package unique.fancysherry.shr.ui.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.UploadImage.NetUtil;
import unique.fancysherry.shr.io.UploadImage.SelectPicPopupWindow;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.dialog.ConfirmDialog;
import unique.fancysherry.shr.ui.otto.BusProvider;
import unique.fancysherry.shr.ui.otto.DataChangeAction;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.SApplication;

public class UserInformationResetActivity extends BaseActivity {
  private String user_id;
  @InjectView(R.id.change_portriat)
  TextView change_portrait;
  @InjectView(R.id.user_information_name_content)
  TextView user_information_name_content;
  @InjectView(R.id.user_information_email_content)
  TextView user_information_email_content;
  @InjectView(R.id.user_information_introduce_number)
  TextView user_information_introduce_number;
  @InjectView(R.id.user_information_introduce_content)
  EditText user_information_introduce_content;
  @InjectView(R.id.user_information_portrait)
  SimpleDraweeView user_information_portrait;
  @InjectView(R.id.user_information_password_layout)
  RelativeLayout user_information_password_layout;
  @InjectView(R.id.user_setting_activity_toolbar)
  Toolbar mToolbar;

  private Activity mContext;
  private SelectPicPopupWindow menuWindow; // 自定义的头像编辑弹出框

  private Uri photoUri;
  /** 使用照相机拍照获取图片 */
  public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
  /** 使用相册中的图片 */
  public static final int SELECT_PIC_BY_PICK_PHOTO = 2;
  /** 获取到的图片路径 */
  private String picPath = "";
  private String resultStr = ""; // 服务端返回结果集
  private String imgUrl = APIConstants.BASE_URL + "/upload_image";
  private String user_avatar;
  private String user_intro;
  private String user_name;
  private String user_email;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_information_reset);
    ButterKnife.inject(this);
    mContext = this;
    Bundle mBundle = getIntent().getExtras();
    user_id = mBundle.getString("user_id");
    user_avatar = mBundle.getString("user_avatar");
    user_name = mBundle.getString("user_name");
    user_intro = mBundle.getString("user_intro");
    user_email = AccountManager.getInstance().getCurrentUser().mAccountBean.username;
    initializeToolbar(mToolbar);
    initView();
  }

  private void initView() {
    user_information_password_layout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showDialog(ConfirmDialog.PASSWORD_CONFIRM);
      }
    });
    user_information_name_content.setText(user_name);
    user_information_email_content.setText(user_email);
    user_information_introduce_content.setText(user_intro);
    user_information_introduce_number.setText(user_information_introduce_content.getText()
        .length()
        + "/50");
    user_information_portrait.setImageURI(Uri.parse(APIConstants.BASE_URL + user_avatar));
    change_portrait.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        menuWindow = new SelectPicPopupWindow(mContext, itemsOnClick);
        menuWindow.showAtLocation(findViewById(R.id.activity_user_setting_layout),
            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
      }
    });

    user_information_introduce_content.addTextChangedListener(textWatcher);
    user_information_introduce_content.setImeOptions(EditorInfo.IME_ACTION_DONE);
    // 软件盘配置
    user_information_introduce_content
        .setOnEditorActionListener(new EditText.OnEditorActionListener() {
          @Override
          public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
              InputMethodManager imm =
                  (InputMethodManager) v.getContext()
                      .getSystemService(Context.INPUT_METHOD_SERVICE);
              imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
              // 如果没有输入任何文字，则不添加新的item
              if (user_intro.equals(user_information_introduce_content.getText().toString()))
                return false;
              else {
                resetUserIntro(user_information_introduce_content.getText().toString());
              }
              // 不修改就不添加
              return true;
            }
            return false;
          }
        });
  }

  public void onDismissDialog(String old_password, String new_password) {
    resetPassword(old_password, new_password);
  }

  /**
   * TextWatcher：接口，继承它要实现其三个方法，分别为Text改变之前、改变的过程中、改变之后各自发生的动作
   */
  private TextWatcher textWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int arg1, int arg2,
        int arg3) {}

    @Override
    public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
      // 让TextView一直跟随EditText输入的内容同步显示
    }

    @Override
    public void afterTextChanged(Editable s) {
      user_information_introduce_number.setText(user_information_introduce_content.getText()
          .length()
          + "/50");
    }
  };


  // 为弹出窗口实现监听类
  private View.OnClickListener itemsOnClick = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      // 隐藏弹出窗口
      menuWindow.dismiss();

      switch (v.getId()) {
        case R.id.takePhotoBtn:// 拍照
          takePhoto();
          break;
        case R.id.pickPhotoBtn:// 相册选择图片
          pickPhoto();
          break;
        case R.id.cancelBtn:// 取消
          break;
        default:
          break;
      }
    }
  };

  void showDialog(String type) {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    Fragment prev = getSupportFragmentManager().findFragmentByTag("reset_password_dialog");
    if (prev != null) {
      ft.remove(prev);
    }
    ConfirmDialog dialogFrag = ConfirmDialog.newInstance(type);
    dialogFrag.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ShrDialog);
    dialogFrag.show(getSupportFragmentManager(), "reset_password_dialog");
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == android.R.id.home) {
      finish();
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * 拍照获取图片
   */
  private void takePhoto() {
    // 执行拍照前，应该先判断SD卡是否存在
    String SDState = Environment.getExternalStorageState();
    if (SDState.equals(Environment.MEDIA_MOUNTED)) {

      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      /***
       * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
       * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
       * 如果不使用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
       */
      ContentValues values = new ContentValues();
      photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
      intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
      startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
    } else {
      Toast.makeText(this, "内存卡不存在", Toast.LENGTH_LONG).show();
    }
  }

  /***
   * 从相册中取图片
   */
  private void pickPhoto() {
    Intent intent = new Intent();
    // 如果要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // 点击取消按钮
    if (resultCode == RESULT_CANCELED) {
      return;
    }

    // 可以使用同一个方法，这里分开写为了防止以后扩展不同的需求
    switch (requestCode) {
      case SELECT_PIC_BY_PICK_PHOTO:// 如果是直接从相册获取
        doPhoto(requestCode, data);
        break;
      case SELECT_PIC_BY_TACK_PHOTO:// 如果是调用相机拍照时
        doPhoto(requestCode, data);
        break;
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  /**
   * 选择图片后，获取图片的路径
   *
   * @param requestCode
   * @param data
   */
  private void doPhoto(int requestCode, Intent data) {

    // 从相册取图片，有些手机有异常情况，请注意
    if (requestCode == SELECT_PIC_BY_PICK_PHOTO) {
      if (data == null) {
        Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
        return;
      }
      photoUri = data.getData();
      if (photoUri == null) {
        Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
        return;
      }
    }

    String[] pojo = {MediaStore.MediaColumns.DATA};
    // The method managedQuery() from the type Activity is deprecated
    // Cursor cursor = managedQuery(photoUri, pojo, null, null, null);
    Cursor cursor = mContext.getContentResolver().query(photoUri, pojo, null, null, null);
    if (cursor != null) {
      int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
      cursor.moveToFirst();
      picPath = cursor.getString(columnIndex);

      // 4.0以上的版本会自动关闭 (4.0--14;; 4.0.3--15)
      if (Integer.parseInt(Build.VERSION.SDK) < 14) {
        cursor.close();
      }
    }

    // 如果图片符合要求将其上传到服务器
    if (picPath != null && (picPath.endsWith(".png") ||
        picPath.endsWith(".PNG") ||
        picPath.endsWith(".jpg") ||
        picPath.endsWith(".JPG"))) {

      BitmapFactory.Options option = new BitmapFactory.Options();
      // 压缩图片:表示缩略图大小为原始图片大小的几分之一，1为原图
      option.inSampleSize = 1;
      // 根据图片的SDCard路径读出Bitmap
      Bitmap bm = BitmapFactory.decodeFile(picPath, option);
      // 显示在图片控件上
      user_information_portrait.setImageURI(photoUri);
      LogUtil.e("before thread image address   " + picPath);
      // pd = ProgressDialog.show(mContext, null, "正在上传图片，请稍候...");
      // new Thread(uploadImageRunnable).start();
      UploadImage();
    } else {
      Toast.makeText(this, "选择图片文件不正确", Toast.LENGTH_LONG).show();
    }
  }

  public void UploadImage() {
    File file = new File(picPath);
    String base64_code = null;
    try {
      base64_code = NetUtil.getBase64Param(file);
      LogUtil.e("base code :" + base64_code);
    } catch (Exception e) {
      e.printStackTrace();
    }
    Map<String, String> params = new HashMap<>();
    params.put("avatar", base64_code);
    GsonRequest<GsonRequest.FormResult> user_avatar_request =
        new GsonRequest<>(Request.Method.PUT,
            imgUrl,
            getHeader(), params,
            GsonRequest.FormResult.class,
            new Response.Listener<GsonRequest.FormResult>() {
              @Override
              public void onResponse(GsonRequest.FormResult result) {
                // handler.post(runnable);
                // 通知相应的界面更新头像
                LogUtil.e(result.message);
                DataChangeAction mDataChangeAction = new DataChangeAction();
                mDataChangeAction.setStr(DataChangeAction.CHANGE_AVATAR);
                BusProvider.getInstance().post(mDataChangeAction);
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(user_avatar_request);
  }

  public Map<String, String> getHeader() {
    Map<String, String> headers = new HashMap<String, String>();
    UserBean currentUser = AccountManager.getInstance().getCurrentUser();
    if (currentUser != null && currentUser.getCookieHolder() != null) {
      currentUser.getCookieHolder().generateCookieString();
      headers.put("Cookie", currentUser.getCookieHolder().generateCookieString());
    }
    LogUtil.e(currentUser.getCookieHolder().generateCookieString());
    headers.put("Accept-Encoding", "gzip, deflate");
    headers
        .put(
            "User-Agent",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36");
    return headers;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    ButterKnife.reset(this);
  }

  /**
   * Callbacks interface that all activities using this fragment must implement.
   */
  public void resetUserIntro(String intro) {
    Map<String, String> params = new HashMap<>();
    params.put("brief", intro);
    params.put("education_information", " ");
    GsonRequest<GsonRequest.FormResult> group_share_request =
        new GsonRequest<>(Request.Method.GET,
            APIConstants.BASE_URL + "/setting",
            getHeader(), params,
            GsonRequest.FormResult.class,
            new Response.Listener<GsonRequest.FormResult>() {
              @Override
              public void onResponse(GsonRequest.FormResult result) {
                if (result.message.equals("success"))
                  LogUtil.e("reset intro success");
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(group_share_request);
  }

  public void resetPassword(String old_password, final String new_password) {
    Map<String, String> params = new HashMap<>();
    params.put("old_password", old_password);
    params.put("new_password", new_password);
    GsonRequest<GsonRequest.FormResult> group_share_request =
        new GsonRequest<>(Request.Method.PUT,
            APIConstants.BASE_URL + "/user/update_passwd",
            getHeader(), params,
            GsonRequest.FormResult.class,
            new Response.Listener<GsonRequest.FormResult>() {
              @Override
              public void onResponse(GsonRequest.FormResult result) {
                if (result.message.equals("success")) {
                  LogUtil.e("reset password success");
                  AccountManager.getInstance().saveNewPassword(new_password);

                }
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(group_share_request);
  }

}
