package com.morticia.compsim.IO.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}