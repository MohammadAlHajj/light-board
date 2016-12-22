package com.lightBoard.view;

import com.lightBoard.controls.Controller;
import com.lightBoard.controls.MasterControls;

import java.awt.Point;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

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
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * this is the main screen of the application. all the logic branches from here
 */
public class MainScreen extends Application implements Controller.IScreenModeSetup
{
    private MasterControls mControls;
    @FXML private Canvas canvas;
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

	    // setup dependencies
	    controller.setupPlayPauseBtn();
		controller.setupMouseDetectionExtendedMode(root);
		controller.setupFullScreenKeyBinding(root);

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

        scene = new Scene(root, 1600, 850);

        // bind the size of the canvas to the size of the scene - the height of the controls
        GridPane grid = (GridPane) root;
        canvas = controller.getCanvas();

        GridPane controlsGrid = controller.getControlsGrid();
	    DoubleBinding heightBinding =
            scene.heightProperty().subtract(
	            controlsGrid.heightProperty()
		            .add(grid.vgapProperty().doubleValue() * 2)
            );
        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(heightBinding);

        // set "canvas" as the used canvas in the master controls, and setup the play/pause button
        mControls.setCanvas(canvas);

        // setup dependencies
        controller.setupPlayPauseBtn();
        controller.setupMuteUnmuteBtn();
        controller.setupColorPickers();
	    controller.setupStandardKeyBinding(root);
	    controller.setupColorOverrideTooltip();
	    controller.setupSoundSweepTooltip();

        // restart the primary stage with the newly created scene and make it maximized
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMaximized(false);

        // guarantees that the cursor is shown when the screen mode is switched
        showCursor(true);
    }

    /**
     * starts the drawing process on the canvas. This process will not stop until the program
     * terminates
     */
    private void startAnimation() {
        mControls.startDrawing();
        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
	            ConcurrentLinkedDeque<Point> buffer = mControls.getBuffer();

                // clear the canvas using the background color
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.setFill(mControls.getBackgroundColor());
                gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                // set Points color
                double red   = mControls.getPatternColor().getRed();
                double green = mControls.getPatternColor().getGreen();
                double blue  = mControls.getPatternColor().getBlue();
                double alpha = 1;

                // get data from now to decrease loop overhead
                double brushSize = mControls.getBrushSize();
                double halfBrushSize = brushSize/2;
                int maxBufferSize = mControls.getMaxBufferSize();
	            int index = 0;
	            Iterator<Point> iterator = buffer.iterator();

                Point p;
                // point to draw the pattern header image on
                Point firstPoint = null;

                // draw desired points
                while (iterator.hasNext() && index <= maxBufferSize) {
                    alpha = ((maxBufferSize - index) * 1.0 / maxBufferSize);
                    gc.setFill(new Color(red, green, blue, alpha));
                    p = iterator.next();
                    if(index == 0)
                        firstPoint = p;
                    gc.fillOval(p.x - halfBrushSize, p.y - halfBrushSize, brushSize, brushSize);
                    index++;
                }
                Image i = mControls.getPatternImage();
                if (i!= null && firstPoint != null) {
                    gc.drawImage(
                        i,
                        firstPoint.x - mControls.getImageSize() / 2,
	                    firstPoint.y - mControls.getImageSize() / 2,
                        mControls.getImageSize(),
                        mControls.getImageSize());
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

    @Override
    public Window getStage(){
	    return primaryStage;
    }
}
