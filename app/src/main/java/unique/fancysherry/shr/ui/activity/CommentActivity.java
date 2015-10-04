package unique.fancysherry.shr.ui.activity;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.Comment;
import unique.fancysherry.shr.io.model.CommentList;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.adapter.recycleview.CommentAdapter;

import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.SApplication;

public class CommentActivity extends AppCompatActivity {

  private RecyclerView comment_list;
  private ImageView send_button;
  private CommentAdapter commentAdapter;
  private LinearLayout comment_send;
  private LinearLayout comment_sending_progress;
  private String share_id;
  private Context context;
  private EditText comment_send_content;
  private TextView comment_sending_progress_text;


  private Handler mHandler;
  private Runnable mRunnable;
  private Runnable mRunable_failure;

  private String comment_content_input;


  private List<Comment> comment_data;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_comment);
    context = this;

    mHandler = new Handler();
    mRunnable = new Runnable() {
      @Override
      public void run() {
        Toast.makeText(context, "评论发送成功", Toast.LENGTH_LONG).show();
        comment_sending_progress.setVisibility(View.INVISIBLE);
        comment_send.setVisibility(View.VISIBLE);
      }
    };

    mRunable_failure = new Runnable() {
      @Override
      public void run() {
        Toast.makeText(context, "评论发送失败", Toast.LENGTH_LONG).show();
        comment_sending_progress.setVisibility(View.INVISIBLE);
        comment_send.setVisibility(View.VISIBLE);
      }
    };


    Bundle mBundle = getIntent().getExtras();
    share_id = mBundle.getString("share_id");

    comment_sending_progress_text=(TextView)findViewById(R.id.comment_sending_progress_text1);
    comment_send_content = (EditText) findViewById(R.id.comment_send_content);
    comment_send = (LinearLayout) findViewById(R.id.comment_send_layout);
    comment_sending_progress = (LinearLayout) findViewById(R.id.comment_sending_progress_layout);
    comment_sending_progress.setVisibility(View.INVISIBLE);
    comment_send.setVisibility(View.VISIBLE);

    comment_list = (RecyclerView) findViewById(R.id.share_comment_list);
    comment_list.setLayoutManager(new LinearLayoutManager(this,
        LinearLayoutManager.VERTICAL, false));
    send_button = (ImageView) findViewById(R.id.comment_send_button);
    send_button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendComment();
      }
    });

    initAdapter();
  }


  public void getCommentList()
  {
    GsonRequest<CommentList> comment_request =
        new GsonRequest<>(Request.Method.GET,
            "http://104.236.46.64:8888/comment", getHeader(),
            getParams_list_comment(),
            CommentList.class,
            new Response.Listener<CommentList>() {
              @Override
              public void onResponse(CommentList comments) {
                commentAdapter.setData(comments.comments);
                comment_data = comments.comments;
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(comment_request);


  }


  public void sendComment()
  {
    comment_sending_progress.setVisibility(View.VISIBLE);
    comment_send.setVisibility(View.INVISIBLE);
    comment_sending_progress_text.setText("评论发送中");

    GsonRequest<GsonRequest.FormResult> comment_request =
        new GsonRequest<>(Request.Method.POST,
                APIConstants.BASE_URL+"/comment", getHeader(),
            getParams_send_comment(),
            GsonRequest.FormResult.class,
            new Response.Listener<GsonRequest.FormResult>() {
              @Override
              public void onResponse(GsonRequest.FormResult result) {

                if (result.message.equals("success")) {
                  mHandler.post(mRunnable);
                  getCommentList();
                  comment_send_content.setText("");
                }
                else
                  mHandler.post(mRunable_failure);


              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
                mHandler.post(mRunable_failure);
              }
            });
    executeRequest(comment_request);
  }

  public void replyComment()
  {
    comment_sending_progress.setVisibility(View.VISIBLE);
    comment_send.setVisibility(View.INVISIBLE);
    comment_sending_progress_text.setText("回复发送中");


    GsonRequest<GsonRequest.FormResult> comment_request =
            new GsonRequest<>(Request.Method.POST,
                    APIConstants.BASE_URL+"/comment", getHeader(),
                    getParams_send_comment(),
                    GsonRequest.FormResult.class,
                    new Response.Listener<GsonRequest.FormResult>() {
                      @Override
                      public void onResponse(GsonRequest.FormResult result) {

                        if (result.message.equals("success")) {
                          mHandler.post(mRunnable);
                          getCommentList();
                          comment_send_content.setText("");
                        }
                        else
                          mHandler.post(mRunable_failure);


                      }
                    }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
                mHandler.post(mRunable_failure);
              }
            });
    executeRequest(comment_request);
  }


  public void initAdapter()
  {
    getCommentList();
    commentAdapter = new CommentAdapter(this);
    comment_list.setAdapter(commentAdapter);
    commentAdapter.setOnItemClickListener(new CommentAdapter.OnRecyclerViewItemClickListener() {
      @Override
      public void onItemClick(View view, Comment data) {}
    });
  }


  public Map<String, String> getHeader()
  {
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


  public Map<String, String> getParams_send_comment()
  {
    comment_content_input = comment_send_content.getText().toString();
    Map<String, String> params = new HashMap<String, String>();
    params.put("share_id", share_id);
    params.put("content", comment_content_input);
    return params;
  }

  public Map<String, String> getParams_reply_comment()
  {
    Map<String, String> params = new HashMap<String, String>();
    params.put("share_id", share_id);
    params.put("content", "");
    params.put("to_user", "");
    return params;
  }


  public Map<String, String> getParams_list_comment()
  {
    Map<String, String> params = new HashMap<String, String>();
    params.put("share_id", share_id);
    return params;
  }



  public void executeRequest(Request request) {
    SApplication.getRequestManager().executeRequest(request, this);
  }



}
