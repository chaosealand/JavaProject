package scene;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuLogic {


    public static void LoadMenu (Stage stage) throws IOException {
        //FXMLLoader loader = new FXMLLoader(MenuLogic.class.getResource("/FXML/Menu.fxml"));
        Parent root = FXMLLoader.load(MenuLogic.class.getResource("/FXML/Menu.fxml"));
        stage.getScene().setRoot(root);
    }

}
