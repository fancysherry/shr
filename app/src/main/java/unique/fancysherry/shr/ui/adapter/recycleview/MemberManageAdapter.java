package unique.fancysherry.shr.ui.adapter.recycleview;

import java.util.List;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.User;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import de.hdodenhof.circleimageview.CircleImageView;
import unique.fancysherry.shr.ui.otto.BusProvider;
import unique.fancysherry.shr.ui.otto.ChangeManageAction;

/**
 * Created by fancysherry on 15-7-14.
 */
public class MemberManageAdapter extends RecyclerView.Adapter<MemberManageAdapter.ViewHolder>
// implements
// View.OnClickListener
{
  private boolean is_select = false;// 当前只能有列表的一项被选中
  private List<User> items = null;

  private Context context;
  private int select_index = -1;

  public MemberManageAdapter(Context pContext)
  {
    this.context = pContext;
    setHasStableIds(true);

  }

  public void refresh_select() {
    select_index = -1;
    is_select = false;
    notifyDataSetChanged();
  }

  public void setData(List<User> items)
  {
    this.items = items;
    notifyDataSetChanged();
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.group_manager_list_item, parent,
            false);

    // itemView.setOnClickListener(this);


    return new ViewHolder(itemView, this, context);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, final int position) {
    holder.group_manage_item_name.setText(items.get(position).name);
    holder.group_manage_item_portrait.setImageURI(Uri.parse(APIConstants.BASE_URL
        + items.get(position).avatar));
    if (is_select && select_index == position) {
      holder.group_manage_list_item_layout.setBackgroundColor(context.getResources().getColor(
          R.color.change_manage_select_color));
    } else {
      holder.group_manage_list_item_layout.setBackgroundColor(context.getResources().getColor(
          R.color.change_manage_color));
    }
    holder.group_manage_list_item_layout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (is_select) {
          select_index = position;
          notifyDataSetChanged();
        } else {
          select_index = position;
          is_select = true;
          holder.group_manage_list_item_layout.setBackgroundColor(context.getResources().getColor(
              R.color.change_manage_select_color));
        }


        ChangeManageAction mChangeManageAction = new ChangeManageAction();
        mChangeManageAction.setUser_id(items.get(position).id);
        mChangeManageAction.setUser_name(items.get(position).name);
        BusProvider.getInstance().post(mChangeManageAction);
      }
    });
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
    SimpleDraweeView group_manage_item_portrait;
    TextView group_manage_item_name;
    TextView group_manage_item_level;
    RelativeLayout group_manage_list_item_layout;
    Context context;
    View view;

    public ViewHolder(View itemView, MemberManageAdapter manageAdapter, Context pContext) {
      super(itemView);
      this.context = pContext;
      this.group_manage_list_item_layout =
          (RelativeLayout) itemView.findViewById(R.id.group_manage_list_item_layout);
      this.group_manage_item_portrait =
          (SimpleDraweeView) itemView.findViewById(R.id.group_manage_item_portrait);
      this.group_manage_item_level = (TextView) itemView.findViewById(R.id.group_manage_item_level);
      this.group_manage_item_name = (TextView) itemView.findViewById(R.id.group_manage_item_name);
      this.view = itemView;
    }
  }
}
