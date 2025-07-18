package skill;

import entity.EntityRole;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import scene.GameControl;
import entity.Jet;

public class undeaftalways {

    public static void apply(double x, double y, GameControl gc) {
        double radius = 200;
        long now = System.currentTimeMillis();
        long undefeatabletime1 = now + 1000000000; // 無敵持續 5 秒

        EntityRole player = gc.Player;

        if (player != null) {
            double dx = player.getX() + player.getWidth() / 2 - x;
            double dy = player.getY() + player.getHeight() / 2 - y;

            if (dx * dx + dy * dy <= radius * radius) {
                player.undefeatable = true;
                player.undefeatableTimeLast = undefeatabletime1;
            }

            PauseTransition disable = new PauseTransition(Duration.millis(1000000000));
            disable.setOnFinished(e -> {
                player.undefeatable = false;
                System.out.println("[盾] 玩家無敵結束！");
            });
            disable.play();
        }
    }
}
