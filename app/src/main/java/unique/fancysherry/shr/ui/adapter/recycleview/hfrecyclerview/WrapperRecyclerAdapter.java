package unique.fancysherry.shr.ui.adapter.recycleview.hfrecyclerview;

import android.support.v7.widget.RecyclerView;


public abstract class WrapperRecyclerAdapter<VH extends RecyclerView.ViewHolder> extends
    RecyclerView.Adapter<VH> {

  public abstract RecyclerView.Adapter<VH> getWrappedAdapter();

}
