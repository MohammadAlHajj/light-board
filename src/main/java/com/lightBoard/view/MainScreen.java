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
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * this is the main screen of the application. all the logic branches from here
 */
public class MainScreen extends Application implements Controller.IScreenModeSetup
{

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

    /**
     * starting point
     * @param primaryStage
     * @throws Exception
     */
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

    /**
     * recreates the scene in fullScreen mode
     * @throws IOException
     */
    @Override
    public void setupExtendedMode()throws IOException
    {
        // load the fxml and git the controller a reference of this class
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
            "/fxml/mainScreen_extended.fxml"));
        root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.setApp(this);

        scene = new Scene(root);

        // bind the size of the canvas to the size of the scene
        canvas = controller.getCanvas();
        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());

        // set "canvas" as the used canvas in the master controls, and setup the play/pause button
        // and the mouse move listener
        mControls.setCanvas(canvas);

        controller.setupPlayPauseBtn();
		controller.setupMouseDetectionExtendedMode(root);

        // restart the primary stage with the newly created scene and make it maximized
		primaryStage.setScene(scene);
        primaryStage.show();
		primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		primaryStage.setFullScreen(true);

        // guarantees that the cursor is shown when the screen mode is switched
        showCursor(true);
	}

    /**
     * recreates the scene in windowed mode
     * @throws IOException
     */
    @Override
    public void setupStandardMode() throws IOException
    {
        // load the fxml and git the controller a reference of this class
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/mainScreen.fxml"));
        root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.setApp(this);

        scene = new Scene(root, 1400, 850);

        // bind the size of the canvas to the size of the scene - the height of the controls
        GridPane grid = (GridPane) root;
        canvas = controller.getCanvas();
        controlsGrid = controller.getControlsGrid();
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

        // set "canvas" as the used canvas in the master controls, and setup the play/pause button
        mControls.setCanvas(canvas);

        controller.setupPlayPauseBtn();
        controller.setupColorPickers();

        // restart the primary stage with the newly created scene and make it maximized
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMaximized(false);

        // guarantees that the cursor is shown when the screen mode is switched
        showCursor(true);
    }

    /**
     * starts the drawing process on the canvas. This process will not stop until the program stops
     */
    private void startAnimation() {
        mControls.startDrawing();
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                LinkedList<Point> buffer = mControls.getBuffer();

                // clear the canvas using the background color
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.setFill(mControls.getBackgroundColor());
                gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                // set Points color
                double red = mControls.getPatternColor().getRed();
                double green = mControls.getPatternColor().getGreen();
                double blue = mControls.getPatternColor().getBlue();
                double alpha = 1;

                double brushSize = mControls.getBrushSize();
                double halfBrushSize = mControls.getBrushSize()/2;

                Point p;

                // draw desired points
                for (int index = 0; index < buffer.size(); index++)
                {
                    try {
                        alpha = ((buffer.size() - index) * 1.0 / buffer.size());
                        gc.setFill(new Color(red, green, blue, alpha));
                        p = buffer.get(index);
                        gc.fillOval(p.x - halfBrushSize, p.y - halfBrushSize, brushSize, brushSize);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };
        timer.start();
	}

    /**
     * shows or hides the cursor
     * @param state this is the desired state (true to show cursor)
     */
	@Override
	public void showCursor(boolean state){
		if (state)  scene.setCursor(Cursor.DEFAULT);
		else        scene.setCursor(Cursor.NONE);
    }
}
