package unique.fancysherry.shr.ui.adapter.recycleview;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.User;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by fancysherry on 15-7-14.
 */
public class DeleteMemberAdapter extends RecyclerView.Adapter<DeleteMemberAdapter.ViewHolder>
// implements
// View.OnClickListener
{

  private List<User> items = null;

  private Context context;

  public DeleteMemberAdapter(Context pContext)
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
        LayoutInflater.from(parent.getContext()).inflate(R.layout.group_member_delete_list_item,
            parent,
            false);

    // itemView.setOnClickListener(this);


    return new ViewHolder(itemView, this, context);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.member_item_nickname.setText(items.get(position).name);
    holder.member_item_profile.setImageURI(Uri.parse(APIConstants.BASE_URL
        + items.get(position).avatar));
    holder.view.setTag(items.get(position));


  }

  @Override
  public int getItemCount() {
    if (items == null)
      return 0;
    else
      return items.size();
  }

  // @Override
  // public long getItemId(int position) {
  // return items.get(position).hashCode();
  // }

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
    SimpleDraweeView member_item_profile;
    TextView member_item_nickname;

    TextView member_item_level;

    TextView delete;
    Context context;
    View view;



    public ViewHolder(View itemView, DeleteMemberAdapter pDeleteMemberAdapter, Context pContext) {
      super(itemView);
      this.context = pContext;
      this.member_item_profile =
          (SimpleDraweeView) itemView.findViewById(R.id.group_member_item_portrait);
      this.member_item_level = (TextView) itemView.findViewById(R.id.group_member_item_level);
      this.member_item_nickname = (TextView) itemView.findViewById(R.id.group_member_item_name);
      this.delete = (TextView) itemView.findViewById(R.id.group_member_item_delete_button);
      this.delete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
      });
      this.view = itemView;
    }


  }

}
