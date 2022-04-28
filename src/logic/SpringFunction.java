package logic;

import static java.lang.Math.*;
public class SpringFunction implements Function,Plottable{
    public final double b;

    public final double m;
    public final double g = 9.8;
    public final double x_0;
    public double v;
    public double p_v;
    public double p_x;
    public final double d_t;
    public final double k;
    public SpringFunction(double b, double x_0, double k, double m, double dt){
        this.b = b;
        this.m = m;
        d_t = dt;
        v=0; 
        p_v=0;
        this.k = k;
        this.x_0 = x_0;
        p_x = x_0;
    }
    public double call(double t){
        double x = (p_x/(d_t*d_t) + p_v/d_t - g - b/m*p_v) /
            (1/(d_t*d_t) + k/m);
        v = (x-p_x)/d_t;
        p_x=x;
        p_v = v;
        x+= m*g/k;
        return x;
    }

    @Override
    public Point getPoint(double t){
        return new Point(t, call(t));
    }

}
