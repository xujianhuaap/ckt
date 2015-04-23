package me.ketie.app.android.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import me.ketie.app.android.utils.LogUtil;

public class DrawImageLayout extends FrameLayout {
    private final int MESSAGE_TYPE_LOADIMAGE_SUCCESS = 1;
    private final int MESSAGE_TYPE_LOADIMAGE_FAILED = MESSAGE_TYPE_LOADIMAGE_SUCCESS + 1;
    private final BuildHanlder buildHanlder;
    private final String LOG_TAG = DrawImageLayout.class.getSimpleName();
    private ImageInfo[] pats = null;
    /**
     * 手指头的x坐标
     */
    private float X = 0f;
    /**
     * 手指头的y坐标
     */
    private float Y = 0f;
    /**
     * 按下时手指头的x坐标与图片的x坐标的距离 *
     */
    private float CX = 0f;
    /**
     * 按下时手指头的y坐标与图片的y坐标的距离 *
     */
    private float CY = 0f;
    private String tag = this.getClass().getSimpleName();
    private DrawImageView topImageInfo = null;
    private float[] rotalP = null;
    private float[] rotalP_2 = null;
    private float[] rotalC = null;
    private float preLength = 480.0f;
    private float length = 480.0f;
    private float preCos = 0f;
    private float cos = 0f;
    private boolean bool = true;
    private boolean Begin = true;
    private float[] p1 = new float[2];
    private float[] p2 = new float[2];
    private HandlerThread buildThreah = new HandlerThread("buildThreah");

    public DrawImageLayout(Context context) {
        super(context);
        buildThreah.start();
        buildHanlder = new BuildHanlder(this, buildThreah.getLooper());
//		setWillNotDraw(false);
    }

    public DrawImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        buildThreah.start();
        buildHanlder = new BuildHanlder(this, buildThreah.getLooper());
    }

    public DrawImageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        buildThreah.start();
        buildHanlder = new BuildHanlder(this, buildThreah.getLooper());
    }

    public DrawImageLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        buildThreah.start();
        buildHanlder = new BuildHanlder(this, buildThreah.getLooper());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void setImages(ImageInfo[] paths) {
        if (paths == null)
            return;
        pats = paths;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                actionDown(event);
                break;
            //副点按下
            case MotionEvent.ACTION_POINTER_DOWN:
                topImageInfo.getSavedMatrix().set(topImageInfo.getmMatrix());
                p2[0] = event.getX(1);
                p2[1] = event.getY(1);
                topImageInfo.setMood(DrawImageView.MOOD_ACTION_POINTERDOWN);
                break;
            case MotionEvent.ACTION_UP:
                CX = 0f;
                CY = 0f;
                topImageInfo.setMood(DrawImageView.MOOD_ACTION_UP);
                Begin = false;
                bool = true;
                return true;
            case MotionEvent.ACTION_POINTER_UP:
                topImageInfo.setMood(DrawImageView.MOOD_ACTION_POINTERUP);
                Begin = false;
                bool = true;
                return true;
            case MotionEvent.ACTION_MOVE:
                boolean b = actionMove(event);
                if (b)
                    return b;
                break;
        }
        topImageInfo.setImageMatrix(topImageInfo.getmMatrix());
        invalidate();
        return true;
    }

    public void addImage(DrawImageView image) {
        image.setPiority(getChildCount());
        if (topImageInfo != null) topImageInfo.setDrawBorder(false);
        this.topImageInfo = image;
        topImageInfo.setDrawBorder(true);
        addView(image);
        image.bringToFront();

    }

    public void buildBitmap(BuildListener listener) {
        buildHanlder.obtainMessage(BuildHanlder.MSG_BUILD, listener).sendToTarget();
    }

    private boolean actionMove(MotionEvent event) {
        if (Begin && topImageInfo.getMood() == DrawImageView.MOOD_ACTION_DOWN) {
            if (spacingSingel(event.getX(0), event.getY(0), p1[0], p1[1]) < 5)
                return true;
            p1[0] = event.getX(0);
            p1[1] = event.getY(0);
            this.X = event.getX();
            this.Y = event.getY();
            topImageInfo.getmMatrix().set(topImageInfo.getSavedMatrix());
            rotalP = rotalPoint(new float[]{this.X, this.Y}, topImageInfo.getPreX(),
                    topImageInfo.getPreY(), topImageInfo.getmMatrix());
            rotalC = getT(topImageInfo.getmWidth() / 2f, topImageInfo.getmHeight() / 2f, X + CX,
                    Y + CY, topImageInfo.getmMatrix());
            float oldPreX = topImageInfo.getPreX();
            float oldPreY = topImageInfo.getPreY();
            topImageInfo.setPreX(X + CX);
            topImageInfo.setPreY(Y + CY);
            topImageInfo.transFrame(topImageInfo.getPreX() - oldPreX, topImageInfo.getPreY() - oldPreY);
        }

        // 两指移动
        if (topImageInfo.getMood() == DrawImageView.MOOD_ACTION_POINTERDOWN) {
            float p1J = spacingSingel(event.getX(0), event.getY(0), p1[0], p1[1]);
            float p2J = spacingSingel(event.getX(1), event.getY(1), p2[0], p2[1]);

            if (p1J < 5 && p2J < 5) {
                return true;
            }
            p1[0] = event.getX(0);
            p1[1] = event.getY(0);
            p2[0] = event.getX(1);
            p2[1] = event.getY(1);
            rotalP = rotalPoint(new float[]{event.getX(0), event.getY(0)},
                    topImageInfo.getPreX(), topImageInfo.getPreY(), topImageInfo.getmMatrix());
            rotalP_2 = rotalPoint(new float[]{event.getX(1), event.getY(1)},
                    topImageInfo.getPreX(), topImageInfo.getPreY(), topImageInfo.getmMatrix());
            if ((Math.abs(rotalP[0] - topImageInfo.getPreX()) < topImageInfo.getmWidth() / 2f)
                    && (Math.abs(rotalP[1]
                    - topImageInfo.getPreY()) < topImageInfo.getmHeight() / 2f)
                    && (Math.abs(rotalP_2[0]
                    - topImageInfo.getPreX()) < topImageInfo.getmWidth() / 2f)
                    && (Math.abs(rotalP_2[1]
                    - topImageInfo.getPreY()) < topImageInfo.getmHeight() / 2f) || true) {
                if (bool) {
                    // 第一次两指头点来，记录下角度和长度
                    preLength = spacing(event);
                    preCos = cos(event);
                    bool = false;
                }
                // 获取最新角度和长度
                length = spacing(event);
                cos = cos(event);
                LogUtil.i(tag, "actionMove() -- 旋转角度:" + cos);
                float width = topImageInfo.getmWidth();
                float height = topImageInfo.getmHeight();
                LogUtil.i(tag, "actionMove() -- width:" + width + "; height:" + height);
                // 放大和缩小
                if (length - preLength != 0) {

                    float scW = (1.0f + (length - preLength) / length);
                    topImageInfo.getmMatrix().postScale(scW, scW, topImageInfo.getPreX(), topImageInfo.getPreY());
                    topImageInfo.scalFrame(scW);
                }

                // 旋转
                if (Math.abs(cos) > 5 && Math.abs(cos) < 177
                        && Math.abs(cos - preCos) < 15) {
                    topImageInfo.getmMatrix().postRotate(cos - preCos);
                    this.getT(width / 2f, height / 2f,
                            topImageInfo.getPreX(), topImageInfo.getPreY(), topImageInfo.getmMatrix());
                    topImageInfo.rotateFrame(width, height);
                }
                preCos = cos;
                preLength = length;

            }
        }
        return false;
    }

    private boolean actionDown(MotionEvent event) {
        order(event);
        // 设置最顶上的imageview
        topImageInfo = findTopImage();
        topImageInfo.bringToFront();
        this.X = event.getX();
        this.Y = event.getY();
        CX = topImageInfo.getPreX() - event.getX();
        CY = topImageInfo.getPreY() - event.getY();
        topImageInfo.getSavedMatrix().set(topImageInfo.getmMatrix());
        Begin = true;
        p1[0] = event.getX();
        p1[1] = event.getY();
        topImageInfo.setMood(DrawImageView.MOOD_ACTION_DOWN);
        return true;
    }

    /**
     * 找到优先级最高的view
     *
     * @return
     */
    private DrawImageView findTopImage() {
        int pre = 0;
        int index = 0;
        for (int i = 0; i < getChildCount(); i++) {
            DrawImageView my = (DrawImageView) getChildAt(i);
            if (my.getPiority() > pre) {
                pre = my.getPiority();
                index = i;
            }
        }
        return (DrawImageView) getChildAt(index);
    }


    /**
     * @param preX   图片中心点x
     * @param preY   图片中心点y
     * @param x      手指头x坐标加上移动的x轴距离
     * @param y      手指头y坐标加上移动的y轴距离
     * @param matrix
     * @return
     */
    public float[] getT(float preX, float preY, float x, float y, Matrix iv) {
        float[] re = new float[2];
        float[] matrixArray = new float[9];
        iv.getValues(matrixArray);
        float a = x - preX * matrixArray[0] - preY * matrixArray[1];
        float b = y - preX * matrixArray[3] - preY * matrixArray[4];
        matrixArray[2] = a;
        matrixArray[5] = b;
        iv.setValues(matrixArray);
        re[0] = a;
        re[1] = b;
        return re;
    }

    /**
     * 得到旋转点
     *
     * @param p      当前手指头的x,y坐标
     * @param X      图片之前的x坐标
     * @param Y      图片之前的y坐标
     * @param width
     * @param height
     * @param matrix
     * @return
     */
    public float[] rotalPoint(float[] p, float X, float Y, Matrix matrix) {
        float re[] = new float[2];
        float matrixArray[] = new float[9];
        matrix.getValues(matrixArray);
        LogUtil.i(tag, "rotalPoint() -- matrixArray[0]: " + matrixArray[0] + "; matrixArray[1] :" + matrixArray[1] + "; matrixArray[2] :" + matrixArray[1]);
        LogUtil.i(tag, "rotalPoint() -- matrixArray[3]: " + matrixArray[3] + "; matrixArray[4] :" + matrixArray[4] + "; matrixArray[5] :" + matrixArray[5]);
        // 计算出x,y的差值
        float a = p[0] - X;
        float b = p[1] - Y;
        // 矩阵公式
        // x' = a*x+b*y+c
        re[0] = a * matrixArray[0] - b * matrixArray[1] + X;
        re[1] = -a * matrixArray[3] + b * matrixArray[4] + Y;
//		re[0] = a * matrixArray[0] + b * matrixArray[1] + X;
//		re[1] = a * matrixArray[3] + b * matrixArray[4] + Y;
        LogUtil.i(tag, "rotalPoint() -- re[0]: " + re[0] + "; re[1] :" + re[1] + "; a :" + a + "; b:" + b + ";X:" + X + ";Y:" + Y);
        return re;
    }


    /**
     * 计算长度
     *
     * @param event
     * @return
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    private float spacingSingel(float newX, float newY, float oldX, float oldY) {
        float x = newX - oldX;
        float y = newY - oldY;
        return FloatMath.sqrt(x * x + y * y);
    }

    /**
     * 计算余弦
     *
     * @param event
     * @return
     */
    private float cos(MotionEvent event) {
        // LogUtil.i("XY", String.valueOf(event.getX(0))
        // + " " + String.valueOf(event.getY(0))
        // + " " + String.valueOf(event.getX(1))
        // + " " + String.valueOf(event.getY(1)));
        if ((event.getX(0) - event.getX(1)) * (event.getY(0) - event.getY(1)) > 0) {
            return (float) ((float) Math.acos(Math.abs(event.getX(0)
                    - event.getX(1))
                    / spacing(event))
                    / Math.PI * 180f);
        }
        if ((event.getX(0) - event.getX(1)) * (event.getY(0) - event.getY(1)) < 0) {
            return (float) ((float) Math.acos(-Math.abs(event.getX(0)
                    - event.getX(1))
                    / spacing(event))
                    / Math.PI * 180f);
        }
        if (event.getX(0) - event.getX(1) == 0) {
            return (float) 90f;
        }
        if (event.getY(0) - event.getY(1) == 0) {
            return 0f;
        }
        return 45f;
    }

    public float[] scale(float preX, float preY, float x, float y, Matrix matrix) {
        float[] matrixArray = new float[9];
        matrix.getValues(matrixArray);
        float a = x - preX;
        float b = y - preY;
        matrixArray[2] = a;
        matrixArray[5] = b;
        matrix.setValues(matrixArray);
        float[] scale = {a, b};
        return scale;
    }

    public void setToO(Matrix matrix) {
        float[] matrixArray = new float[9];
        matrix.getValues(matrixArray);
        float a = 0f;
        float b = 0f;
        matrixArray[2] = a;
        matrixArray[5] = b;
        matrix.setValues(matrixArray);
    }

    public void order(MotionEvent event) {
        DrawImageView temp = null;
        for (int i = (getChildCount() - 1); i > -1; i--) {
            temp = (DrawImageView) getChildAt(i);
            // 获取触控点
            float tx = event.getX();
            float ty = event.getY();
            // 存放新坐标的数组
            float[] dst = new float[2];
            // 触控点坐标的数组
            float[] src = {tx, ty};
            Matrix matrix = new Matrix();
            // 获取绘制图片的Matrix，并转换mantrix
            // set inverse to be the inverse of this matrix.
            if (temp.getImageMatrix().invert(matrix)) {
                // 触控坐标根据matrix转换成新的坐标，并存放于dst
                matrix.mapPoints(dst, src);
            }
            boolean isSelect = false;
            float[] ma = new float[9];
            temp.getImageMatrix().getValues(ma);

            /**
             * 判断是否击中bitmap
             */
            if (dst[0] >= 0 && dst[0] <= temp.getmWidth() && dst[1] >= 0
                    && dst[1] <= temp.getmHeight()) {
                isSelect = true;
            }
            if (isSelect) {
                LogUtil.d(LOG_TAG, "当前选中第%d层图片", temp.getPiority());
                for (int j = (getChildCount() - 1); j > -1; j--) {
                    DrawImageView child = (DrawImageView) getChildAt(j);
                    if (child.getPiority() > temp.getPiority()) {
                        child.setPiority(child.getPiority() - 1);
                    }
                    child.setDrawBorder(false);
                    child.invalidate();
                }
                temp.setPiority(getChildCount() - 1);
                temp.setDrawBorder(true);
                return;
            }
//			boolean b = pointIsOnView(temp,event);
//			if(b){
//				topImageInfo = temp;
//				return;
//			}
        }
    }


    private boolean pointIsOnView(DrawImageView temp, MotionEvent event) {
        rotalP = rotalPoint(new float[]{event.getX(), event.getY()},
                temp.getPreX(),
                temp.getPreY(),
                temp.getmMatrix());
        if ((Math.abs(temp.getPreX() - rotalP[0]) < temp.getmWidth() / 2)
                && (Math.abs(temp.getPreY()
                - rotalP[1]) < temp.getmHeight() / 2)) {
            for (int j = (getChildCount() - 1); j > -1; j--) {
                LogUtil.i(tag, "pointIsOnView() -- j:" + j);
                DrawImageView child = (DrawImageView) getChildAt(j);
                if (child.getPiority() > temp.getPiority()) {
                    child.setPiority(child.getPiority() - 1);
                }
                child.setDrawBorder(false);
                child.invalidate();
            }
            temp.setPiority(getChildCount() - 1);
            temp.setDrawBorder(true);

            return true;
        }
        return false;
    }

    public interface BuildListener {
        public void onComplete(DrawImageLayout thiz, Bitmap bitmap);

        public void onFaild(DrawImageLayout thiz);
    }

    private final class BuildHanlder extends Handler {
        public static final int MSG_BUILD = 0x1;
        private final DrawImageLayout layout;

        public BuildHanlder(DrawImageLayout layout, Looper looper) {
            super(looper);
            this.layout = layout;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_BUILD: {
                    BuildListener listener = (BuildListener) msg.obj;
                    Bitmap bit = Bitmap.createBitmap(layout.getMeasuredWidth(), layout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bit);
                    topImageInfo.setDrawBorder(false);
                    draw(canvas);
                    topImageInfo.setDrawBorder(true);
                    listener.onComplete(layout, bit);
                }
            }

        }
    }
}