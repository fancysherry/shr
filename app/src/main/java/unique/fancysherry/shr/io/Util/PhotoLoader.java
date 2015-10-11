package unique.fancysherry.shr.io.Util;

import java.io.IOException;

import android.text.TextUtils;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.android.volley.VolleyError;

import unique.fancysherry.shr.io.ProgressListener;
import unique.fancysherry.shr.io.RequestManager;
import unique.fancysherry.shr.io.VolleyCommonListener;
import unique.fancysherry.shr.io.cache.AbstractMMBean;
import unique.fancysherry.shr.io.cache.BaseMMBean;
import unique.fancysherry.shr.io.cache.BigBitmapBean;
import unique.fancysherry.shr.io.cache.BigDrawable;
import unique.fancysherry.shr.io.request.BaseRequest;
import unique.fancysherry.shr.io.request.PhotoRequest;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.TextUtil;
import unique.fancysherry.shr.util.config.SApplication;

/**
 * Created by suanmiao on 14/12/7.
 */
public class PhotoLoader {

  public static final int INVALID_VALUE = -1;

  private int viewWidth = INVALID_VALUE;
  private int viewHeight = INVALID_VALUE;

  private String url;

  private AbstractMMBean content;

  private Option loadOption;

  private int contentLength;

  private static boolean saveTraffic = false;

  public static enum ContentState {
    DONE,
    LOADING,
    NONE
  }

  public static enum LoadSource {
    ONLY_FROM_CACHE,
    ONLY_FROM_NETWORK,
    BOTH
  }

  private ContentState contentState = ContentState.NONE;



  protected static RequestManager mRequestManager;

  public PhotoLoader(String url, int viewWidth, int viewHeight, Option option) {
    this.viewWidth = viewWidth;
    this.viewHeight = viewHeight;
    this.url = url;
    this.loadOption = option;
    mRequestManager = SApplication.getRequestManager();
  }

  public int getViewWidth() {
    return viewWidth;
  }

  public int getViewHeight() {
    return viewHeight;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public String getCacheKey() {
    return getUrl() + getLoadOption().cacheSuffix;
  }

  public void setContent(AbstractMMBean content) {
    this.content = content;
  }

  public AbstractMMBean getContent() {
    return content;
  }

  public Option getLoadOption() {
    if (loadOption == null) {
      loadOption = new Option();
    }
    return loadOption;
  }

  public void setLoadOption(Option loadOption) {
    this.loadOption = loadOption;
  }

  public void setContentLength(int contentLength) {
    this.contentLength = contentLength;
  }

  public int getContentLength() {
    return contentLength;
  }

  public ContentState getLoadingState() {
    return contentState;
  }

  public void setContentState(ContentState contentState) {
    this.contentState = contentState;
  }



  /**
   * get Photo object from view or create a new instance
   *
   * @param view the view to load image on
   * @param url target url to request image
   * @return
   */
  public static PhotoLoader getObject(ImageView view, String url, Option option) {
    /**
     * scheme :
     * when there is a Photo object in imageView's tag,just compare the url ,if not equal ,cancel
     * old and create a new request
     */
    if (view == null) {
      return null;
    }
    url = TextUtil.parseUrl(url);
    if (view.getTag() != null) {
      PhotoLoader result = (PhotoLoader) view.getTag();
      if (!TextUtils.equals(url, result.getUrl())) {
        result.setContentState(ContentState.NONE);
        result.setUrl(url);
      }
      return result;
    } else {
      int width = 0, height = 0;
      if (view.getLayoutParams() != null) {
        width = view.getLayoutParams().width > 0 ? view.getLayoutParams().width : 0;
        height = view.getLayoutParams().height > 0 ? view.getLayoutParams().height : 0;
      }
      return new PhotoLoader(url, width, height, option);
    }
  }

  public static void loadScrollItemImg(final ImageView imageView, String url,
      int defaultResourceID, int scrollState) {
    loadScrollItemImg(imageView, url, defaultResourceID, scrollState, null);
  }

  public static void loadScrollItemImg(final ImageView imageView, String url,
      int defaultResourceID, int scrollState, Option option) {
    if (TextUtils.isEmpty(url)) {
      return;
    }
    url = TextUtil.parseUrl(url);

    final PhotoLoader mPhotoLoader = PhotoLoader.getObject(imageView, url, option);
    if (mPhotoLoader != null) {
      if (mPhotoLoader.getLoadingState() != ContentState.DONE) {
        mPhotoLoader.loadFromRamCache(mRequestManager, imageView);
        if (saveTraffic()) {
          return;
        }
        // no cache got
        if (mPhotoLoader.getLoadingState() == ContentState.NONE) {
          imageView.setImageResource(defaultResourceID);
          if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            mPhotoLoader.setContentState(ContentState.LOADING);
            if (saveTraffic) {
              mPhotoLoader.getLoadOption().loadSource = LoadSource.ONLY_FROM_CACHE;
            } else {
              mPhotoLoader.getLoadOption().loadSource = LoadSource.BOTH;
            }



            PhotoRequest request = mPhotoLoader.createVolleyRequest(mPhotoLoader, imageView);
            executeVolleyRequest(request, imageView);
          }
        }
      }
      imageView.setTag(mPhotoLoader);
    }
  }

  public static void loadImg(final ImageView imageView, String url, int defaultResourceID) {
    loadImg(imageView, url, defaultResourceID, null);
  }

  public static void loadImg(final ImageView imageView, String url, int defaultResourceID,
      Option option) {
    final PhotoLoader mPhotoLoader = PhotoLoader.getObject(imageView, url, option);
    if (mPhotoLoader != null) {
      if (mPhotoLoader.getLoadingState() != ContentState.DONE) {
        mPhotoLoader.loadFromRamCache(mRequestManager, imageView);
        if (mPhotoLoader.getLoadingState() != ContentState.DONE) {
          imageView.setImageResource(defaultResourceID);
          mPhotoLoader.setContentState(ContentState.LOADING);
          if (saveTraffic) {
            mPhotoLoader.getLoadOption().loadSource = LoadSource.ONLY_FROM_CACHE;
          } else {
            mPhotoLoader.getLoadOption().loadSource = LoadSource.BOTH;
          }

          PhotoRequest request = mPhotoLoader.createVolleyRequest(mPhotoLoader, imageView);
          executeVolleyRequest(request, imageView);
        }
      }
      imageView.setTag(mPhotoLoader);
    }
  }

  public static void reloadImg(final ImageView imageView) {
    if (imageView != null && imageView.getTag() != null) {
      final PhotoLoader mPhotoLoader = (PhotoLoader) imageView.getTag();
      if (mPhotoLoader.getLoadingState() != ContentState.DONE) {
        mPhotoLoader.setContentState(ContentState.LOADING);
        if (saveTraffic) {
          mPhotoLoader.getLoadOption().loadSource = LoadSource.ONLY_FROM_CACHE;
        } else {
          mPhotoLoader.getLoadOption().loadSource = LoadSource.BOTH;
        }

        PhotoRequest request = mPhotoLoader.createVolleyRequest(mPhotoLoader, imageView);
        executeVolleyRequest(request, imageView);

        imageView.setTag(mPhotoLoader);
      }
    }
  }



  protected PhotoRequest createVolleyRequest(final PhotoLoader pPhotoLoader,
      final ImageView imageView) {
    return new PhotoRequest(this, getUrl(), new VolleyCommonListener<PhotoLoader>() {
      @Override
      public void onErrorResponse(VolleyError error) {
        LogUtil.e("volley photo request error " + error);
        pPhotoLoader.setContentState(ContentState.NONE);
      }

      @Override
      public void onResponse(PhotoLoader pPhotoLoader) {
        LogUtil.e("volley photo request success " + pPhotoLoader);
        if (pPhotoLoader.getContent() != null) {
          processResult(pPhotoLoader, imageView);
        }
      }
    });
  }



  public static void executeVolleyRequest(BaseRequest request,
      final ImageView imageView) {
    LogUtil.e("start volley photo request " + request);
    mRequestManager.executeRequest(request, imageView);
  }

  private static void processResult(PhotoLoader pPhotoLoader, ImageView imageView) {
    pPhotoLoader.setContentState(ContentState.DONE);
    if (pPhotoLoader.getLoadOption().mResultHandler == null) {
      AbstractMMBean content = pPhotoLoader.getContent();

      if (content != null) {
        if (content.getDataType() == AbstractMMBean.TYPE_BITMAP) {
          imageView.setImageBitmap(((BaseMMBean) content).getDataBitmap());
        } else if (content.getDataType() == AbstractMMBean.TYPE_BIG_BITMAP) {
          BigDrawable drawable = new BigDrawable(((BigBitmapBean) content).getData());
          imageView.setImageDrawable(drawable);
        }
      }
    } else {
      pPhotoLoader.getLoadOption().mResultHandler.onResult(pPhotoLoader.getContent(), imageView);
    }
  }

  public void loadFromRamCache(RequestManager requestManager, ImageView imageView) {
    this.contentState = ContentState.NONE;
    try {
      AbstractMMBean result = requestManager.getCacheManager().getFromRam(getCacheKey());
      if (result != null) {
        this.setContent(result);
        processResult(this, imageView);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static class Option {
    public boolean sampleBigBitmap = true;
    /**
     * the bitmap will be sampled to the size of image view, this will save memory
     */
    public boolean sampleToImageSize = false;
    /**
     * the max size for normal bitmap ,if 'sampleBigBitmap' is true and the bitmap original size
     * exceeds the size,it will be saved in normal bitmap
     */
    public int sampledMaxBitmapSize;
    /**
     * whether cache original photo,when 'sampleToImageSize' is true,but you want to use original
     * photo,we will cache it to file ,the suffix is 'original'
     */
    public boolean cacheOriginalPhoto = false;
    public LoadSource loadSource;
    public String cacheSuffix = "";
    public ProgressListener progressListener;

    public ResultHandler mResultHandler;

    public static final String SUFFIX_ORIGINAL = "original";

    public Option() {
      this.loadSource = LoadSource.BOTH;
      this.sampleBigBitmap = true;
    }
  }

  public interface ResultHandler {
    public void onResult(AbstractMMBean content, ImageView targetImage);
  }

  private static boolean saveTraffic() {
    return saveTraffic;
  }

  public static void setSaveTraffic(boolean saveTraffic) {
    PhotoLoader.saveTraffic = saveTraffic;
  }

}
