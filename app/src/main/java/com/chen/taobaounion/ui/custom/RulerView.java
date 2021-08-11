package com.chen.taobaounion.ui.custom;

/**
 * Created by Administrator on 2017/6/16.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.IntRange;

import com.chen.taobaounion.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Screen size independent ruler view.
 */
public class RulerView extends View {

    // 刻度工具类
    private Unit unit;

    /**
     * 屏幕对应dpi值
     * ydpi
     * xdpi
     */
    private DisplayMetrics dm;

    /**
     * 绘制辅助
     *
     * @see BeelineDrawImpl
     * @see FlatDrawImpl
     */
    private IDrawHelper mDrawHelper;

    // 存储触摸点坐标
    TouchHelper mTouchHelper;
    //刻度绘制
    private Paint scalePaint;
    //当前刻度绘制
    private Paint scaleCurrentPaint;
    //测量出的刻度值
    private Paint labelPaint;
    //背景
    private Paint backgroundPaint;
    //触摸位置点
    private Paint pointerPaint;

    private float guideScaleTextSize;
    private float graduatedScaleWidth;
    private float graduatedScaleBaseLength;
    private int scaleColor;
    private int scaleCurrentColor;

    private float labelTextSize;
    private String defaultLabelText;
    private int labelColor;

    private int backgroundColor;

    private float pointerRadius;
    private float pointerStrokeWidth;
    private int pointerColor;

    //能否触摸
    public boolean doNoTouch = false;
    //当前在屏幕的触摸位置
    private PointF mMainMovePoint;
    //用于保存刻度值在屏幕上的位置
    private List<Integer> mPointers;

    private Vibrator mVibrator;

    /**
     * Creates a new RulerView.
     */
    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        mVibrator = (Vibrator) App.getContext().getSystemService(VIBRATOR_SERVICE);
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.rulerView, defStyleAttr, defStyleRes);

        guideScaleTextSize = a.getDimension(R.styleable.rulerView_guideScaleTextSize, 40);
        graduatedScaleWidth = a.getDimension(R.styleable.rulerView_graduatedScaleWidth, 3);
        graduatedScaleBaseLength =
                a.getDimension(R.styleable.rulerView_graduatedScaleBaseLength, 100);
        scaleColor = a.getColor(R.styleable.rulerView_scaleColor, 0xFF616161);
        scaleCurrentColor = a.getColor(R.styleable.rulerView_scaleCurrentColor, Color.RED);

        labelTextSize = a.getDimension(R.styleable.rulerView_labelTextSize, 50);
        defaultLabelText = a.getString(R.styleable.rulerView_defaultLabelText);
        if (defaultLabelText == null) {
            defaultLabelText = "Measure with two fingers";
        }
        labelColor = a.getColor(R.styleable.rulerView_labelColor, 0xFF03070A);

        backgroundColor = a.getColor(R.styleable.rulerView_backgroundColor, 0xFFF2F2F2);

        pointerColor = a.getColor(R.styleable.rulerView_pointerColor, 0xFF48D5E9);
        pointerRadius = a.getDimension(R.styleable.rulerView_pointerRadius, 60);
        pointerStrokeWidth = a.getDimension(R.styleable.rulerView_pointerStrokeWidth, 3);

        dm = getResources().getDisplayMetrics();
        unit = new Unit(dm.ydpi);
        unit.setType(a.getInt(R.styleable.rulerView_unit, 0));
        int rulerMode;
        mDrawHelper = (rulerMode = a.getInt(R.styleable.rulerView_rulerMode, 1)) == 1 ?
                new BeelineDrawImpl() : new FlatDrawImpl();
        mTouchHelper = new TouchHelper(rulerMode);
        mPointers = new ArrayList<>();
        a.recycle();

        initRulerView();
    }

    /**
     * 设置单位类型
     */
    public void setUnitType(int type) {
        unit.type = type;
        invalidate();
    }

    /**
     * 获取单位类型
     */
    public int getUnitType() {
        return unit.type;
    }

    private void initRulerView() {

        scalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scalePaint.setStrokeWidth(graduatedScaleWidth);
        scalePaint.setTextSize(guideScaleTextSize);
        scalePaint.setColor(scaleColor);

        scaleCurrentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scaleCurrentPaint.setStrokeWidth(graduatedScaleWidth);
        scaleCurrentPaint.setTextSize(guideScaleTextSize);
        scaleCurrentPaint.setColor(scaleCurrentColor);

        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setTextSize(labelTextSize);
        labelPaint.setColor(labelColor);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);

        pointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointerPaint.setColor(pointerColor);
        pointerPaint.setStrokeWidth(pointerStrokeWidth);
        pointerPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            if (mTouchHelper != null) {
                mTouchHelper.dealEvent(event);
            }
            invalidate();
            return true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPaint(backgroundPaint);
        if (mDrawHelper != null) {
            if (mTouchHelper != null) {
                if (mTouchHelper.initScope()) {
                    mDrawHelper.drawRange(canvas, mTouchHelper.getScopeArray());
                }
            }
            mDrawHelper.drawScale(canvas);
        }
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return 200;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return 200;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minWidth = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int width = Math.max(minWidth, MeasureSpec.getSize(widthMeasureSpec));

        int minHeight = getPaddingBottom() + getPaddingTop() + getSuggestedMinimumHeight();
        int height = Math.max(minHeight, MeasureSpec.getSize(heightMeasureSpec));

        setMeasuredDimension(width, height);
    }

    public String getModleJson() {
        if (mTouchHelper != null) {
            return mTouchHelper.saveModel();
        }
        return null;
    }

    public void loadModel(String str) {
        if (mTouchHelper != null) {
            mTouchHelper.loadModel(str);
        }
    }

    /**
     * 获取尺子类型
     */
    public int getType() {
        return mTouchHelper.getType();
    }

    /**
     * 获取处理后测量高度
     */
    public float getH() {
        return mTouchHelper.getHeightValue() / unit.getPixelsPerUnit() * unit.emm();
    }

    /**
     * 获取处理后测量宽度
     */
    public float getW() {
        return mTouchHelper.getWidthValue() / unit.getPixelsPerUnit() * unit.emm();
    }

    public String getUnit() {
        String suffix = "CM";
        if (getUnitType() == Unit.INCH) {
            suffix = getH() > 1 ? "Inches" : "Inch";
        } else if (getUnitType() == Unit.MM) {
            suffix = "MM";
        }
        return suffix;
    }

    /**
     * 处理触摸事件，保存模型，导入模型。
     */
    // TODO: 2019/2/17  保存模型，导入模型。 优化触摸
    class TouchHelper {
        private SparseArray<PointF> scopePointers = new SparseArray<>();
        private SparseArray<PointF> lastPointers = new SparseArray<PointF>() {
            public void addAll(SparseArray<PointF> oldArray) {
                try {
                    if (this.size() == 0) {
                        for (int i = 0; i < oldArray.size(); i++) {
                            this.put(oldArray.keyAt(i), new PointF(oldArray.valueAt(i).x, oldArray.valueAt(i).y));
                        }
                    } else {
                        for (int i = 0; i < oldArray.size(); i++) {
                            PointF f = get(oldArray.keyAt(i));
                            f.x = oldArray.valueAt(i).x;
                            f.y = oldArray.valueAt(i).y;
                        }
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) {
                        throw e;
                    } else {
                        e.printStackTrace();
                    }
                }
            }
        };
        static final int LEFT = 0;
        static final int TOP = 1;
        static final int RIGHT = 2;
        static final int BOTTOM = 3;
        /**
         * 1 BEELINE_RULER
         * 2 FLAT_RULER
         */
        private int type;

        public int getType() {
            return type;
        }

        String saveModel() {
            JSONObject obj = new JSONObject();
            try {
                obj.put("type", type);
                obj.put("unit", unit.type);
                obj.put("TOP_X", scopePointers.get(TOP).x);
                obj.put("TOP_Y", scopePointers.get(TOP).y);
                obj.put("BOTTOM_X", scopePointers.get(BOTTOM).x);
                obj.put("BOTTOM_Y", scopePointers.get(BOTTOM).y);
                obj.put("RIGHT_X", scopePointers.get(RIGHT).x);
                obj.put("RIGHT_Y", scopePointers.get(RIGHT).y);
                obj.put("LEFT_X", scopePointers.get(LEFT).x);
                obj.put("LEFT_Y", scopePointers.get(LEFT).y);
                return obj.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            return null;
        }

        void loadModel(String str) {
            try {
                JSONObject obj = new JSONObject(str);
                type = obj.getInt("type");
                unit.type = obj.getInt("unit");
                scopePointers.put(TOP, new PointF((float) obj.getDouble("TOP_X"), (float) obj.getDouble("TOP_Y")));
                scopePointers.put(BOTTOM, new PointF((float) obj.getDouble("BOTTOM_X"), (float) obj.getDouble("BOTTOM_Y")));
                scopePointers.put(RIGHT, new PointF((float) obj.getDouble("RIGHT_X"), (float) obj.getDouble("RIGHT_Y")));
                if (obj.has("LEFT_X") && obj.has("LEFT_Y")) {
                    scopePointers.put(LEFT, new PointF((float) obj.getDouble("LEFT_X"), (float) obj.getDouble("LEFT_Y")));
                } else {
                    scopePointers.put(LEFT, new PointF(0, (float) obj.getDouble("RIGHT_Y")));
                }
                try {
                    JavaR.set(lastPointers, "addAll", scopePointers);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                invalidate();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClassCastException e) {
                //
            }
        }

        TouchHelper(int type) {
            this.type = type;
            scopePointers = new SparseArray<>();
        }

        boolean initScope() {
            if (scopePointers.size() > 0) {
                return true;
            }
            int mW = getWidth() / 2;
            int mH = getHeight() / 2;
            int l = type == FLAT_RULER ? mW / 2 : getPaddingLeft();
            int t = mH / 2;
            int r = type == FLAT_RULER ? mW * 3 / 2 : getWidth() - getPaddingRight();
            int b = mH * 3 / 2;
            scopePointers.put(TOP, new PointF(mW, t));
            scopePointers.put(BOTTOM, new PointF(mW, b));
            scopePointers.put(RIGHT, new PointF(r, mH));
            scopePointers.put(LEFT, new PointF(l, mH));
            try {
                JavaR.set(lastPointers, "addAll", scopePointers);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        /**
         * 落下点距离最近的点
         *
         * @param movePoint 落下点
         */
        private void dealScopePoint(PointF movePoint) {
            mMainMovePoint = movePoint;
            PointF tempP;
            int upD = distance(movePoint, TOP);
            int downD = distance(movePoint, BOTTOM);
            int temp = upD < downD ? upD : downD;
            if (type == FLAT_RULER) {
                int rightD = distance(movePoint, RIGHT);
                int leftD = distance(movePoint, LEFT);
                temp = temp < leftD ? temp : leftD;
                temp = temp < rightD ? temp : rightD;
            }
            // 取后一位 temp & BOTTOM
            tempP = scopePointers.get(temp & BOTTOM);
            if (tempP != null) {
                tempP.set(movePoint);
                switch (temp & BOTTOM) {
                    case LEFT:
                    case RIGHT:
                        if (movePoint.y < scopePointers.get(TOP).y) {
                            scopePointers.get(TOP).y = movePoint.y;
                        } else if (movePoint.y > scopePointers.get(BOTTOM).y) {
                            scopePointers.get(BOTTOM).y = movePoint.y;
                        }
                        break;
                    default:
                        if (movePoint.x < scopePointers.get(LEFT).x) {
                            scopePointers.get(LEFT).x = movePoint.x;
                        } else if (movePoint.x > scopePointers.get(RIGHT).x) {
                            scopePointers.get(RIGHT).x = movePoint.x;
                        }
                }
                if (scopePointers.get(TOP).y > scopePointers.get(BOTTOM).y) {
                    movePoint.y = scopePointers.get(TOP).y;
                    scopePointers.get(TOP).y = scopePointers.get(BOTTOM).y;
                    scopePointers.get(BOTTOM).y = movePoint.y;
                }
                if (type == FLAT_RULER && scopePointers.get(LEFT).x > scopePointers.get(RIGHT).x) {
                    movePoint.x = scopePointers.get(LEFT).x;
                    scopePointers.get(LEFT).x = scopePointers.get(RIGHT).x;
                    scopePointers.get(RIGHT).x = movePoint.x;
                }
                scopePointers.get(LEFT).y = scopePointers.get(RIGHT).y = (scopePointers.get(BOTTOM).y + scopePointers.get(TOP).y) / 2;
                scopePointers.get(TOP).x = scopePointers.get(BOTTOM).x = (scopePointers.get(RIGHT).x + scopePointers.get(LEFT).x) / 2;
            }
            try {
                JavaR.set(lastPointers, "addAll", scopePointers);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private int distance(PointF a, @IntRange(from = LEFT, to = BOTTOM) int key) {
            if (scopePointers.get(key) == null) {
                return 0;
            }
            return (((int) (Math.pow(a.x - scopePointers.get(key).x, 2) + Math.pow(a.y - scopePointers.get(key).y, 2))) << 2) + key;
        }

        // TODO: 2019/3/14 .............
        float getMotionXorY(int key, boolean getX) {
            if (scopePointers.get(key) == null) {
                throw new NullPointerException("what");
            }
            return getX ? scopePointers.get(key).x : scopePointers.get(key).y;
        }

        /**
         * 获取测量高度在屏幕所占的像素值
         */
        float getHeightValue() {
            try {
                return Math.abs(getMotionXorY(TOP, false) - getMotionXorY(BOTTOM, false));
            } catch (NullPointerException e) {
                return 0;
            }
        }

        /**
         * 获取测量宽度在屏幕所占的像素值
         */
        float getWidthValue() {
            try {
                return Math.abs(getMotionXorY(LEFT, true) - getMotionXorY(RIGHT, true));
            } catch (NullPointerException e) {
                return 0;
            }
        }

        SparseArray<PointF> getScopeArray() {
            try {
                if (scopePointers.get(TOP).equals(scopePointers.get(BOTTOM))) {
                    return null;
                }
            } catch (NullPointerException e) {
                return null;
            }
            return scopePointers;
        }

        PointF tempP = new PointF();

        void dealEvent(MotionEvent event) {
            if (doNoTouch) {
                tempP = null;
            }
            int pointerIndex = event.getActionIndex();
            if (pointerIndex > 4) {
                return;
            }
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE: {
                    for (int i = 0; i < event.getPointerCount(); i++) {
                        try {
                            tempP.set(event.getX(event.getPointerId(i)), event.getY(event.getPointerId(i)));
                            dealScopePoint(tempP);
                        } catch (Exception e) {
                            //
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }

    /**
     * 直尺绘画
     */
    class BeelineDrawImpl extends AbstractDrawHelper {
        @Override
        public void drawScale(Canvas canvas) {
            Iterator<Unit.Graduation> pixelsIterator = unit.getPixelIterator(getHeight() - getPaddingTop());
            float sHeightY, eHeightX, eHeightY;
            float textOffset = scalePaint.measureText("0") / 2;
            while (pixelsIterator.hasNext()) {
                Unit.Graduation graduation = pixelsIterator.next();
                sHeightY = eHeightY = getPaddingTop() + graduation.pixelOffset;
                eHeightX = graduation.relativeLength * graduatedScaleBaseLength;
                float dx = eHeightX + guideScaleTextSize / 2;
                float dy = sHeightY + textOffset;
                //绘画刻度线
                canvas.drawLine(0, sHeightY, eHeightX, eHeightY, scalePaint);
                //绘画刻度值
                if (mMainMovePoint != null) {
                    drawCurrentScaleText(canvas, graduation, dx, dy, true, true, true);
                } else {
                    drawScaleText(canvas, graduation, dx, dy, true, -1);
                }
            }
        }

        @Override
        public void drawRange(Canvas canvas, SparseArray<PointF> pointArray) {
            super.drawRange(canvas, pointArray);
            float textX = ((float) getWidth()) / 3 - labelTextSize / 2;
            float textY = ((float) getHeight() / 2) - labelTextSize / 2;
            try {
                if (pointArray.size() > 0) {
                    String labelText = unit.getStringRepresentation(getH());
                    canvas.drawText(labelText, textX, textY, labelPaint);
                } else {
                    canvas.drawText(defaultLabelText, textX, textY, labelPaint);
                }
            } catch (NullPointerException e) {
                //
            }
        }
    }

    /**
     * 90度直尺绘画
     */
    class FlatDrawImpl extends AbstractDrawHelper {

        @Override
        public void drawScale(Canvas canvas) {
            Iterator<Unit.Graduation> pixelsIterator = unit.getPixelIterator(getHeight() - getPaddingTop());
            float startX = getPaddingLeft(), startY, stopX, stopY;
            float textOffset = scalePaint.measureText("0") / 2;
            while (pixelsIterator.hasNext()) {
                Unit.Graduation graduation = pixelsIterator.next();
                startY = stopY = getPaddingTop() + graduation.pixelOffset;
                stopX = Math.min(graduation.relativeLength * graduatedScaleBaseLength, startY);
                canvas.drawLine(startX, startY, stopX, stopY, scalePaint);
                if (stopX != startY) {
                    float dx = stopX + guideScaleTextSize / 2;
                    float dy = startY + textOffset;
                    if (mMainMovePoint != null) {
                        drawCurrentScaleText(canvas, graduation, dx, dy, true, false, true);
                    } else {
                        drawScaleText(canvas, graduation, dx, dy, true, -1);
                    }
                }
            }
            pixelsIterator = unit.getPixelIterator(getWidth() - getPaddingTop());
            startY = getPaddingTop();
            while (pixelsIterator.hasNext()) {
                Unit.Graduation graduation = pixelsIterator.next();
                startX = stopX = getPaddingLeft() + graduation.pixelOffset;
                stopY = Math.min(graduation.relativeLength * graduatedScaleBaseLength, startX);
                canvas.drawLine(startX, startY, stopX, stopY, scalePaint);
                if (stopX != startY) {
                    float dx = startX - guideScaleTextSize / 3;
                    float dy = stopY + 3 * textOffset;
                    if (mMainMovePoint != null) {
                        drawCurrentScaleText(canvas, graduation, dx, dy, false, false, false);
                    } else {
                        drawScaleText(canvas, graduation, dx, dy, false, -1);
                    }
                }
            }
        }

        @Override
        public void drawRange(Canvas canvas, SparseArray<PointF> pointArray) {
            super.drawRange(canvas, pointArray);
            float textX = ((float) getWidth()) / 5 - labelTextSize / 2;
            float textY = ((float) getHeight() / 2) - labelTextSize / 2;
            try {
                //高度
                String labelTextH = unit.getStringRepresentation(getH());
                canvas.save();
                canvas.translate(
                        regular(pointArray.get(TouchHelper.RIGHT).x, getWidth()),
                        regular(pointArray.get(TouchHelper.BOTTOM).y / 2 + pointArray.get(TouchHelper.TOP).y / 2,
                                getHeight()
                        )
                );
                canvas.rotate(-90);
                canvas.drawText(labelTextH, 0, 0, labelPaint);
                canvas.restore();
                //宽度
                String labelTextW = unit.getStringRepresentation(getW());
                canvas.drawText(labelTextW,
                        regular(pointArray.get(TouchHelper.RIGHT).x / 3 + pointArray.get(TouchHelper.LEFT).x / 2, getWidth()),
                        regular(pointArray.get(TouchHelper.BOTTOM).y, getHeight()),
                        labelPaint);
            } catch (NullPointerException e) {
                canvas.drawText(defaultLabelText, textX, textY, labelPaint);
            }
        }

        float regular(float value, int max) {
            if (value < 0) {
                value = 0;
            }
            value += 0.9f * graduatedScaleBaseLength;
            if (value > max) {
                value = max - 0.5f * graduatedScaleBaseLength;
            }
            return value;
        }
    }

    //上一个刻度值
    private int lastPointer = -1;
    //当前刻度值
    private int currentPointer;

    abstract class AbstractDrawHelper implements IDrawHelper {

        void drawScaleText(Canvas canvas, Unit.Graduation graduation, float dx, float dy, boolean isRotate, int type) {
            float value = graduation.value;
            if (value % 1 == 0) {
                //刻度值
                String text = (int) value + "";
                //保存刻度值位置
                mPointers.add(graduation.pixelOffset);
                canvas.save();
                canvas.translate(dx, dy);
                if (isRotate) {
                    //刻度值旋转
                    canvas.rotate(90);
                }
                try {
                    if (mMainMovePoint != null) {
                        if (isCurrentPointerText(type, value, 0)) {
                            drawPointerText(canvas, text, 0);
                        } else if (isCurrentPointerText(type, value, 1)) {
                            drawPointerText(canvas, text, 1);
                        } else if (isCurrentPointerText(type, value, 2)) {
                            drawPointerText(canvas, text, 2);
                        } else if (isCurrentPointerText(type, value, 3)) {
                            drawPointerText(canvas, text, 3);
                        } else if (isCurrentPointerText(type, value, 4)) {
                            drawPointerText(canvas, text, 4);
                        } else if (isCurrentPointerText(type, value, 5)) {
                            drawPointerText(canvas, text, 5);
                        } else if (isCurrentPointerText(type, value, 6)) {
                            drawPointerText(canvas, text, 6);
                        } else if (isCurrentPointerText(type, value, 7)) {
                            drawPointerText(canvas, text, 7);
                        } else if (isCurrentPointerText(type, value, 8)) {
                            drawPointerText(canvas, text, 8);
                        } else if (isCurrentPointerText(type, value, 9)) {
                            drawPointerText(canvas, text, 9);
                        } else if (isCurrentPointerText(type, value, 10)) {
                            drawPointerText(canvas, text, 10);
                        } else if (isCurrentPointerText(type, value, 11)) {
                            drawPointerText(canvas, text, 11);
                        } else if (isCurrentPointerText(type, value, 12)) {
                            drawPointerText(canvas, text, 12);
                        } else if (isCurrentPointerText(type, value, 13)) {
                            drawPointerText(canvas, text, 13);
                        } else if (isCurrentPointerText(type, value, 14)) {
                            drawPointerText(canvas, text, 14);
                        } else if (isCurrentPointerText(type, value, 15)) {
                            drawPointerText(canvas, text, 15);
                        } else if (isCurrentPointerText(type, value, 16)) {
                            drawPointerText(canvas, text, 16);
                        } else if (isCurrentPointerText(type, value, 17)) {
                            drawPointerText(canvas, text, 17);
                        } else if (isCurrentPointerText(type, value, 18)) {
                            drawPointerText(canvas, text, 18);
                        } else if (isCurrentPointerText(type, value, 19)) {
                            drawPointerText(canvas, text, 19);
                        } else if (isCurrentPointerText(type, value, 20)) {
                            drawPointerText(canvas, text, 20);
                        } else {
                            canvas.drawText(text, 0, 0, scalePaint);
                        }
                    } else {
                        canvas.drawText(text, 0, 0, scalePaint);
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

                canvas.restore();
            }
        }

        @Override
        public void drawRange(Canvas canvas, SparseArray<PointF> pointArray) {
            try {
                float top = mTouchHelper.getMotionXorY(TouchHelper.TOP, false);
                float down = mTouchHelper.getMotionXorY(TouchHelper.BOTTOM, false);
                float left = mTouchHelper.getMotionXorY(TouchHelper.LEFT, true);
                float right = mTouchHelper.getMotionXorY(TouchHelper.RIGHT, true);
                canvas.drawRect(left, top, right, down, pointerPaint);
            } catch (NullPointerException e) {
                //
            }
        }

        void drawCurrentScaleText(Canvas canvas, Unit.Graduation graduation, float dx, float dy, boolean isRotate, boolean hasZero, boolean isY) {
            try {
                //设置高亮的范围
                int range = (int) ((mPointers.get(1) - mPointers.get(0)) * 0.1);
                if (isInMeasurePosition(isY, range, 0)) {
                    drawScaleText(canvas, graduation, dx, dy, isRotate, hasZero ? 0 : 1);
                } else if (isInMeasurePosition(isY, range, 1)) {
                    drawScaleText(canvas, graduation, dx, dy, isRotate, hasZero ? 1 : 2);
                } else if (isInMeasurePosition(isY, range, 2)) {
                    drawScaleText(canvas, graduation, dx, dy, isRotate, hasZero ? 2 : 3);
                } else if (isInMeasurePosition(isY, range, 3)) {
                    drawScaleText(canvas, graduation, dx, dy, isRotate, hasZero ? 3 : 4);
                } else if (isInMeasurePosition(isY, range, 4)) {
                    drawScaleText(canvas, graduation, dx, dy, isRotate, hasZero ? 4 : 5);
                } else if (isInMeasurePosition(isY, range, 5)) {
                    drawScaleText(canvas, graduation, dx, dy, isRotate, hasZero ? 5 : 6);
                } else if (isInMeasurePosition(isY, range, 6)) {
                    drawScaleText(canvas, graduation, dx, dy, isRotate, hasZero ? 6 : 7);
                } else if (isInMeasurePosition(isY, range, 7)) {
                    drawScaleText(canvas, graduation, dx, dy, isRotate, hasZero ? 7 : 8);
                } else if (isInMeasurePosition(isY, range, 8)) {
                    drawScaleText(canvas, graduation, dx, dy, isRotate, hasZero ? 8 : 9);
                } else if (isInMeasurePosition(isY, range, 9)) {
                    drawScaleText(canvas, graduation, dx, dy, isRotate, hasZero ? 9 : 10);
                } else if (isInMeasurePosition(isY, range, 10)) {
                    drawScaleText(canvas, graduation, dx, dy, isRotate, hasZero ? 10 : 11);
                } else if (isInMeasurePosition(isY, range, 11)) {
                    drawScaleText(canvas, graduation, dx, dy, isRotate, hasZero ? 11 : 12);
                } else if (isInMeasurePosition(isY, range, 12)) {
                    drawScaleText(canvas, graduation, dx, dy, isRotate, hasZero ? 12 : 13);
                } else if (isInMeasurePosition(isY, range, 13)) {
                    drawScaleText(canvas, graduation, dx, dy, isRotate, hasZero ? 13 : 14);
                } else if (isInMeasurePosition(isY, range, 14)) {
                    drawScaleText(canvas, graduation, dx, dy, isRotate, hasZero ? 14 : 15);
                } else if (isInMeasurePosition(isY, range, 15)) {
                    drawScaleText(canvas, graduation, dx, dy, isRotate, hasZero ? 15 : 16);
                } else if (isInMeasurePosition(isY, range, 16)) {
                    drawScaleText(canvas, graduation, dx, dy, isRotate, hasZero ? 16 : 17);
                } else if (isInMeasurePosition(isY, range, 17)) {
                    drawScaleText(canvas, graduation, dx, dy, isRotate, hasZero ? 17 : 18);
                } else if (isInMeasurePosition(isY, range, 18)) {
                    drawScaleText(canvas, graduation, dx, dy, isRotate, hasZero ? 18 : 19);
                } else if (isInMeasurePosition(isY, range, 19)) {
                    drawScaleText(canvas, graduation, dx, dy, isRotate, hasZero ? 19 : 20);
                } else if (isInMeasurePosition(isY, range, 20)) {
                    drawScaleText(canvas, graduation, dx, dy, isRotate, hasZero ? 20 : 21);
                } else {
                    drawScaleText(canvas, graduation, dx, dy, isRotate, -1);
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 绘制刻度值和设置震动
     */
    private void drawPointerText(Canvas canvas, String text, int i) {
        canvas.drawText(text, 0, 0, scaleCurrentPaint);
        lastPointer = currentPointer;
        currentPointer = i;
        if (currentPointer != lastPointer) {
            mVibrator.vibrate(100);
        }
    }

    /**
     * 是否在触发刻度值高亮的范围
     * @param isY 是Y轴或是X轴
     * @param range 范围
     * @param i 当前刻度值
     */
    private boolean isInMeasurePosition(boolean isY, int range, int i) {
        if (mPointers.size() > i) {
            return (isY ? (int) mMainMovePoint.y : (int) mMainMovePoint.x) > mPointers.get(i) - range
                    && (isY ? (int) mMainMovePoint.y : (int) mMainMovePoint.x) < mPointers.get(i) + range;
        }
        return false;
    }

    /**
     * 是否在当前的刻度值绘制位置
     */
    private boolean isCurrentPointerText(int type, float value, int i) {
        return value == i && type == i;
    }

    /**
     * 绘制辅助类
     *
     * @see BeelineDrawImpl
     * @see FlatDrawImpl
     */
    interface IDrawHelper {
        // TODO: 2019/1/29 判断是否已绘制

        /**
         * 绘制刻度
         */
        void drawScale(Canvas canvas);

        /**
         * 绘制 范围和值
         */
        void drawRange(Canvas canvas, SparseArray<PointF> pointArray);
    }

    /**
     * {@link Graduation} 存储对应刻度的相对信息
     *
     * @see #getPixelIterator(int) 刻度迭代器
     */
    public class Unit {
        /**
         * 刻度
         */
        class Graduation {
            // 刻度值
            float value;
            // 刻度对应像素
            int pixelOffset;
            //标志长度
            float relativeLength;
        }

        public static final int INCH = 0;
        public static final int CM = 1;
        public static final int MM = 2;

        private int type = CM;
        /**
         * 屏幕的dpi
         */
        private float dpi;

        Unit(float dpi) {
            this.dpi = dpi;
        }

        public void setType(int type) {
            if (type == INCH || type == CM || type == MM) {
                this.type = type;
            }
        }

        public String getStringRepresentation(float value) {
            String suffix = "";
            if (type == INCH) {
                suffix = value > 1 ? "Inches" : "Inch";
            } else if (type == CM) {
                suffix = "CM";
            } else if (type == MM) {
                suffix = "MM";
            }
            return String.format("%.3f %s", value, suffix);
        }

        /**
         * @param numberOfPixels 刻尺显示占有像素
         * @return 刻度迭代器
         * next() 下一个刻度
         */
        public Iterator<Graduation> getPixelIterator(final int numberOfPixels) {
            return new Iterator<Graduation>() {
                // 当前刻度
                int graduationIndex = 0;
                Graduation graduation = new Graduation();

                /**
                 * @return 返回刻度值
                 */
                private float getValue() {
                    return graduationIndex * getPrecision();
                }

                /**
                 *
                 * @return 根据屏幕dpi和刻度值返回对应像素
                 */
                private int getPixels() {
                    return (int) (getValue() * getPixelsPerUnit());
                }

                @Override
                public boolean hasNext() {
                    return getPixels() <= numberOfPixels;
                }

                @Override
                public Graduation next() {
                    // Returns the same Graduation object to avoid allocation.
                    graduation.value = getValue();
                    graduation.pixelOffset = getPixels();
                    graduation.relativeLength = getGraduatedScaleRelativeLength(graduationIndex);

                    graduationIndex++;
                    return graduation;
                }

                @Override
                public void remove() {

                }
            };
        }

        public float getPixelsPerUnit() {
            if (type == INCH) {
                return dpi;
            } else if (type == CM) {
                return dpi / 2.54f;
            } else if (type == MM) {
                return dpi / 2.54f;
            }
            return 0;
        }

        private int emm() {
            return type == MM ? 10 : 1;
        }

        private float getPrecision() {
            if (type == INCH) {
                return 1 / 4f;
            } else if (type == CM) {
                return 1 / 10f;
            } else if (type == MM) {
                return 1 / 10f;
            }
            return 0;
        }

        /**
         * @param graduationIndex 刻度值
         * @return 当前刻度的标志长度
         */
        private float getGraduatedScaleRelativeLength(int graduationIndex) {
            if (type == INCH) {
                if (graduationIndex % 4 == 0) {
                    return 1f;
                } else if (graduationIndex % 2 == 0) {
                    return 3 / 4f;
                } else {
                    return 1 / 2f;
                }
            } else if (type == CM) {
                if (graduationIndex % 10 == 0) {
                    return 1;
                } else if (graduationIndex % 5 == 0) {
                    return 3 / 4f;
                } else {
                    return 1 / 2f;
                }
            } else if (type == MM) {
                if (graduationIndex % 10 == 0) {
                    return 1;
                } else if (graduationIndex % 5 == 0) {
                    return 3 / 4f;
                } else {
                    return 1 / 2f;
                }
            }
            return 0;
        }
    }

}