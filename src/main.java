import Director.Director;
import javafx.application.Application;
import javafx.stage.Stage;

public class main extends Application {
    public static void main (String[] args) {
        Application.launch(main.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        Director.getInstance().initalize(stage);
    }
}
