package unique.fancysherry.shr.ui.adapter.recycleview.ViewAdapter;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.Notify;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by fancysherry on 2/8/16.
 */
public class NotificationGratitudeHolder extends RecyclerView.ViewHolder {
  public View view;
  public Context context;
  public SimpleDraweeView notification_thank_person;
  public TextView notification_thank_shr_title;

  public NotificationGratitudeHolder(View itemView, Context context) {
    super(itemView);
    this.view = itemView;
    this.context = context;
    this.notification_thank_person =
        (SimpleDraweeView) itemView.findViewById(R.id.notification_thank_person_avatar);
    this.notification_thank_shr_title =
        (TextView) itemView.findViewById(R.id.notification_thank_shr_title);
  }

  public void onBindViewHolder(Notify notify) {
    notification_thank_person.setImageURI(Uri.parse(APIConstants.BASE_URL
        + notify.avatar));
    notification_thank_shr_title.setText(notify.title);
    view.setTag(null);
  }
}
