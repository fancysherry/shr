package unique.fancysherry.shr.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.Comment;
import unique.fancysherry.shr.io.model.CommentList;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.adapter.recycleview.CommentAdapter;

import unique.fancysherry.shr.util.LogUtil;

public class CommentActivity extends BaseActivity {
  private RecyclerView comment_list;
  private CommentAdapter commentAdapter;
  private LinearLayout comment_send;
  private LinearLayout comment_sending_progress;
  private String share_id;
  private Context context;
  private EditText comment_send_content;
  private TextView comment_sending_progress_text;
  private String comment_content_input;
  @InjectView(R.id.comment_activity_toolbar)
  Toolbar mToolbar;

  private List<Comment> comment_data;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_comment);
    ButterKnife.inject(this);
    context = this;
    Bundle mBundle = getIntent().getExtras();
    share_id = mBundle.getString("share_id");
    comment_sending_progress_text = (TextView) findViewById(R.id.comment_sending_progress_text1);
    comment_send_content = (EditText) findViewById(R.id.comment_send_content);
    comment_send = (LinearLayout) findViewById(R.id.comment_send_layout);
    comment_sending_progress = (LinearLayout) findViewById(R.id.comment_sending_progress_layout);
    comment_sending_progress.setVisibility(View.INVISIBLE);
    comment_send.setVisibility(View.VISIBLE);
    comment_list = (RecyclerView) findViewById(R.id.share_comment_list);
    comment_list.setLayoutManager(new LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false));
    initAdapter();
    initializeToolbar(mToolbar);
  }

  @OnClick({R.id.comment_send_button})
  public void click(View mView){
    switch (mView.getId()){
      case R.id.comment_send_button:
        sendComment();
        break;
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

  public void getCommentList() {
    Map<String, String> params = new HashMap<String, String>();
    params.put("share_id", share_id);
    GsonRequest<CommentList> comment_request =
        new GsonRequest<>(Request.Method.GET,
            APIConstants.BASE_URL + "/comment", getHeader(),
            params,
            CommentList.class,
            new Response.Listener<CommentList>() {
              @Override
              public void onResponse(CommentList comments) {
                commentAdapter.setData(comments.comments);
                comment_data = comments.comments;
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    getSupportActionBar().setTitle("评论 " + comment_data.size());
                  }
                });
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(comment_request);
  }


  public void sendComment() {
    comment_sending_progress.setVisibility(View.VISIBLE);
    comment_send.setVisibility(View.INVISIBLE);
    comment_sending_progress_text.setText("评论发送中");
    comment_content_input = comment_send_content.getText().toString();
    Map<String, String> params = new HashMap<String, String>();
    params.put("share_id", share_id);
    params.put("content", comment_content_input);

    GsonRequest<GsonRequest.FormResult> comment_request =
        new GsonRequest<>(Request.Method.POST,
            APIConstants.BASE_URL + "/comment", getHeader(),
            params,
            GsonRequest.FormResult.class,
            new Response.Listener<GsonRequest.FormResult>() {
              @Override
              public void onResponse(GsonRequest.FormResult result) {
                if (result.message.equals("success")) {
                  getCommentList();
                  runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      comment_send_content.setText("");
                      Toast.makeText(context, "评论发送成功", Toast.LENGTH_LONG).show();
                      comment_sending_progress.setVisibility(View.INVISIBLE);
                      comment_send.setVisibility(View.VISIBLE);
                      getSupportActionBar().setTitle("评论 " + comment_data.size());
                    }
                  });
                } else
                  runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      Toast.makeText(context, "评论发送失败", Toast.LENGTH_LONG).show();
                      comment_sending_progress.setVisibility(View.INVISIBLE);
                      comment_send.setVisibility(View.VISIBLE);
                      getSupportActionBar().setTitle("评论 " + comment_data.size());
                    }
                  });
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    Toast.makeText(context, "评论发送失败", Toast.LENGTH_LONG).show();
                    comment_sending_progress.setVisibility(View.INVISIBLE);
                    comment_send.setVisibility(View.VISIBLE);
                    getSupportActionBar().setTitle("评论 " + comment_data.size());
                  }
                });
              }
            });
    executeRequest(comment_request);
  }

  public void initAdapter() {
    getCommentList();
    commentAdapter = new CommentAdapter(this);
    comment_list.setAdapter(commentAdapter);
    commentAdapter.setOnItemClickListener(new CommentAdapter.OnRecyclerViewItemClickListener() {
      @Override
      public void onItemClick(View view, Comment data) {}
    });
  }

}
