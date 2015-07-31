package unique.fancysherry.shr.ui.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.SApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewGroupFragment extends Fragment {

  OnNewGroupListener onNewGroupLisner;

  public interface OnNewGroupListener
  {
    public void OnNewGroupFinish(String group_name);
  }

  private EditText new_group_name;
  private EditText new_group_introduce;
  private ImageView new_group_button;

  private Handler mHandler;
  private Runnable mRunnable;

  private String group_name_input;
  private String group_intro_input;


  public NewGroupFragment() {

    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    mHandler = new Handler();
    mRunnable = new Runnable() {
      @Override
      public void run() {
        Toast.makeText(getActivity(), "新组创建成功", Toast.LENGTH_LONG).show();
        onNewGroupLisner.OnNewGroupFinish(group_name_input);
      }
    };


    // Inflate the layout for this fragment

    View view = inflater.inflate(R.layout.fragment_new_group, container, false);
    new_group_name = (EditText) view.findViewById(R.id.new_group_name);
    new_group_introduce = (EditText) view.findViewById(R.id.new_group_introduce);
    new_group_button = (ImageView) view.findViewById(R.id.new_group_button);
    new_group_button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new_group_request();
      }
    });

    return view;
  }

  public void new_group_request()
  {
    GsonRequest<GsonRequest.FormResult> comment_request =
        new GsonRequest<>(Request.Method.POST,
            "http://104.236.46.64:8888/group", getHeader(),
            getParams(),
            GsonRequest.FormResult.class,
            new Response.Listener<GsonRequest.FormResult>() {
              @Override
              public void onResponse(GsonRequest.FormResult result) {

                if (result.message.equals("success")) {
                  mHandler.post(mRunnable);
                  new_group_name.setText("");
                  new_group_introduce.setText("");
                }
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(comment_request);
  }


  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    // This makes sure that the container activity has implemented
    // the callback interface. If not, it throws an exception
    try {
      onNewGroupLisner = (OnNewGroupListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString()
          + " must implement OnHeadlineSelectedListener");
    }
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


  public Map<String, String> getParams()
  {
    group_name_input = new_group_name.getText().toString();
    group_intro_input=new_group_introduce.getText().toString();
    Map<String, String> params = new HashMap<String, String>();
    params.put("name", group_name_input);
    params.put("intro",group_intro_input);
    return params;
  }


  public void executeRequest(Request request) {
    SApplication.getRequestManager().executeRequest(request, this);
  }



}
