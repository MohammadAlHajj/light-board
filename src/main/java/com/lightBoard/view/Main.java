package com.lightBoard.view;

import com.lightBoard.controls.Controller;
import com.lightBoard.controls.MasterControls;

import java.awt.Point;
import java.io.IOException;
import java.util.LinkedList;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    private MasterControls mControls;
    @FXML private Canvas canvas;
    @FXML private GridPane controlsGrid;
    private Controller controller;
    private Parent root;
    private Scene scene;
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        this.primaryStage = primaryStage;
        mControls = MasterControls.INSTANCE;

        primaryStage.setTitle("Light Board");

        setupStandardMode();

        startAnimation();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setupExtendedMode()throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main_extended.fxml"));
        root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.setApp(this);

        scene = new Scene(root, 1400, 850);

        canvas = (Canvas)root.lookup("#canvas");
        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());

        mControls.setCanvas(canvas);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setupStandardMode() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.setApp(this);

        scene = new Scene(root, 1400, 850);

        GridPane grid = (GridPane) root;
        canvas = (Canvas)root.lookup("#canvas");
        controlsGrid = (GridPane) root.lookup("#controlsGrid");
        DoubleBinding heightBinding =
            grid.heightProperty().subtract(
                controlsGrid.getHeight() +
                    controlsGrid.getPadding().getTop() +
                    controlsGrid.getPadding().getBottom() +
                    grid.getPadding().getTop() +
                    grid.getPadding().getBottom() + 200
            );
        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(heightBinding);

        mControls.setCanvas(canvas);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startAnimation() {
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

                double brushSize = mControls.getBrushSize();

                // draw desired points
                for (int index = 0; index < buffer.size(); index++)
                {
                    try {
                        alpha = ((buffer.size() - index) * 1.0 / buffer.size());
                        gc.setFill(new Color(red, green, blue, alpha));
                        Point p = buffer.get(index);
                        gc.fillOval(p.x, p.y, brushSize, brushSize);
                    } catch (Exception e){}
                }
            }
        };
        mControls.startDrawing();
        timer.start();
    }
}
