package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import Director.Director;

import java.io.IOException;

public class GameOverController {

    @FXML
    private Button btnBackToMenu;

    @FXML
    public void initialize() {
        // 點擊按鈕 → 回到主選單場景
        btnBackToMenu.setOnAction(event -> {
            try {
                Director.getInstance().ToMenu();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
