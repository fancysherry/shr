package unique.fancysherry.shr.ui.adapter.recycleview;

import java.util.List;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.model.User;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by fancysherry on 15-7-14.
 */
public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder>
    implements
    View.OnClickListener
{
  private List<User> items = null;

  private Context context;

  public MemberAdapter(Context pContext)
  {
    this.context = pContext;
    setHasStableIds(true);
  }

  public void setData(List<User> items)
  {
    this.items = items;
    Log.e("memberAdapter", "size   " + items.size());
    notifyDataSetChanged();
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.member_list_item_group_activity,
            parent, false);
    // itemView.setOnClickListener(this);
    return new ViewHolder(itemView, this, context);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    Log.e("memberAdapter", items.get(position).name + "   " + position);
    holder.member_item_name.setText(items.get(position).name);
    // holder.member_item_shr_num.setText(String.valueOf(items.get(position).shares.size()));
    // holder.member_item_gratitude_num.setText(items.get(position).gratitude_shares_sum);
    // holder.member_item_level.setText(items.get(position).level);
    // holder.view.setTag(items.get(position));
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
    CircleImageView member_item_portrait;
    TextView member_item_name;
    TextView member_item_level;
    TextView member_item_shr_num;
    TextView member_item_gratitude_num;
    Context context;
    View view;

    public ViewHolder(View itemView, MemberAdapter memberAdapter, Context pContext) {
      super(itemView);
      this.context = pContext;
      this.member_item_portrait =
          (CircleImageView) itemView.findViewById(R.id.member_item_profile);
      this.member_item_level = (TextView) itemView.findViewById(R.id.member_item_level);
      this.member_item_name = (TextView) itemView.findViewById(R.id.member_item_nickname);
      this.member_item_shr_num = (TextView) itemView.findViewById(R.id.member_item_shr_num);
      this.member_item_gratitude_num =
          (TextView) itemView.findViewById(R.id.member_item_gratitude_num);
      this.view = itemView;
    }


  }

}
