package unique.fancysherry.shr.ui.adapter.recycleview;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import unique.fancysherry.shr.io.model.InboxShare;
import unique.fancysherry.shr.io.model.Share;

/**
 * Created by fancysherry on 15-7-30.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {
  private int space = 20;// default

  private String type;

  public DividerItemDecoration(int space, String type) {
    this.space = space;
    this.type = type;
  }

  public DividerItemDecoration() {}

  //
  // public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
  // if (itemPosition == 0)
  // outRect.set(0, space, 0, space);
  // else
  // outRect.set(0, 0, 0, space);
  // }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State
      state) {

    if (type.equals("share")) {
      GroupShareAdapter mAdapter = (GroupShareAdapter) parent.getAdapter();
      List<Share> view_type_array = mAdapter.getViewTypeArray();
      int size = view_type_array.size();
      if (view_type_array.get(mAdapter.getPosition()).getType() != null)
        outRect.set(0, 0, 0, 0);
      else
      {
        outRect.left = 0;
        outRect.right = 0;

        if (view_type_array.get(mAdapter.getPosition() - 1).getType() == null)
          outRect.top = space;

        if (mAdapter.getPosition() + 1 < size) {
          if (view_type_array.get(mAdapter.getPosition() + 1).getType() == null)
            outRect.bottom = space;
        }
        else
          outRect.bottom = space;
      }
    } else {
      InboxShareAdapter mAdapter = (InboxShareAdapter) parent.getAdapter();
      List<InboxShare> view_type_array = mAdapter.getViewTypeArray();
      int size = view_type_array.size();
      if (view_type_array.get(mAdapter.getPosition()).getType() != null)
        outRect.set(0, 0, 0, 0);
      else
      {
        outRect.left = 0;
        outRect.right = 0;

        if (view_type_array.get(mAdapter.getPosition() - 1).getType() == null)
          outRect.top = space;

        if (mAdapter.getPosition() + 1 < size) {
          if (view_type_array.get(mAdapter.getPosition() + 1).getType() == null)
            outRect.bottom = space;
        }
        else
          outRect.bottom = space;
      }
    }

  }
}
