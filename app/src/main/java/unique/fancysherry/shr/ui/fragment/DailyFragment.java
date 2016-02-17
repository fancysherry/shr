package unique.fancysherry.shr.ui.fragment;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.Share;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.activity.BrowserActivity;
import unique.fancysherry.shr.ui.adapter.recycleview.GroupShareAdapter;
import unique.fancysherry.shr.ui.adapter.recycleview.ItemDecoration.ShrItemDecoration;
import unique.fancysherry.shr.util.LogUtil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
  private List<Share> daily_data = new ArrayList<>();
  private List<String> daily_data_id = new ArrayList<>();
  private GroupShareAdapter groupShareAdapter;
  private RecyclerView daily_list;



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
  }

  public void initAdapter() {
    groupShareAdapter = new GroupShareAdapter(getActivity());
    daily_list.setAdapter(groupShareAdapter);
    daily_list.addItemDecoration(new ShrItemDecoration(20, "share"));
    groupShareAdapter
        .setOnItemClickListener(new GroupShareAdapter.OnRecyclerViewItemClickListener() {
          @Override
          public void onItemClick(View view, Share data) {
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
    view = inflater.inflate(R.layout.fragment_daily, container, false);
    daily_list = (RecyclerView) view.findViewById(R.id.daily_list);
    daily_list.setLayoutManager(new LinearLayoutManager(getActivity(),
        LinearLayoutManager.VERTICAL, false));
    initAdapter();
    return view;
  }

  public void getShareList() {
    Map<String, String> params = new HashMap<String, String>();
    params.put("action", "all");
    GsonRequest<GsonRequest.FormResult> daily_request =
        new GsonRequest<>(Request.Method.POST, APIConstants.BASE_URL + "/dairy", getHeader(),
            params, GsonRequest.FormResult.class,
            new Response.Listener<GsonRequest.FormResult>() {
              @Override
              public void onResponse(GsonRequest.FormResult result) {
                if (result.message.equals("success")) {
                  daily_data_id = result.share_id_list;
                  loadShare();
                }
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(daily_request);
  }

  private void loadShare() {
    for (String id : daily_data_id) {
      Map<String, String> params = new HashMap<String, String>();
      params.put("share_id", id);
      GsonRequest<Share> share_request =
          new GsonRequest<>(Request.Method.GET, APIConstants.BASE_URL + "/share", getHeader(),
              params, Share.class,
              new Response.Listener<Share>() {
                @Override
                public void onResponse(Share share) {
                  daily_data.add(share);
                  try {
                    groupShareAdapter.setData(daily_data);
                  } catch (ParseException e) {
                    e.printStackTrace();
                  }
                }
              }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError pVolleyError) {
                  LogUtil.e("response error " + pVolleyError);
                }
              });
      executeRequest(share_request);
    }
  }

}
