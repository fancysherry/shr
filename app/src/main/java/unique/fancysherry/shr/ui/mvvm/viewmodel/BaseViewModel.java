package unique.fancysherry.shr.ui.mvvm.viewmodel;



import android.content.Context;

import unique.fancysherry.shr.io.request.BaseRequest;
import unique.fancysherry.shr.io.RequestManager;
import unique.fancysherry.shr.ui.mvvm.UICallback;
import unique.fancysherry.shr.ui.mvvm.model.BaseModel;
import unique.fancysherry.shr.ui.mvvm.view.BaseView;
import unique.fancysherry.shr.util.config.SApplication;


/**
 * Created by fancysherry on 15-7-11.
 */
public abstract class BaseViewModel<V extends BaseView, T extends BaseModel> {
  protected RequestManager mRequestManager;

  protected V baseView;

  protected Context mContext;
  private UICallback uiCallback;

  public BaseViewModel(V view, Context context, UICallback uiCallback) {
    this.baseView = view;
    this.mRequestManager = SApplication.getRequestManager();
    this.mContext = context;
    this.uiCallback = uiCallback;
  }

  public void notifyUIChange() {
    if (this.uiCallback != null) {
      uiCallback.notifyUIChange();
    }
  }



  public abstract void bind(int index, T baseModel, int scrollState, float scrollSpeed);

  public abstract void idleReload();

  public V getItemView() {
    return baseView;
  }

  public RequestManager getRequestManager() {
    return mRequestManager;
  }

  public Context getContext() {
    return mContext;
  }

  public UICallback getUiCallback() {
    return uiCallback;
  }



  protected void executeRequest(BaseRequest<T> request) {
    mRequestManager.executeRequest(request, this);
  }

  public void cancelRequest() {
    mRequestManager.cancelRequest(this);
  }

}
