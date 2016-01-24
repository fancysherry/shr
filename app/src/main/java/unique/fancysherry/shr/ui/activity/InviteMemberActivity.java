package unique.fancysherry.shr.ui.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.adapter.recycleview.InvitedMemberAdapter;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.SApplication;

public class InviteMemberActivity extends BaseActivity {
  @InjectView(R.id.group_invited_list)
  RecyclerView group_invited_list;
  @InjectView(R.id.invite_btn)
  Button invite_btn;
  @InjectView(R.id.invite_email)
  EditText invite_email;
  @InjectView(R.id.invite_activity_toolbar)
  Toolbar mToolbar;

  private String group_id;
  private Activity activity;
  private String invite_other_email;
  private InvitedMemberAdapter invitedMemberAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_invite_member);
    ButterKnife.inject(this);
    activity = this;
    Bundle mBundle = getIntent().getExtras();
    group_id = mBundle.getString("group_id");
    initView();
    initAdapter();
    initializeToolbar(mToolbar);
  }

  private void initView() {
    invite_btn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        invite_other_email = invite_email.getText().toString();
        inviteOthers();
      }
    });
  }

  private void initAdapter() {
    invitedMemberAdapter = new InvitedMemberAdapter(this);
    group_invited_list.setLayoutManager(new LinearLayoutManager(this,
        LinearLayoutManager.VERTICAL, false));
    group_invited_list.setAdapter(invitedMemberAdapter);
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

  public void inviteOthers() {
    Map<String, String> params = new HashMap<String, String>();
    params.put("group_id", group_id);
    params.put("email", invite_other_email);
    GsonRequest<GsonRequest.FormResult> invite_request =
        new GsonRequest<>(Request.Method.POST,
            APIConstants.BASE_URL + "/user/invite",
            getHeader(), params,
            GsonRequest.FormResult.class,
            new Response.Listener<GsonRequest.FormResult>() {
              @Override
              public void onResponse(GsonRequest.FormResult result) {
                // shareAdapter.setData(shares.shares);
                // mShares = shares.shares;

                if (result.message.equals("success")) {
                  Toast.makeText(activity, "邀请成功", Toast.LENGTH_LONG).show();
                }
                // handler.post(runnable_user);
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(invite_request);
  }

}
