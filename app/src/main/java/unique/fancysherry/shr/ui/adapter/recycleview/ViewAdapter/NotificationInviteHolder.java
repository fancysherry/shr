package unique.fancysherry.shr.ui.adapter.recycleview.ViewAdapter;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.Notify;
import unique.fancysherry.shr.ui.activity.UserActivity;
import unique.fancysherry.shr.ui.otto.BusProvider;
import unique.fancysherry.shr.ui.otto.NotifyInviteAction;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by fancysherry on 2/8/16.
 */
public class NotificationInviteHolder extends RecyclerView.ViewHolder {
  public View view;
  public SimpleDraweeView notification_invite_person;
  public TextView notification_invite_group;
  public TextView notification_invite_accept_btn;
  public Context context;

  public NotificationInviteHolder(View itemView, Context context) {
    super(itemView);
    this.context = context;
    this.notification_invite_person =
        (SimpleDraweeView) itemView.findViewById(R.id.notification_invite_person_avatar);
    this.notification_invite_group =
        (TextView) itemView.findViewById(R.id.notification_invite_group);
    this.notification_invite_accept_btn =
        (TextView) itemView.findViewById(R.id.notification_invite_accept_btn);
    this.view = itemView;
  }

  public void onBindViewHolder(final Notify notify) {
    notification_invite_group.setText(notify.group_name);
    notification_invite_person.setImageURI(Uri.parse(APIConstants.BASE_URL
        + notify.avatar));
    if (!notify.isfinish) {
      notification_invite_accept_btn.setText("加入");
      notification_invite_accept_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          NotifyInviteAction mNotifyInviteAction = new NotifyInviteAction();
          mNotifyInviteAction.setKey(notify.key);
          BusProvider.getInstance().post(mNotifyInviteAction);
          notification_invite_accept_btn.setText("已加入");
          notify.isfinish = true;
        }
      });
    }
    notification_invite_person.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent mIntent = new Intent(context, UserActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("user_id", notify.user_id);
        mIntent.putExtras(mBundle);
        context.startActivity(mIntent);
      }
    });
    view.setTag(null);
  }
}
