package logic;
import static java.lang.Math.*;
public class AngleFunction implements Function{
    public final double b;
    public final double alpha_0;
    public final double l;
    public final double m;
    public final double g = 9.8;
    public double alpha;
    public double p_alpha;
    public double v;
    public double p_v;
    public final double d_t;
  
    public AngleFunction(double b, double a_0, double l, double m, double dt){
        this.alpha_0 = toRadians(a_0);
        alpha = alpha_0;
        p_alpha = alpha_0;
        this.b = b;
        this.l = l;
        this.m = m;
        d_t = dt;
        v=p_v=0; 
    }
    public double call(double t){
        alpha = ((p_v/d_t - g*sin(p_alpha)-b*p_v/m)/
        (l/(d_t*d_t) + g*cos(p_alpha)));
        alpha += p_alpha;
        
        v = (alpha-p_alpha)/d_t*l;

        p_alpha = alpha;
        p_v = v;
        return alpha;
    }

}
