package unique.fancysherry.shr.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.account.AccountManager;
import unique.fancysherry.shr.account.UserBean;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.InboxShare;
import unique.fancysherry.shr.io.model.InboxShareList;
import unique.fancysherry.shr.io.model.Share;
import unique.fancysherry.shr.io.model.ShareList;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.activity.BrowserActivity;
import unique.fancysherry.shr.ui.adapter.recycleview.DividerItemDecoration;
import unique.fancysherry.shr.ui.adapter.recycleview.GroupShareAdapter;
import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.SApplication;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link InboxShareFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InboxShareFragment extends Fragment {
  private List<InboxShare> inbox_shares_data=new ArrayList<>();

  // TODO: Rename and change types and number of parameters
  public static InboxShareFragment newInstance(String param1, String param2) {
    InboxShareFragment fragment = new InboxShareFragment();
    return fragment;
  }

  public InboxShareFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  public void initAdapter() {
//    groupShareAdapter = new GroupShareAdapter(getActivity());
//    share_list.setAdapter(groupShareAdapter);
//    share_list.addItemDecoration(new DividerItemDecoration());
//    groupShareAdapter
//            .setOnItemClickListener(new GroupShareAdapter.OnRecyclerViewItemClickListener() {
//              @Override
//              public void onItemClick(View view, Share data) {
//                Intent mIntent = new Intent(getActivity(), BrowserActivity.class);
//                mIntent.putExtra("id", data.id);
//                startActivity(mIntent);
//              }
//            });
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_inbox_share, container, false);
  }


  public void getShareList() {
//    GsonRequest<InboxShareList> group_share_request =
//            new GsonRequest<>(Request.Method.GET,
//                    APIConstants.BASE_URL + "/inbox_share",
//                    getHeader(), null,
//                    ShareList.class,
//                    new Response.Listener<InboxShareList>() {
//                      @Override
//                      public void onResponse(InboxShareList inbox_shares) {
//                        inbox_shares_data = inbox_shares.inbox_shares;
//
//                        try {
//                          // groupShareAdapter.setTimeViewSize(DateUtil.calDaySize(shares_data));
//                          groupShareAdapter.setData(shares.shares);
//                          // groupShareAdapter.setViewTypeArray();
//                        } catch (ParseException e) {
//                          e.printStackTrace();
//                        }
//                        mShares = shares.shares;
//                        handler.post(runnable_changle_layout);
//                      }
//                    }, new Response.ErrorListener() {
//              @Override
//              public void onErrorResponse(VolleyError pVolleyError) {
//                LogUtil.e("response error " + pVolleyError);
//              }
//            });
//    executeRequest(group_share_request);
  }

  public Map<String, String> getHeader() {
    Map<String, String> headers = new HashMap<>();
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

  public void executeRequest(Request request) {
    SApplication.getRequestManager().executeRequest(request, this);
  }


}
