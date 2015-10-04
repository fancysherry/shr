package unique.fancysherry.shr.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;

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
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.activity.BrowserActivity;
import unique.fancysherry.shr.ui.adapter.recycleview.HeaderAdapter;
import unique.fancysherry.shr.ui.adapter.recycleview.ShareAdapter;
import unique.fancysherry.shr.ui.adapter.recycleview.stickheader.OnHeaderClickListener;
import unique.fancysherry.shr.ui.adapter.recycleview.stickheader.StickyHeadersBuilder;
import unique.fancysherry.shr.ui.adapter.recycleview.stickheader.StickyHeadersItemDecoration;
import unique.fancysherry.shr.ui.widget.Dialog.DialogPlus;
import unique.fancysherry.shr.ui.widget.Dialog.Holder;
import unique.fancysherry.shr.ui.widget.Dialog.OnClickListener;
import unique.fancysherry.shr.ui.widget.Dialog.OnDismissListener;
import unique.fancysherry.shr.ui.widget.Dialog.OnItemClickListener;
import unique.fancysherry.shr.ui.widget.Dialog.ViewHolder;
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
  private Button first_shr_bt;

  private StickyHeadersItemDecoration top;
  private ShareAdapter shareAdapter;
  private List<Share> mShares;
  private List<String> header_items = new ArrayList<>();
  private Handler handler;
  private Runnable runnable;
  private Runnable runnable_changle_layout;

  private EditText dialog_intro_input;

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

  private void showDialog(int gravity) {
    Holder holder = new ViewHolder(R.layout.dialog_shr_content);
    LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
    View diaglog_view = mLayoutInflater.inflate(R.layout.dialog_shr_content, null);
    dialog_intro_input = (EditText) diaglog_view.findViewById(R.id.dialog_shr_content_intro);


    OnClickListener clickListener = new OnClickListener() {
      @Override
      public void onClick(DialogPlus dialog, View view) {
        switch (view.getId()) {

          case R.id.dialog_shr_content_tagview1:
            Toast.makeText(getActivity(), "We're glad that you love it", Toast.LENGTH_LONG).show();
            post_share_url();

            break;
          case R.id.dialog_shr_content_tagview2:
            Toast.makeText(getActivity(), "Confirm button clicked", Toast.LENGTH_LONG).show();
            break;
          case R.id.dialog_shr_content_tagview3:
            Toast.makeText(getActivity(), "Close button clicked", Toast.LENGTH_LONG).show();
            break;
        }
        dialog.dismiss();
      }
    };

    OnDismissListener dismissListener = new OnDismissListener() {
      @Override
      public void onDismiss(DialogPlus dialog) {}
    };
    showOnlyContentDialog(holder, gravity, dismissListener, clickListener);



  }



  private void showOnlyContentDialog(Holder holder, int gravity,
      OnDismissListener dismissListener, OnClickListener clickListener
      ) {
    final DialogPlus dialog = DialogPlus.newDialog(getActivity())
        .setContentHolder(holder)
        .setGravity(gravity)
        .setOnDismissListener(dismissListener)
        .setCancelable(true)
        .setOnClickListener(clickListener)
        .create();
    dialog.show();
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view;
    view = inflater.inflate(R.layout.fragment_unique, container, false);
    first_shr_bt = (Button) view.findViewById(R.id.new_group_button);
    first_shr_bt.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showDialog(Gravity.CENTER);
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


  public void post_share_url() {
    GsonRequest<GsonRequest.FormResult> group_share_url_request =
        new GsonRequest<>(Request.Method.POST,
            APIConstants.BASE_URL + "/share",
            getHeader(), getParams_share(),
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


  public Map<String, String> getParams_share() {
    JSONArray mJSONArray = new JSONArray();
    String[] gourps = new String[2];
    gourps[0] = "unique";
    gourps[1] = "text";
    mJSONArray.put(gourps[0]);
    mJSONArray.put(gourps[1]);

    String url = "http://stackoverflow.com/questions/8126299/android-share-browser-url-to-app";
    String intro = dialog_intro_input.getText().toString();
    Map<String, String> params = new HashMap<String, String>();
    params.put("title", "aaaaaaaaaaaa");
    params.put("url", url);
    params.put("comment", intro);
    params.put("groups", mJSONArray.toString());
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
