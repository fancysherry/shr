package unique.fancysherry.shr.ui.adapter;

import android.support.v7.widget.RecyclerView;


public abstract class SlowBaseRecyclerAdapter extends RecyclerView.Adapter {

  protected boolean mListBusy;

  public void setListBusy(boolean busy) {
    mListBusy = busy;
  }
}
