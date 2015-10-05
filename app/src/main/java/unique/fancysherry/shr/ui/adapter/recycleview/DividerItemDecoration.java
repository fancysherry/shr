package unique.fancysherry.shr.ui.adapter.recycleview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by fancysherry on 15-7-30.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {
  private int space = 20;// default

  public DividerItemDecoration(int space) {
    this.space = space;
  }

  public DividerItemDecoration() {
  }


  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    outRect.left = space;
    outRect.right = space;
    outRect.bottom = space;

    // Add top margin only for the first item to avoid double space between items
    if (parent.getChildPosition(view) == 0)
      outRect.top = space;
  }

}
