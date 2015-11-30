package unique.fancysherry.shr.ui.adapter.recycleview;

import java.net.MalformedURLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.model.Share;
import unique.fancysherry.shr.util.IconFinder;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by fancysherry on 15-7-14.
 */
public class UserShareAdapter extends RecyclerView.Adapter<UserShareAdapter.ViewHolder>
    implements
    View.OnClickListener {

  private List<Share> items = null;

  private Context context;

  public UserShareAdapter(Context pContext)
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
        LayoutInflater.from(parent.getContext()).inflate(R.layout.user_share_list_item, parent,
            false);

    itemView.setOnClickListener(this);


    return new ViewHolder(itemView, this, context);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.user_share_title.setText(items.get(position).title);
    holder.user_share_group.setText(items.get(position).group);
    holder.user_share_time.setText(getTime(items.get(position).share_time));
    try {
      holder.user_share_icon.setImageURI(Uri.parse(IconFinder.get64xIcon(items.get(position).url)));
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    holder.view.setTag(items.get(position));


  }


  private String getTime(String time)
  {
    Pattern pattern = Pattern.compile("[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]");
    Matcher matcher = pattern.matcher(time);
    if (matcher.find()) {
      return matcher.group(0);
    }
    else
      return null;
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
    SimpleDraweeView user_share_icon;
    TextView user_share_title;

    TextView user_share_group;
    TextView user_share_time;

    Context context;
    View view;



    public ViewHolder(View itemView, UserShareAdapter pGroupShareAdapter, Context pContext) {
      super(itemView);
      this.context = pContext;
      this.user_share_icon =
          (SimpleDraweeView) itemView.findViewById(R.id.user_share_list_item_share_icon);
      this.user_share_title =
          (TextView) itemView.findViewById(R.id.user_share_list_item_share_title);

      this.user_share_group = (TextView) itemView.findViewById(R.id.user_share_list_item_group);


      this.user_share_time = (TextView) itemView.findViewById(R.id.user_share_list_item_time);

      this.view = itemView;
    }


  }

}
