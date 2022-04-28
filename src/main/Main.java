package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import java.util.concurrent.atomic.AtomicInteger;

import com.aquafx_project.AquaFx;
import com.guigarage.flatterfx.FlatterFX;

import static java.lang.Math.*;

import java.io.IOException;
public class Main extends Application {

    private AtomicInteger tick = new AtomicInteger(0);
    
    @Override
    public void start(Stage stage) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/graphics/FXMLScene.fxml"));
        Scene scene = new Scene(root,500,500);

        
        stage.setScene(scene);
        stage.setTitle("демонстрация затухающих колебаний");
        stage.setMinHeight(600);
        stage.setMinWidth(600);

        
        stage.show();
        //AquaFx.style();

        //FlatterFX.style();
    }

    public static void main(String[] args) {
        launch(args);
    }
}