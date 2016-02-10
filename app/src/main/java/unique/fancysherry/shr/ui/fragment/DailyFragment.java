package unique.fancysherry.shr.ui.fragment;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.InboxShare;
import unique.fancysherry.shr.io.model.InboxShareList;
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.activity.BrowserActivity;
import unique.fancysherry.shr.ui.adapter.recycleview.ItemDecoration.ShrItemDecoration;
import unique.fancysherry.shr.ui.adapter.recycleview.InboxShareAdapter;
import unique.fancysherry.shr.util.LogUtil;

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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link DailyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyFragment extends BaseFragment {
  private List<InboxShare> inbox_shares_data = new ArrayList<>();
  private InboxShareAdapter inboxShareAdapter;
  private RecyclerView inbox_share_list;
  private LinearLayout linearLayout;

  private Handler handler;
  private Runnable runnable;
  private Runnable runnable_changle_layout;
  private Runnable runnable_tagGroup;
  private User mUser;

  // TODO: Rename and change types and number of parameters
  public static DailyFragment newInstance() {
    DailyFragment fragment = new DailyFragment();
    return fragment;
  }

  public DailyFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getShareList();
    handler = new Handler();
    runnable_changle_layout = new Runnable() {
      @Override
      public void run() {
        if (inbox_shares_data.size() == 0) {
          inbox_share_list.setVisibility(View.INVISIBLE);
          linearLayout.setVisibility(View.VISIBLE);
        } else {
          inbox_share_list.setVisibility(View.VISIBLE);
          linearLayout.setVisibility(View.INVISIBLE);
        }
      }
    };
  }

  public void initAdapter() {
    inboxShareAdapter = new InboxShareAdapter(getActivity());
    inbox_share_list.setAdapter(inboxShareAdapter);
    inbox_share_list.addItemDecoration(new ShrItemDecoration(20, "inboxshare"));
    inboxShareAdapter
        .setOnItemClickListener(new InboxShareAdapter.OnRecyclerViewItemClickListener() {
          @Override
          public void onItemClick(View view, InboxShare data) {
            Intent mIntent = new Intent(getActivity(), BrowserActivity.class);
            mIntent.putExtra("id", data.id);
            mIntent.putExtra(APIConstants.TYPE, APIConstants.INBOX_SHARE_TYPE);
            startActivity(mIntent);
          }
        });
  }

  /***
   * 和ShareContentFragment 公用同一组视图
   * 
   * @param inflater
   * @param container
   * @param savedInstanceState
   * @return
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view;
    view = inflater.inflate(R.layout.fragment_share_content, container, false);
    inbox_share_list = (RecyclerView) view.findViewById(R.id.group_share_list);
    inbox_share_list.setLayoutManager(new LinearLayoutManager(getActivity(),
        LinearLayoutManager.VERTICAL, false));
    linearLayout = (LinearLayout) view.findViewById(R.id.no_content_layout);
    linearLayout.setVisibility(View.INVISIBLE);
    initAdapter();
    return view;
  }

  public void getShareList() {
    GsonRequest<InboxShareList> group_share_request =
        new GsonRequest<>(Request.Method.GET,
            APIConstants.BASE_URL + "/inbox_share",
            getHeader(), null,
            InboxShareList.class,
            new Response.Listener<InboxShareList>() {
              @Override
              public void onResponse(InboxShareList inbox_shares) {
                inbox_shares_data = inbox_shares.inbox_shares;
                try {
                  // groupShareAdapter.setTimeViewSize(DateUtil.calDaySize(shares_data));
                  inboxShareAdapter.setData(inbox_shares.inbox_shares);
                  // groupShareAdapter.setViewTypeArray();
                } catch (ParseException e) {
                  e.printStackTrace();
                }
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

}
