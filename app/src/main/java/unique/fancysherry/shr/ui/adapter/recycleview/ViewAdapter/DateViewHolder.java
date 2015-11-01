package unique.fancysherry.shr.ui.adapter.recycleview.ViewAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.net.MalformedURLException;
import java.text.ParseException;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.model.Share;
import unique.fancysherry.shr.ui.adapter.recycleview.GroupShareAdapter;
import unique.fancysherry.shr.ui.adapter.recycleview.InboxShareAdapter;
import unique.fancysherry.shr.util.DateUtil;

/**
 * Created by fancysherry on 15-10-9.
 */
public class DateViewHolder extends RecyclerView.ViewHolder
{
  TextView share_time;
  Context context;
  View view;

  public DateViewHolder(View itemView, GroupShareAdapter pGroupShareAdapter, Context pContext) {
    super(itemView);
    this.context = pContext;
    this.share_time = (TextView) itemView.findViewById(R.id.share_list_datetime_item_time);
    this.view = itemView;
  }

  public DateViewHolder(View itemView, InboxShareAdapter pGroupShareAdapter, Context pContext) {
    super(itemView);
    this.context = pContext;
    this.share_time = (TextView) itemView.findViewById(R.id.share_list_datetime_item_time);
    this.view = itemView;
  }

  public void onBindViewHolder(String time) throws ParseException {
    share_time.setText(DateUtil.toListDate(time));
    view.setTag(null);
  }


}
