package unique.fancysherry.shr.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.ui.widget.Dialog.OnItemClickListener;

/**
 * Created by fancysherry on 15-10-6.
 */
public class TagGroup extends ViewGroup {

  private final float default_border_stroke_width;
  private final float default_text_size;
  private final float default_horizontal_spacing;
  private final float default_vertical_spacing;
  private final float default_horizontal_padding;
  private final float default_vertical_padding;


  /** The text to be displayed when the text of the INPUT tag is empty. */
  private CharSequence inputHint;

  /** The tag outline border color. */
  private int borderColor;

  /** The tag text color. */
  private int textColor;

  /** The tag background color. */
  private int backgroundColor;

  /** The dash outline border color. */
  private int dashBorderColor;

  /** The input tag hint text color. */
  private int inputHintColor;

  /** The input tag type text color. */
  private int inputTextColor;

  /** The checked tag outline border color. */
  private int checkedBorderColor;

  /** The check text color */
  private int checkedTextColor;

  /** The checked marker color. */
  private int checkedMarkerColor;

  /** The checked tag background color. */
  private int checkedBackgroundColor;

  /** The tag background color, when the tag is being pressed. */
  private int pressedBackgroundColor;

  /** The tag outline border stroke width, default is 0.5dp. */
  private float borderStrokeWidth;

  /** The tag text size, default is 13sp. */
  private float textSize;

  /** The horizontal tag spacing, default is 8.0dp. */
  private int horizontalSpacing;

  /** The vertical tag spacing, default is 4.0dp. */
  private int verticalSpacing;

  /** The horizontal tag padding, default is 12.0dp. */
  private int horizontalPadding;

  /** The vertical tag padding, default is 3.0dp. */
  private int verticalPadding;

  /** Listener used to dispatch tag change event. */
  private OnTagChangeListener mOnTagChangeListener;

  /** Listener used to dispatch tag click event. */
  private OnTagClickListener mOnTagClickListener;

  /** Listener used to handle tag click event. */
  private InternalTagClickListener mInternalTagClickListener = new InternalTagClickListener();

  public TagGroup(Context context) {
    this(context, null);
  }

  public TagGroup(Context context, AttributeSet attrs) {
    this(context, attrs, R.attr.tagGroupStyle);
  }

  public TagGroup(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    default_border_stroke_width = dp2px(0.5f);
    default_text_size = sp2px(13.0f);
    default_horizontal_spacing = dp2px(8.0f);
    default_vertical_spacing = dp2px(4.0f);
    default_horizontal_padding = dp2px(12.0f);
    default_vertical_padding = dp2px(3.0f);

    // Load styled attributes.
    final TypedArray a =
        context.obtainStyledAttributes(attrs, R.styleable.TagGroup, defStyleAttr, R.style.TagGroup);
    try {
      borderStrokeWidth =
          a.getDimension(R.styleable.TagGroup_atg_borderStrokeWidth, default_border_stroke_width);
      textSize = a.getDimension(R.styleable.TagGroup_atg_textSize, default_text_size);
      horizontalSpacing =
          (int) a.getDimension(R.styleable.TagGroup_atg_horizontalSpacing,
              default_horizontal_spacing);
      verticalSpacing =
          (int) a.getDimension(R.styleable.TagGroup_atg_verticalSpacing, default_vertical_spacing);
      horizontalPadding =
          (int) a.getDimension(R.styleable.TagGroup_atg_horizontalPadding,
              default_horizontal_padding);
      verticalPadding =
          (int) a.getDimension(R.styleable.TagGroup_atg_verticalPadding, default_vertical_padding);
    } finally {
      a.recycle();
    }
  }

  /**
   * Returns the tag view at the specified position in the group.
   *
   * @param index the position at which to get the tag view from.
   * @return the tag view at the specified position or null if the position
   *         does not exists within this group.
   */
  protected TagView getTagAt(int index) {
    return (TagView) getChildAt(index);
  }


  /***
   * 显示部分的tag
   *
   * @param tagList
   */
  public void setTagsDailog(List<String> tagList) {
    setTagsDailog(tagList.toArray(new String[tagList.size()]));
  }

  public void setTagsDailog(String... tags) {
    removeAllViews();
    if (tags.length == 2) {
      appendTag(tags[0]);
      Log.e("tag", "   " + tags[0]);
      Log.e("tag", "   " + tags[1]);
      appendTag(tags[1]);
      appendTag("@me");
    }
    else if (tags.length == 1) {
      appendTag(tags[0]);
      Log.e("tag", "   " + tags[0]);
      appendTag("@me");
    }
    else if (tags.length > 2) {
      appendTag(tags[0]);
      appendTag(tags[1]);
      appendTag("@me");
      appendTag("...");
    }
  }


  /**
   * @see #setAllTags(String...)
   */
  public void setAllTagsDailog(List<String> tagList) {
    setAllTagsDailog(tagList.toArray(new String[tagList.size()]));
  }

  /**
   * Set the tags. It will remove all previous tags first.
   *
   * @param tags the tag list to set.
   */
  public void setAllTagsDailog(String... tags) {
    removeAllViews();
    for (final String tag : tags) {
      appendTag(tag);
      Log.e("tag", "   " + tag);
    }
    appendTag("@me");
    appendTag("<-");
  }


  /***
   * 显示部分的tag
   * 
   * @param tagList
   */
  public void setTags(List<String> tagList) {
    setTags(tagList.toArray(new String[tagList.size()]));
  }

  public void setTags(String... tags) {
    removeAllViews();
    if (tags.length == 2) {
      appendTag("+");
      appendTag(tags[0]);
      Log.e("tag", "   " + tags[0]);
      Log.e("tag", "   " + tags[1]);
      appendTag(tags[1]);
    }
    else if (tags.length == 1) {
      appendTag("+");
      appendTag(tags[0]);
    }
    else if (tags.length > 2) {
      appendTag("+");
      appendTag(tags[0]);
      appendTag(tags[1]);
      appendTag("...");
    }
  }


  /**
   * @see #setAllTags(String...)
   */
  public void setAllTags(List<String> tagList) {
    setAllTags(tagList.toArray(new String[tagList.size()]));
  }

  /**
   * Set the tags. It will remove all previous tags first.
   *
   * @param tags the tag list to set.
   */
  public void setAllTags(String... tags) {
    removeAllViews();
    appendTag("+");
    for (final String tag : tags) {
      appendTag(tag);
      Log.e("tag", "   " + tag);
    }
    appendTag("<-");
  }

  public String[] getTags() {
    final int count = getChildCount();
    final List<String> tagList = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      final TagView tagView = getTagAt(i);
      tagList.add(tagView.getText().toString());
    }
    return tagList.toArray(new String[tagList.size()]);
  }

  /**
   * Append tag to this group.
   *
   * @param tag the tag to append.
   */
  protected void appendTag(CharSequence tag) {
    final TagView newTag =
        (TagView) LayoutInflater.from(getContext()).inflate(R.layout.include_tagview, null);
    newTag.setText(tag);
    newTag.setOnClickListener(mInternalTagClickListener);
    addView(newTag, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT));
  }

  /**
   * Register a callback to be invoked when a tag is clicked.
   *
   * @param l the callback that will run.
   */
  public void setOnTagClickListener(OnTagClickListener l) {
    mOnTagClickListener = l;
  }

  protected void deleteTag(TagView tagView) {
    removeView(tagView);
    if (mOnTagChangeListener != null) {
      mOnTagChangeListener.onDelete(TagGroup.this, tagView.getText().toString());
    }
  }



  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

    measureChildren(widthMeasureSpec, heightMeasureSpec);

    int width = 0;
    int height = 0;

    int row = 0; // The row counter.
    int rowWidth = 0; // Calc the current row width.
    int rowMaxHeight = 0; // Calc the max tag height, in current row.

    final int count = getChildCount();
    for (int i = 0; i < count; i++) {
      final View child = getChildAt(i);
      final int childWidth = child.getMeasuredWidth();
      final int childHeight = child.getMeasuredHeight();

      if (child.getVisibility() != GONE) {
        rowWidth += childWidth;
        if (rowWidth > widthSize) { // Next line.
          rowWidth = childWidth; // The next row width.
          height += rowMaxHeight + verticalSpacing;
          rowMaxHeight = childHeight; // The next row max height.
          row++;
        } else { // This line.
          rowMaxHeight = Math.max(rowMaxHeight, childHeight);
        }
        rowWidth += horizontalSpacing;
      }
    }
    // Account for the last row height.
    height += rowMaxHeight;

    // Account for the padding too.
    height += getPaddingTop() + getPaddingBottom();

    // If the tags grouped in one row, set the width to wrap the tags.
    if (row == 0) {
      width = rowWidth;
      width += getPaddingLeft() + getPaddingRight();
    } else {// If the tags grouped exceed one line, set the width to match the parent.
      width = widthSize;
    }

    setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width,
        heightMode == MeasureSpec.EXACTLY ? heightSize : height);
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    final int parentLeft = getPaddingLeft();
    final int parentRight = r - l - getPaddingRight();
    final int parentTop = getPaddingTop();
    final int parentBottom = b - t - getPaddingBottom();

    int childLeft = parentLeft;
    int childTop = parentTop;

    int rowMaxHeight = 0;

    final int count = getChildCount();
    for (int i = 0; i < count; i++) {
      final View child = getChildAt(i);
      final int width = child.getMeasuredWidth();
      final int height = child.getMeasuredHeight();

      if (child.getVisibility() != GONE) {
        if (childLeft + width > parentRight) { // Next line
          childLeft = parentLeft;
          childTop += rowMaxHeight + verticalSpacing;
          rowMaxHeight = height;
        } else {
          rowMaxHeight = Math.max(rowMaxHeight, height);
        }
        child.layout(childLeft, childTop, childLeft + width, childTop + height);

        childLeft += width + horizontalSpacing;
      }
    }
  }

  /**
   * Register a callback to be invoked when this tag group is changed.
   *
   * @param l the callback that will run
   */
  public void setOnTagChangeListener(OnTagChangeListener l) {
    mOnTagChangeListener = l;
  }

  public float dp2px(float dp) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
        getResources().getDisplayMetrics());
  }

  public float sp2px(float sp) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
        getResources().getDisplayMetrics());
  }

  @Override
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
    return new TagGroup.LayoutParams(getContext(), attrs);
  }

  /**
   * Interface definition for a callback to be invoked when a tag group is changed.
   */
  public interface OnTagChangeListener {
    /**
     * Called when a tag has been appended to the group.
     *
     * @param tag the appended tag.
     */
    void onAppend(TagGroup tagGroup, String tag);

    /**
     * Called when a tag has been deleted from the the group.
     *
     * @param tag the deleted tag.
     */
    void onDelete(TagGroup tagGroup, String tag);
  }

  /**
   * Interface definition for a callback to be invoked when a tag is clicked.
   */
  public interface OnTagClickListener {
    /**
     * Called when a tag has been clicked.
     *
     * @param tag The tag text of the tag that was clicked.
     */
    void onTagClick(String tag);
  }

  /**
   * Per-child layout information for layouts.
   */
  public static class LayoutParams extends ViewGroup.LayoutParams {
    public LayoutParams(Context c, AttributeSet attrs) {
      super(c, attrs);
    }

    public LayoutParams(int width, int height) {
      super(width, height);
    }
  }

  /**
   * The tag view click listener for internal use.
   */
  class InternalTagClickListener implements OnClickListener {
    @Override
    public void onClick(View v) {
      final TagView tag = (TagView) v;

      if (mOnTagClickListener != null) {
        mOnTagClickListener.onTagClick(tag.getText().toString());
      }
    }
  }
}
