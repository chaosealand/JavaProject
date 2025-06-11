package utils;

import javafx.animation.PauseTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import entity.Jet;
import scene.GameControl;


public class ShieldVisual {

    // 静态变量来存储护盾状态
    private static boolean shieldActive = false;
    private static long shieldEndTime = 0;
    private static int radius = 150;
    private static Jet protectedJet;
    private static GameControl gameControl;

    /**
     * 在Canvas上绘制护盾效果
     * @param gc GameControl实例，用于访问图形上下文
     * @param jet 需要保护的飞机
     * @param durationMillis 护盾持续时间（毫秒）
     */
    public static void playEMP(GameControl gc, Jet jet, long durationMillis) {
        // 记录护盾状态
        shieldActive = true;
        protectedJet = jet;
        gameControl = gc;
        shieldEndTime = System.currentTimeMillis() + durationMillis;

        // 设置玩家为无敵状态
        jet.undefeatable = true;
        jet.undefeatableTimeLast = durationMillis;

        // 定时器在护盾结束后关闭护盾
        PauseTransition removeDelay = new PauseTransition(Duration.millis(durationMillis));
        removeDelay.setOnFinished(e -> {
            shieldActive = false;
            System.out.println("Shield deactivated");
        });
        removeDelay.play();
    }

    /**
     * 渲染护盾效果
     * 这个方法应该在GameControl的RenderAll方法中被调用，以便每帧都能更新护盾位置
     */
    public static void renderShield() {
        if (!shieldActive || protectedJet == null || gameControl == null) {
            return;
        }

        // 如果护盾时间已到，则停止绘制
        if (System.currentTimeMillis() > shieldEndTime) {
            shieldActive = false;
            return;
        }

        // 在Canvas上绘制护盾
        gameControl.graphicsContext.setFill(Color.BLUE.deriveColor(0, 1, 1, 0.4));
        gameControl.graphicsContext.fillOval(
            protectedJet.getCenterX() - radius,
            protectedJet.getCenterY() - radius,
            radius * 2,
            radius * 2
        );

        // 画一个边框，使护盾更加明显
        gameControl.graphicsContext.setStroke(Color.CYAN);
        gameControl.graphicsContext.setLineWidth(2);
        gameControl.graphicsContext.strokeOval(
            protectedJet.getCenterX() - radius,
            protectedJet.getCenterY() - radius,
            radius * 2,
            radius * 2
        );
    }

    /**
     * 检查护盾是否激活
     */
    public static boolean isShieldActive() {
        return shieldActive;
    }
}