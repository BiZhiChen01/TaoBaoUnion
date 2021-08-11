package com.chen.taobaounion.ui.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.SurfaceHolder;

import com.chen.taobaounion.R;

import java.text.DecimalFormat;

/*
 *  This file is part of Level (an Android Bubble Level).
 *  <https://github.com/avianey/Level>
 *
 *  Copyright (C) 2014 Antoine Vianey
 *
 *  Level is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Level is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Level. If not, see <http://www.gnu.org/licenses/>
 */
public class LevelPainter implements Runnable {

    /**
     * Etats du thread
     */
    private boolean initialized;
    private boolean wait;

    /**
     * Possesseur de la surface
     * 保证同步，提供画布
     */
    private final SurfaceHolder surfaceHolder;

    /**
     * SurfaceView 初始化后赋值
     *
     * @see #canvasWidth {@link #canvasHeight} 画布的宽高
     */
    private int canvasWidth;
    private int canvasHeight;

    /**
     * 方向改变或画布确定，赋值。
     * 跟背景图绘制范围有关
     */
    private int minLevelX;
    private int maxLevelX;

    /**
     * 与 {@link #levelMaxDimension} 和 {@link #canvasWidth} 的值有关
     * 与 {@link #halfBubbleWidth} 泡泡位置有关
     * 描述图的宽高
     */
    private int levelWidth;
    private int levelHeight;

    /**
     * 泡泡的宽高
     */
    private int levelMinusBubbleWidth;
    private int levelMinusBubbleHeight;
    /**
     * 中心点
     */
    private int canvasMidX;
    private int canvasMidY;

    /**
     * 泡泡的宽高信息
     */
    private int bubbleWidth;
    private int bubbleHeight;
    private int halfBubbleWidth;
    private int halfBubbleHeight;
    private int halfMarkerGap;
    /**
     * 方向改变或画布确定，赋值。
     * 跟背景图绘制范围有关
     */
    private int minLevelY;
    private int maxLevelY;

    private int markerThickness;
    private int levelBorderWidth;
    private int infoHeight;
    private int lcdWidth;
    private int lcdHeight;
    private int lockWidth;
    private int lockHeight;
    /**
     * 绘制间隔 8dip
     */
    private int displayPadding;
    /**
     * 20dip
     */
    private int displayGap;
    private int infoY;
    private int sensorY;
    private int sensorGap;

    /**
     * 最大范围 {@link #canvasWidth} 有关
     */
    private int levelMaxDimension;

    /**
     * Rect
     */
    private Rect displayRect;
    private Rect lockRect;

    /**
     * Angles
     */
    private float angle1;
    private float angle2;

    private static final double LEVEL_ASPECT_RATIO = 0.150;
    private static final double BUBBLE_WIDTH = 0.150;
    private static final double BUBBLE_ASPECT_RATIO = 1.000;
    private static final double MARKER_GAP = BUBBLE_WIDTH + 0.020;

    /**
     * Angle max
     */
    private static final double MAX_SINUS = Math.sin(Math.PI / 4);

    /**
     * Orientation
     */
    private Orientation orientation;

    /**
     * 记录上次的时间
     */
    private long lastTime;

    /**
     * angleX、angleY都是1，0，-1的值
     */
    private double angleX;
    private double angleY;
    private double speedX;
    private double speedY;

    /**
     * 对应坐标
     */
    private double x, y;

    /**
     * 水平线、水平线中的两条灰色线
     */
    private Drawable level1D;
    private Drawable marker1D;

    /**
     * 中间的圆盘、滚动的小球、X、Y轴显示的背景图
     */
    private Drawable level2D;
    private Drawable bubble2D;
    private Drawable display;

    /**
     * 水平线中的阴影（高亮）、水平线中滚动的小球
     */
    private Bitmap shadow, bubble1D, bubble3D;

    /**
     * 黏度和黏度值
     */
    private Viscosity viscosity;
    private double viscosityValue;

    /**
     * Format des angles
     */
    private DecimalFormat displayFormat;
    private String displayBackgroundText;

    private OrientationProvider orientationProvider;

    /**
     * Fonts and colors
     */
    private static final String FONT_LCD = "fonts/lcd.ttf";
    private Paint lcdForegroundPaint;
    private Paint infoPaint;
    private int backgroundColor;

    /**
     * Config angles
     */
    private boolean showAngle;
    private DisplayType angleType;

    /**
     * Locked
     */
    private static final String LOCKED = "LOCKED";
    private boolean lockEnabled;
    private boolean locked;

    /**
     * Animation
     */
    private boolean ecoMode;
    private final Handler handler;
    private long frameRate;

    public LevelPainter(SurfaceHolder surfaceHolder, Context context,
                        Handler handler, int width, int height,
                        boolean showAngle, DisplayType angleType,
                        Viscosity viscosity, boolean lockEnabled,
                        boolean ecoMode, Orientation last) {

        // get handles to some important objects
        this.surfaceHolder = surfaceHolder;

        // economy mode
        this.ecoMode = ecoMode;
        this.handler = handler;
        this.frameRate = 1000 / context.getResources().getInteger(R.integer.frame_rate);

        // drawable
        this.level1D = context.getResources().getDrawable(R.drawable.level_1d);
        this.level2D = context.getResources().getDrawable(R.drawable.bg_2);
        this.bubble2D = context.getResources().getDrawable(R.drawable.bg_2_dot);
        this.marker1D = context.getResources().getDrawable(R.drawable.marker_1d);
        this.display = context.getResources().getDrawable(R.drawable.bg_1);
        this.shadow = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg_4);
        this.bubble1D = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg_3_dot3);
        this.bubble3D = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg_3_dot);

        // 气泡的速度
        this.viscosity = viscosity;

        // config
        this.showAngle = showAngle;
        this.displayFormat = new DecimalFormat(angleType.getDisplayFormat());
        this.displayBackgroundText = angleType.getDisplayBackgroundText();
        this.angleType = angleType;

        // 背景颜色
        this.backgroundColor = context.getResources().getColor(R.color.silver);

        // 字体格式
        Typeface lcd = Typeface.createFromAsset(context.getAssets(), FONT_LCD);

        // 画笔
        this.infoPaint = new Paint();
        this.infoPaint.setColor(context.getResources().getColor(R.color.black));
        this.infoPaint.setAntiAlias(true);
        this.infoPaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.info_text));
        this.infoPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        this.infoPaint.setTextAlign(Paint.Align.CENTER);

        this.lcdForegroundPaint = new Paint();
        this.lcdForegroundPaint.setColor(context.getResources().getColor(R.color.lcd_front));
        this.lcdForegroundPaint.setAntiAlias(true);
        this.lcdForegroundPaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.lcd_text));
        this.lcdForegroundPaint.setTypeface(lcd);
        this.lcdForegroundPaint.setTextAlign(Paint.Align.CENTER);

        // dimens
        Rect rect = new Rect();
        this.infoHeight = rect.height();
        this.lcdForegroundPaint.getTextBounds(("X" + displayBackgroundText), 0, ("X" + displayBackgroundText).length(), rect);
        this.lcdHeight = rect.height();
        this.lcdWidth = rect.width();
        this.lcdForegroundPaint.getTextBounds(LOCKED, 0, LOCKED.length(), rect);
        this.lockHeight = rect.height();
        this.lockWidth = rect.width();
        this.levelBorderWidth = context.getResources().getDimensionPixelSize(R.dimen.level_border_width);
        this.markerThickness = context.getResources().getDimensionPixelSize(R.dimen.marker_thickness);
        this.displayGap = context.getResources().getDimensionPixelSize(R.dimen.display_gap);
        this.sensorGap = context.getResources().getDimensionPixelSize(R.dimen.sensor_gap);
        this.displayPadding = context.getResources().getDimensionPixelSize(R.dimen.display_padding);
        this.displayRect = new Rect();
        this.lockRect = new Rect();

        // init
        this.locked = false;
        this.lockEnabled = lockEnabled;
        this.orientation = last == null ? Orientation.TOP : last;
        this.wait = true;
        this.initialized = false;
    }

    public void clean() {
        // suppression des ressources
        // afin de bypasser les problemes
        // de cache des xml drawable
        synchronized (this.surfaceHolder) {
            level1D = null;
            level2D = null;
            bubble1D = null;
            bubble2D = null;
            marker1D = null;
            display = null;
        }
    }

    @Override
    public void run() {
        Canvas c = null;
        updatePhysics();
        try {
            c = this.surfaceHolder.lockCanvas(null);
            if (c != null) {
                synchronized (this.surfaceHolder) {
                    doDraw(c);
                }
            }
        } finally {
            // do this in a finally so that if an exception is thrown
            // during the above, we don't leave the Surface in an
            // inconsistent state
            try {
                if (c != null) {
                    this.surfaceHolder.unlockCanvasAndPost(c);
                }
            } catch (Exception e) {
                // todo
            }
        }
        // lancement du traitement differe en mode eco
        handler.removeCallbacks(this);
        if (!wait && !ecoMode) {
            handler.postDelayed(this, frameRate - System.currentTimeMillis() + lastTime);
            //handler.postDelayed(this, 200);
        }
    }

    /**
     * Mise en pause du thread
     * 暂停线程
     */
    public void pause(boolean paused) {
        wait = !initialized || paused;
        // si on est en mode eco et
        // que la pause est supprimee
        // relance du traitement
        if (!wait) {
            handler.postDelayed(this, frameRate);
        }
    }

    /**
     * Modification / initialisation de la taille de l'ecran
     * 更改/初始化屏幕尺寸
     */
    public void setSurfaceSize(int width, int height) {
        canvasWidth = width;
        canvasHeight = height;

        levelMaxDimension = Math.min(Math.min(height, width) - 2 * displayGap,
                Math.max(height, width) - 2 * (sensorGap + 2 * infoHeight + 3 * displayGap + lcdHeight));
        //适配表面视图
        adaptSurface();
    }

    /**
     * 绘制前 调整参数
     */
    private void updatePhysics() {
        long currentTime = System.currentTimeMillis();
        if (false && ecoMode) {
            switch (orientation) {
                case ALL:
                case LANDING:
                    y = (angleY * levelMinusBubbleHeight + minLevelY + maxLevelY) / 2;
                default:
                    x = (angleX * levelMinusBubbleWidth + minLevelX + maxLevelX) / 2;
            }
        } else {
            if (lastTime > 0) {
                //滑动时间差
                double timeDiff = (currentTime - lastTime) / 1000.0;
                double posX = orientation.getReverse() * (2 * x - minLevelX - maxLevelX) / levelMinusBubbleWidth;
                switch (orientation) {
                    case TOP:
                    case BOTTOM:
                        speedX = orientation.getReverse() * (angleX - posX) * viscosityValue;
                        break;
                    case LEFT:
                    case RIGHT:
                        speedX = orientation.getReverse() * (angleY - posX) * viscosityValue;
                        break;
                    case ALL:
                    case LANDING:
                        double posY = (2 * y - minLevelY - maxLevelY) / levelMinusBubbleHeight;
                        speedX = (angleX - posX) * viscosityValue;
                        speedY = (angleY - posY) * viscosityValue;
                        y += speedY * timeDiff;
                        break;
                }
                x += speedX * timeDiff;
                // en cas de latence elevee
                // si la bubble a trop deviee
                // elle est replacee correctement
                switch (orientation) {
                    case ALL:
                    case LANDING:
                        if (Math.sqrt((canvasMidX - x) * (canvasMidX - x)
                                + (canvasMidY - y) * (canvasMidY - y)) > levelMaxDimension / 2 - halfBubbleWidth) {
                            x = (angleX * levelMinusBubbleWidth + minLevelX + maxLevelX) / 2;
                            y = (angleY * levelMinusBubbleHeight + minLevelY + maxLevelY) / 2;
                        }
                        break;
                    default:
                        if (x < minLevelX + halfBubbleWidth || x > maxLevelX - halfBubbleWidth) {
                            x = (angleX * levelMinusBubbleWidth + minLevelX + maxLevelX) / 2;
                        }
                }
            }
        }
        lastTime = currentTime;
    }

    /**
     * 绘制X、Y轴的底部显示内容
     *
     * @param canvas   画布
     * @param text     显示内容
     * @param difBG    背景图的大小
     * @param difText  文本的大小
     * @param rotation 旋转角度
     */
    private void doDrawText(Canvas canvas, String text, int difBG, int difText, int top, int bottom, int offsetUp, int rotation, int deviation) {
        if (!showAngle) {
            return;
        }
        if (displayRect == null || display == null) {
            return;
        }
        canvas.rotate(rotation, canvasMidX, canvasMidY);
        display.setBounds(
                displayRect.left + difBG - displayPadding / 3 + deviation,
                top,
                displayRect.right + difBG + displayPadding / 4 + deviation,
                bottom);
        display.draw(canvas);
        canvas.drawText(
                text + "°",
                canvasMidX + difText + deviation,
                displayRect.centerY() + lcdHeight / 3 - offsetUp,
                lcdForegroundPaint);
        canvas.rotate(-rotation, canvasMidX, canvasMidY);
    }

    /**
     * 画出水平线
     */
    private void doDrawLine(Canvas canvas, int left, int top, int right, int bottom, int rotation,
                            int bTop, int bLeft) {
        if (level1D == null || bubble3D == null || bubble1D == null) {
            return;
        }
        bottom = top + (int) ((right - left) * 0.11f);
        // level
        canvas.rotate(rotation, canvasMidX, canvasMidY);
        // positionnement
        level1D.setBounds(left, top, right, bottom);
        level1D.draw(canvas);
//        canvas.drawBitmap((orientation == RIGHT || orientation == LEFT) ? bubble3D : bubble1D,
        canvas.drawBitmap(bubble1D,
                bLeft,
                bTop,
                infoPaint);
        // marker
        drawMarker(canvas, left / 2 + right / 2, top, bottom);
        drawMarker(canvas, left / 2 + right / 2, top, bottom);
    }

    /**
     * 画水平线中的两条灰色线
     */
    private void drawMarker(Canvas canvas, int canvasMidX, int t, int b) {
        if (marker1D == null) {
            return;
        }
        marker1D.setBounds(
                canvasMidX - halfMarkerGap - markerThickness,
                t,
                canvasMidX - halfMarkerGap,
                b);
        marker1D.draw(canvas);
        marker1D.setBounds(
                canvasMidX + halfMarkerGap,
                t,
                canvasMidX + halfMarkerGap + markerThickness,
                b);
        marker1D.draw(canvas);
    }

    /**
     * (t - t2)/(t-t1) = 0.7
     */
    private int zoomDisc(boolean bored, int t1, int t) {
        int t2 = t1;
        if (bored) {
            t2 = (int) (0.25 * t + 0.75 * t1);
        }
        return t2;
    }

    /**
     * 返回最大值
     */
    private int f(int t, float f) {
        return (int) Math.max(t - displayGap * f, 0);
    }

    /**
     * 画中间的圆盘
     *
     * @param bored 是否切换模式
     */
    private void doDrawDisc(Canvas canvas, boolean bored) {
        if (level2D == null || bubble2D == null) {
            return;
        }
        level2D.setBounds(
                f(zoomDisc(bored, minLevelX, maxLevelX), bored ? 0.6f : 0),
                f(zoomDisc(bored, minLevelY, maxLevelY), bored ? 3.6f : 4.6f),
                f(maxLevelX, bored ? 0.6f : 0),
                f(maxLevelY, bored ? 3.6f : 4.6f));
        int left = f(zoomDisc(bored, (int) (x - halfBubbleWidth), maxLevelX), bored ? 0.6f : 0);
        int top = f(zoomDisc(bored, (int) (y - halfBubbleHeight), maxLevelY), bored ? 3.6f : 4.6f);
        int right = f(zoomDisc(bored, (int) (x + halfBubbleWidth), maxLevelX), bored ? 0.6f : 0);
        int bottom = f(zoomDisc(bored, (int) (y + halfBubbleHeight), maxLevelY), bored ? 3.6f : 4.6f);
        bubble2D.setBounds(left, top, right, bottom);
        level2D.draw(canvas);
        bubble2D.draw(canvas);
    }

    /**
     * 圆盘和
     * 竖线
     */
    private void doDraw(Canvas canvas) {
        canvas.save();
        canvas.drawColor(backgroundColor);
        if (null == displayRect) {
            return;
        }
        switch (orientation) {
            case ALL:
                doDrawText(canvas, "X:" + displayFormat.format(angle2),
                        -(displayRect.width() + displayGap) / 2 - 2 * displayPadding,
                        -(displayRect.width() + displayGap) / 2 - 2 * displayPadding,
                        canvasHeight - f(minLevelY, 4.2f) - (displayRect.bottom - displayRect.top),
                        canvasHeight - f(minLevelY, 4.2f) + displayGap / 2,
                        displayRect.bottom + displayPadding / 2 - (canvasHeight - f(minLevelY, 4.2f)) - displayGap / 2,
                        0, 0);
                doDrawText(canvas, "Y:" + displayFormat.format(angle1),
                        (displayRect.width() + displayGap) / 2 + 2 * displayPadding,
                        (displayRect.width() + displayGap) / 2 + 2 * displayPadding,
                        canvasHeight - f(minLevelY, 4.2f) - (displayRect.bottom - displayRect.top),
                        canvasHeight - f(minLevelY, 4.2f) + displayGap / 2,
                        displayRect.bottom + displayPadding / 2 - (canvasHeight - f(minLevelY, 4.2f)) - displayGap / 2,
                        0, 0);
                doDrawDisc(canvas, true);
                doDrawLine(canvas,
                        minLevelX,
                        f(minLevelY, 4.2f),
                        minLevelX + levelWidth,
                        f(minLevelY + bubbleHeight, 4.2f),
                        0,
                        f(minLevelY, 4.2f),
                        (int) (this.x - halfBubbleWidth));
                int offsetY = ((int) y - ((maxLevelY + minLevelY + displayGap)) / 2);
                offsetY *= (levelWidth + halfBubbleWidth * 0.2) / levelWidth;
                doDrawLine(canvas,
                        minLevelX + displayGap,
                        minLevelY,
                        minLevelX + levelWidth + displayGap,
                        f(minLevelY + bubbleHeight, 4.2f),
                        -90,
                        minLevelY,
                        minLevelX + levelWidth / 2 - halfBubbleWidth / 2 - offsetY
                );
                //+ f((int)this.y, MainActivity.isAd ?5.4f:4.4f
                canvas.restore();
                break;
            case LANDING:
                doDrawText(canvas, "X:" + displayFormat.format(angle2),
                        -(displayRect.width() + displayGap) / 2 - 2 * displayPadding,
                        -(displayRect.width() + displayGap) / 2 - 2 * displayPadding,
                        canvasHeight - f(minLevelY, 4.2f) - (displayRect.bottom - displayRect.top - displayGap),
                        canvasHeight - f(minLevelY, 4.2f) + displayGap * 3 / 2,
                        displayRect.bottom + displayPadding / 2 - (canvasHeight - f(minLevelY, 4.2f)) - displayGap * 3 / 2,
                        0, 0);
                doDrawText(canvas, "Y:" + displayFormat.format(angle1),
                        (displayRect.width() + displayGap) / 2 + 2 * displayPadding,
                        (displayRect.width() + displayGap) / 2 + 2 * displayPadding,
                        canvasHeight - f(minLevelY, 4.2f) - (displayRect.bottom - displayRect.top - displayGap),
                        canvasHeight - f(minLevelY, 4.2f) + displayGap * 3 / 2,
                        displayRect.bottom + displayPadding / 2 - (canvasHeight - f(minLevelY, 4.2f)) - displayGap * 3 / 2,
                        0, 0);
                doDrawDisc(canvas, false);
                canvas.drawBitmap(shadow, (minLevelX + maxLevelX) / 2 - shadow.getWidth() / 2,
                        f(maxLevelY + 16, 4.0f), infoPaint);
                canvas.restore();
                break;
            case LEFT:
                doDrawText(canvas, "X:" + displayFormat.format(angle1),
                        0, 0, displayRect.top - displayPadding * 3 / 4,
                        displayRect.bottom + displayPadding / 2,
                        0, orientation.getRotation(), 2 * displayGap);
                doDrawLine(canvas, minLevelX + 2 * displayGap, minLevelY, maxLevelX + 2 * displayGap, maxLevelY, orientation.getRotation(),
                        minLevelY, (int) (this.x - bubble1D.getWidth() / 2 + 2 * displayGap));
                canvas.restore();
                break;
            case RIGHT:
                doDrawText(canvas, "X:" + displayFormat.format(angle1),
                        0, 0, displayRect.top - displayPadding * 3 / 4,
                        displayRect.bottom + displayPadding / 2,
                        0, orientation.getRotation(), -2 * displayGap);
                doDrawLine(canvas, minLevelX - 2 * displayGap, minLevelY, maxLevelX - 2 * displayGap, maxLevelY, orientation.getRotation(),
                        minLevelY, (int) (this.x - bubble1D.getWidth() / 2 - 2 * displayGap));
                canvas.restore();
                break;
            case TOP:
                doDrawText(canvas, "X:" + displayFormat.format(angle1),
                        0, 0,
                        canvasHeight - f(canvasMidY - levelMaxDimension / 2, 4.2f) - (displayRect.bottom - displayRect.top),
                        canvasHeight - f(canvasMidY - levelMaxDimension / 2, 4.2f) + displayGap / 2,
                        displayRect.bottom + displayPadding / 2 - (canvasHeight - f(canvasMidY - levelMaxDimension / 2, 4.2f)) - displayGap / 2,
                        orientation.getRotation(), 0);
                doDrawLine(canvas, minLevelX, minLevelY - displayGap, maxLevelX, maxLevelY, orientation.getRotation(),
                        minLevelY - displayGap, (int) (this.x - halfBubbleWidth));
                canvas.restore();
                break;
            case BOTTOM:
                doDrawText(canvas, "X:" + displayFormat.format(angle1),
                        0, 0,
                        canvasHeight - f(canvasMidY - levelMaxDimension / 2, 4.2f) - (displayRect.bottom - displayRect.top),
                        canvasHeight - f(canvasMidY - levelMaxDimension / 2, 4.2f) + displayGap / 2,
                        displayRect.bottom + displayPadding / 2 - (canvasHeight - f(canvasMidY - levelMaxDimension / 2, 4.2f)) - displayGap / 2,
                        orientation.getRotation(), 0);
                doDrawLine(canvas, minLevelX, minLevelY + displayGap, maxLevelX, maxLevelY, orientation.getRotation(),
                        minLevelY + displayGap, (int) (this.x - halfBubbleWidth));
                canvas.restore();
                break;
        }
    }

    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * 上下
     * 左右
     */
    private void setOrientation(Orientation newOrientation) {
        if (!(lockEnabled && locked) || !initialized ||
                newOrientation == Orientation.ALL ||
                orientation == Orientation.ALL) {
            synchronized (this.surfaceHolder) {
                orientation = newOrientation;
                adjust();
            }
        }
    }

    /**
     * 适配表面视图
     */
    private void adaptSurface() {
        synchronized (this.surfaceHolder) {
            adjust();
        }
    }

    /**
     * 调整大小
     */
    private void adjust() {
        //canvas
        switch (orientation) {
            case LEFT:        // left
            case RIGHT:    // right
                infoY = (canvasHeight - canvasWidth) / 2 + canvasWidth - infoHeight;
                break;
            case TOP:        // top
            case BOTTOM:    // bottom
            default:        // landing
                infoY = canvasHeight - infoHeight;
                break;
        }

        sensorY = infoY - infoHeight - sensorGap;

        canvasMidX = canvasWidth / 2;
        canvasMidY = canvasHeight / 2;

        // level
        switch (orientation) {
            case ALL:
                levelWidth = levelMaxDimension;
                levelHeight = levelMaxDimension;
                break;
            case LANDING:    // landing
                levelWidth = levelMaxDimension - 2 * displayGap;
                levelHeight = levelMaxDimension - 2 * displayGap;
                break;
            case TOP:        // top
            case BOTTOM:    // bottom
                levelWidth = canvasWidth - 2 * displayGap;
                levelHeight = (int) (levelWidth * LEVEL_ASPECT_RATIO);
                break;
            case LEFT:        // left
            case RIGHT:    // right
                levelWidth = canvasWidth;
                levelHeight = (int) (levelWidth * LEVEL_ASPECT_RATIO);
                break;
        }

        viscosityValue = levelWidth * viscosity.getCoeff();

        minLevelX = canvasMidX - levelWidth / 2;
        maxLevelX = canvasMidX + levelWidth / 2;
        minLevelY = canvasMidY - levelHeight / 2;
        maxLevelY = canvasMidY + levelHeight / 2;

        // bubble
        halfBubbleWidth = (int) (levelWidth * BUBBLE_WIDTH / 2);
        halfBubbleHeight = (int) (halfBubbleWidth * BUBBLE_ASPECT_RATIO);
        bubbleWidth = 2 * halfBubbleWidth;
        bubbleHeight = 2 * halfBubbleHeight;

        // display
        displayRect.set(
                canvasMidX - lcdWidth / 2 - displayPadding,
                sensorY - displayGap - 2 * displayPadding - lcdHeight - infoHeight / 2,
                canvasMidX + lcdWidth / 2 + displayPadding,
                sensorY - displayGap - infoHeight / 2);

        // lock
        lockRect.set(
                canvasMidX - lockWidth / 2 - displayPadding,
                canvasMidY - canvasHeight / 2 + displayGap,
                canvasMidX + lockWidth / 2 + displayPadding,
                canvasMidY - canvasHeight / 2 + displayGap + 2 * displayPadding + lockHeight);

        // marker
        halfMarkerGap = (int) (levelWidth * MARKER_GAP / 2);

        // autres
        levelMinusBubbleWidth = levelWidth - bubbleWidth - 2 * levelBorderWidth;
        levelMinusBubbleHeight = levelHeight - bubbleHeight - 2 * levelBorderWidth;
        x = ((double) (maxLevelX + minLevelX)) / 2;
        y = ((double) (maxLevelY + minLevelY)) / 2;
        if (!initialized) {
            initialized = true;
            pause(false);
        }
    }

    /**
     * 获取底部边距
     */
    public int getMarginBottom() {
        return f(canvasMidY - levelMaxDimension / 2, 4.2f) + displayRect.bottom - displayRect.top + displayGap / 2;
    }

    /**
     * 方向改变修改参数
     *
     * @param newOrientation 方向
     * @param newPitch       //
     * @param newRoll        //
     * @param newBalance     //
     */
    public void onOrientationChanged(Orientation newOrientation, float newPitch, float newRoll, float newBalance) {
        if (!orientation.equals(newOrientation)) {
            setOrientation(newOrientation);
        }
        if (!wait) {
            switch (orientation) {
                case TOP:
                case BOTTOM:
                    angle1 = Math.abs(newBalance);
                    angleX = Math.sin(Math.toRadians(newBalance)) / MAX_SINUS;
                    break;
                case ALL:
                case LANDING:
                    angle2 = Math.abs(newRoll);
                    angleX = Math.sin(Math.toRadians(newRoll)) / MAX_SINUS;
                case RIGHT:
                case LEFT:
                    angle1 = Math.abs(newPitch);
                    angleY = Math.sin(Math.toRadians(newPitch)) / MAX_SINUS;
                    if (angle1 > 90) {
                        angle1 = 180 - angle1;
                    }
                    break;
            }
            switch (angleType) {
                case INCLINATION:
                    angle1 = 100 * angle1 / 45;
                    angle2 = 100 * angle2 / 45;
                    break;
                case ROOF_PITCH:
                    angle1 = 12 * (float) Math.tan(Math.toRadians(angle1));
                    angle2 = 12 * (float) Math.tan(Math.toRadians(angle2));
                    break;
            }
            // correction des angles affiches
            if (angle1 > angleType.getMax()) {
                angle1 = angleType.getMax();
            }
            if (angle2 > angleType.getMax()) {
                angle2 = angleType.getMax();
            }
            // correction des angles aberrants
            // pour ne pas que la bulle sorte de l'ecran
            if (angleX > 1) {
                angleX = 1;
            } else if (angleX < -1) {
                angleX = -1;
            }
            if (angleY > 1) {
                angleY = 1;
            } else if (angleY < -1) {
                angleY = -1;
            }
            // correction des angles a plat
            // la bulle ne doit pas sortir du niveau
            /*
             * 正方形约束在内切圆
             * 坐标X(Y)的约束力度随斜线与X(Y)轴夹角
             */
            if ((orientation.equals(Orientation.LANDING) ||
                    orientation.equals(Orientation.ALL)) && angleX != 0 && angleY != 0) {
                double n = Math.sqrt(angleX * angleX + angleY * angleY);
                double t = Math.acos(Math.abs(angleX) / n);
                double l = 1 / Math.max(Math.abs(Math.cos(t)), Math.abs(Math.sin(t)));
                angleX = angleX / l;
                angleY = angleY / l;
            }
            // lancement de l'animation si mode eco
            if (ecoMode) {
                handler.post(this);
            }
        }
    }

    public void setLockStatus(boolean locked) {
        this.locked = locked;
        if (orientationProvider != null)
            orientationProvider.setLocked(locked);
    }

    public boolean getLockStatus() {
        return locked;
    }

    public void setProvider(OrientationProvider provider) {
        orientationProvider = provider;
    }

    public Drawable getBubble2D() {
        return bubble2D;
    }

    public void setBubble2D(Drawable bubble2D) {
        this.bubble2D = bubble2D;
    }

    public Drawable getLevel1D() {
        return level1D;
    }

    public Drawable getLevel2D() {
        return level2D;
    }
}
