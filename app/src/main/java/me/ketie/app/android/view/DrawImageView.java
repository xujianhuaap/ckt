package me.ketie.app.android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import me.ketie.app.android.utils.DocumentSelector;
import me.ketie.app.android.utils.ScalingUtilities;


public class DrawImageView extends ImageView {
	private float preX;
	private float preY;
	private float mWidth;
	private float mHeight;
	private Matrix mMatrix=new Matrix();
	private Matrix savedMatrix=new Matrix();
	private Matrix scaleMatrix=new Matrix();
	
	private int piority;
	boolean isDrawBorder= false;
	boolean isInit = false;
	private int mood = 0;
	public static final int MOOD_ACTION_DOWN=1;
	public static final int MOOD_ACTION_POINTERDOWN=2;
	public static final int MOOD_ACTION_POINTERUP=3;
	public static final int MOOD_ACTION_UP=4;
	public static final int MOOD_ACTION_MOVE=5;
	private float[] mFrame = new float[8];
	public DrawImageView(Context context,int preX,int preY,int mWidth,int mHeight,int piority) {
		super(context);
		this.setScaleType(ScaleType.MATRIX);
        this.preX=preX;
        this.preY=preY;
        this.mWidth=mWidth;
        this.mHeight=mHeight;
        this.piority=piority;
        mMatrix.postTranslate(preX, preY);
        getImageMatrix().set(mMatrix);
	}

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
    }
    public void setImageBitmap(String path){
        Bitmap bit = ScalingUtilities.createCenterScropBitmap(path,(int)mWidth,(int)mHeight);
        setImageBitmap(bit);
    }
    public void setImageBitmap(Uri uri){
        Bitmap bit = ScalingUtilities.createCenterScropBitmap(DocumentSelector.getPath(getContext(),uri),(int)mWidth,(int)mHeight);
        setImageBitmap(bit);
    }
    private int color ;
    private boolean setColor = false;
    
	 public void setColor(int color) {
		this.color = color;
	}
	 

	public void setSetColor(boolean setColor) {
		this.setColor = setColor;
	}


	private String tag= this.getClass().getSimpleName();

	@Override
     protected void onDraw(Canvas canvas) {
        super.onDraw(canvas); 
      	BitmapDrawable bd = (BitmapDrawable)this.getDrawable();
      	if(bd!=null&&!isInit)
      	{
      		isInit = true;
            Bitmap bitmap = bd.getBitmap();
            this.mWidth = bitmap.getWidth();
      		this.mHeight = bitmap.getHeight();
      		setDefaultFrame(mWidth, mHeight);
      	}
      	dra(canvas);
    }


	public float getPreX() {
		return preX;
	}


	public void setPreX(float preX) {
		this.preX = preX;
	}


	public float getPreY() {
		return preY;
	}


	public void setPreY(float preY) {
		this.preY = preY;
	}


	public int getColor() {
		return color;
	}


	public float getmWidth() {
      	BitmapDrawable bd = (BitmapDrawable)this.getDrawable();
      	if(bd!=null)
      	{
      		this.mWidth = bd.getBitmap().getWidth();
      	}
		return mWidth;
	}


	public void setmWidth(float mWidth) {
		this.mWidth = mWidth;
	}


	public float getmHeight() {
      	BitmapDrawable bd = (BitmapDrawable)this.getDrawable();
      	if(bd!=null)
      	{
      		this.mHeight = bd.getBitmap().getHeight();
      	}
		return mHeight;
	}


	public void setmHeight(float mHeight) {
		this.mHeight = mHeight;
	}


	public int getPiority() {
		return piority;
	}


	public void setPiority(int piority) {
		this.piority = piority;
	}

	public void setDefaultFrame(float desX, float desY) {
//		mFrame[0] = this.preX - desX;
//		mFrame[1] = this.preY - desY;
//
//		mFrame[2] = this.preX + desX;
//		mFrame[3] = this.preY - desY;
//
//		mFrame[4] = this.preX + desX;
//		mFrame[5] = this.preY + desY;
//
//		mFrame[6] = this.preX - desX;
//		mFrame[7] = this.preY + desY;



        mFrame[0] = this.preX;
        mFrame[1] = this.preY;

        mFrame[2] = this.preX + desX;
        mFrame[3] = this.preY;
        mFrame[4] = this.preX + desX;
        mFrame[5] = this.preY + desY;

        mFrame[6] = this.preX;
        mFrame[7] = this.preY + desY;
	}

	public void transFrame(float offsetX, float offsetY) {
		mFrame[0] += offsetX;
		mFrame[1] += offsetY;
		mFrame[2] += offsetX;
		mFrame[3] += offsetY;
		mFrame[4] += offsetX;
		mFrame[5] += offsetY;
		mFrame[6] += offsetX;
		mFrame[7] += offsetY;
	}
	
	public void scalFrame(float scale){
		rotateFrame(mWidth, mHeight);
	}
	

	public void rotateFrame(float width,float height) {
		setDefaultFrame(width, height);
		
		float[] temp = new float[mFrame.length];
		System.arraycopy(mFrame, 0, temp, 0, mFrame.length);
		float[] matrixArray = new float[9];
		this.mMatrix.getValues(matrixArray);
		mFrame[0] = temp[0]*matrixArray[0] + temp[1]*matrixArray[1];
		mFrame[1] = temp[1]*matrixArray[4] + temp[0]*matrixArray[3];
		mFrame[2] = temp[2]*matrixArray[0] + temp[3]*matrixArray[1];
		mFrame[3] = temp[3]*matrixArray[4] + temp[2]*matrixArray[3];
		mFrame[4] = temp[4]*matrixArray[0] + temp[5]*matrixArray[1];
		mFrame[5] = temp[5]*matrixArray[4] + temp[4]*matrixArray[3];
		mFrame[6] = temp[6]*matrixArray[0] + temp[7]*matrixArray[1];
		mFrame[7] = temp[7]*matrixArray[4] + temp[6]*matrixArray[3];
		if(matrixArray[2] > mFrame[0]) {
			float offsetX = matrixArray[2] - mFrame[0];
			float offsetY = mFrame[1] - matrixArray[5];
			mFrame[0] += offsetX;
			mFrame[1] -= offsetY;
			mFrame[2] += offsetX;
			mFrame[3] -= offsetY;
			mFrame[4] += offsetX;
			mFrame[5] -= offsetY;
			mFrame[6] += offsetX;
			mFrame[7] -= offsetY;
		} else {
			float offsetX = mFrame[0] - matrixArray[2];
			float offsetY = matrixArray[5] - mFrame[1];
			mFrame[0] -= offsetX;
			mFrame[1] += offsetY;
			mFrame[2] -= offsetX;
			mFrame[3] += offsetY;
			mFrame[4] -= offsetX;
			mFrame[5] += offsetY;
			mFrame[6] -= offsetX;
			mFrame[7] += offsetY;
		}
		
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isOnView(float x,float y){
		Matrix inMatrix = new Matrix();
//		inMatrix.set(mMatrix);
		mMatrix.invert(inMatrix);
		float[] xy = new float[2];
		inMatrix.mapPoints(xy,new float[]{x,y});
		if(xy[0] > 0 && xy[0] < mWidth && xy[1] > 0 && xy[1] < mHeight)
		{
			return true;
		}else{
		}
		return false;
	}


	public float[] getmFrame() {
		return mFrame;
	}


	public Matrix getmMatrix() {
		return mMatrix;
	}


	public void setmMatrix(Matrix mMatrix) {
		this.mMatrix = mMatrix;
	}
	class Point {
		float x0, y0, x1, y1, x2, y2, x3, y3;
	}

	private void drawAl(Point point, Canvas canvas, int color, int alpha) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(4);
		//if (alpha >= 0)
			//paint.setAlpha(alpha);
		Path p = new Path();
		p.moveTo(point.x0, point.y0);
		p.lineTo(point.x1, point.y1);
		p.lineTo(point.x2, point.y2);
		p.lineTo(point.x3, point.y3);
		p.close();
		canvas.drawPath(p, paint);
	}
	
	private void dra(Canvas canvas){
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG));
//		canvas.drawBitmap(canvasBitmap, 0, 0, null);
		Point point = new Point();
		point.x0 = mFrame[0];
		point.y0 = mFrame[1];
		point.x1 = mFrame[2];
		point.y1 = mFrame[3];
		point.x2 = mFrame[4];
		point.y2 = mFrame[5];
		point.x3 = mFrame[6];
		point.y3 = mFrame[7];
		int color = Color.parseColor("#f977a7");
		if(!isDrawBorder){
			color = Color.parseColor("#00000000");
		}
		drawAl(point, canvas, color, 0);
	}


	public void setDrawBorder(boolean isDrawBorder) {
		this.isDrawBorder = isDrawBorder;
	}


	public Matrix getSavedMatrix() {
		return savedMatrix;
	}


	public void setSavedMatrix(Matrix savedMatrix) {
		this.savedMatrix = savedMatrix;
	}


	public int getMood() {
		return mood;
	}


	public void setMood(int mood) {
		this.mood = mood;
	}


	public Matrix getScaleMatrix() {
		return scaleMatrix;
	}


	public void setScaleMatrix(Matrix scaleMatrix) {
		this.scaleMatrix = scaleMatrix;
	}
	
	
	
}
