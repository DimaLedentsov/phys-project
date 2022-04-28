package logic;
import static java.lang.Math.*;
public class DeviationFunction extends AngleFunction implements Plottable{
    public DeviationFunction(double b, double alpha_0, double l, double m, double dt){
        super(b, alpha_0, l, m, dt);
    }

    @Override
    public Point getPoint(double t){
        double angle = super.call(t);
        return new Point(t, sin(angle)*l);
    }
}
