package unique.fancysherry.shr.io.cache.generator;

import java.io.InputStream;

import unique.fancysherry.shr.io.cache.AbstractMMBean;
import unique.fancysherry.shr.io.image.PhotoLoader;


/**
 * Created by suanmiao on 15/4/23.
 */
public interface IMMBeanGenerator {

  public AbstractMMBean generateMMBeanFromTotalStream(InputStream stream);

  public AbstractMMBean constructMMBeanFromNetworkStream(PhotoLoader loadOption, InputStream stream);

  public AbstractMMBean constructMMBeanFromNetworkData(PhotoLoader loadOption, byte[] data);

}
