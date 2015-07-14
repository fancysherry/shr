package unique.fancysherry.shr.ui.mvvm;

/**
 * Created by fancysherry on 15-7-11.
 */
public interface UICallback {
  public void notifyUIChange();

  public void notifyException(Exception exception);

}
