package Animation;

import Director.Director;
import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneTransition extends Animation{

    private static Image image = new Image("Image/Black.jpg");

    Canvas Black = new Canvas(Director.WIDTH,Director.HEIGHT);


    public static void SceneTransition(Scene scene, Parent newRoot, double timeSeconds) {
        Stage stage = (Stage) scene.getWindow();

        // Create a temporary black canvas overlay
        Canvas black = new Canvas(Director.WIDTH, Director.HEIGHT);
        GraphicsContext gc = black.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, Director.WIDTH, Director.HEIGHT);

        // Wrap old root in a StackPane so we can layer the canvas
        Parent oldRoot = scene.getRoot();
        StackPane layeredRoot = new StackPane(oldRoot, black);
        scene.setRoot(layeredRoot);

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(timeSeconds / 2), black);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Fade out
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(timeSeconds / 2), black);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeIn.setOnFinished(e -> {
            // Switch to new content under the canvas
            scene.setRoot(new StackPane(newRoot, black));
            fadeOut.play();
        });

        // 当淡出动画完成后，移除黑色覆盖层，仅保留新场景内容
        fadeOut.setOnFinished(e -> {
            try {
                scene.setRoot(newRoot);
            } catch (Exception ex) {

            }
        });

        fadeIn.play();
    }
}
