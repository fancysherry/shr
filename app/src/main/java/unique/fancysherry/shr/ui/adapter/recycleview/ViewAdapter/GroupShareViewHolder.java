package unique.fancysherry.shr.ui.adapter.recycleview.ViewAdapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.net.MalformedURLException;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.model.Share;
import unique.fancysherry.shr.ui.adapter.recycleview.GroupShareAdapter;
import unique.fancysherry.shr.util.IconFinder;

/**
 * Created by fancysherry on 15-10-9.
 */
public class GroupShareViewHolder extends RecyclerView.ViewHolder
{
  SimpleDraweeView share_icon;
  TextView share_title;
  SimpleDraweeView user_portrait;
  TextView user_introduce;
  ImageView share_button;
  TextView share_user;
  Context context;
  View view;

  public GroupShareViewHolder(View itemView, GroupShareAdapter pGroupShareAdapter, Context pContext) {
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

  public void onBindViewHolder(Share share) {


    share_title.setText(share.title);
    if (!share.intro.equals(""))
      user_introduce.setText(share.intro);
    try {
      share_icon.setImageURI(getIconUri(share.url));
      Log.e("share_url", share.url);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    view.setTag(share);
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

}