package entity;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.util.Duration;
import scene.GameControl;
import utils.Team;

public class SpikeBullet extends Bullet {
    private double countdown; // 倒计时，单位为秒
    private boolean dissolved = false; // 标记是否已经分裂
    private static Image spikeBulletImage = new Image("/Image/bullet.png"); // 使用现有子弹图像，可以替换为专用图像

    public SpikeBullet(double x, double y, double Angle, double countdown, GameControl GC, Team team) {
        super(x, y, Angle, GC, team);
        this.countdown = countdown;
        this.image = spikeBulletImage; // 设置为尖刺子弹的图像

        // 创建倒计时，在指定时间后分裂子弹
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(countdown), e -> dissolveIntoBullets()));
        timeline.setCycleCount(1);
        timeline.play();
    }

    /**
     * 将尖刺子弹分裂成6个普通子弹，每个以不同角度射出
     */
    private void dissolveIntoBullets() {
        if (!alive || dissolved) return; // 如果子弹已经死亡或已分裂，则不执行

        dissolved = true;

        GC.bullets.remove(this);
        alive = false; // 标记原始尖刺子弹为死亡状态

        // 创建6个子弹，角度分别为 n * PI/3，其中n从0到5
        for (int n = 0; n < 6; n++) {
            double newAngle = n * Math.PI / 3; // 每个子弹的角度
            // 创建一个普通子弹，继承原始子弹的位置和团队属性
            Bullet subBullet = new Bullet(x, y,  newAngle, 3, GC, team);
        }
    }

    @Override
    public void render() {
        // 调用父类的渲染方法
        super.render();

        // 如果子弹已经分裂但还没被移除，这里确保它会被移除
        if (dissolved && alive) {
            alive = false;
        }
    }
}
