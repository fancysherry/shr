package unique.fancysherry.shr.ui.activity;

import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.Group;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.adapter.recycleview.DeleteMemberAdapter;
import unique.fancysherry.shr.ui.dialog.ConfirmDialog;
import unique.fancysherry.shr.ui.otto.BusProvider;
import unique.fancysherry.shr.ui.otto.DataChangeAction;
import unique.fancysherry.shr.ui.otto.DeleteMemberAction;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.SApplication;

public class GroupMemberDeleteActivity extends AppCompatActivity {
  @InjectView(R.id.group_member_list)
  RecyclerView group_member_list;

  private String group_id;
  private Group group;

  private String delete_member_id;
  private String delete_member_name;

  private DeleteMemberAdapter deleteMemberAdapter;

  private Handler handler;
  private Runnable runnable;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_group_member_delete);
    BusProvider.getInstance().register(this);
    ButterKnife.inject(this);
    Bundle mBundle = getIntent().getExtras();
    group_id = mBundle.getString("group_id");
    initAdapter();
    getGroupData();
    initializeToolbar();

    handler = new Handler();
    runnable = new Runnable() {
      @Override
      public void run() {
        deleteMemberAdapter.setData(group.users);
      }
    };

  }

  private void initAdapter()
  {
    deleteMemberAdapter = new DeleteMemberAdapter(this);
    group_member_list.setLayoutManager(new LinearLayoutManager(this,
        LinearLayoutManager.VERTICAL, false));
    group_member_list.setAdapter(deleteMemberAdapter);
  }

  // Resolve the given attribute of the current theme
  private int getAttributeColor(int resId) {
    TypedValue typedValue = new TypedValue();
    getTheme().resolveAttribute(resId, typedValue, true);
    int color = 0x000000;
    if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT
        && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
      // resId is a color
      color = typedValue.data;
    } else {
      // resId is not a color
    }
    return color;
  }

  protected void initializeToolbar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      getWindow().setStatusBarColor(getAttributeColor(R.attr.colorPrimaryDark));
    }
    Toolbar mToolbar = (Toolbar) findViewById(R.id.group_member_activity_toolbar);
    setSupportActionBar(mToolbar);
    getSupportActionBar().setTitle("");
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);

  }

  void showDialog(String type, String name) {
    if (type.equals(ConfirmDialog.DELETE_MEMBER_CONFIRM)) {
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      Fragment prev = getSupportFragmentManager().findFragmentByTag("delete_dialog_one");
      if (prev != null) {
        ft.remove(prev);
      }
      ConfirmDialog dialogFrag = ConfirmDialog.newInstance(type);
      dialogFrag.delete_name(name);
      dialogFrag.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ShrDialog);
      dialogFrag.show(getSupportFragmentManager(), "delete_dialog_one");
    }
    else if (type.equals(ConfirmDialog.DELETE_MEMBER_CONFIRM_AGAIN)) {
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      Fragment prev = getSupportFragmentManager().findFragmentByTag("delete_dialog_two");
      if (prev != null) {
        ft.remove(prev);
      }
      ConfirmDialog dialogFrag = ConfirmDialog.newInstance(type);
      dialogFrag.delete_name(name);
      dialogFrag.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ShrDialog);
      dialogFrag.show(getSupportFragmentManager(), "delete_dialog_two");
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

  public void getGroupData() {
    GsonRequest<Group> group_member_request =
        new GsonRequest<>(Request.Method.GET,
            APIConstants.BASE_URL + "/group/info?group_id=" + group_id,
            getHeader(), null,
            Group.class,
            new Response.Listener<Group>() {
              @Override
              public void onResponse(Group pGroup) {
                // shareAdapter.setData(shares.shares);
                // mShares = shares.shares;
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

  public void DeleteMember() {
    GsonRequest<GsonRequest.FormResult> delete_member_request =
        new GsonRequest<>(Request.Method.PUT,
            APIConstants.BASE_URL + "/group/info?group_id=" + group_id,
            getHeader(), getParams_delete(),
            GsonRequest.FormResult.class,
            new Response.Listener<GsonRequest.FormResult>() {
              @Override
              public void onResponse(GsonRequest.FormResult result) {
                if (result.message.equals("success")) {
                  getGroupData();
                  showDialog(ConfirmDialog.DELETE_MEMBER_CONFIRM_AGAIN, delete_member_name);
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

  public Map<String, String> getHeader() {
    Map<String, String> headers = new HashMap<String, String>();
    UserBean currentUser = AccountManager.getInstance().getCurrentUser();
    if (currentUser != null && currentUser.getCookieHolder() != null) {
      currentUser.getCookieHolder().generateCookieString();
      headers.put("Cookie", currentUser.getCookieHolder().generateCookieString());
    }

    headers
        .put(
            "User-Agent",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36");
    return headers;
  }


  public Map<String, String> getParams() {
    Map<String, String> params = new HashMap<String, String>();
    params.put("group_id", group_id);
    return params;
  }

  public Map<String, String> getParams_delete() {
    Map<String, String> params = new HashMap<String, String>();
    params.put("group_id", group_id);
    params.put("user_id", delete_member_id);
    return params;
  }


  public void executeRequest(Request request) {
    SApplication.getRequestManager().executeRequest(request, this);
  }

  // 这个注解一定要有,表示订阅了TestAction,并且方法的用 public 修饰的.方法名可以随意取,重点是参数,它是根据你的参数进行判断
  @Subscribe
  public void onDeleteMember(DeleteMemberAction pDeleteMemberAction) {
    if (pDeleteMemberAction.getVerify().equals(DeleteMemberAction.VERIFY_YES)) {
      DeleteMember();
    }
    else if (pDeleteMemberAction.getVerify().equals(DeleteMemberAction.VERIFY_NO)) {
      pDeleteMemberAction.getDelete_btn().setBackgroundColor(
          getResources().getColor(R.color.delete_member_color));
    }
    else {
      delete_member_id = pDeleteMemberAction.getUser_id();
      delete_member_name = pDeleteMemberAction.getUser_name();
      showDialog(ConfirmDialog.DELETE_MEMBER_CONFIRM, pDeleteMemberAction.getUser_name());
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    BusProvider.getInstance().unregister(this);
  }


}
