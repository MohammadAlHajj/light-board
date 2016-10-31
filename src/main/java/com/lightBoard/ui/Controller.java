package com.lightBoard.ui;

import com.lightBoard.controls.MasterControls;
import com.lightBoard.controls.patterns.CircularPattern;
import com.lightBoard.controls.patterns.DiagonalDownPattern;
import com.lightBoard.controls.patterns.DiagonalUpPattern;
import com.lightBoard.controls.patterns.HorizontalPattern;
import com.lightBoard.controls.patterns.InfinityPattern;
import com.lightBoard.controls.patterns.VerticalPatterm;
import com.lightBoard.ui.labelFormatters.TailLengthLabelFormatter;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.text.Text;

public class Controller implements Initializable
{
    private MasterControls mControls;

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

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // FIXME: 10/12/2016 fix the text here
        assert infinityBtn != null : "fx:id=\"myButton\" was not injected: check your FXML file 'simple.fxml'.";

        mControls = MasterControls.INSTANCE;

//        tailLengthTxt.setTextFormatter( new TextFormatter<String>(getTaleLengthFilter()));
//        brushSizeTxt.setTextFormatter(  new TextFormatter<String>(getBrushSizeFilter()));
//        speedTxt.setTextFormatter(      new TextFormatter<String>(getSpeedFilter()));

        tailLengthTxt.setText(mControls.getMaxBufferSize() + "");
        brushSizeTxt.setText((int)mControls.getBrushSize() + "");
        // max is 10000
        speedTxt.setText((int)(speedSlider.getMax() +1 - mControls.getRepeatDelay()/100.0) + "");

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

        speedSlider.setValue(speedSlider.getMax() +1 - mControls.getRepeatDelay()/100.0 );
        speedSlider.setLabelFormatter(new TailLengthLabelFormatter(
            "Slow", "Fast", speedSlider.getMax()));
        speedSlider.valueProperty().addListener((ov, old_val, new_val) ->{
            int convertedSpeed = (int)(100 * (speedSlider.getMax() +1 - new_val.doubleValue()));
            mControls.setRepeatDelay(convertedSpeed);
            speedTxt.setText(Math.round(speedSlider.getValue()) +"");
        });

    }

    private UnaryOperator<TextFormatter.Change> getTaleLengthFilter() {
        return (t) -> {
            System.out.println(t);
            if (!isInt(t.getControlNewText().trim()) && t.isContentChange())
                return null;
            else {
                mControls.setMaxBufferSize(Integer.parseInt(t.getControlNewText()));
                return t;
            }
        };
    }

    private UnaryOperator<TextFormatter.Change> getBrushSizeFilter() {
        return (t) -> {
            System.out.println(t);
            if (!isDouble(t.getControlNewText().trim()) && t.isContentChange())
                return null;
            else {
                mControls.setBrushSize(Float.parseFloat(t.getControlNewText()));
                return t;
            }
        };
    }

    private UnaryOperator<TextFormatter.Change> getSpeedFilter() {
        return (t) -> {
            System.out.println(t);
            if (!isInt(t.getControlNewText().trim()) && t.isContentChange())
                return null;
            else {
                mControls.setRepeatDelay(Integer.parseInt(t.getControlNewText()));
                return t;
            }
        };
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

    public void playPause(ActionEvent event) {
        if (mControls.playPause())
            playPauseBtn.setText("Pause");
        else playPauseBtn.setText("Play");
    }
}
