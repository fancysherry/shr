package unique.fancysherry.shr.ui.adapter.recycleview;

import java.util.ArrayList;
import java.util.List;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.Notify;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.ui.activity.UserActivity;
import unique.fancysherry.shr.ui.otto.BusProvider;
import unique.fancysherry.shr.ui.otto.NotifyInviteAction;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by fancysherry on 15-7-14.
 */
public class NotificationInviteAdapter
    extends RecyclerView.Adapter<NotificationInviteAdapter.ViewHolder>
    implements
    View.OnClickListener
{
  private List<Notify> items = new ArrayList<>();

  private Context context;

  public NotificationInviteAdapter(Context pContext)
  {
    this.context = pContext;
    setHasStableIds(true);
  }

  public void setData(List<Notify> items)
  {
    for (int i = 0; i < items.size(); i++) {
      if (items.get(i).notify_type.equals(Notify.INVITE))
        this.items.add(items.get(i));
    }
    notifyDataSetChanged();
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_invite_accept_list_item,
            parent, false);
    // itemView.setOnClickListener(this);
    return new ViewHolder(itemView, this, context);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, final int position) {
    holder.notification_invite_content.setText(items.get(position).nickname
        + " 邀请你加入 " + items.get(position).group_name);
    holder.notification_invite_person.setImageURI(Uri.parse(APIConstants.BASE_URL
        + items.get(position).avatar));
    if (!items.get(position).isfinish) {
      holder.notification_invite_accept_btn.setText("加入");
      holder.notification_invite_accept_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          NotifyInviteAction mNotifyInviteAction = new NotifyInviteAction();
          mNotifyInviteAction.setKey(items.get(position).key);
          BusProvider.getInstance().post(mNotifyInviteAction);
          holder.notification_invite_accept_btn.setText("已加入");
          items.get(position).isfinish = true;
        }
      });
    }
    holder.notification_invite_person.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent mIntent = new Intent(context, UserActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("user_id", items.get(position).user_id);
        mIntent.putExtras(mBundle);
        context.startActivity(mIntent);
      }
    });
    // holder.member_item_level.setText(items.get(position).level);
    holder.view.setTag(items.get(position));
  }

  @Override
  public int getItemCount() {
    if (items == null)
      return 0;
    else
      return items.size();
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



  public static class ViewHolder extends RecyclerView.ViewHolder
  {
    SimpleDraweeView notification_invite_person;
    TextView notification_invite_content;
    TextView notification_invite_accept_btn;
    Context context;
    View view;

    public ViewHolder(View itemView, NotificationInviteAdapter notificationInviteAdapter,
        Context pContext) {
      super(itemView);
      this.context = pContext;
      this.notification_invite_person =
          (SimpleDraweeView) itemView.findViewById(R.id.notification_invite_person);
      this.notification_invite_content =
          (TextView) itemView.findViewById(R.id.notification_invite_content);
      this.notification_invite_accept_btn =
          (TextView) itemView.findViewById(R.id.notification_invite_accept_btn);
      this.view = itemView;
    }
  }

}
