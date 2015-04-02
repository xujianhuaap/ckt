package me.ketie.app.android.view;

/**
 * Created by henjue on 2015/4/2.
 */
public class Rect  implements Cloneable {
    private Point lt;
    private Point rt;
    private Point rb;
    private Point lb;
    public void offset(float offsetX,float offsetY){
        lt.offset(offsetX,offsetY);
        rt.offset(offsetX,offsetY);
        rb.offset(offsetX,offsetY);
        lb.offset(offsetX,offsetY);
    }
    Rect(){
        lt=new Point(0,0);
        rt=new Point(0,0);
        rb=new Point(0,0);
        lb=new Point(0,0);
    }
    Rect(Point lt, Point rt, Point rb, Point lb) {
        this.lt = lt;
        this.rt = rt;
        this.rb = rb;
        this.lb = lb;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new Rect(this);
    }

    Rect(Rect in){
        this(in.lt,in.rt,in.rb,in.lb);
    }
}
