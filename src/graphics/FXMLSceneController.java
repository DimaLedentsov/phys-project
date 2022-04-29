package graphics;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import exceptions.InvalidNumberException;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import logic.AnimTask;
import logic.DeviationFunction;
import logic.PendulumFunction;
import logic.Plottable;
import logic.Simulator;
import logic.SpringFunction;

import static java.lang.Math.*;

public class FXMLSceneController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    public LineChart<Number, Number> chart;

    NumberAxis xAxis;
    NumberAxis yAxis;


    @FXML TextField resistance;

    @FXML TextField angle;

    @FXML TextField length;

    @FXML TextField mass;

    @FXML TextField startX;

    @FXML TextField rigity;


    @FXML Button begin;

    AnimTask animTask;

    @FXML MenuItem about;

    @FXML Canvas canvas;

    @FXML AnchorPane pane;

    @FXML VBox vbox;

    @FXML TabPane tabPane;

    @FXML Button update;
    @FXML Button add;
    @FXML Button remove;
    
    @FXML Button stopContinue;
    @FXML SplitPane splitPane;
    @FXML CheckBox isSpringBox;
    boolean running;

    @FXML List<XYChart.Series<Number, Number>> specialSeries;

    Set<Integer> uniqueNumbers;

    private Desktop desktop = Desktop.getDesktop();
    List<Simulator> simulators;

    volatile double dragStartX;
    volatile double dragStartY;
    volatile double scrollStartX;
    volatile double scrollStartY;
    @FXML
    void initialize() {
        
        assert chart != null : "fx:id=\"chart\" was not injected: check your FXML file 'FXMLScene.fxml'.";
        
        simulators = new LinkedList<>();
        initChart();
        uniqueNumbers = new HashSet<>();
        running = true;
        //initBeginButton();
        //initAboutOption();
        addTab();
        
        //initTabPane();
        //initCanvas();

    }
    void initTabPane(){

        Tab newTab = new Tab();

        VBox vbox = new VBox();
        newTab.setContent(vbox);
        vbox.getChildren().add(new Button("aaa"));
        newTab.setText("график 3");
        tabPane.getTabs().add(newTab);
    }

    @FXML void addTab(){
        Tab newTab = new Tab();

        Label resistanceText = new Label("сопротивление среды (Н*с/м):");
        TextField resistance = new TextField("1");
        resistance.setId("resistance");

        Label angleText = new Label("начальный угол (град):");
        TextField angle = new TextField("90");
        angle.setId("angle");

        Label lengthText = new Label("длина (м):");
        TextField length = new TextField("10");
        length.setId("length");

        Label massText = new Label("масса (кг):");
        TextField mass = new TextField("5");
        mass.setId("mass");


        CheckBox isSpringBox = new CheckBox("пружинный маятник");
        isSpringBox.setId("checkbox");
        


        VBox vbox = new VBox(resistanceText, resistance, angleText, angle, lengthText, length, massText, mass, isSpringBox);
        newTab.setContent(vbox);


        newTab.setText("график " + Integer.toString(tabPane.getTabs().size()+1));
/*
        newTab.setOnSelectionChanged((e)->{
            if(newTab.isSelected()){
                getDataFromTab(newTab);
            }
        });*/

        isSpringBox.setOnAction((e)->{
            if(isSpringBox.isSelected()){
                vbox.getChildren().clear();
            
                
                Label rigidityText = new Label("коэффициент жесткости (Н/м):");
                TextField rigity = new TextField("1");
                rigity.setId("rigity"); 
                
                Label startXText = new Label("начальная координата (м):");
                TextField startX = new TextField("1");
                startX.setId("startX"); 
                
                vbox.getChildren().addAll(resistanceText, resistance, rigidityText, rigity, startXText, startX, massText, mass, isSpringBox);
                newTab.setContent(vbox);
            } else{
                vbox.getChildren().clear();
                vbox.getChildren().addAll(resistanceText, resistance, angleText, angle, lengthText, length, massText, mass, isSpringBox);
            }
        });
        tabPane.getTabs().add(newTab);
    }


    @FXML void removeTab(){
        int idx = tabPane.getSelectionModel().getSelectedIndex();
        tabPane.getTabs().remove(idx);
        List<Tab> tabs = tabPane.getTabs();
        for(int i =0; i< tabs.size();i++){
            tabs.get(i).setText("график " + Integer.toString(i+1));
        }
        
    }

    @FXML void updateTab(){

    }
    void updateSpecialSeries(){
        specialSeries.get(0).getData().get(0).setYValue(yAxis.getLowerBound());
        specialSeries.get(0).getData().get(1).setYValue(yAxis.getUpperBound());
        specialSeries.get(1).getData().get(0).setYValue(xAxis.getLowerBound());
        specialSeries.get(1).getData().get(1).setYValue(xAxis.getUpperBound());
    }
    public void initChart(){
        specialSeries = new LinkedList<XYChart.Series<Number,Number>>();
        xAxis = (NumberAxis)chart.getXAxis();
        yAxis = (NumberAxis)chart.getYAxis();
        xAxis.setAnimated(false);
        xAxis.setLabel("t, с");

        yAxis.setAnimated(false);
        yAxis.setLabel("x, м");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Values");

        //chart = new LineChart<>(xAxis, yAxis);
        chart.setAnimated(true);
        chart.getData().add(series);
        chart.setCreateSymbols(false);
        chart.setLegendVisible(true);
    
      
        final double lowerX = 0;
        final double upperX = 1000;
        //xAxis.setAutoRanging(true);

    
        AtomicBoolean isDragging = new AtomicBoolean(false);
        AtomicBoolean isScrolling = new AtomicBoolean(false);
        chart.setOnDragDetected((e)->{
            
            //e.consume();
            Point2D mouseSceneCoords = new Point2D(e.getSceneX(), e.getSceneY());
            double x = xAxis.sceneToLocal(mouseSceneCoords).getX();
            double y = yAxis.sceneToLocal(mouseSceneCoords).getY();
            dragStartX = xAxis.getValueForDisplay(x).doubleValue();
            dragStartY = yAxis.getValueForDisplay(y).doubleValue();
            chart.getData().removeAll(specialSeries);
            specialSeries.clear();
            isDragging.set(true);
            xAxis.setTickLabelsVisible(false);
            yAxis.setTickLabelsVisible(false);
            chart.setVerticalGridLinesVisible(false);
            chart.setHorizontalGridLinesVisible(false);
        
            
        });
        chart.setOnMouseDragged((e)->{
            //e.consume();
            if(isDragging.get() && !running){
                final double minX = xAxis.getLowerBound();
                final double maxX = xAxis.getUpperBound();
                final double minY = yAxis.getLowerBound();
                final double maxY = yAxis.getUpperBound();
                Point2D mouseSceneCoords = new Point2D(e.getSceneX(), e.getSceneY());
                double x = xAxis.sceneToLocal(mouseSceneCoords).getX();
                double y = yAxis.sceneToLocal(mouseSceneCoords).getY();
                
                x = xAxis.getValueForDisplay(x).doubleValue();
                y = yAxis.getValueForDisplay(y).doubleValue();
                double dx = (dragStartX-x)/5;
                double dy = (dragStartY-y)/5;
              
                //System.out.println(dx);
                //dx = xAxis.getValueForDisplay(dx).doubleValue();
                //dy = xAxis.getValueForDisplay(dy).doubleValue();
       
                //if(Math.abs(dy)>1) dy/=10;
                //if(Math.abs(dx)>1) dx/=10;
                yAxis.setLowerBound(minY+dy);
                yAxis.setUpperBound(maxY+dy);
                
                xAxis.setLowerBound(minX+dx);
                xAxis.setUpperBound(maxX+dx);
                
            }
        });


        
        chart.setOnMouseReleased(e-> {
            isDragging.set(false);
            xAxis.setTickLabelsVisible(true);
            yAxis.setTickLabelsVisible(true);
            chart.setVerticalGridLinesVisible(true);
            chart.setHorizontalGridLinesVisible(true);
         
        });
        chart.setOnMousePressed((e->{
            if(running) return;
            
            Point2D mouseSceneCoords = new Point2D(e.getSceneX(), e.getSceneY());
            double x = xAxis.sceneToLocal(mouseSceneCoords).getX();
            double y = yAxis.sceneToLocal(mouseSceneCoords).getY();
           
            x = xAxis.getValueForDisplay(x).doubleValue();
            y = yAxis.getValueForDisplay(y).doubleValue();
      
            chart.getData().removeAll(specialSeries);
            specialSeries.clear();
            specialSeries.add(new Series<>());
            specialSeries.add(new Series<>());
            specialSeries.get(0).getData().addAll(new Data<>(x,yAxis.getLowerBound()), new Data<>(x,yAxis.getUpperBound()));
            specialSeries.get(1).getData().addAll(new Data<>(xAxis.getLowerBound(),y), new Data<>(xAxis.getUpperBound(),y));
            specialSeries.get(0).setName("x = " + String.format("%.3f", new Double(x)));
            specialSeries.get(1).setName("y = " + String.format("%.3f", new Double(y)));

            chart.getData().addAll(specialSeries);
            
        }));
        

        /*chart.setOnScrollStarted((event)->{
            Point2D mouseSceneCoords = new Point2D(event.getSceneX(), event.getSceneY());
            double x = xAxis.sceneToLocal(mouseSceneCoords).getX();
            double y = yAxis.sceneToLocal(mouseSceneCoords).getY();
           
            scrollStartX= xAxis.getValueForDisplay(x).doubleValue();
            scrollStartY = yAxis.getValueForDisplay(y).doubleValue();
            final double minX = xAxis.getLowerBound();
            final double maxX = xAxis.getUpperBound();
            final double minY = yAxis.getLowerBound();
            final double maxY = yAxis.getUpperBound();
            final double thresholdX = minX + (maxX - minX) / 2d;
            final double thresholdY = minY + (maxY - minY) / 2d;
            xAxis.setLowerBound(minX+(scrollStartX-thresholdX));
            xAxis.setUpperBound(maxX+(scrollStartX-thresholdX));
            yAxis.setLowerBound(minY+(scrollStartY-thresholdY));
            yAxis.setUpperBound(maxY+(scrollStartY-thresholdY));
            System.out.println("aa");
        });*/

        chart.setOnMouseMoved((e->{
            if(running) return;
            
            if(!isScrolling.get()){
                Point2D mouseSceneCoords = new Point2D(e.getSceneX(), e.getSceneY());
                double x = xAxis.sceneToLocal(mouseSceneCoords).getX();
                double y = yAxis.sceneToLocal(mouseSceneCoords).getY();
                
                x = xAxis.getValueForDisplay(x).doubleValue();
                y = yAxis.getValueForDisplay(y).doubleValue();
                scrollStartX = x;
                scrollStartY = y;
            }

        }));

      
        chart.setOnScroll(new EventHandler<ScrollEvent>() {

            @Override
            public void handle(ScrollEvent event) {
                //event.consume();
                isScrolling.set(true);
                chart.getData().removeAll(specialSeries);
                specialSeries.clear();
                double minX = xAxis.getLowerBound();
                double maxX = xAxis.getUpperBound();
                double minY = yAxis.getLowerBound();
                double maxY = yAxis.getUpperBound();

                final double thresholdX = minX + (maxX - minX) / 2d;
                final double thresholdY = minY + (maxY - minY) / 2d;
                //final double x = event.getX();
                //final double y = event.getY();
                //Point2D mouseSceneCoords = new Point2D(event.getSceneX(), event.getSceneY());
                //double x = xAxis.sceneToLocal(mouseSceneCoords).getX();
                //double y = yAxis.sceneToLocal(mouseSceneCoords).getY();
               
                final double valueX = scrollStartX;//xAxis.getValueForDisplay(x).doubleValue();
                final double valueY = scrollStartY;//yAxis.getValueForDisplay(y).doubleValue();
                final double direction = event.getDeltaY();

                final double dx = -(thresholdX-valueX)/20;
                final double dy = -(thresholdY-valueY)/20;
                final double zx = (maxX-minX)/50;
                final double zy = (maxY-minY)/50;

                //xAxis.setLowerBound(minX+(scrollStartX-thresholdX));
                //xAxis.setUpperBound(maxX+(scrollStartX-thresholdX));
                //yAxis.setLowerBound(minY+(scrollStartY-thresholdY));
                //yAxis.setUpperBound(maxY+(scrollStartY-thresholdY));
                //minX = xAxis.getLowerBound();
                //maxX = xAxis.getUpperBound();
                //minY = yAxis.getLowerBound();
                //maxY = yAxis.getUpperBound();

                if (direction > 0) {
                    if(maxX-minX <= 1){
                        xAxis.setTickUnit(0.01);
                    }
                    else if(maxX-minX <= 30){
                        xAxis.setTickUnit(0.5);
                    }
                    if(maxY-minY <= 1){
                        yAxis.setTickUnit(0.01);
                    } else if(maxY-minY <= 30){
                        yAxis.setTickUnit(0.5);
                    }
                    
                    
                    if (maxX - minX >= 0.5) {
                        
                        double newMinX = minX+dx+zx;
                        double newMaxX = maxX+dx-zx;
                        newMinX = Math.round(newMinX*100.0)/100.0;
                        newMaxX = Math.round(newMaxX*100.0)/100.0;
                        //if (valueX > thresholdX) {
                            xAxis.setLowerBound(newMinX);
                        //} else {
                            xAxis.setUpperBound(newMaxX);
                        //}
                    }
                    if (maxY - minY >= 0.5) {
                        double newMinY = minY+dy+zy;
                        double newMaxY = maxY+dy-zy;
                        newMinY = Math.round(newMinY*100.0)/100.0;
                        newMaxY = Math.round(newMaxY*100.0)/100.0;
                        //if (valueY > thresholdY) {
                            yAxis.setLowerBound(newMinY);
                        //} else {
                            yAxis.setUpperBound(newMaxY);
                        //}
                    }
                    
                } else {
                    if(maxX-minX >30){
                        xAxis.setTickUnit(10);
                    }
                    else if(maxX-minX > 1){
                        xAxis.setTickUnit(0.5);
                    }
                    if(maxY-minY >30){
                        yAxis.setTickUnit(10);
                    }
                    else if(maxY-minY >1){
                        yAxis.setTickUnit(0.5);
                    }
                   
                    double newMinX = minX+dx-zx;
                    double newMaxX = maxX+dx+zx;
                    newMinX = Math.round(newMinX*100.0)/100.0;
                    newMaxX = Math.round(newMaxX*100.0)/100.0;
                    //if (valueX < thresholdX) {
                        
                        xAxis.setLowerBound(newMinX);
                    //} else {
                        xAxis.setUpperBound(newMaxX);
                    //}    


                    double newMinY = minY+dy-zy;
                    double newMaxY = maxY+dy+zy;
                    newMinY = Math.round(newMinY*100.0)/100.0;
                    newMaxY = Math.round(newMaxY*100.0)/100.0;
                    //if (valueY < thresholdY) {
                        //double nextBound = newMinY;
                        yAxis.setLowerBound(newMinY);
                    //} else {
                        //double nextBound = newMaxY;//Math.min(upperX, newMaxX);
                        yAxis.setUpperBound(newMaxY);
                    //} 
                    isScrolling.set(false);
                }
            }
        });
    }

   

    void getDataFromTab(Tab tab){
        VBox content  = (VBox)tab.getContent();
        isSpringBox = (CheckBox)content.lookup("#checkbox");
        resistance = (TextField)content.lookup("#resistance");
        mass = (TextField)content.lookup("#mass");
        if(!isSpringBox.isSelected()){
            angle = (TextField)content.lookup("#angle");
            length = (TextField)content.lookup("#length");
        } else{
            rigity = (TextField)content.lookup("#rigity");
            startX = (TextField)content.lookup("#startX");
        }
        
    }

    public void stopOrContinue(){
        if(running){
            stop();           
        }else{
            start();
        }
    }
    void stop(){
        if(animTask!=null && animTask.isRunning()) animTask.kill();
        running = false;
        xAxis.setAutoRanging(false);
        xAxis.setTickUnit(0.5);
        yAxis.setAutoRanging(false);
        yAxis.setTickUnit(0.5);
        stopContinue.setText("продолжить");
        chart.setAnimated(false);
    }
    void start(){
       
        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);
        animTask = new AnimTask(simulators, chart);
        animTask.setDaemon(true);
        animTask.start();
        running = true;
        stopContinue.setText("стоп");
        chart.setAnimated(false);
    }
    void getAllData(){
        
        for(Tab tab: tabPane.getTabs()){
            getDataFromTab(tab);
            if(isSpringBox.isSelected()){
                double k,x_0,m,b,dt;
                try{
                    dt = 0.01;
                    b = Double.parseDouble(resistance.getText());
                    m = Double.parseDouble(mass.getText());
                    k = Double.parseDouble(rigity.getText());
                    x_0 = Double.parseDouble(startX.getText());

                    if(b>3 || b<0) throw new InvalidNumberException("сопротивление среды должно быть от 0 до 3");
                    
                    if(x_0<=-20 || x_0>20) throw new InvalidNumberException("начальная координата должна быть от -20 до 20");
                    if(m<=0 || m>20) throw new InvalidNumberException("масса должа быть от 0 до 20");
                    if(k<=0 || k>100) throw new InvalidNumberException("коэффициент жесткости должен быть от 0 до 10");
                    Plottable x = new SpringFunction(b, x_0, k, m, dt);
                    Simulator sim = new Simulator(x);
                    simulators.add(sim);
                    XYChart.Series<Number, Number> series = new XYChart.Series<>();
                    //tab.setStyle(series.getData().get(0).getNode().getStyle());
                    series.setName(tab.getText());
                    chart.getData().add(series);
                
                }catch (InvalidNumberException e){
                    error(e.getMessage());
                } catch (NumberFormatException e){
                    error("неправильное число");
                }
            } else {
                double b,alpha_0,l,m;
                try{
                    b = Double.parseDouble(resistance.getText());
                    alpha_0 = Double.parseDouble(angle.getText());
                    l = Double.parseDouble(length.getText());
                    m = Double.parseDouble(mass.getText());
                    if(b>3 || b<0) throw new InvalidNumberException("сопротивление среды должно быть от 0 до 3");
                    if(alpha_0>90 || alpha_0<-90) throw new InvalidNumberException("начальный угол должен быть от -90 до 90 градусов");
                    if(l<=0 || l>20) throw new InvalidNumberException("длина должна быть от 0 до 20");
                    if(m<=0 || m>20) throw new InvalidNumberException("масса должа быть от 0 до 20");
                    Plottable x = new DeviationFunction(b, alpha_0, l, m,0.01);
                    Simulator sim = new Simulator(x);
                    simulators.add(sim);
                    XYChart.Series<Number, Number> series = new XYChart.Series<>();
                    series.setName(tab.getText());
                    chart.getData().add(series);
                }catch (InvalidNumberException e){
                    error(e.getMessage());
                } catch (NumberFormatException e){
                    error("неправильное число");
                }
            }
        }
    }
    @FXML void begin(){
        if(animTask!=null && animTask.isRunning()) animTask.kill();
        running = true;
        simulators.clear();
        chart.getData().clear();
        getAllData();
        if(simulators.isEmpty()) error("нет данных для построения графиков");
        else start();
    }
   

    @FXML void about(){
        info(
                "Над проектом работали студенты P3108:\n\n" + 
                "Леденцов Дмитрий\n" + 
                "Аталян Александр\n" + 
                "Вестфаль Мишель"
        );
    }

    @FXML void saveAs(){
        Stage stage = (Stage)splitPane.getScene().getWindow();
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png files (*.png)", "*.png"));

        File file = fileChooser.showSaveDialog(null);
        WritableImage image = chart.snapshot(new SnapshotParameters(), null);
        try {
            file.renameTo(new File(file.getName()+".png"));
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException|IllegalArgumentException e) {
            error("невозможно сохранить");
        }
    }

    public void error(String s){
        Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("ОШИБКА");
		//alert.setHeaderText(s);
		alert.setContentText(s);
        alert.setHeaderText(null);
		alert.showAndWait();
    }

    public void info(String s){
        Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("ИНФОРМАЦИЯ");
		//alert.setHeaderText(s);
		alert.setContentText(s);
        alert.setHeaderText(null);
		alert.showAndWait();
    }
    

}
