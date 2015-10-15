package unique.fancysherry.shr.ui.adapter.recycleview;

import java.util.List;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.model.User;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by fancysherry on 15-7-14.
 */
public class MemberManageAdapter extends RecyclerView.Adapter<MemberManageAdapter.ViewHolder>
// implements
// View.OnClickListener
{

  private List<User> items = null;

  private Context context;

  public MemberManageAdapter(Context pContext)
  {
    this.context = pContext;
    setHasStableIds(true);

  }


  public void setData(List<User> items)
  {
    this.items = items;
    notifyDataSetChanged();
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.group_manager_list_item, parent, false);

    // itemView.setOnClickListener(this);


    return new ViewHolder(itemView, this, context);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.group_manage_item_name.setText(items.get(position).name);
//    holder.member_item_shr_num.setText(String.valueOf(items.get(position).shares.size()));
//    holder.member_item_gratitude_num.setText(items.get(position).gratitude_shares_sum);
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

  // @Override
  // public void onClick(View v) {
  // if (mOnItemClickListener != null) {
  // // 注意这里使用getTag方法获取数据
  // mOnItemClickListener.onItemClick(v, (Share) v.getTag());
  // }
  // }
  //
  // private OnRecyclerViewItemClickListener mOnItemClickListener = null;
  //
  // public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
  // this.mOnItemClickListener = listener;
  // }
  //
  // public interface OnRecyclerViewItemClickListener {
  // void onItemClick(View view, Share data);
  // }



  public static class ViewHolder extends RecyclerView.ViewHolder
  {
    CircleImageView group_manage_item_portrait;
    TextView group_manage_item_name;

    TextView group_manage_item_level;


    Context context;
    View view;



    public ViewHolder(View itemView, MemberManageAdapter manageAdapter, Context pContext) {
      super(itemView);
      this.context = pContext;
      this.group_manage_item_portrait =
              (CircleImageView) itemView.findViewById(R.id.group_manage_item_portrait);
      this.group_manage_item_level = (TextView) itemView.findViewById(R.id.group_manage_item_level);
      this.group_manage_item_name = (TextView) itemView.findViewById(R.id.group_manage_item_name);

      this.view = itemView;
    }


  }

}
