package unique.fancysherry.shr.io.delivery;


import unique.fancysherry.shr.io.IVolleyActionDelivery;
import unique.fancysherry.shr.io.cache.CacheManager;
import unique.fancysherry.shr.io.image.PhotoLoader;
import unique.fancysherry.shr.util.config.SApplication;

public abstract class BaseCachePhotoActionDelivery implements IVolleyActionDelivery<PhotoLoader> {

  public CacheManager getCacheManager() {
    return SApplication.getRequestManager().getCacheManager();
  }
}
