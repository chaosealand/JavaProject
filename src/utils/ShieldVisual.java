package utils;

import javafx.animation.PauseTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.scene.layout.Pane;
import entity.Jet;


public class ShieldVisual {

    public static void playEMP(Pane root, Jet jet, long durationMillis) {
        // 建立半透明護盾圈
        Circle shieldCircle = new Circle(jet.getCenterX(), jet.getCenterY(), 200); // 半徑 100 可自行調整
        shieldCircle.setFill(Color.BLUE);
        shieldCircle.setOpacity(0.4);
        root.getChildren().add(shieldCircle);

        Timeline tracker = new Timeline(new KeyFrame(Duration.millis(20), e -> {
            shieldCircle.setCenterX(jet.getCenterX());
            shieldCircle.setCenterY(jet.getCenterY());
        }));
        tracker.setCycleCount(Timeline.INDEFINITE);
        tracker.play();

        //無敵結束時移除護盾 + 停止追蹤
        PauseTransition removeDelay = new PauseTransition(Duration.millis(durationMillis));
        removeDelay.setOnFinished(e -> {
            tracker.stop();
            root.getChildren().remove(shieldCircle);
        });
        removeDelay.play();
    }
}
