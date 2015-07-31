package unique.fancysherry.shr.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.io.model.Group;
import unique.fancysherry.shr.io.model.Share;
import unique.fancysherry.shr.io.model.ShareList;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.activity.BrowserActivity;
import unique.fancysherry.shr.ui.adapter.recycleview.HeaderAdapter;
import unique.fancysherry.shr.ui.adapter.recycleview.ShareAdapter;
import unique.fancysherry.shr.ui.adapter.recycleview.stickheader.OnHeaderClickListener;
import unique.fancysherry.shr.ui.adapter.recycleview.stickheader.StickyHeadersBuilder;
import unique.fancysherry.shr.ui.adapter.recycleview.stickheader.StickyHeadersItemDecoration;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.SApplication;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ShareContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShareContentFragment extends Fragment implements OnHeaderClickListener {
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private static String group_name;
  private String group_id;

  private RecyclerView share_list;
  private LinearLayout linearLayout;

  private StickyHeadersItemDecoration top;
  private ShareAdapter shareAdapter;
  private List<Share> mShares;
  private List<String> header_items = new ArrayList<>();
  private Handler handler;
  private Runnable runnable;
  private Runnable runnable_changle_layout;

  OnGetGroupIdListener onGetGroupIdListener;


  public interface OnGetGroupIdListener
  {
    public void OnGetGroupId(String id);
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
        if (mShares == null) {
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
    view = inflater.inflate(R.layout.fragment_unique, container, false);
    share_list = (RecyclerView) view.findViewById(R.id.unique_group_share_list);
    share_list.setLayoutManager(new LinearLayoutManager(getActivity(),
            LinearLayoutManager.VERTICAL, false));
    linearLayout = (LinearLayout) view.findViewById(R.id.no_content_layout);
    linearLayout.setVisibility(View.INVISIBLE);
    initAdapter();
    return view;
  }

  public void initAdapter() {
    shareAdapter = new ShareAdapter(getActivity());
    top = new StickyHeadersBuilder()
        .setAdapter(shareAdapter)
        .setRecyclerView(share_list)
        .setStickyHeadersAdapter(new HeaderAdapter(header_items))
        .build();
    share_list.setAdapter(shareAdapter);
    share_list.addItemDecoration(top);
    shareAdapter.setOnItemClickListener(new ShareAdapter.OnRecyclerViewItemClickListener() {
      @Override
      public void onItemClick(View view, Share data) {
        Intent mIntent = new Intent(getActivity(), BrowserActivity.class);
        mIntent.putExtra("id", data.id);
        startActivity(mIntent);
      }
    });
  }


  public void getGroupId() {
    GsonRequest<Group> group_share_request =
        new GsonRequest<>(Request.Method.GET,
            "http://104.236.46.64:8888/group?group_name=" + group_name,
            getHeader(), null,
            Group.class,
            new Response.Listener<Group>() {
              @Override
              public void onResponse(Group pGroup) {
                LogUtil.e("group_id" + pGroup.group_id);
                group_id = pGroup.group_id;
                handler.post(runnable);
                onGetGroupIdListener.OnGetGroupId(group_id);
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(group_share_request);
  }



  public void getShareList() {
    GsonRequest<ShareList> group_share_request =
        new GsonRequest<>(Request.Method.GET,
            "http://104.236.46.64:8888/group/shares?group_id=55b30c94bb77ce4b216c31fc",
            getHeader(), null,
            ShareList.class,
            new Response.Listener<ShareList>() {
              @Override
              public void onResponse(ShareList shares) {
                LogUtil.e("share" + shares.shares.toString());
                shareAdapter.setData(shares.shares);
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
    params.put("email", "123@123.com");
    params.put("password", "123");
    return params;
  }



  @Override
  public void onHeaderClick(View header, long headerId) {
    TextView text = (TextView) header.findViewById(R.id.title);
    Toast.makeText(getActivity().getApplicationContext(), "Click on " + text.getText(),
        Toast.LENGTH_SHORT).show();
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



  public void executeRequest(Request request) {
    SApplication.getRequestManager().executeRequest(request, this);
  }


}
