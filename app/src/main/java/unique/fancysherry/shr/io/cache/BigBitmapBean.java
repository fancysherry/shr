package unique.fancysherry.shr.io.cache;

import java.io.OutputStream;



public class BigBitmapBean extends AbstractMMBean {
  private BigBitmap mData;

  public BigBitmapBean(BigBitmap bitmap) {
    this.mData = bitmap;
    this.dataType = TYPE_BIG_BITMAP;
  }

  public BigBitmap getData() {
    return mData;
  }

  @Override
  public void writeData(OutputStream stream) {
    this.mData.toStream(stream);
  }

  @Override
  public int getSize() {
    return mData.getTotalSize();
  }
}
