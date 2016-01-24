package unique.fancysherry.shr.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.Group;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.adapter.recycleview.MemberManageAdapter;
import unique.fancysherry.shr.ui.dialog.ConfirmDialog;
import unique.fancysherry.shr.ui.otto.BusProvider;
import unique.fancysherry.shr.ui.otto.ChangeManageAction;
import unique.fancysherry.shr.ui.otto.DeleteMemberAction;
import unique.fancysherry.shr.util.LogUtil;

public class GroupChangeManagerActivity extends BaseActivity {
  @InjectView(R.id.group_manage_list)
  RecyclerView group_manage_list;
  @InjectView(R.id.assure_change_manage_btn)
  TextView assure_change_manage_btn;
  @InjectView(R.id.group_manage_activity_toolbar)
  Toolbar mToolbar;
  private String group_id;
  private Group group;
  private MemberManageAdapter memberManageAdapter;
  private Handler handler;
  private Runnable runnable;
  private Activity activity;
  private String change_manage_id;
  private String change_manage_name;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_group_change_manager);
    BusProvider.getInstance().register(this);
    ButterKnife.inject(this);
    initAdapter();
    activity = this;
    Bundle mBundle = getIntent().getExtras();
    group_id = mBundle.getString("group_id");
    initializeToolbar(mToolbar);
    getGroupData();

    handler = new Handler();
    runnable = new Runnable() {
      @Override
      public void run() {
        memberManageAdapter.setData(group.users);
      }
    };
    assure_change_manage_btn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (change_manage_name == null || change_manage_id == null)
          Toast.makeText(activity, "请选择一个管理员", Toast.LENGTH_SHORT).show();
        else
          showDialog(ConfirmDialog.CHANGE_MANAGER_CONFIRM, change_manage_name);
      }
    });
  }

  private void initAdapter() {
    memberManageAdapter = new MemberManageAdapter(this);
    group_manage_list.setLayoutManager(new LinearLayoutManager(this,
        LinearLayoutManager.VERTICAL, false));
    group_manage_list.setAdapter(memberManageAdapter);
  }

  void showDialog(String type, String name) {
    if (type.equals(ConfirmDialog.CHANGE_MANAGER_CONFIRM)) {
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      Fragment prev = getSupportFragmentManager().findFragmentByTag("change_manage_dialog_one");
      if (prev != null) {
        ft.remove(prev);
      }
      ConfirmDialog dialogFrag = ConfirmDialog.newInstance(type);
      dialogFrag.change_manage_name(name);
      dialogFrag.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ShrDialog);
      dialogFrag.show(getSupportFragmentManager(), "change_manage_dialog_one");
    } else if (type.equals(ConfirmDialog.CHANGE_MANAGER_CONFIRM_FINISH)) {
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      Fragment prev = getSupportFragmentManager().findFragmentByTag("change_manage_dialog_two");
      if (prev != null) {
        ft.remove(prev);
      }
      ConfirmDialog dialogFrag = ConfirmDialog.newInstance(type);
      dialogFrag.change_manage_name(name);
      dialogFrag.change_manage_group_name(group.group_name);
      dialogFrag.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ShrDialog);
      dialogFrag.show(getSupportFragmentManager(), "change_manage_dialog_two");
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == android.R.id.home) {
      finish();
    }
    return super.onOptionsItemSelected(item);
  }

  public void ChangeManage() {
    Map<String, String> params = new HashMap<String, String>();
    params.put("group_id", group_id);
    params.put("user_id", change_manage_id);
    GsonRequest<GsonRequest.FormResult> delete_member_request =
        new GsonRequest<>(Request.Method.POST,
            APIConstants.BASE_URL + "/group/change_admin",
            getHeader(), params,
            GsonRequest.FormResult.class,
            new Response.Listener<GsonRequest.FormResult>() {
              @Override
              public void onResponse(GsonRequest.FormResult result) {
                if (result.message.equals("success")) {
                  getGroupData();
                  showDialog(ConfirmDialog.CHANGE_MANAGER_CONFIRM_FINISH, change_manage_name);
                }
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(delete_member_request);
  }

  public void getGroupData() {
    GsonRequest<Group> group_member_request =
        new GsonRequest<>(Request.Method.GET,
            APIConstants.BASE_URL + "/group/info?group_id=" + group_id,
            getHeader(), null,
            Group.class,
            new Response.Listener<Group>() {
              @Override
              public void onResponse(Group pGroup) {
                group = pGroup;
                handler.post(runnable);
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(group_member_request);
  }

  @Subscribe
  public void onChangeManage(ChangeManageAction pChangeManageAction) {
    if (pChangeManageAction.getVerify().equals(DeleteMemberAction.VERIFY_YES)) {
      ChangeManage();
    } else if (pChangeManageAction.getVerify().equals(DeleteMemberAction.VERIFY_NO)) {
      memberManageAdapter.refresh_select();
    } else if (pChangeManageAction.isFinished() == true) {
      Intent mIntent = new Intent(activity, MainActivity.class);
      startActivity(mIntent);
    } else {
      change_manage_id = pChangeManageAction.getUser_id();
      change_manage_name = pChangeManageAction.getUser_name();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    BusProvider.getInstance().unregister(this);
  }

}
