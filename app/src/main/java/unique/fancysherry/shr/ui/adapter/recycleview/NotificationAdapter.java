package unique.fancysherry.shr.ui.adapter.recycleview;

import java.util.ArrayList;
import java.util.List;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.model.Notify;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.ui.adapter.recycleview.ViewAdapter.NotificationCommentHolder;
import unique.fancysherry.shr.ui.adapter.recycleview.ViewAdapter.NotificationGratitudeHolder;
import unique.fancysherry.shr.ui.adapter.recycleview.ViewAdapter.NotificationInviteHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by fancysherry on 15-7-14.
 */
public class NotificationAdapter
    extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements
      View.OnClickListener {
  private List<Notify> items = new ArrayList<>();
  private Context context;

  public NotificationAdapter(Context pContext) {
    this.context = pContext;
    setHasStableIds(true);
  }

  public void setData(List<Notify> items) {
    for (int i = 0; i < items.size(); i++) {
      if (items.get(i).notify_type.equals(Notify.INVITE)
          || items.get(i).notify_type.equals(Notify.COMMENT)
          || items.get(i).notify_type.equals(Notify.GRATITUDE))
        this.items.add(items.get(i));
    }
    notifyDataSetChanged();
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = null;
    if (viewType == 1) {
      itemView =
          LayoutInflater.from(parent.getContext()).inflate(
              R.layout.notification_invite_accept_list_item,
              parent, false);
      // itemView.setOnClickListener(this);
      return new NotificationInviteHolder(itemView, context);
    } else if (viewType == 2) {
      itemView =
          LayoutInflater.from(parent.getContext()).inflate(
              R.layout.notification_comment_list_item,
              parent, false);
      // itemView.setOnClickListener(this);
      return new NotificationCommentHolder(itemView, context);
    } else {
      itemView =
          LayoutInflater.from(parent.getContext()).inflate(
              R.layout.notification_thank_list_item,
              parent, false);
      // itemView.setOnClickListener(this);
      return new NotificationGratitudeHolder(itemView, context);
    }
  }

  @Override
  public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
    if (holder instanceof NotificationInviteHolder) {
      NotificationInviteHolder notificationInviteHolder = (NotificationInviteHolder) holder;
      notificationInviteHolder.onBindViewHolder(items.get(position));
    } else if (holder instanceof NotificationCommentHolder) {
      NotificationCommentHolder notificationCommentHolder = (NotificationCommentHolder) holder;
      notificationCommentHolder.onBindViewHolder(items.get(position));
    } else if (holder instanceof NotificationGratitudeHolder) {
      NotificationGratitudeHolder notificationGratitudeHolder =
          (NotificationGratitudeHolder) holder;
      notificationGratitudeHolder.onBindViewHolder(items.get(position));
    }
  }

  @Override
  public int getItemCount() {
    if (items == null)
      return 0;
    else
      return items.size();
  }

  @Override
  public int getItemViewType(int position) {
    // Log.e("position:", String.valueOf(position));
    // Implement your logic here
    if (items.get(position).notify_type.equals(Notify.INVITE)) {
      return 1;
    } else if (items.get(position).notify_type.equals(Notify.COMMENT)) {
      return 2;
    } else if (items.get(position).notify_type.equals(Notify.GRATITUDE)) {
      return 3;
    } else {
      return -1;
    }

  }

  @Override
  public long getItemId(int position) {
    return items.get(position).hashCode();
  }

  @Override
  public void onClick(View v) {
    if (mOnItemClickListener != null) {
      // 注意这里使用getTag方法获取数据
      mOnItemClickListener.onItemClick(v, (User) v.getTag());
    }
  }

  private OnRecyclerViewItemClickListener mOnItemClickListener = null;

  public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
    this.mOnItemClickListener = listener;
  }

  public interface OnRecyclerViewItemClickListener {
    void onItemClick(View view, User data);
  }


}
