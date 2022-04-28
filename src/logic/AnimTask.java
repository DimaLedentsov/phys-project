package logic;

import java.util.List;

import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class AnimTask extends Thread{
    private List<Simulator> sims;
    LineChart<Number,Number> chart;
    volatile boolean running;
    final double step = 0.01;
    final int TIME_K = 1000;
    public AnimTask(List<Simulator> sims, LineChart<Number,Number> chart){
        this.chart = chart;
        this.running = true;
        this.sims = sims;
        sims.forEach((s)->s.setStep(step));
    }
    public void run(){
        while (isRunning()) { 
            try {
                Thread.sleep((int) (step * TIME_K));
                for(int i=0; i<sims.size();i++){
                    Simulator sim = sims.get(i);
                    final int  j = i;
                    Platform.runLater(
                        () -> {
                            Point point = sim.next();
                            NumberAxis axis = (NumberAxis)chart.getXAxis();
                            if(!axis.isAutoRanging() && axis.getUpperBound()<point.x){
                                axis.setUpperBound(point.x+10);
                            }
                            if(isRunning()) chart.getData().get(j).getData().add(new XYChart.Data<>(point.x, point.y));
                        });
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void kill(){
        running = false;
    }

    public void proceed(){
        running = true;
    }

    public boolean isRunning(){
        return running;
    }
}
