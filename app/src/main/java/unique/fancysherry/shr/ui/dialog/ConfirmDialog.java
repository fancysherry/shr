package unique.fancysherry.shr.ui.dialog;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.ui.activity.UserActivity;
import unique.fancysherry.shr.ui.activity.UserInformationResetActivity;
import unique.fancysherry.shr.ui.otto.BusProvider;
import unique.fancysherry.shr.ui.otto.ChangeManageAction;
import unique.fancysherry.shr.ui.otto.DeleteMemberAction;
import unique.fancysherry.shr.ui.widget.TagGroup;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by fancysherry on 15-12-1.
 * dialog 与activity和fragment之间的通信统一使用回调方法，而不使用otto bus
 */
public class ConfirmDialog extends DialogFragment {
  public final static String PASSWORD_CONFIRM = "PASSWORD_CONFIRM";
  public final static String CHANGE_MANAGER_CONFIRM = "CHANGE_MANAGER_CONFIRM";
  public final static String CHANGE_MANAGER_CONFIRM_FINISH = "CHANGE_MANAGER_CONFIRM_FINISH";
  public final static String DELETE_MEMBER_CONFIRM = "DELETE_MEMBER_CONFIRM";
  public final static String DELETE_MEMBER_CONFIRM_AGAIN = "DELETE_MEMBER_CONFIRM_AGAIN";
  public final static String PUT_BLACKLIST_CONFIRM = "PUT_BLACKLIST_CONFIRM";
  private TextView dialog_notification_title;
  private TextView dialog_notification_subtitle;
  private TextView dialog_notification_no;
  private TextView dialog_notification_yes;

  private EditText dialog_notification_password_old;
  private EditText dialog_notification_password_new;
  private String dialog_type;
  private String delete_name;
  private String change_manage_name;
  private String change_manage_group_name = " ";

  // private boolean is_override_delete_dismiss = true;

  public static ConfirmDialog newInstance(String type) {
    ConfirmDialog dialogFragment = new ConfirmDialog();
    Bundle bundle = new Bundle();
    bundle.putString("type", type);
    dialogFragment.setArguments(bundle);
    return dialogFragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    int style = 0;
    style = DialogFragment.STYLE_NO_TITLE;// 无标题样式
    setStyle(style, 0);// 设置样式

    // float width=getResources().getDimension(R.dimen.dialog_width);
    // getDialog().getWindow().setLayout(width,);
  }

  @Override
  public void dismiss() {
    super.dismiss();
    if (dialog_type.equals(DELETE_MEMBER_CONFIRM)) {
      DeleteMemberAction mDeleteMemberAction = new DeleteMemberAction();
      mDeleteMemberAction.setVerify(DeleteMemberAction.VERIFY_NO);
      BusProvider.getInstance().post(mDeleteMemberAction);
    }
    else if (dialog_type.equals(DELETE_MEMBER_CONFIRM)) {
      ChangeManageAction mChangeManageAction = new ChangeManageAction();
      mChangeManageAction.setVerify(ChangeManageAction.VERIFY_NO);
      BusProvider.getInstance().post(mChangeManageAction);
    }
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = null;
    String type = getArguments().getString("type");
    this.dialog_type = type;


    if (type.equals(PASSWORD_CONFIRM)) {
      view = inflater.inflate(R.layout.dialog_notification_password, container);
      dialog_notification_password_old =
          (EditText) view.findViewById(R.id.dialog_notification_password_old_input);
      dialog_notification_password_new =
          (EditText) view.findViewById(R.id.dialog_notification_password_new_input);
      dialog_notification_no = (TextView) view.findViewById(R.id.dialog_notification_password_no);
      dialog_notification_yes = (TextView) view.findViewById(R.id.dialog_notification_password_yes);
      dialog_notification_no.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          dismiss();
        }
      });
      dialog_notification_yes.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          UserInformationResetActivity mUserInformationResetActivity =
              (UserInformationResetActivity) getActivity();
          mUserInformationResetActivity.onDismissDialog(dialog_notification_password_old.getText()
              .toString(), dialog_notification_password_new.getText().toString());
          dismiss();
        }
      });
    }



    else if (type.equals(CHANGE_MANAGER_CONFIRM)) {
      view = inflater.inflate(R.layout.dialog_notification_large, container);
      dialog_notification_title =
          (TextView) view.findViewById(R.id.dialog_notification_small_title);

      if (change_manage_name != null)
        dialog_notification_title.setText("确认将管理员移交给" + change_manage_name);

      dialog_notification_subtitle =
          (TextView) view.findViewById(R.id.dialog_notification_small_subtitle);
      dialog_notification_subtitle.setText("此操作无法撤销");
      dialog_notification_no = (TextView) view.findViewById(R.id.dialog_notification_small_no);
      dialog_notification_yes = (TextView) view.findViewById(R.id.dialog_notification_small_yes);
      dialog_notification_no.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          dismiss();
        }
      });
      dialog_notification_yes.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          ChangeManageAction mChangeManageAction = new ChangeManageAction();
          mChangeManageAction.setVerify(ChangeManageAction.VERIFY_YES);
          BusProvider.getInstance().post(mChangeManageAction);
          dismiss();
        }
      });
    }



    else if (type.equals(CHANGE_MANAGER_CONFIRM_FINISH)) {
      view = inflater.inflate(R.layout.dialog_notification_small_one_btn, container);
      dialog_notification_title =
          (TextView) view.findViewById(R.id.dialog_notification_small_title);
      if (change_manage_name != null && change_manage_group_name != null)
        dialog_notification_title.setText(change_manage_name + "已成为" + change_manage_group_name
            + "的管理员");
      dialog_notification_yes = (TextView) view.findViewById(R.id.dialog_notification_small_yes);
      dialog_notification_yes.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          ChangeManageAction mChangeManageAction = new ChangeManageAction();
          mChangeManageAction.setFinished(true);
          BusProvider.getInstance().post(mChangeManageAction);
          dismiss();
        }
      });
    }



    else if (type.equals(DELETE_MEMBER_CONFIRM)) {
      view = inflater.inflate(R.layout.dialog_notification_large, container);
      dialog_notification_title =
          (TextView) view.findViewById(R.id.dialog_notification_small_title);

      if (delete_name != null)
        dialog_notification_title.setText("确认将" + delete_name + "从群中移除");

      dialog_notification_subtitle =
          (TextView) view.findViewById(R.id.dialog_notification_small_subtitle);
      dialog_notification_subtitle.setText("此操作无法撤销");
      dialog_notification_no = (TextView) view.findViewById(R.id.dialog_notification_small_no);
      dialog_notification_yes = (TextView) view.findViewById(R.id.dialog_notification_small_yes);
      dialog_notification_no.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          dismiss();
        }
      });
      dialog_notification_yes.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          DeleteMemberAction mDeleteMemberAction = new DeleteMemberAction();
          mDeleteMemberAction.setVerify(DeleteMemberAction.VERIFY_YES);
          BusProvider.getInstance().post(mDeleteMemberAction);
          dismiss();
        }
      });

    }



    else if (type.equals(DELETE_MEMBER_CONFIRM_AGAIN)) {
      view = inflater.inflate(R.layout.dialog_notification_small_one_btn, container);
      dialog_notification_title =
          (TextView) view.findViewById(R.id.dialog_notification_small_title);
      if (delete_name != null)
        dialog_notification_title.setText("已经将" + delete_name + "从群中移除");
      dialog_notification_yes = (TextView) view.findViewById(R.id.dialog_notification_small_yes);
      dialog_notification_yes.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          dismiss();
        }
      });
    }



    else if (type.equals(PUT_BLACKLIST_CONFIRM)) {
      view = inflater.inflate(R.layout.dialog_notification_small, container);
      dialog_notification_title =
          (TextView) view.findViewById(R.id.dialog_notification_small_title);
      dialog_notification_title.setText("是否要屏蔽该用户");
      dialog_notification_no = (TextView) view.findViewById(R.id.dialog_notification_small_no);
      dialog_notification_yes = (TextView) view.findViewById(R.id.dialog_notification_small_yes);
      dialog_notification_no.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          dismiss();
        }
      });
      dialog_notification_yes.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          UserActivity mUserActivity = (UserActivity) getActivity();
          mUserActivity.onDismissDialog();
          dismiss();
        }
      });
    }
    else {
      // todo default layout
    }
    return view;
  }

  public void delete_name(String delete_name) {
    this.delete_name = delete_name;
    if (dialog_notification_title != null && dialog_type.equals(DELETE_MEMBER_CONFIRM))
      dialog_notification_title.setText("确认将" + delete_name + "从群中移除");
    else if (dialog_notification_title != null && dialog_type.equals(DELETE_MEMBER_CONFIRM_AGAIN))
      dialog_notification_title.setText("已将" + delete_name + "从群中移除");
  }

  public void change_manage_name(String change_manage_name)
  {
    this.change_manage_name = change_manage_name;
    if (dialog_notification_title != null && dialog_type.equals(CHANGE_MANAGER_CONFIRM))
      dialog_notification_title.setText("确认将管理员移交给" + change_manage_name);
    else if (dialog_notification_title != null && dialog_type.equals(CHANGE_MANAGER_CONFIRM_FINISH))
      dialog_notification_title.setText(change_manage_name + "已成为" + change_manage_group_name
          + "的管理员");
  }

  public void change_manage_group_name(String change_manage_group_name)
  {
    this.change_manage_group_name = change_manage_group_name;
    if (dialog_notification_title != null && dialog_type.equals(CHANGE_MANAGER_CONFIRM))
      dialog_notification_title.setText("确认将管理员移交给" + change_manage_name);
    else if (dialog_notification_title != null && dialog_type.equals(CHANGE_MANAGER_CONFIRM_FINISH))
      dialog_notification_title.setText(change_manage_name + "已成为" + change_manage_group_name
          + "的管理员");
  }
}
