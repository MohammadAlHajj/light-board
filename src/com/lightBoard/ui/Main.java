package com.lightBoard.ui;

import com.lightBoard.controls.DrawingPanel;
import com.lightBoard.controls.MasterControls;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

    private DrawingPanel drawingPanel;
    private MasterControls mControls;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        mControls = new MasterControls();

        primaryStage.setTitle("Hello World");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        initViews(grid);

        Scene scene = new Scene(grid, 500, 400);
        scene.getStylesheets().add(Main.class.getResource("main.css").toExternalForm());


        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initViews(GridPane grid) {
        Canvas canvas = new Canvas(300, 250);
        grid.add(canvas, 0, 0);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
