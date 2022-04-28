package test;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Alberto Pedroni <acepsut@gmail.com>
 */
public class LineChartSample extends Application {
    
  @Override  
    public void start(Stage stage) {  
        stage.setTitle("Line Chart Sample");  
        // defining the axes  
        final NumberAxis xAxis = new NumberAxis();  
        final NumberAxis yAxis = new NumberAxis();   
        final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);   
        // defining a series  
        XYChart.Series<Number, Number> series = new XYChart.Series<>();  
        // populating the series with data  
        series.getData().add(new XYChart.Data<Number, Number>(1, 23));  
        series.getData().add(new XYChart.Data<Number, Number>(2, 14));  
        series.getData().add(new XYChart.Data<Number, Number>(3, 15));  
        series.getData().add(new XYChart.Data<Number, Number>(4, 24));  
        series.getData().add(new XYChart.Data<Number, Number>(5, 34));  
        series.getData().add(new XYChart.Data<Number, Number>(6, 36));  
        series.getData().add(new XYChart.Data<Number, Number>(7, 22));  
        series.getData().add(new XYChart.Data<Number, Number>(8, 45));  
        series.getData().add(new XYChart.Data<Number, Number>(9, 43));  
        series.getData().add(new XYChart.Data<Number, Number>(10, 17));  
        series.getData().add(new XYChart.Data<Number, Number>(11, 29));  
        series.getData().add(new XYChart.Data<Number, Number>(12, 25));  
        
        final Pane root = new Pane(); 
        SplitPane sp = new SplitPane();
        sp.setOrientation(Orientation.VERTICAL);
        sp.setDividerPosition(0, 0.7);
        
        final Pane pane1 = new Pane();
        lineChart.getData().add(series);
        lineChart.setPrefSize(800, 600);
        lineChart.setLegendVisible(false);
        pane1.getChildren().add(lineChart);
        pane1.setPrefSize(800, 600);
   
        Pane pane2 = new Pane();
      
        root.heightProperty().addListener(new ChangeListener<Number>() {
                        @Override
                        public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                            lineChart.setPrefSize(pane1.getPrefWidth(), pane1.getPrefHeight());
                        }
                    });
        
        sp.getItems().addAll(pane1, pane2);
        root.getChildren().add(sp);
        
        Scene scene = new Scene(root, 800, 600);   
        stage.setScene(scene);  
        stage.show();  

    }  
    public static void main(String[] args) {  
        launch(args);  
    }  
} 