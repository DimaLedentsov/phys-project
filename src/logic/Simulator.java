package logic;

public class Simulator{
    public final Plottable function;
    private double time;
    private double step;
    public Simulator(Plottable f){
        function = f;
        time = 0;
        step = 0.001;
    }

    public double getTime(){
        return time;
    }
    public Point next(){
        time += step;
        return function.getPoint(time);
    }
    public Point getPoint(){
        return function.getPoint(time);
    }

    public void setStep(double s){
        step = s;
        time-=step;
    }
}
