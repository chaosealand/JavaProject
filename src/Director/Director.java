package Director;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import scene.GameControl;
import scene.MenuLogic;

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOver.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
