package unique.fancysherry.shr.ui.adapter.recycleview;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.net.MalformedURLException;
import java.util.List;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.model.Share;
import unique.fancysherry.shr.util.IconFinder;

/**
 * Created by fancysherry on 15-7-14.
 */
public class GroupShareAdapter extends RecyclerView.Adapter<GroupShareAdapter.ViewHolder>
    implements
    View.OnClickListener {

  private List<Share> items = null;

  private int data_position = 0;

  private Context context;

  public GroupShareAdapter(Context pContext)
  {
    this.context = pContext;
    setHasStableIds(true);
  }

  public enum CommonFrature {
    COMMON, FOOTER, DATE
  }

  public void setData(List<Share> items)
  {
    this.items = items;
    notifyDataSetChanged();
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = null;
    switch (viewType) {
      case 0:
        itemView =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.group_share_list_item,
                parent,
                false);
        itemView.setOnClickListener(this);
        data_position++;
        break;
      case 1:
        itemView =
            LayoutInflater.from(parent.getContext()).inflate(
                R.layout.group_share_list_datetime_item,
                parent, false);
        break;
    }

    return new ViewHolder(itemView, this, context);
  }


  @Override
  public int getItemViewType(int position) {
    // Implement your logic here

    return 0;
  }


  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.share_title.setText(items.get(position).title);
    if (!items.get(position).intro.equals(""))
      holder.user_introduce.setText(items.get(position).intro);
    try {
      holder.share_icon.setImageURI(getIconUri(items.get(position).url));
      Log.e("share_url", items.get(position).url);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    holder.view.setTag(items.get(position));
  }

  private Uri getIconUri(String url) throws MalformedURLException {
    String icon_url = IconFinder.getIconUrlString(url);
    if (icon_url != null) {
      Log.e("icon_url", icon_url);
      Log.e("icon_Uri", Uri.parse(icon_url).toString());
      return Uri.parse(icon_url);
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
    SimpleDraweeView share_icon;
    TextView share_title;
    SimpleDraweeView user_portrait;
    TextView user_introduce;
    ImageView share_button;
    TextView share_user;
    Context context;
    View view;

    public ViewHolder(View itemView, GroupShareAdapter pGroupShareAdapter, Context pContext) {
      super(itemView);
      this.context = pContext;
      this.share_icon = (SimpleDraweeView) itemView.findViewById(R.id.share_list_item_share_icon);
      this.share_title = (TextView) itemView.findViewById(R.id.share_list_item_share_title);
      this.user_portrait =
          (SimpleDraweeView) itemView.findViewById(R.id.share_list_item_user_portrait);
      this.user_introduce = (TextView) itemView.findViewById(R.id.share_list_item_user_introduce);
      this.share_button = (ImageView) itemView.findViewById(R.id.share_list_item_share_button);
      this.share_user = (TextView) itemView.findViewById(R.id.share_list_item_share_user);

      this.view = itemView;
    }


  }

}
