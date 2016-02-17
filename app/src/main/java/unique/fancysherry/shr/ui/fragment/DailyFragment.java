package unique.fancysherry.shr.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.Share;
import unique.fancysherry.shr.ui.activity.BrowserActivity;
import unique.fancysherry.shr.ui.adapter.recycleview.GroupShareAdapter;
import unique.fancysherry.shr.ui.adapter.recycleview.ItemDecoration.ShrItemDecoration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link DailyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyFragment extends BaseFragment {
  private List<Share> daily_data = new ArrayList<>();
  private GroupShareAdapter groupShareAdapter;
  private RecyclerView inbox_share_list;



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
    inbox_share_list.setAdapter(groupShareAdapter);
    inbox_share_list.addItemDecoration(new ShrItemDecoration(20, "inboxshare"));
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
    inbox_share_list = (RecyclerView) view.findViewById(R.id.daily_list);
    inbox_share_list.setLayoutManager(new LinearLayoutManager(getActivity(),
        LinearLayoutManager.VERTICAL, false));
    initAdapter();
    return view;
  }

  public void getShareList() {
    JSONObject param = new JSONObject();
    try {
      param.put("action", "all");
    } catch (JSONException e) {
      e.printStackTrace();
    }

    JsonObjectRequest daily_request =
        new JsonObjectRequest(APIConstants.BASE_URL + "/daily", param,
            new Response.Listener<JSONObject>() {
              @Override
              public void onResponse(JSONObject response) {
                try {
                  String message = response.getString("message");
                  JSONArray share_id_list = response.getJSONArray("share_id_list");
                  daily_data = new Gson().fromJson(share_id_list.toString(),
                      new TypeToken<List<Share>>() {}.getType());

                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
              }
            });
    executeRequest(daily_request);
  }

}
