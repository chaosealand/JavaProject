package controller;

import Director.Director;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import java.io.IOException;

public class GameWinController {

    @FXML
    private Text winMessage;

    @FXML
    private Button btnRestart;

    @FXML
    private Button btnMenu;

    // 設置勝利訊息
    public void setWinMessage(String message) {
        if (winMessage != null) {
            winMessage.setText(message);
        }
    }

    @FXML
    private void onRestartClicked() {
        Director.getInstance().StartGame();

    }

    @FXML
    private void onMenuClicked() {
        try {
            Director.getInstance().ToMenu();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
