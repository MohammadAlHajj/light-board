package com.lightBoard.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class Controller {

    @FXML private Button infinityBtn;

    @FXML
    public void changePattern(ActionEvent event)
    {
        if (event.getSource().equals(infinityBtn)) {

        }
    }
}
