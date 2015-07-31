package unique.fancysherry.shr.ui.adapter.recycleview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.model.Share;

/**
 * Created by fancysherry on 15-7-14.
 */
public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder>
    implements
    View.OnClickListener {

  private List<Share> items = null;

  private Context context;

  public ShareAdapter(Context pContext)
  {
    this.context = pContext;
    setHasStableIds(true);

  }


  public void setData(List<Share> items)
  {
    this.items = items;
    notifyDataSetChanged();
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.share_list_item, parent, false);

    itemView.setOnClickListener(this);


    return new ViewHolder(itemView, this, context);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.share_title.setText(items.get(position).title);
    holder.comment_number.setText(items.get(position).comment_sum);
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

  @Override
  public void onClick(View v) {
    if (mOnItemClickListener != null) {
      // 注意这里使用getTag方法获取数据
      mOnItemClickListener.onItemClick(v, (Share) v.getTag());
    }
  }

  private OnRecyclerViewItemClickListener mOnItemClickListener = null;

  public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
    this.mOnItemClickListener = listener;
  }

  public interface OnRecyclerViewItemClickListener {
    void onItemClick(View view, Share data);
  }



  public static class ViewHolder extends RecyclerView.ViewHolder
  {
    ImageView share_icon;
    TextView share_title;

    ImageView user_portrait;
    TextView user_introduce;

    ImageView share_button;
    TextView share_user;

    ImageView commment_button;
    TextView comment_number;

    Context context;
    View view;



    public ViewHolder(View itemView, ShareAdapter shareAdapter, Context pContext) {
      super(itemView);
      this.context = pContext;
      this.share_icon = (ImageView) itemView.findViewById(R.id.share_list_item_share_icon);
      this.share_title = (TextView) itemView.findViewById(R.id.share_list_item_share_title);

      this.user_portrait = (ImageView) itemView.findViewById(R.id.share_list_item_user_portrait);
      this.user_introduce = (TextView) itemView.findViewById(R.id.share_list_item_user_introduce);

      this.commment_button = (ImageView) itemView.findViewById(R.id.share_list_item_comment_button);
      this.comment_number = (TextView) itemView.findViewById(R.id.share_list_item_comment_number);

      this.share_button = (ImageView) itemView.findViewById(R.id.share_list_item_share_button);
      this.share_user = (TextView) itemView.findViewById(R.id.share_list_item_share_user);

      this.view = itemView;
    }


  }

}
