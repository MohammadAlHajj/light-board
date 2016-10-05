package com.lightBoard.ui;

import com.lightBoard.controls.DrawingPanel;
import com.lightBoard.controls.MasterControls;
import com.lightBoard.controls.patterns.InfinityPattern;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.LinkedList;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

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
        grid.setAlignment(Pos.CENTER);

        initViews(grid);

        Scene scene = new Scene(grid, 1100, 850);
//        scene.getStylesheets().add(Main.class.getResource("main.css").toExternalForm());


        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initViews(GridPane grid) {
        Canvas canvas = new Canvas(1000, 800);
        grid.add(canvas, 0, 0);
        mControls.startDrawing(canvas);



        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                LinkedList<Point> buffer = mControls.getBuffer();

                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.setFill(mControls.getBackColor());
                gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                // set Points color
                double red = mControls.getPatternColor().getRed();
                double green = mControls.getPatternColor().getGreen();
                double blue = mControls.getPatternColor().getBlue();
                double alpha = 1;

                System.out.println(buffer.get(0));
                // draw desired points
                int index;
                for (index = 0; index < buffer.size(); index++)
                {
                    alpha = ((buffer.size() - index) * 1.0 / buffer.size());
                    gc.setFill(new Color(red, green, blue, alpha));
                    Point p = buffer.get(index);
                    gc.fillOval(p.x, p.y, 10, 10);
//            System.out.println("Point at " + p.toString());
                }
            }
        };
        timer.start();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
