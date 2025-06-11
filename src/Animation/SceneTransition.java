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

public class SceneTransition extends Animation{ // 場景轉場動畫類，繼承Animation

    private static Image image = new Image("Image/Black.jpg"); // 預設黑色圖片

    Canvas Black = new Canvas(Director.WIDTH,Director.HEIGHT); // 黑色畫布遮罩

    public static void SceneTransition(Scene scene, Parent newRoot, double timeSeconds) { // 靜態方法：進行場景轉場，參數：場景、要切換的新root、動畫時間
        Stage stage = (Stage) scene.getWindow(); // 取得視窗

        // Create a temporary black canvas overlay
        Canvas black = new Canvas(Director.WIDTH, Director.HEIGHT); // 建立黑色遮罩畫布
        GraphicsContext gc = black.getGraphicsContext2D(); // 取得畫布繪圖上下文
        gc.setFill(Color.BLACK); // 設為黑色
        gc.fillRect(0, 0, Director.WIDTH, Director.HEIGHT); // 填滿整個畫布

        // Wrap old root in a StackPane so we can layer the canvas
        Parent oldRoot = scene.getRoot(); // 取得目前root
        StackPane layeredRoot = new StackPane(oldRoot, black); // 疊加黑色畫布在舊root之上
        scene.setRoot(layeredRoot); // 設定新root

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(timeSeconds / 2), black); // 進場淡入
        fadeIn.setFromValue(0); // 初始透明
        fadeIn.setToValue(1); // 淡入到完全黑

        // Fade out
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(timeSeconds / 2), black); // 離場淡出
        fadeOut.setFromValue(1); // 從黑色開始
        fadeOut.setToValue(0); // 變透明

        fadeIn.setOnFinished(e -> {
            // Switch to new content under the canvas
            scene.setRoot(new StackPane(newRoot, black)); // 把黑畫布疊在新root之上
            fadeOut.play(); // 執行淡出
        });

        // 当淡出动画完成后，移除黑色覆盖层，仅保留新场景内容
        fadeOut.setOnFinished(e -> {
            try {
                scene.setRoot(newRoot); // 完全切換到新root
            } catch (Exception ex) {
                // 捕獲異常但不做任何處理
            }
        });

        fadeIn.play(); // 開始淡入動畫
    }
}
