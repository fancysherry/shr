package unique.fancysherry.shr.ui.fragment;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.APIConstants;
import unique.fancysherry.shr.io.model.NotifyList;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.adapter.recycleview.ItemDecoration.NotificationItemDecoration;
import unique.fancysherry.shr.ui.adapter.recycleview.NotificationAdapter;
import unique.fancysherry.shr.ui.otto.BusProvider;
import unique.fancysherry.shr.ui.otto.DataChangeAction;
import unique.fancysherry.shr.ui.otto.NotifyInviteAction;
import unique.fancysherry.shr.util.LogUtil;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.otto.Subscribe;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends BaseFragment {
  private RecyclerView invite_list;
  private Runnable runnable_get_invite_list;
  private Runnable runnable_accept_invite;
  private Handler handler;
  private NotifyList notifyList;
  private NotificationAdapter notificationAdapter;

  public static NotificationFragment newInstance(String param1, String param2) {
    NotificationFragment fragment = new NotificationFragment();
    return fragment;
  }

  public NotificationFragment() {}

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    handler = new Handler();
    runnable_accept_invite = new Runnable() {
      @Override
      public void run() {

      }
    };
    runnable_get_invite_list = new Runnable() {
      @Override
      public void run() {
        notificationAdapter.setData(notifyList.notifies);
      }
    };
    BusProvider.getInstance().register(this);
    get_invite_list();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View mView = inflater.inflate(R.layout.fragment_notification, container, false);
    invite_list = (RecyclerView) mView.findViewById(R.id.invite_list);
    initAdapter();
    return mView;
  }

  public void initAdapter() {
    invite_list.setLayoutManager(new LinearLayoutManager(getActivity(),
        LinearLayoutManager.VERTICAL, false));
    notificationAdapter = new NotificationAdapter(getActivity());
    invite_list.setAdapter(notificationAdapter);
    invite_list.addItemDecoration(new NotificationItemDecoration());
  }

  public void get_invite_list() {
    GsonRequest<NotifyList> group_share_request =
        new GsonRequest<>(Request.Method.GET,
            APIConstants.BASE_URL + "/user/notify",
            getHeader(), null,
            NotifyList.class,
            new Response.Listener<NotifyList>() {
              @Override
              public void onResponse(NotifyList pNotifyList) {
                notifyList = pNotifyList;
                handler.post(runnable_get_invite_list);
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(group_share_request);
  }

  public void accept_invite(String key) {
    GsonRequest<GsonRequest.FormResult> has_read_request =
        new GsonRequest<>(Request.Method.PUT,
            APIConstants.BASE_URL + "/user/notify/",
            getHeader(), null,
            GsonRequest.FormResult.class,
            new Response.Listener<GsonRequest.FormResult>() {
              @Override
              public void onResponse(GsonRequest.FormResult pGroup) {}
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    GsonRequest<GsonRequest.FormResult> accept_invite_request =
        new GsonRequest<>(Request.Method.GET,
            APIConstants.BASE_URL + "/user/accept/" + key,
            getHeader(), null,
            GsonRequest.FormResult.class,
            new Response.Listener<GsonRequest.FormResult>() {
              @Override
              public void onResponse(GsonRequest.FormResult pGroup) {
                if (pGroup.message.equals("success"))
                  Toast.makeText(getActivity(), "accept invite", Toast.LENGTH_LONG)
                      .show();
                handler.post(runnable_accept_invite);
                DataChangeAction mDataChangeAction = new DataChangeAction();
                mDataChangeAction.setStr(DataChangeAction.ADD_GROUP);
                BusProvider.getInstance().post(mDataChangeAction);
              }
            }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError pVolleyError) {
                LogUtil.e("response error " + pVolleyError);
              }
            });
    executeRequest(accept_invite_request);
    executeRequest(has_read_request);
  }


  // 这个注解一定要有,表示订阅了TestAction,并且方法的用 public 修饰的.方法名可以随意取,重点是参数,它是根据你的参数进行判断
  @Subscribe
  public void onNotifyInviteAccept(NotifyInviteAction notifyInviteAction) {
    // 这里更新视图或者后台操作,从TestAction获取传递参数.
    accept_invite(notifyInviteAction.getKey());
  }

  @Override
  public void onDestroy() {
    BusProvider.getInstance().unregister(this);
    super.onDestroy();
  }


}
