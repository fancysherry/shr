package unique.fancysherry.shr.ui.fragment;

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
import android.widget.LinearLayout;

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
import unique.fancysherry.shr.io.model.User;
import unique.fancysherry.shr.io.request.GsonRequest;
import unique.fancysherry.shr.ui.activity.BrowserActivity;
import unique.fancysherry.shr.ui.adapter.recycleview.DividerItemDecoration;
import unique.fancysherry.shr.ui.adapter.recycleview.GroupShareAdapter;
import unique.fancysherry.shr.ui.adapter.recycleview.InboxShareAdapter;
import unique.fancysherry.shr.ui.widget.Dialog.DialogPlus;
import unique.fancysherry.shr.ui.widget.Dialog.Holder;
import unique.fancysherry.shr.ui.widget.Dialog.OnClickListener;
import unique.fancysherry.shr.ui.widget.Dialog.OnDismissListener;
import unique.fancysherry.shr.ui.widget.Dialog.ViewHolder;
import unique.fancysherry.shr.ui.widget.TagGroup;
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
  private List<InboxShare> inbox_shares_data = new ArrayList<>();
  private InboxShareAdapter inboxShareAdapter;


  private RecyclerView inbox_share_list;
  private LinearLayout linearLayout;
  private Button first_shr_bt;

  private Handler handler;
  private Runnable runnable;
  private Runnable runnable_changle_layout;
  private Runnable runnable_tagGroup;
  private User mUser;

  private EditText dialog_intro_input;
  private TagGroup tagGroup;
  private ArrayList<String> test_taggroup = new ArrayList<>();

  // TODO: Rename and change types and number of parameters
  public static InboxShareFragment newInstance() {
    InboxShareFragment fragment = new InboxShareFragment();
    return fragment;
  }

  public InboxShareFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getShareList();
    handler = new Handler();
    runnable = new Runnable() {
      @Override
      public void run() {
        getShareList();
      }
    };

    runnable_tagGroup = new Runnable() {
      @Override
      public void run() {
        // initData();
      }
    };

    runnable_changle_layout = new Runnable() {
      @Override
      public void run() {
        if (inbox_shares_data.size() == 0) {
          inbox_share_list.setVisibility(View.INVISIBLE);
          linearLayout.setVisibility(View.VISIBLE);
        }
        else {
          inbox_share_list.setVisibility(View.VISIBLE);
          linearLayout.setVisibility(View.INVISIBLE);
        }
      }
    };
  }

  public void initAdapter() {
    inboxShareAdapter = new InboxShareAdapter(getActivity());
    inbox_share_list.setAdapter(inboxShareAdapter);
    inbox_share_list.addItemDecoration(new DividerItemDecoration(20,"inboxshare"));
    inboxShareAdapter
        .setOnItemClickListener(new InboxShareAdapter.OnRecyclerViewItemClickListener() {
          @Override
          public void onItemClick(View view, InboxShare data) {
            Intent mIntent = new Intent(getActivity(), BrowserActivity.class);
            mIntent.putExtra("id", data.id);
            mIntent.putExtra(APIConstants.TYPE,APIConstants.INBOX_SHARE_TYPE);
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
    first_shr_bt = (Button) view.findViewById(R.id.new_group_button);
    first_shr_bt.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showDialog(Gravity.CENTER);
      }
    });
    inbox_share_list = (RecyclerView) view.findViewById(R.id.unique_group_share_list);
    inbox_share_list.setLayoutManager(new LinearLayoutManager(getActivity(),
        LinearLayoutManager.VERTICAL, false));
    linearLayout = (LinearLayout) view.findViewById(R.id.no_content_layout);
    linearLayout.setVisibility(View.INVISIBLE);
    initAdapter();
    return view;
  }

  private void showDialog(int gravity) {
    Holder holder = new ViewHolder(R.layout.dialog_shr_content);
    LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
    View diaglog_view = mLayoutInflater.inflate(R.layout.dialog_shr_content, null);
    dialog_intro_input = (EditText) diaglog_view.findViewById(R.id.dialog_shr_content_intro);
    // tagGroup = (TagGroup) diaglog_view.findViewById(R.id.user_groups_tagGroup);
    // Log.e("taggroup",tagGroup.toString());
    // getUserData();

    OnClickListener clickListener = new OnClickListener() {
      @Override
      public void onClick(DialogPlus dialog, View view) {
        switch (view.getId()) {
          case R.id.dialog_shr_content_tagview1:
            break;

          case R.id.dialog_shr_content_tagview2:
            break;
          case R.id.dialog_shr_content_tagview3:
            // case R.id.user_groups_tagGroup:
            // LogUtil.e("################# click");
            //
            // // post_share_url();
            // tagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            // @Override
            // public void onTagClick(String tag) {
            // if (tag.equals("..."))
            // tagGroup.setAllTags(test_taggroup);
            // else if (tag.equals("<-"))
            // tagGroup.setTags(test_taggroup);
            // else
            // {
            // Toast.makeText(getActivity(), "We're glad that you love it" + tag,
            // Toast.LENGTH_LONG).show();
            // }
            // }
            // });
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
