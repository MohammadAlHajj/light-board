package com.lightBoard.controls;

import com.lightBoard.controls.patterns.CircularPattern;
import com.lightBoard.controls.patterns.DiagonalDownPattern;
import com.lightBoard.controls.patterns.DiagonalUpPattern;
import com.lightBoard.controls.patterns.HorizontalPattern;
import com.lightBoard.controls.patterns.InfinityPattern;
import com.lightBoard.controls.patterns.VerticalPatterm;
import com.lightBoard.view.Main;
import com.lightBoard.view.labelFormatters.TailLengthLabelFormatter;

import java.beans.Visibility;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class Controller implements Initializable
{
    private Main application;

    private MasterControls mControls;

    @FXML private GridPane rootGrid;

    @FXML private Button infinityBtn;
    @FXML private Button horizontalBtn;
    @FXML private Button verticalBtn;
    @FXML private Button diagonalUpBtn;
    @FXML private Button diagonalDownBtn;
    @FXML private Button CircleBtn;

    @FXML private Text tailLengthTxt;
    @FXML private Text brushSizeTxt;
    @FXML private Text speedTxt;

    @FXML private Slider speedSlider;
    @FXML private Slider brushSizeSlider;
    @FXML private Slider tailLengthSlider;

    @FXML private Button playPauseBtn;

    /**
     * fullscreen mode only
     */
    @FXML private AnchorPane controlsLayer;


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // FIXME: 10/12/2016 fix the text here
        assert infinityBtn != null : "fx:id=\"myButton\" was not injected: check your FXML file 'simple.fxml'.";

        mControls = MasterControls.INSTANCE;

//        tailLengthSlider.setMax(MasterControls.MAX_TAIL_LENGTH);
        tailLengthSlider.setValue(mControls.getMaxBufferSize());
        tailLengthSlider.setLabelFormatter(new TailLengthLabelFormatter(
            "Short", "Long", tailLengthSlider.getMax()));
        tailLengthSlider.valueProperty().addListener((ov, old_val, new_val)-> {
            mControls.setMaxBufferSize(new_val.intValue());
            tailLengthTxt.setText(new_val.intValue() + "");
        });

        brushSizeSlider.setValue(mControls.getBrushSize());
        brushSizeSlider.setLabelFormatter(new TailLengthLabelFormatter(
            "Thin", "Thick", brushSizeSlider.getMax()));
        brushSizeSlider.valueProperty().addListener((ov, old_val, new_val)-> {
            mControls.setBrushSize(new_val.intValue());
            brushSizeTxt.setText(new_val.intValue() + "");
        });

		int min = MasterControls.MIN_SPEED;
		int max = MasterControls.MAX_SPEED;
		int originalSliderValue = (int)(
			(max - mControls.getRepeatDelay()) * ((speedSlider.getMax()-1)/(max-min)) +1
						// invert direction of max
                				// scale to fit in slider
		);
	    speedSlider.setValue(originalSliderValue);
        speedSlider.setLabelFormatter(new TailLengthLabelFormatter(
            "Slow", "Fast", speedSlider.getMax()));
        speedSlider.valueProperty().addListener((ov, old_val, new_val) ->{
			double convertedSpeed = max - ((new_val.doubleValue()-1) / ((speedSlider.getMax()-1)/(max-min)));
			System.out.println(convertedSpeed);
			mControls.setRepeatDelay((int)convertedSpeed);
            speedTxt.setText(Math.round(speedSlider.getValue()) +"");
        });

		tailLengthTxt.setText(mControls.getMaxBufferSize() + "");
		brushSizeTxt.setText((int)mControls.getBrushSize() + "");
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

    public void togglePlayPause(ActionEvent event) {
        mControls.togglePlayPause();
        setupPlayPauseBtn();
    }

    public void setupPlayPauseBtn() {
        if (mControls.isPlaying()) {
            playPauseBtn.setText("Pause");
            playPauseBtn.setId("pauseBtn");
//            playPauseBtn.setStyle("-fx-background-color: linear-gradient(#58ba67, #2d552f);");
        }
        else {
            playPauseBtn.setText("Play");
            playPauseBtn.setId("playBtn");
//            playPauseBtn.setStyle("-fx-background-color: linear-gradient(#b45f5f, #572929);");
        }
    }

    public void setUpRoot() throws IOException
    {
        if (mControls.isExtendedMode()){
            mControls.setExtendedMode(false);
            application.setupStandardMode();
        }
        else {
            mControls.setExtendedMode(true);
            application.setupExtendedMode();
        }
        setupPlayPauseBtn();
    }

	private ScheduledExecutorService executer;
	private ScheduledFuture<?> scheduledFuture;
    public void setupMouseDetectionExtendedMode(Node root)
    {
		executer = Executors.newSingleThreadScheduledExecutor();
		root.setOnMouseMoved(event -> {
			controlsLayer.setVisible(true);

			if (scheduledFuture != null && !scheduledFuture.isCancelled() && !scheduledFuture.isCancelled())
				scheduledFuture.cancel(true);

			scheduledFuture = executer.schedule(() -> controlsLayer.setVisible(false), 3000, TimeUnit.MILLISECONDS);
        });
    }

    public void setApp(Main app){
        this.application = app;
    }
}
