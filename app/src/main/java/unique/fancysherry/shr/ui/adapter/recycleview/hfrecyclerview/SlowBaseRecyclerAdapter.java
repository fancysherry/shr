package unique.fancysherry.shr.ui.adapter.recycleview.hfrecyclerview;

import android.support.v7.widget.RecyclerView;


public abstract class SlowBaseRecyclerAdapter extends RecyclerView.Adapter {

  protected boolean mListBusy;

  public void setListBusy(boolean busy) {
    mListBusy = busy;
  }
}
