package unique.fancysherry.shr.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.Group;
import unique.fancysherry.shr.io.model.Share;
import unique.fancysherry.shr.io.model.ShareList;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.activity.BrowserActivity;
import unique.fancysherry.shr.ui.adapter.recycleview.DividerItemDecoration;
import unique.fancysherry.shr.ui.adapter.recycleview.GroupShareAdapter;
import unique.fancysherry.shr.ui.dialog.ShrDialog;
import unique.fancysherry.shr.ui.widget.TagGroup;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.SApplication;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ShareContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShareContentFragment extends BaseFragment {
  private static String group_name;
  private String group_id;
  private List<Share> shares_data = null;
  private RecyclerView share_list;
  private LinearLayout linearLayout;
  private Button first_shr_bt;
  private GroupShareAdapter groupShareAdapter;
  private List<Share> mShares;
  private Handler handler;
  private Runnable runnable;
  private Runnable runnable_changle_layout;
  private Runnable runnable_tagGroup;
  private User mUser;

  private EditText dialog_intro_input;
  private TagGroup tagGroup;
  private ArrayList<String> test_taggroup = new ArrayList<>();

  OnGetGroupIdListener onGetGroupIdListener;


  public interface OnGetGroupIdListener {
    void OnGetGroupId(String id);
    void OnGetGroupName(String name);
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @return A new instance of fragment SetttingFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static ShareContentFragment newInstance(String sgroup_name) {
    ShareContentFragment fragment = new ShareContentFragment();
    group_name = sgroup_name;
    return fragment;
  }

  public ShareContentFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getGroupId();
    handler = new Handler();
    runnable = new Runnable() {
      @Override
      public void run() {
        getShareList();
      }
    };

    runnable_changle_layout = new Runnable() {
      @Override
      public void run() {
        if (mShares.size() == 0) {
          share_list.setVisibility(View.INVISIBLE);
          linearLayout.setVisibility(View.VISIBLE);
        }
        else {
          share_list.setVisibility(View.VISIBLE);
          linearLayout.setVisibility(View.INVISIBLE);
        }
      }
    };
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view;
    view = inflater.inflate(R.layout.fragment_share_content, container, false);
    first_shr_bt = (Button) view.findViewById(R.id.new_group_button);
    first_shr_bt.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
//        start_dialog();
      }
    });
    share_list = (RecyclerView) view.findViewById(R.id.unique_group_share_list);
    share_list.setLayoutManager(new LinearLayoutManager(getActivity(),
        LinearLayoutManager.VERTICAL, false));
    linearLayout = (LinearLayout) view.findViewById(R.id.no_content_layout);
    linearLayout.setVisibility(View.INVISIBLE);
    initAdapter();
    return view;
  }

  public void initAdapter() {
    groupShareAdapter = new GroupShareAdapter(getActivity());
    share_list.setAdapter(groupShareAdapter);
    share_list.addItemDecoration(new DividerItemDecoration(20, "share"));
    groupShareAdapter
        .setOnItemClickListener(new GroupShareAdapter.OnRecyclerViewItemClickListener() {
          @Override
          public void onItemClick(View view, Share data) {
            Intent mIntent = new Intent(getActivity(), BrowserActivity.class);
            mIntent.putExtra("id", data.id);
            mIntent.putExtra(APIConstants.TYPE, APIConstants.SHARE_TYPE);
            startActivity(mIntent);
          }
        });
  }

  public void getShareUserId(String uid) {
    GsonRequest<User> group_share_user_id_request =
        new GsonRequest<>(Request.Method.GET,
            APIConstants.BASE_URL + "/homepage?uid=" + uid,
            getHeader(), null,
            User.class,
            new Response.Listener<User>() {
              @Override
              public void onResponse(User pUser) {
                handler.post(runnable);
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(group_share_user_id_request);
  }

  public void getGroupId() {
    GsonRequest<Group> group_share_id_request =
        new GsonRequest<>(Request.Method.GET,
            APIConstants.BASE_URL + "/group?group_name=" + group_name,
            getHeader(), null,
            Group.class,
            new Response.Listener<Group>() {
              @Override
              public void onResponse(Group pGroup) {
                LogUtil.e("group_id" + pGroup.group_id);
                group_id = pGroup.group_id;
                handler.post(runnable);
                onGetGroupIdListener.OnGetGroupId(group_id);
                onGetGroupIdListener.OnGetGroupName(group_name);
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(group_share_id_request);
  }


  public void getShareList() {
    GsonRequest<ShareList> group_share_request =
        new GsonRequest<>(Request.Method.GET,
            APIConstants.BASE_URL + "/group/shares?group_id=" + group_id,
            getHeader(), null,
            ShareList.class,
            new Response.Listener<ShareList>() {
              @Override
              public void onResponse(ShareList shares) {
                LogUtil.e("share" + shares.shares.toString());
                shares_data = shares.shares;

                try {
                  // groupShareAdapter.setTimeViewSize(DateUtil.calDaySize(shares_data));
                  groupShareAdapter.setData(shares.shares);
                  // groupShareAdapter.setViewTypeArray();
                } catch (ParseException e) {
                  e.printStackTrace();
                }
                mShares = shares.shares;
                handler.post(runnable_changle_layout);
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(group_share_request);
  }


  public void getUserData() {
    GsonRequest<User> group_share_request =
        new GsonRequest<>(Request.Method.GET,
            APIConstants.BASE_URL + "/homepage",
            getHeader(), null,
            User.class,
            new Response.Listener<User>() {
              @Override
              public void onResponse(User pUser) {
                mUser = pUser;
                handler.post(runnable_tagGroup);
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(group_share_request);
  }

  public void post_share_url(String group_name) {
    GsonRequest<GsonRequest.FormResult> group_share_url_request =
        new GsonRequest<>(Request.Method.POST,
            APIConstants.BASE_URL + "/share",
            getHeader(), getParams_share(group_name),
            GsonRequest.FormResult.class,
            new Response.Listener<GsonRequest.FormResult>() {
              @Override
              public void onResponse(GsonRequest.FormResult pGroup) {
                if (pGroup.message.equals("success"))
                  Toast.makeText(getActivity(), "share a page successful", Toast.LENGTH_LONG)
                      .show();
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(group_share_url_request);
  }

  public Map<String, String> getParams_share(String group_name) {
    JSONArray mJSONArray = new JSONArray();
    String[] gourps = new String[2];
    gourps[0] = "unique";
    gourps[1] = "text";
    mJSONArray.put(gourps[0]);
    mJSONArray.put(gourps[1]);

    String url = "http://stackoverflow.com/questions/8126299/android-share-browser-url-to-app";
    String intro = dialog_intro_input.getText().toString();
    Map<String, String> params = new HashMap<>();
    params.put("title", "aaaaaaaaaaaa");
    params.put("url", url);
    params.put("comment", intro);
    params.put("groups", null);
    return params;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    // This makes sure that the container activity has implemented
    // the callback interface. If not, it throws an exception
    try {
      onGetGroupIdListener = (OnGetGroupIdListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString()
          + " must implement OnHeadlineSelectedListener");
    }
  }


}
