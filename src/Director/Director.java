package Director;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import scene.GameControl;
import scene.MenuLogic;
import utils.StatBoard;

import java.io.IOException;

public class Director {


    public static final double WIDTH = 1920, HEIGHT = 1080; //遊戲視窗的長寬

    private Stage stage ;
    private static final Director Instance = new Director();
    public GameControl GC ;

    public static Director getInstance() {
        return Instance ;
    }

    public void initalize (Stage stage) throws IOException {

        AnchorPane anchorpane= new AnchorPane();
        Scene scene = new Scene(anchorpane,WIDTH,HEIGHT);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.setScene(scene);
        this.stage = stage;

        ToMenu();
        stage.show();

    }

    public void ToMenu () throws IOException { //載入主菜單，還得再stage.show()才能切畫面
        MenuLogic.LoadMenu(stage);
        StatBoard.reset();
    }

    public void ExitGame () { //關閉遊戲
        stage.close();
    }

    public void StartGame () {
        GameControl gc1 = new GameControl();
        GC = gc1;
        gc1.initialize(stage);
        stage.show();
    }
    public void ToGameOver(Stage stage) {
        try {
            //FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOver.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOver.fxml"));
            Parent root = loader.load();

            // 使用 SceneTransition 類添加淡入淡出效果
            // 如果場景已經存在，則使用淡入淡出效果
            if (stage.getScene() != null) {
                Animation.SceneTransition.SceneTransition(stage.getScene(), root, 2.0); // 2秒鐘的轉換時間
            } else {
                // 如果沒有現有場景，則創建一個新場景並直接顯示
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void ToGameWin(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Victory.fxml"));
            Parent root = loader.load();

            // 可以設定勝利訊息，例如：
            // GameWinController controller = loader.getController();
            // controller.setWinMessage("你贏了！恭喜！");

            // 跟 ToGameOver 一樣可用 SceneTransition
            if (stage.getScene() != null) {
                Animation.SceneTransition.SceneTransition(stage.getScene(), root, 2.0);
            } else {
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
