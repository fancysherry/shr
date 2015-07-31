package unique.fancysherry.shr.ui.adapter.recycleview;

import java.util.List;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.model.Comment;
import unique.fancysherry.shr.io.model.Share;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by fancysherry on 15-7-14.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>
    implements
    View.OnClickListener {

  private List<Comment> items = null;

  private Context context;

  public CommentAdapter(Context pContext)
  {
    this.context = pContext;
    setHasStableIds(true);

  }


  public void setData(List<Comment> items)
  {
    this.items = items;
    notifyDataSetChanged();
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);

    itemView.setOnClickListener(this);


    return new ViewHolder(itemView, this, context);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.comment_nickname.setText(items.get(position).nickname);
    holder.comment_content.setText(items.get(position).content);
    holder.comment_time.setText(items.get(position).time);
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
      mOnItemClickListener.onItemClick(v, (Comment) v.getTag());
    }
  }

  private OnRecyclerViewItemClickListener mOnItemClickListener = null;

  public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
    this.mOnItemClickListener = listener;
  }

  public interface OnRecyclerViewItemClickListener {
    void onItemClick(View view, Comment data);
  }



  public static class ViewHolder extends RecyclerView.ViewHolder
  {
    ImageView comment_portrait;
    TextView comment_nickname;
    TextView comment_content;
    TextView comment_time;


    Context context;
    View view;



    public ViewHolder(View itemView, CommentAdapter commentAdapter, Context pContext) {
      super(itemView);
      this.context = pContext;
      this.comment_portrait = (ImageView) itemView.findViewById(R.id.comment_item_message_profile);
      this.comment_nickname = (TextView) itemView.findViewById(R.id.comment_item_message_nickname);
      this.comment_content =
          (TextView) itemView.findViewById(R.id.comment_item_message_text_content);
      this.comment_time = (TextView) itemView.findViewById(R.id.comment_item_message_time);


      this.view = itemView;
    }


  }

}
