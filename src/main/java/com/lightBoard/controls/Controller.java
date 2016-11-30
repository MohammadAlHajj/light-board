package com.lightBoard.controls;

import com.lightBoard.controls.patterns.CircularPattern;
import com.lightBoard.controls.patterns.DiagonalDownPattern;
import com.lightBoard.controls.patterns.DiagonalUpPattern;
import com.lightBoard.controls.patterns.HorizontalPattern;
import com.lightBoard.controls.patterns.InfinityPattern;
import com.lightBoard.controls.patterns.VerticalPatterm;
import com.lightBoard.view.labelFormatters.TwoValueLabelFormatter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.xml.stream.EventFilter;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Binding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * the controller linked to the main screen of the application
 */
public class Controller implements Initializable
{
	public interface IScreenModeSetup{
		void setupStandardMode() throws IOException;
		void setupExtendedMode() throws IOException;
		void showCursor(boolean state);
		Window getStage();
	}
	/**
	 * application reference
	 */
    private IScreenModeSetup application;
	/**
	 * master Controls singleton reference
	 */
    private MasterControls mControls;

	/**
	 * pattern control buttons
	 */
    @FXML private Button infinityBtn;
    @FXML private Button horizontalBtn;
    @FXML private Button verticalBtn;
    @FXML private Button diagonalUpBtn;
    @FXML private Button diagonalDownBtn;
    @FXML private Button CircleBtn;

	/**
	 * pattern property control sliders and their display text
	 */
	@FXML private Slider speedSlider;
	@FXML private Slider tailThicknessSlider;
	@FXML private Slider tailLengthSlider;
    @FXML private Text tailLengthTxt;
    @FXML private Text tailThicknessTxt;
    @FXML private Text speedTxt;

	/**
	 * play/Pause Button
	 */
    @FXML private Button playPauseBtn;

	/**
	 * the canvas that the program draws the patterns on
	 */
	@FXML private Canvas canvas;

	/**
	 * Color controls
	 */
	@FXML private ColorPicker foregroundCP;
	@FXML private ColorPicker backgroundCP;
	@FXML private CheckBox colorOverrideCB;

	/**
	 * normal screen mode only
	 */
	@FXML private GridPane controlsGrid;

    /**
     * fullscreen mode only
     */
    @FXML private AnchorPane controlsLayer;
	@FXML private VBox bottomBox;
	@FXML private VBox leftBox;

	/**
	 * pattern image header controls
	 */
	@FXML private ImageView patternHeaderPreview;
	@FXML private HBox patternHeaderPreviewHBox;

	/**
	 * called right after init automatically by javafx
	 * @param location
	 * @param resources
	 */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // FIXME: 10/12/2016 fix the text here
        assert infinityBtn != null : "fx:id=\"myButton\" was not injected: check your FXML file 'simple.fxml'.";

        mControls = MasterControls.INSTANCE;

	    setupSlidersAndTextDisplay();

	    if (patternHeaderPreview != null)
	    {
		    patternHeaderPreview.imageProperty().bind(mControls.patternImageProperty());

		    ReadOnlyDoubleProperty minSideSizeProperty;
		    if (patternHeaderPreviewHBox.heightProperty().getValue() <
			    patternHeaderPreviewHBox.widthProperty().getValue())
		    {
			    minSideSizeProperty = patternHeaderPreviewHBox.heightProperty();
		    }
		    else minSideSizeProperty = patternHeaderPreviewHBox.widthProperty();

		    patternHeaderPreview.fitHeightProperty().bind(minSideSizeProperty);
		    patternHeaderPreview.fitWidthProperty().bind(minSideSizeProperty);
	    }
    }

	/**
	 * sets up the pattern property sliders and their text display according to the master controls
	 */
	private void setupSlidersAndTextDisplay()
	{
		// get tail length and set slider accordingly
		tailLengthSlider.setValue(mControls.getMaxBufferSize());
		tailLengthSlider.setLabelFormatter(new TwoValueLabelFormatter(
			"Short", "Long", tailLengthSlider.getMax()));
		tailLengthSlider.valueProperty().addListener((ov, old_val, new_val)-> {
			mControls.setMaxBufferSize(new_val.intValue());
			tailLengthTxt.setText(new_val.intValue() + "");
		});

		// get tail thickness and set slider accordingly
		 tailThicknessSlider.setValue(mControls.getBrushSize());
		 tailThicknessSlider.setLabelFormatter(new TwoValueLabelFormatter(
			"Thin", "Thick",  tailThicknessSlider.getMax()));
		 tailThicknessSlider.valueProperty().addListener((ov, old_val, new_val)-> {
			mControls.setBrushSize(new_val.intValue());
			tailThicknessTxt.setText(new_val.intValue() + "");
		});

		// get speed and set slider accordingly
		int min = Settings.getMinSpeedMicros();
		int max = Settings.getMaxSpeedMicros();
		int originalSliderValue = (int)(
			(max - mControls.getRepeatDelay()) * ((speedSlider.getMax()-1)/(max-min)) +1
			// invert direction of max
			// scale to fit in slider
		);
		speedSlider.setValue(originalSliderValue);
		speedSlider.setLabelFormatter(new TwoValueLabelFormatter(
			"Slow", "Fast", speedSlider.getMax()));
		speedSlider.valueProperty().addListener((ov, old_val, new_val) ->{
			double convertedSpeed =
				max - ((new_val.doubleValue()-1) / ((speedSlider.getMax()-1)/(max-min)));
			double b = 1/99.0 * (Math.log(Settings.getMinSpeedMicros()) - Math.log(Settings
				.getMaxSpeedMicros()));
			double a = 1000.0 / (Math.pow(Math.E, b));
			double convertedExpSpeed = a * Math.pow(Math.E, b * new_val.doubleValue());
			System.out.println(convertedExpSpeed);
			mControls.setRepeatDelay((int)convertedExpSpeed);
			speedTxt.setText(Math.round(speedSlider.getValue()) +"");
		});

		// update text displays
		tailLengthTxt.setText((int)tailLengthSlider.getValue( )+ "");
		tailThicknessTxt.setText((int) tailThicknessSlider.getValue() + "");
		speedTxt.setText((int) speedSlider.getValue() + "");
	}

	private boolean isDouble (String s){
        assert s != null;
        return s.matches("\\d+(.\\d*)?");
    }

    private boolean isInt (String s){
        assert s != null;
        return s.matches("\\d+");
    }

	/**
	 * switches to the pattern requested by the user
	 * @param event Event used to get the source of the call and pick the appropriate pattern
	 */
	@FXML
    public void changePattern(ActionEvent event)
    {
        if (event.getSource().equals(infinityBtn)) {
            mControls.setPattern(new InfinityPattern());
        }else if (event.getSource().equals(horizontalBtn)) {
            mControls.setPattern(new HorizontalPattern());
        }else if (event.getSource().equals(verticalBtn)) {
            mControls.setPattern(new VerticalPatterm());
        }else if (event.getSource().equals(diagonalUpBtn)) {
            mControls.setPattern(new DiagonalUpPattern());
        }else if (event.getSource().equals(diagonalDownBtn)) {
            mControls.setPattern(new DiagonalDownPattern());
        }else if (event.getSource().equals(CircleBtn)){
            mControls.setPattern(new CircularPattern());
        }
    }

	/**
	 * called when the play/pause button is pressed
	 * toggles the play status in the master control and asks for the appropriate changes
	 */
	@FXML
	public void togglePlayPause() {
        mControls.togglePlayPause();
        setupPlayPauseBtn();
    }

	/**
	 * checks the play state in master controls and changes the text and style of
	 * the button accordingly
	 */
	public void setupPlayPauseBtn() {
        if (mControls.isPlaying()) {
            playPauseBtn.setText("Pause");
            playPauseBtn.setId("pauseBtn");
        }
        else {
            playPauseBtn.setText("Play");
            playPauseBtn.setId("playBtn");
        }
    }

	/**
	 * called when the changeScreenMode button is pressed
	 * changes screen state in master controls and asks for changes accordingly
	 *
	 * @throws IOException
	 */
	@FXML
    public void toggleFullscreen() throws IOException
    {
        if (mControls.isExtendedMode()){
            mControls.setExtendedMode(false);
            application.setupStandardMode();
        }
        else {
            mControls.setExtendedMode(true);
            application.setupExtendedMode();
        }
        // clears the residue of the pattern from the canvas
        mControls.getBuffer().clear();
        // clears any possible scheduled fade of the mouse cursor
	    if (scheduledFuture != null && !scheduledFuture.isCancelled())
	        scheduledFuture.cancel(true);
    }

	public void toggleColorOverride() {
		mControls.setBypassColorCorrection(colorOverrideCB.isSelected());
		changeColor();
	}


	/**
	 * sets up a listener for the mouse movement
	 * once the listener is triggered, the controls layer will be shown with animation and
	 * sceduled to disappear after x milliseconds
	 * @param root the node that will listen to the mouse movement
	 */
	private ScheduledExecutorService executor;
	private ScheduledFuture<?> scheduledFuture;
	public void setupMouseDetectionExtendedMode(Node root)
    {
		executor = Executors.newSingleThreadScheduledExecutor();
		root.setOnMouseMoved(event -> {
			if (scheduledFuture != null && scheduledFuture.isDone())
				animateControlsFadeIn();

			if (scheduledFuture != null && !scheduledFuture.isCancelled() && !scheduledFuture.isDone())
				scheduledFuture.cancel(true);

			scheduledFuture = executor.schedule(this::animateControlsFadeOut,
				Settings.getFadeDelayMillis(), TimeUnit.MILLISECONDS);
        });
    }

	/**
	 * animates the fullscreen controls fading in
	 */
    public void animateControlsFadeIn()
    {
    	// show the cursor
	    application.showCursor(true);

	    // timeline and duration
	    Timeline fadeInTimeline = new Timeline();
	    Duration duration = new Duration(Settings.getFadeLengthMillis());

	    // setup the key frames of the timeline and bind the corresponding values
	    DoubleProperty angle = new SimpleDoubleProperty();
	    KeyValue fadeControlsKV = new KeyValue(controlsLayer.opacityProperty(), 1);
	    KeyValue rotateKV = new KeyValue(angle, -90);
	    KeyFrame animationKF = new KeyFrame(duration, fadeControlsKV, rotateKV);

	    // creates the rotation transform of the bottom box. the angle of rotation is linked to
	    // the previously created keyframes
	    // rotation is around the x axis
	    Rotate bottomBoxRotate = new Rotate();
	    bottomBoxRotate.angleProperty().bind(angle);
	    bottomBoxRotate.setAxis(new Point3D(1, 0, 0));
	    bottomBoxRotate.setPivotY(bottomBox.getLayoutX() + bottomBox.getHeight());
	    bottomBox.getTransforms().add(bottomBoxRotate);

	    // creates the rotation transform of the left box. the angle of rotation is linked to
	    // the previously created keyframes
	    // rotation is around the y axis
	    Rotate leftBoxRotate = new Rotate();
	    leftBoxRotate.angleProperty().bind(angle);
	    leftBoxRotate.setAxis(new Point3D(0, 1, 0));
	    leftBoxRotate.setPivotY(leftBox.getLayoutY());
	    leftBox.getTransforms().add(leftBoxRotate);

	    // start the animation
	    fadeInTimeline.getKeyFrames().addAll(animationKF);
	    fadeInTimeline.play();
    }

	/**
	 * animates the fullscreen controls fading out
	 */
	public void animateControlsFadeOut()
    {
    	// timeline and duration
	    Timeline fadeOutTimeline = new Timeline();
	    Duration duration = new Duration(Settings.getFadeLengthMillis());

	    // setup the key frames of the timeline and bind the corresponding values
	    DoubleProperty angle = new SimpleDoubleProperty();
	    KeyValue fadeControlsKV = new KeyValue(controlsLayer.opacityProperty(), 0);
	    KeyValue rotateKV = new KeyValue(angle, 90);
	    // also hide the cursor onFinish
	    KeyFrame animationKF = new KeyFrame(duration, (event) ->application.showCursor(false),
		    fadeControlsKV, rotateKV);

	    // creates the rotation transform of the bottom box. the angle of rotation is linked to
	    // the previously created keyframes
	    // rotation is around the x axis
	    Rotate bottomBoxRotate = new Rotate();
	    bottomBoxRotate.angleProperty().bind(angle);
	    bottomBoxRotate.setAxis(new Point3D(1, 0, 0));
	    bottomBoxRotate.setPivotY(bottomBox.getLayoutX() + bottomBox.getHeight());
	    bottomBox.getTransforms().add(bottomBoxRotate);

	    // creates the rotation transform of the left box. the angle of rotation is linked to
	    // the previously created keyframes
	    // rotation is around the y axis
	    Rotate leftBoxRotate = new Rotate();
	    leftBoxRotate.angleProperty().bind(angle);
	    leftBoxRotate.setAxis(new Point3D(0, 1, 0));
	    leftBoxRotate.setPivotY(leftBox.getLayoutY());
	    leftBox.getTransforms().add(leftBoxRotate);

	    // start the animation
	    fadeOutTimeline.getKeyFrames().addAll(animationKF);
	    fadeOutTimeline.play();
    }

	/**
	 * sets the color of the colorPickers to the colors in the Master Controls
	 */
	public void setupColorPickers() {
		foregroundCP.setValue(mControls.getPatternColor());
		backgroundCP.setValue(mControls.getBackgroundColor());
	}

	/**
	 * updates the colors in Master controls and asks for the appropriate changes
	 */
    public void changeColor() {
	    mControls.setPatternColor(foregroundCP.getValue());
	    mControls.setBackgroundColor(backgroundCP.getValue());
	    setupColorPickers();
    }

	/**
	 * picks a new image file for pattern header image. the file is accepted only if it has an
	 * acceptable extension
	 */
	public void selectPatternHeaderImage()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");

		// only the below extensions are allowed
		FileChooser.ExtensionFilter filter =
			new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif", "*.bmp");
		fileChooser.getExtensionFilters().add(filter);

		File imageFile = fileChooser.showOpenDialog(application.getStage());

		// if the file has an accepted extension, make it the new pattern header image
		if (imageFile != null && fileMatchesFilter(imageFile, filter)) {
			Image image = new Image(imageFile.toURI().toString());
			mControls.setPatternImage(image);
			patternHeaderPreview.setImage(image);
		}
	}

	/**
	 * checks if the file ends with one of the extensions the filter has
	 *
	 * @param imageFile the newly selected file
	 * @param filter holder of the acceptable extensions
	 * @return true if the file ends with one of the extensions the filter has
	 */
	private boolean fileMatchesFilter(File imageFile, FileChooser.ExtensionFilter filter) {
		for (String s : filter.getExtensions())
			if (imageFile.getName().endsWith(s.substring(1)))
				return true;
		return false;

	}


	/**
	 * removes current Pattern header image
 	 */
	public void clearPatternHeaderImage() {
		mControls.setPatternImage(null);
		patternHeaderPreview.setImage(null);
	}

	/**
	 * key bindings specific for Fullscreen Mode
	 * @param root root node
	 */
	public void setupFullScreenKeyBinding(Node root){
		root.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode().equals(KeyCode.ESCAPE) ||
				(event.getCode().equals(KeyCode.ENTER) && event.isAltDown()))
			{
				try { toggleFullscreen(); }
				catch (IOException e){ e.printStackTrace(); }
			}
			else if (event.getCode().equals(KeyCode.SPACE))
				togglePlayPause();
		});
	}

	/**
	 * key bindings specific for normal screen Mode
	 * @param root root node
	 */
	public void setupStandardKeyBinding(Node root) {
		root.addEventFilter(KeyEvent.KEY_PRESSED,event -> {
			System.out.println(event);
			if ((event.getCode().equals(KeyCode.ENTER) && event.isAltDown()))
			{
				try { toggleFullscreen(); }
				catch (IOException e){ e.printStackTrace(); }
			}
			else if (event.getCode().equals(KeyCode.SPACE))
				togglePlayPause();
		});
	}


	/**
	 * gives this controller a copy of the app it is linked to
	 * @param app the controlling app
	 */
	public void setApp(IScreenModeSetup app){
        this.application = app;
    }

	public Canvas getCanvas() {return canvas;}
	public GridPane getControlsGrid() {return controlsGrid;}
	public CheckBox getColorOverrideCB() {return colorOverrideCB;}
}
