package unique.fancysherry.shr.ui.activity;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.io.model.Group;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.adapter.recycleview.MemberAdapter;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.SApplication;

public class GroupActivity extends ActionBarActivity {
  @InjectView(R.id.group_create_time)
  TextView group_create_time;
  @InjectView(R.id.group_header_share_number)
  TextView group_shr_num;
  @InjectView(R.id.group_invite_button)
  ImageView group_invite_bt;
  @InjectView(R.id.group_manage_button)
  ImageView group_manage_bt;
  @InjectView(R.id.group_activity_group_members_list)
  RecyclerView group_member_list;

  private Group group;
  private String group_id;
  private Handler handler;
  private Runnable runnable;

  private MemberAdapter memberAdapter;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_group);
    ButterKnife.inject(this);
    Bundle mBundle = getIntent().getExtras();
    group_id = mBundle.getString("group_id");
    getGroupData();
    initAdapter();



    handler = new Handler();
    runnable = new Runnable() {
      @Override
      public void run() {
        group_create_time.setText(group.create_time);
//        group_shr_num.setText(String.valueOf(group.shares.size()));
        memberAdapter.setData(group.users);
      }
    };


  }

  private void initAdapter()
  {
    memberAdapter = new MemberAdapter(this);
    group_member_list.setLayoutManager(new LinearLayoutManager(this,
        LinearLayoutManager.VERTICAL, false));
    group_member_list.setAdapter(memberAdapter);
  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
    ButterKnife.reset(this);
  }


  public void getGroupData()
  {
    GsonRequest<Group> group_share_request =
        new GsonRequest<>(Request.Method.GET,
            "http://104.236.46.64:8888/group/info?group_id=" + group_id,
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
    executeRequest(group_share_request);


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


  public void executeRequest(Request request) {
    SApplication.getRequestManager().executeRequest(request, this);
  }



}
