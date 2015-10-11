package unique.fancysherry.shr.ui.adapter.recycleview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.io.model.Share;
import unique.fancysherry.shr.ui.adapter.recycleview.ViewAdapter.DateViewHolder;
import unique.fancysherry.shr.ui.adapter.recycleview.ViewAdapter.GroupShareViewHolder;
import unique.fancysherry.shr.util.DateUtil;

/**
 * Created by fancysherry on 15-7-14.
 */
public class GroupShareAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements
    View.OnClickListener {

  private List<Share> items = null;

  private ArrayList<Share> view_type_array = null;

  private int mPosition = 0;

  // private int time_size;

  private Context context;

  public GroupShareAdapter(Context pContext)
  {
    this.context = pContext;
    setHasStableIds(true);//必须该方法，不然可能bindviewtype不执行
  }

  public enum CommonFrature {
    COMMON, FOOTER, DATE
  }

  public int getPosition()
  {
    return mPosition;
  }

  public void setPosition(int position)
  {
    this.mPosition = position;
  }

  public List<Share> getViewTypeArray()
  {
    return view_type_array;
  }

  public void setData(List<Share> items) throws ParseException {
    this.items = items;
    Collections.reverse(this.items);
    setViewTypeArray();
    notifyDataSetChanged();
  }

  // public void setTimeViewSize(int time_size) {
  // this.time_size = time_size;
  // }


  // 该算法是想对不同日期的view进行分组，如-1-1-2-2-2-2-3-3-3-3-4-4-4-4(share_time_flag)，同样的数字代表日期相同，然后插入0进去形成新的数组。
  // 0代表显示时间的viewtype,>0代表不同日期的share item的viewtype；
  // public int[] setViewTypeArray() throws ParseException {
  // int[] share_item_array = new int[items.size()];
  // int share_time_flag = -1;
  // String flag = items.get(0).share_time;
  //
  //
  // // share_item_array 形成了 -1-1-2-2-2-2-3-3-3-3-4-4-4-4(share_time_flag)
  // for (int i = 0; i < items.size(); i++) {
  // if (DateUtil.isDateEquals(items.get(i).share_time, flag))
  // share_item_array[i] = share_time_flag;
  // else {
  // flag = items.get(i).share_time;
  // share_time_flag--;
  // share_item_array[i] = share_time_flag;
  // }
  // }
  //
  // // 插入0的操作
  // int flag1=share_item_array[0];
  // for (int j = 0; j < time_size + items.size(); j++) {
  // }
  // return null;
  // }



  // 上面的写的过于繁琐，现在用一个简单的算法，效率低一点，但是代码更美观
  public void setViewTypeArray() throws ParseException {
    Log.e("调用次数", "setViewTypeArray()");
    String flag = items.get(0).share_time;
    view_type_array = new ArrayList<>();
    view_type_array.add(new Share("date"));
    for (int i = 0; i < items.size(); i++) {
      // Log.e("share_time",i+items.get(i).share_time);
      if (DateUtil.isDateEquals(items.get(i).share_time, flag))
        view_type_array.add(items.get(i));
      else {
        flag = items.get(i).share_time;
        view_type_array.add(new Share("date"));
        view_type_array.add(items.get(i));
      }
    }
//    Log.e("view_type_array_size", String.valueOf(view_type_array.size()));
//    for (int j = 0; j < view_type_array.size(); j++)
//    {
//      if (view_type_array.get(j) != null)
//        Log.e("view_type_array", j + " " + view_type_array.get(j).share_time);
//      else
//        Log.e("view_type_array", j + " null");
//    }

    // Log.e("view_type_array_size", String.valueOf(view_type_array.size()));

  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = null;
    if (CommonFrature.COMMON.ordinal() == viewType) {
      itemView =
          LayoutInflater.from(parent.getContext()).inflate(R.layout.group_share_list_item,
              parent,
              false);
      itemView.setOnClickListener(this);
      // Log.e("viewtype1:", String.valueOf(viewType));
      return new GroupShareViewHolder(itemView, this, context);
    }
    else {
      itemView =
          LayoutInflater.from(parent.getContext()).inflate(
              R.layout.group_share_list_datetime_item,
              parent, false);
      // Log.e("viewtype2:", String.valueOf(viewType));
      return new DateViewHolder(itemView, this, context);
    }

  }

  @Override
  public int getItemViewType(int position) {
    // Log.e("position:", String.valueOf(position));

    // Implement your logic here
    if (view_type_array.get(position).getType() == null) {
      Log.e("position_viewtype:", position + "    common");
      return CommonFrature.COMMON.ordinal();
    }
    else {
      Log.e("position_viewtype:", position + "    date");
      return CommonFrature.DATE.ordinal();
    }
  }


  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    setPosition(position);
    if (holder instanceof DateViewHolder) {
      DateViewHolder mDateViewHolder = (DateViewHolder) holder;
      try {
        Log.e("position_val:", position + view_type_array.get(position + 1).share_time);
        mDateViewHolder.onBindViewHolder(view_type_array.get(position + 1).share_time);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    else {
      Log.e("position_val:", position + view_type_array.get(position).url);
      GroupShareViewHolder mGroupShareViewHolder = (GroupShareViewHolder) holder;
      mGroupShareViewHolder.onBindViewHolder(view_type_array.get(position));
    }
  }



  @Override
  public int getItemCount() {
    if (view_type_array == null)
      return 0;
    else {
      // Log.e("size1-------------", String.valueOf(items.size()));
      // Log.e("size2-------------", String.valueOf(time_size));
      return view_type_array.size();
    }
  }

  @Override
  public long getItemId(int position) {
    return view_type_array.get(position).hashCode();
  }

  @Override
  public void onClick(View v) {
    if (mOnItemClickListener != null && v.getTag() != null) {
      // 注意这里使用getTag方法获取数据
      mOnItemClickListener.onItemClick(v, (Share) v.getTag());
    }
  }

  private OnRecyclerViewItemClickListener mOnItemClickListener = null;

  public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
    this.mOnItemClickListener = listener;
  }

  public interface OnRecyclerViewItemClickListener {
    void onItemClick(View view, Share data);
  }



}
