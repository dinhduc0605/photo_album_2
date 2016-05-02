package com.framgia.photoalbum.crop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.framgia.photoalbum.util.DimenUtils;

public class HighLightView extends View {

    private static final float OUTLINE_DP = 2;

    private static final int DEFAULT_HIGHLIGHT_COLOR = 0xFF33B5E5;

    private static final int EVENT_NONE = 1;
    private static final int EVENT_GROW_BOTTOM = 2;
    private static final int EVENT_GROW_LEFT = 4;
    private static final int EVENT_MOVE = 5;
    private static final int EVENT_GROW_RIGHT = 6;
    private static final int EVENT_GROW_TOP = 8;

    private Paint mOutsidePaint;
    private Paint mOutlinePaint;
    private Paint mHandlePaint;
    private float mHandleRadius = 10f;

    private float mOutlineWidth;
    private View mViewContext;

    private int mHighlightColor = DEFAULT_HIGHLIGHT_COLOR;
    private Rect mDrawRect;
    private Rect mViewDrawingRect;
    private RectF mCropRect;
    private RectF mImageRect;

    private Path path;

    private Matrix mMatrix;
    private float lastX;
    private float lastY;
    private int mTouchEvent;

    private int mScreenW, mScreenH;
    private ImageView mImageShow;

    public HighLightView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HighLightView(Context context) {
        super(context);
    }

    private void setup(Matrix m, RectF cropRect, RectF imageRect) {
        this.mCropRect = cropRect;
        this.mImageRect = imageRect;
        this.mDrawRect = computeLayout();
        this.mMatrix = new Matrix(m);
        this.path = new Path();

        mViewContext = this;

        mOutsidePaint = new Paint();
        mOutsidePaint.setARGB(125, 50, 50, 50);

        mOutlinePaint = new Paint();
        mOutlinePaint.setStyle(Paint.Style.STROKE);
        mOutlinePaint.setAntiAlias(true);
        mOutlineWidth = DimenUtils.dpToPx(mViewContext.getContext(), OUTLINE_DP);
        mOutlinePaint.setStrokeWidth(mOutlineWidth);

        mHandlePaint = new Paint();
        mHandlePaint.setStyle(Paint.Style.FILL);
        mHandlePaint.setColor(mHighlightColor);
    }

    /**
     * @return the real size of mCropRect
     */
    private Rect computeLayout() {
        RectF r = new RectF(mCropRect.left, mCropRect.top,
                mCropRect.right, mCropRect.bottom);

        return new Rect(Math.round(r.left), Math.round(r.top),
                Math.round(r.right), Math.round(r.bottom));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        getDrawingRect(mViewDrawingRect);

        path.reset();

        path.addRect(new RectF(mDrawRect), Path.Direction.CW);
        mOutlinePaint.setColor(mHighlightColor);

        canvas.clipPath(path, Region.Op.DIFFERENCE);
        canvas.drawRect(mViewDrawingRect, mOutsidePaint);

        canvas.restore();

        canvas.drawPath(path, mOutlinePaint);
        drawThirds(canvas);
        drawHandlers(canvas);
    }

    // Draw grid of 3 * 3
    private void drawThirds(Canvas canvas) {
        mOutlinePaint.setStrokeWidth(1);

        float xThird = (mDrawRect.right - mDrawRect.left) / 3;
        float yThird = (mDrawRect.bottom - mDrawRect.top) / 3;

        canvas.drawLine(mDrawRect.left + xThird, mDrawRect.top,
                mDrawRect.left + xThird, mDrawRect.bottom, mOutlinePaint);
        canvas.drawLine(mDrawRect.left + xThird * 2, mDrawRect.top,
                mDrawRect.left + xThird * 2, mDrawRect.bottom, mOutlinePaint);
        canvas.drawLine(mDrawRect.left, mDrawRect.top + yThird,
                mDrawRect.right, mDrawRect.top + yThird, mOutlinePaint);
        canvas.drawLine(mDrawRect.left, mDrawRect.top + yThird * 2,
                mDrawRect.right, mDrawRect.top + yThird * 2, mOutlinePaint);
    }

    /**
     * Draw a circle in middle of each edge in Rect
     */
    private void drawHandlers(Canvas canvas) {
        int xMiddle = mDrawRect.centerX();
        int yMiddle = mDrawRect.centerY();

        canvas.drawCircle(xMiddle, mDrawRect.top, mHandleRadius, mHandlePaint);
        canvas.drawCircle(xMiddle, mDrawRect.bottom, mHandleRadius, mHandlePaint);
        canvas.drawCircle(mDrawRect.left, yMiddle, mHandleRadius, mHandlePaint);
        canvas.drawCircle(mDrawRect.right, yMiddle, mHandleRadius, mHandlePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();

                mTouchEvent = getTouchEvent(lastX, lastY);
                break;
            case MotionEvent.ACTION_UP:
                // Handle action when user release
                break;
            case MotionEvent.ACTION_MOVE:
                handleMotion(mTouchEvent, event.getX() - lastX, event.getY() - lastY);
                lastX = event.getX();
                lastY = event.getY();
                break;
        }

        return true;
    }

    // Handle motion (dx,dy) in screen space.
    private void handleMotion(int event, float dx, float dy) {
        Rect r = computeLayout();
        if (event == EVENT_NONE) {
            return;
        }

        if (event == EVENT_MOVE) {
            moveBy(dx * (mCropRect.width() / r.width()), dy * (mCropRect.height() / r.height()));
            return;
        }

        if (event == EVENT_GROW_RIGHT || event == EVENT_GROW_LEFT) {
            dy = 0;
        } else if (event == EVENT_GROW_TOP || event == EVENT_GROW_BOTTOM) {
            dx = 0;
        }

        growBy(event, dx, dy);
    }

    /**
     * Move mCropRect with (dx, dy)
     *
     * @param dx the change of x cord
     * @param dy the change of y cord
     */
    private void moveBy(float dx, float dy) {
        Rect invalRect = new Rect(mDrawRect);

        mCropRect.offset(dx, dy);

        // Set the mCropRect inside Image only
        mCropRect.offset(
                Math.max(0, mDrawRect.left - mCropRect.left),
                Math.max(0, mDrawRect.top - mCropRect.top));

        mCropRect.offset(
                Math.min(0, mDrawRect.right - mCropRect.right),
                Math.min(0, mDrawRect.bottom - mCropRect.bottom));

        mDrawRect = computeLayout();

        // Calculate invalRect in order to invalidate only Pixel in this Rect
        invalRect.union(mDrawRect);
        invalRect.inset(-(int) mHandleRadius, -(int) mHandleRadius);
        invalidate(invalRect);
    }

    private void growBy(int event, float dx, float dy) {
        RectF r = new RectF(mCropRect);

        dx *= event == EVENT_GROW_LEFT ? -1 : 1;
        dy *= event == EVENT_GROW_TOP ? -1 : 1;

        r.inset(-dx, -dy);

        // Don't let the cropping rectangle shrink too fast
        final float widthCap = 75F;

        if (r.width() < widthCap) {
            r.inset(-(widthCap - r.width()) / 2F, 0F);
        }

        float heightCap = widthCap;

        if (r.height() < heightCap) {
            r.inset(0F, -(heightCap - r.height()) / 2F);
        }

        mCropRect.set(r);

        mDrawRect = computeLayout();
        invalidate();
    }

    private int getTouchEvent(float x, float y) {
        Rect r = computeLayout();

        final float hysteresis = 20f;

        int event = EVENT_NONE;

        if (Math.abs(r.left - x) <= hysteresis) {
            event = EVENT_GROW_LEFT;
        } else if (Math.abs(r.right - x) <= hysteresis) {
            event = EVENT_GROW_RIGHT;
        } else if (Math.abs(r.top - y) <= hysteresis) {
            event = EVENT_GROW_TOP;
        } else if (Math.abs(r.bottom - y) <= hysteresis) {
            event = EVENT_GROW_BOTTOM;
        } else if (event == EVENT_NONE && r.contains((int) x, (int) y)) {
            event = EVENT_MOVE;
        }

        return event;
    }

}