package unique.fancysherry.shr.ui.adapter.recycleview.ViewAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.net.MalformedURLException;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.Util.PhotoLoader;
import unique.fancysherry.shr.io.model.InboxShare;
import unique.fancysherry.shr.io.model.Share;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.ui.adapter.recycleview.GroupShareAdapter;
import unique.fancysherry.shr.ui.adapter.recycleview.InboxShareAdapter;
import unique.fancysherry.shr.util.IconFinder;
import unique.fancysherry.shr.util.IconLoad;

/**
 * Created by fancysherry on 15-10-9.
 */
public class GroupShareViewHolder extends RecyclerView.ViewHolder
{
  // ImageView share_icon;
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

  public GroupShareViewHolder(View itemView, InboxShareAdapter pGroupShareAdapter, Context pContext) {
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

  public void onBindViewHolder(final Share share) {
    share_title.setText(share.title);
    share_user.setText(shareUserText(share));
    user_portrait.setImageURI(Uri.parse(APIConstants.BASE_URL + share.origin.avatar));
    try {
      share_icon.setImageURI(Uri.parse(IconFinder.get64xIcon(share.url)));
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    // try {
    // // share_icon.setImageURI(getIconUri(share.url));
    // IconLoad.load(share_icon, IconFinder.getIconUrlString(share.url));
    // } catch (MalformedURLException e) {
    // e.printStackTrace();
    // }


    if (!share.intro.equals(""))
      user_introduce.setText(share.intro);

    // try {
    // // IconLoad.load(share_icon, IconFinder.getIconUrlString(share.url));
    // PhotoLoader.loadImg(share_icon, IconFinder.getIconUrlString(share.url));
    // // share_icon.setImageURI(getIconUri(share.url));
    // Log.e("share_url", share.url);
    // } catch (MalformedURLException e) {
    // e.printStackTrace();
    // }
    view.setTag(share);
  }

  public void onBindViewHolder(InboxShare inboxshare) {
    share_title.setText(inboxshare.title);
    share_user.setText(inboxshare.nickname);
    user_portrait.setImageURI(Uri.parse(APIConstants.BASE_URL + inboxshare.avatar));

    try {
      share_icon.setImageURI(Uri.parse(IconFinder.get64xIcon(inboxshare.url)));
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    // if (!inboxshare.intro.equals(""))
    // user_introduce.setText(share.intro);

    // try {
    // // IconLoad.load(share_icon, IconFinder.getIconUrlString(share.url));
    // PhotoLoader.loadImg(share_icon, IconFinder.getIconUrlString(inboxshare.url));
    // // share_icon.setImageURI(getIconUri(share.url));
    // Log.e("share_url", inboxshare.url);
    // } catch (MalformedURLException e) {
    // e.printStackTrace();
    // }
    view.setTag(inboxshare);
  }

  // private Uri getIconUri(String url) throws MalformedURLException {
  // String icon_url = IconFinder.getIconUrlString(url);
  // if (icon_url != null) {
  // Log.e("icon_url", icon_url);
  // Log.e("icon_Uri", Uri.parse(icon_url).toString());
  // return Uri.parse(icon_url);
  // }
  // else
  // return null;
  // }

  private String shareUserText(Share share)
  {
    String result = null;
    int size = share.others.size() + 1;
    if (size > 3)
      result = share.origin.nickname + "," +
          share.others.get(0).name + "," +
          share.others.get(1).name +
          "等" + size + "人分享过";

    else if (size == 1)
      result = share.origin.nickname + "分享过";
    else
    {
      int count = 0;
      result = share.origin.nickname;
      for (User user : share.others)
      {
        result = result + "," + share.others.get(count).name;
      }
      result = result + "分享过";
    }
    return result;
  }
}
