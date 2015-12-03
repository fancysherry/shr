package unique.fancysherry.shr.ui.dialog;

import android.os.Bundle;

import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.ui.otto.BusProvider;
import unique.fancysherry.shr.ui.otto.ShareUrlAction;
import unique.fancysherry.shr.ui.widget.TagGroup;

/**
 * Created by fancysherry on 15-12-1.
 */
public class ShrDialog extends DialogFragment {
  private TextView dialog_shr_content_title;
  private EditText dialog_shr_content_intro;
  private TagGroup user_groups_tagGroup;
  private ImageView dialog_shr_content_exit;
  private static ArrayList<String> test_taggroup = new ArrayList<>();

  public static ShrDialog newInstance(ArrayList<String> test_taggroup) {
    ShrDialog dialogFragment = new ShrDialog();
    Bundle bundle = new Bundle();
    bundle.putStringArrayList("taggroup", test_taggroup);
    dialogFragment.setArguments(bundle);

    return dialogFragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    test_taggroup = getArguments().getStringArrayList("taggroup");

    // int styleNum = 1;
    // int style = 0;
    // switch (styleNum) {
    // case 0:
    // style = DialogFragment.STYLE_NORMAL;// 默认样式
    // break;
    // case 1:
    // style = DialogFragment.STYLE_NO_TITLE;// 无标题样式
    // break;
    // case 2:
    // style = DialogFragment.STYLE_NO_FRAME;// 无边框样式
    // break;
    // case 3:
    // style = DialogFragment.STYLE_NO_INPUT;// 不可输入，不可获得焦点样式
    // break;
    // }
    // setStyle(style, 0);// 设置样式

  }

  /**
   * 清楚taggroup的数据
   */
  @Override
  public void dismiss() {
    super.dismiss();
    user_groups_tagGroup.removeAllViews();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_shr_content_test, container);
    dialog_shr_content_title = (TextView) view.findViewById(R.id.dialog_shr_content_title);
    dialog_shr_content_intro = (EditText) view.findViewById(R.id.dialog_shr_content_intro);
    dialog_shr_content_exit = (ImageView) view.findViewById(R.id.dialog_shr_content_exit);
    dialog_shr_content_exit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });
    user_groups_tagGroup = (TagGroup) view.findViewById(R.id.user_groups_tagGroup);
    user_groups_tagGroup.setTagsDailog(test_taggroup);
    user_groups_tagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
      @Override
      public void onTagClick(String tag) {
        if (tag.equals("..."))
          user_groups_tagGroup.setAllTagsDailog(test_taggroup);
        else if (tag.equals("<-"))
          user_groups_tagGroup.setTagsDailog(test_taggroup);
        else {
          String comment=dialog_shr_content_intro.getText().toString();
          ShareUrlAction mShareUrlAction = new ShareUrlAction();
          mShareUrlAction.setGroup_name(tag);
          mShareUrlAction.setComment(comment);
          BusProvider.getInstance().post(mShareUrlAction);
        }
      }
    });
    return view;
  }
}
