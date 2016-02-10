package unique.fancysherry.shr.ui.adapter.recycleview.ItemDecoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;

/**
 * Created by fancysherry on 15-10-11.
 */
public class NotificationItemDecoration extends RecyclerView.ItemDecoration {

  private int space = 20;// default

  public NotificationItemDecoration(int space) {
    this.space = space;
  }

  public NotificationItemDecoration() {}


  public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
    if (itemPosition == 0)
      outRect.set(0, 0, 0, space);
    else
      outRect.set(0, 0, 0, space);
  }


}
