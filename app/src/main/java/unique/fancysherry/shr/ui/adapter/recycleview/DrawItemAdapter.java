package unique.fancysherry.shr.ui.adapter.recycleview;

import java.util.List;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.model.Group;
import unique.fancysherry.shr.ui.widget.BadgeView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by fancysherry on 15-7-14.
 */
public class DrawItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements
    View.OnClickListener {

  private List<Group> group_item = null;
  private List<String> item_message_count = null;

  private Context context;

  public DrawItemAdapter(Context pContext)
  {
    this.context = pContext;
    setHasStableIds(true);

  }

  public enum Feature {
    COMMON, ADD
  }

  public void setData(List<Group> group_item, List<String> item_message_count)
  {
    this.item_message_count = item_message_count;
    this.group_item = group_item;
//    group_item.add(new Group()); // view type 2
    notifyDataSetChanged();
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = null;
    if (viewType == Feature.COMMON.ordinal()) {
      itemView =
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.drawer_list_item, parent, false);
      itemView.setOnClickListener(this);
      return new ViewHolder(itemView, this, context);
    }
    else
    {
      itemView = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.drawer_list_item_add, parent, false);
      itemView.setOnClickListener(this);
      return new AddViewHolder(itemView, this, context);
    }
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof ViewHolder) {
      ViewHolder mViewHolder = (ViewHolder) holder;
      mViewHolder.drawer_item_title.setText(group_item.get(position).name);
      mViewHolder.drawer_item_count.setTargetView(mViewHolder.drawer_item_title);
      mViewHolder.drawer_item_count.setBadgeCount(0);// TODO test
      mViewHolder.drawer_item_count.setBadgeMargin(0, 0, 8, 0);
      mViewHolder.drawer_item_count.setBadgeGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
      mViewHolder.view.setTag(group_item.get(position));
    }

    else
    {
      AddViewHolder mAddViewHolder = (AddViewHolder) holder;
      mAddViewHolder.view.setTag(group_item.get(position));
    }
  }


  @Override
  public int getItemViewType(int position) {

    // Implement your logic here
//    if (position != group_item.size()-1) {
      return Feature.COMMON.ordinal();
    // }
    // else {
    // return Feature.ADD.ordinal();
    // }
  }

  @Override
  public int getItemCount() {
    if (group_item == null)
      return 0;
    else
      return group_item.size();
  }

  @Override
  public long getItemId(int position) {
    return group_item.get(position).hashCode();
  }

  @Override
  public void onClick(View v) {
    if (mOnItemClickListener != null) {
      // 注意这里使用getTag方法获取数据
      mOnItemClickListener.onItemClick(v, (Group) v.getTag());
    }
  }

  private OnRecyclerViewItemClickListener mOnItemClickListener = null;

  public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
    this.mOnItemClickListener = listener;
  }

  public interface OnRecyclerViewItemClickListener {
    void onItemClick(View view, Group group);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder
  {
    TextView drawer_item_title;
    BadgeView drawer_item_count;

    Context context;
    View view;

    public ViewHolder(View itemView, DrawItemAdapter shareAdapter, Context pContext) {
      super(itemView);
      this.context = pContext;
      this.drawer_item_title = (TextView) itemView.findViewById(R.id.drawer_item_title);
      this.drawer_item_count = new BadgeView(pContext);

      this.view = itemView;
    }
  }

  public static class AddViewHolder extends RecyclerView.ViewHolder
  {
    TextView drawer_item_add;
    Context context;
    View view;

    public AddViewHolder(View itemView, DrawItemAdapter shareAdapter, Context pContext) {
      super(itemView);
      this.context = pContext;
      this.drawer_item_add = (TextView) itemView.findViewById(R.id.drawer_add_group);

      this.view = itemView;
    }
  }

}
