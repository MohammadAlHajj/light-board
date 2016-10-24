package com.lightBoard.ui;

import com.lightBoard.controls.MasterControls;
import com.lightBoard.controls.patterns.DiagonalDownPattern;
import com.lightBoard.controls.patterns.DiagonalUpPattern;
import com.lightBoard.controls.patterns.HorizontalPattern;
import com.lightBoard.controls.patterns.InfinityPattern;
import com.lightBoard.controls.patterns.VerticalPatterm;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class Controller implements Initializable
{
    private MasterControls mControls;

    @FXML private Button infinityBtn;
    @FXML private Button horizontalBtn;
    @FXML private Button verticalBtn;
    @FXML private Button diagonalUpBtn;
    @FXML private Button diagonalDownBtn;

    @FXML private TextField tailLengthTxt;
    @FXML private TextField brushSizeTxt;
    @FXML private TextField speedTxt;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // FIXME: 10/12/2016 fix the text here
        assert infinityBtn != null : "fx:id=\"myButton\" was not injected: check your FXML file 'simple.fxml'.";

        mControls = MasterControls.INSTANCE;

        tailLengthTxt.setTextFormatter( new TextFormatter<String>(getTaleLengthFilter()));
        brushSizeTxt.setTextFormatter(  new TextFormatter<String>(getBrushSizeFilter()));
        speedTxt.setTextFormatter(      new TextFormatter<String>(getSpeedFilter()));

        tailLengthTxt.setText(mControls.getMaxBufferSize() + "");
        brushSizeTxt.setText( mControls.getBrushSize() + "");
        speedTxt.setText(     mControls.getRepeatDelay() + "");

    }

    private UnaryOperator<TextFormatter.Change> getTaleLengthFilter() {
        return (t) -> {
            if (!isInt(t.getControlNewText().trim()))
                return null;
            else {
                mControls.setMaxBufferSize(Integer.parseInt(t.getControlNewText()));
                return t;
            }
        };
    }

    private UnaryOperator<TextFormatter.Change> getBrushSizeFilter() {
        return (t) -> {

            if (!isDouble(t.getControlNewText().trim()))
                return null;
            else {
                mControls.setBrushSize(Float.parseFloat(t.getControlNewText()));
                return t;
            }
        };
    }

    private UnaryOperator<TextFormatter.Change> getSpeedFilter() {
        return (t) -> {
            if (!isInt(t.getControlNewText().trim()))
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
        }
    }
}
