package unique.fancysherry.shr.ui.adapter.recycleview.ItemDecoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import unique.fancysherry.shr.io.model.Share;

/**
 * Created by fancysherry on 15-10-11.
 */
public class UserItemDecoration extends RecyclerView.ItemDecoration {

  private int space = 35;// default

  public UserItemDecoration(int space) {
    this.space = space;
  }

  public UserItemDecoration() {}


  public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
    if (itemPosition == 0)
      outRect.set(0, 0, 0, space);
    else
      outRect.set(0, 0, 0, space);
  }


}
