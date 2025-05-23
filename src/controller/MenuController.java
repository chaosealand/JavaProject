package controller;

import Director.Director;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MenuController {

    @FXML
    private Button PlayBtn;

    @FXML
    void Exit(ActionEvent event) {
        Director.getInstance().ExitGame();
    }

    @FXML
    void Play(ActionEvent event) {
        Director.getInstance().StartGame();
    }

}
