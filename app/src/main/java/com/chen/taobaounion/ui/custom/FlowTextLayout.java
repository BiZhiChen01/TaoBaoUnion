package com.chen.taobaounion.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chen.taobaounion.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlowTextLayout extends ViewGroup {

    public static final float DEFAULT_SPACE = 10;

    private float mItemHorizontalSpace = DEFAULT_SPACE;
    private float mItemVerticalSpace = DEFAULT_SPACE;
    private List<String> mTextList = new ArrayList<>();
    private int mSelfWidth;
    private int mItemHeight;
    private OnFlowTextItemClickListener mItemClickListener = null;

    public int getContentSize() {
        return mTextList.size();
    }

    public FlowTextLayout(Context context) {
        this(context, null);
    }

    public FlowTextLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowTextLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlowTextStyle);
        mItemHorizontalSpace = ta.getDimension(R.styleable.FlowTextStyle_horizontalSpace, DEFAULT_SPACE);
        mItemVerticalSpace = ta.getDimension(R.styleable.FlowTextStyle_verticalSpace, DEFAULT_SPACE);
        ta.recycle();
    }

    public void setTextList(List<String> textList) {
        removeAllViews();
        this.mTextList.clear();
        this.mTextList.addAll(textList);
        Collections.reverse(mTextList);
        //遍历内容
        for (String text : mTextList) {
            //添加子view
            TextView item = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.flow_text_view, this, false);
            item.setText(text);
            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onFlowItemClick(text);
                }
            });
            addView(item);
        }
    }

    private List<List<View>> lines = new ArrayList<>();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() == 0) {
            return;
        }
        List<View> line = null;
        lines.clear();
        mSelfWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        //测量
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View itemView = getChildAt(i);
            if (itemView.getVisibility() != VISIBLE) {
                //不需要进行测量
                continue;
            }
            measureChild(itemView, widthMeasureSpec, heightMeasureSpec);

            if (line == null) {
                line = createNewLine(itemView);
            } else {
                if (canBeAdd(itemView, line)) {
                    line.add(itemView);
                } else {
                    line = createNewLine(itemView);
                }
            }
        }
        mItemHeight = getChildAt(0).getMeasuredHeight();
        int selfHeight = (int) (lines.size() * mItemHeight + mItemVerticalSpace * (lines.size() + 1) + 0.5f);
        setMeasuredDimension(mSelfWidth, selfHeight);
    }

    private List<View> createNewLine(View itemView) {
        List<View> line = new ArrayList<>();
        line.add(itemView);
        lines.add(line);
        return line;
    }

    /**
     * 判断当前行是否可以在继续添加数据
     * @param itemView
     * @param line
     */
    private boolean canBeAdd(View itemView, List<View> line) {
        //所有已近添加的子view宽度相加 + (line.size() + 1) * mItemHorizontalSpace + itemView.getMeasuredWidth()
        //条件：如果小于/等于当前控件的宽度，则可以添加，否则不能添加
        int totalWidth = itemView.getMeasuredWidth();
        for (View view : line) {
            //叠加所有已近添加的宽度
            totalWidth += view.getMeasuredWidth();
        }
        //水平间距的宽度
        totalWidth += mItemHorizontalSpace * (line.size() + 1);
        //如果小于/等于当前控件的宽度，则可以添加，否则不能添加
        return totalWidth <= mSelfWidth;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //摆放孩子
        int topOffset = (int) mItemHorizontalSpace;
        for (List<View> views : lines) {
            //views是每一行
            int leftOffset = (int) mItemHorizontalSpace;
            for (View view : views) {
                //view是每一行里的每个item
                view.layout(leftOffset, topOffset, leftOffset+view.getMeasuredWidth(), topOffset+view.getMeasuredHeight());
                leftOffset += view.getMeasuredWidth() + mItemHorizontalSpace;
            }
            topOffset += mItemHeight + mItemHorizontalSpace;
        }
    }

    public float getItemHorizontalSpace() {
        return mItemHorizontalSpace;
    }

    public void setItemHorizontalSpace(float itemHorizontalSpace) {
        mItemHorizontalSpace = itemHorizontalSpace;
    }

    public float getItemVerticalSpace() {
        return mItemVerticalSpace;
    }

    public void setItemVerticalSpace(float itemVerticalSpace) {
        mItemVerticalSpace = itemVerticalSpace;
    }

    public void setOnFlowTextItemClickListener(OnFlowTextItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface OnFlowTextItemClickListener {
        void onFlowItemClick(String text);
    }
}
