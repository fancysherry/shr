package unique.fancysherry.shr.ui.widget;

/**
 * Created by fancysherry on 15-10-31.
 */

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.util.LogUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;


public class BlacklistPopupWindow extends PopupWindow {

  public TextView putblack, cancelBtn;
  public View mMenuView;

  public void change_text(String isblack)
  {
    LogUtil.e("log:" + isblack);
    putblack.setText(isblack);
  }

  /**
   * use the inflate to get textview make the settext() not working
   * but use getcontentview did
   * 
   * @param itemsOnClick
   */
  @SuppressLint("InflateParams")
  public BlacklistPopupWindow(View contentView, OnClickListener itemsOnClick) {
    super(contentView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
//    // 设置SelectPicPopupWindow弹出窗体的宽
//    this.setWidth(LayoutParams.MATCH_PARENT);
//    // 设置SelectPicPopupWindow弹出窗体的高
//    this.setHeight(LayoutParams.WRAP_CONTENT);
//    // 设置SelectPicPopupWindow弹出窗体可点击
//    this.setFocusable(true);
//    // 设置SelectPicPopupWindow弹出窗体动画效果
    this.setAnimationStyle(R.style.PopupAnimation);
    // 实例化一个ColorDrawable颜色为半透明
    ColorDrawable dw = new ColorDrawable(0x80000000);
    // 设置SelectPicPopupWindow弹出窗体的背景
    this.setBackgroundDrawable(dw);

    mMenuView = contentView;
    putblack = (TextView) getContentView().findViewById(R.id.putblack);
    cancelBtn = (TextView) getContentView().findViewById(R.id.cancelBtn);
    // 设置按钮监听
    cancelBtn.setOnClickListener(itemsOnClick);
    putblack.setOnClickListener(itemsOnClick);
    // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
    mMenuView.setOnTouchListener(new OnTouchListener() {

      @Override
      @SuppressLint("ClickableViewAccessibility")
      public boolean onTouch(View v, MotionEvent event) {

        int height = mMenuView.findViewById(R.id.pop_layout).getTop();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_UP) {
          if (y < height) {
            dismiss();
          }
        }
        return true;
      }
    });

  }
}
