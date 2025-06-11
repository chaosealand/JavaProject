package skill;

import entity.EntityRole;
import entity.Jet;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import scene.GameControl;
import utils.Team;

public class bladeskill {

    private static final Image[] bladeFrames = new Image[]{
            new Image("/Image/Blade/blade1.png"),
            new Image("/Image/Blade/blade2.png"),
            new Image("/Image/Blade/blade3.png"),
            new Image("/Image/Blade/blade4.png"),
            new Image("/Image/Blade/blade5.png")
    };

    public static void activateBlade(Jet player, GameControl gc) {
        double bladeX = player.getX() + player.getWidth() - 20;  // 從右邊發出
        double bladeY = player.getY() + player.getHeight() / 2 - 50;

        ImageView bladeView = new ImageView(bladeFrames[0]);
        bladeView.setFitWidth(300);
        bladeView.setFitHeight(300);
        bladeView.setLayoutX(bladeX);
        bladeView.setLayoutY(bladeY);

        bladeView.setRotate(180);

        gc.root.getChildren().add(bladeView);

        // ✅ 播放動畫（1~5），停在第 5 張
        Timeline animation = new Timeline();
        for (int i = 0; i < bladeFrames.length; i++) {
            int frame = i;
            animation.getKeyFrames().add(
                    new KeyFrame(Duration.millis(i * 60), e -> {
                        bladeView.setImage(bladeFrames[frame]);
                    })
            );
        }
        animation.setCycleCount(1);
        animation.play();

        // ✅ 播完動畫後刀光持續存在，直到飛出畫面才消失
        final Timeline moveTimeline = new Timeline();
        KeyFrame moveFrame = new KeyFrame(Duration.millis(20), e -> {
            // 持續向右移動
            bladeView.setLayoutX(bladeView.getLayoutX() + 12);

            double bx = bladeView.getLayoutX();
            double by = bladeView.getLayoutY();
            double bw = bladeView.getFitWidth();
            double bh = bladeView.getFitHeight();

            // ✅ 判斷是否擊中敵人（Team.ENEMY）
            for (EntityRole entity : gc.entities) {
                if (entity.getTeam() == Team.enemy && entity.isAlive() &&
                        entity.getX() < bx + bw &&
                        entity.getX() + entity.getWidth() > bx &&
                        entity.getY() < by + bh &&
                        entity.getY() + entity.getHeight() > by) {
                    entity.setAlive(false); // 直接死亡
                }
            }

            // ✅ 撞到邊界刪除
            if (bx > gc.root.getWidth()) {
                gc.root.getChildren().remove(bladeView);
                moveTimeline.stop(); // ✅ 安全終止
            }
        });

        moveTimeline.getKeyFrames().add(moveFrame);
        moveTimeline.setCycleCount(Timeline.INDEFINITE);
        moveTimeline.play();
    }
}
