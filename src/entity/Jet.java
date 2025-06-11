package entity;

import Director.Director;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import scene.GameControl;
import skill.bladeskill;
import skill.bladeskill;
import utils.KeyProcessor;
import utils.MouseTracker;
import utils.Team;
import javafx.scene.media.AudioClip;

import java.security.Key;

public class Jet extends EntityRole { // 飛機類（玩家/敵人），繼承 EntityRole

    public static final Image jetImage = new Image("/Image/JetImage.png");
    public static final Image JetImageLeft = new Image("/Image/JetImageLeft.png");
    public static final Image JetImageRight = new Image("/Image/JetImageRight.png");
    public static final Image EnemyImage = new Image("/Image/Enemy1.png");


    public static final double PlayerWidth = 120, PlayerHeight = 144;
    public static final double EnemyWidth = 60, EnemyHeight = 72;

    private static final float SpeedLimit = 5f;
    private static final float Acc = 0.6f;
    private static final float Resistance = 0.01f;

    //技能冷卻管理
    private final long fireCooldown = 200;
    private final long dashCooldown = 1000;
    private final long dashDuration = 100;
    private final long shieldcooldown = 10000;
    private long lastBladeTime = 0;
    private final long bladeCooldown = 10000;

    private long lastFireTime = 0;
    private long lastDashTime = 0;
    private long dashEndTime = 0;
    private long lastShieldTime = 0;
    private boolean isDashing = false;

    public boolean shieldAvailable = false; // 是否可以使用護盾
    public boolean BladeAvailable = false; // 是否可以使用脈衝技能

    float Vx = 0;
    float Vy = 0;
    private float Ax = 0, Ay = 0;

    private static final AudioClip fireSound = new AudioClip(Jet.class.getResource("/Audio/basicGunfire.wav").toExternalForm());
    private static final AudioClip explosionSound = new AudioClip((Jet.class.getResource("/Audio/boom.wav").toExternalForm()));

    private final float ExplosionDuration = 1000;
    public boolean Exploded = false;
    private int explosionframe =0;
    private long lastExplosiontick = 0;
    private static final Image[] PlayerExplosion = {
            new Image("Image/PlayerExplosion/explosion1.png"),
            new Image("Image/PlayerExplosion/explosion2.png"),
            new Image("Image/PlayerExplosion/explosion3.png"),
            new Image("Image/PlayerExplosion/explosion4.png"),
            new Image("Image/PlayerExplosion/explosion5.png"),
            new Image("Image/PlayerExplosion/explosion6.png"),
            new Image("Image/PlayerExplosion/explosion7.png"),
            new Image("Image/PlayerExplosion/explosion8.png"),
    };


    public Jet(Image image, double x, double y, double width, double height, GameControl GC, Team team) {
        super(image, x, y, width, height, GC, team); // 指定圖片建構
    }
    public Jet(double x, double y, GameControl GC, Team team) { // 預設玩家/敵人建構
        super(jetImage, x, y, PlayerWidth, PlayerHeight, GC, team); // 預設玩家圖片/尺寸
        this.team = team;
        if (team == Team.enemy) { // 若是敵人則改用敵人圖/尺寸
            image = EnemyImage;
            width = EnemyWidth;
            height = EnemyHeight;
        }
    }

    @Override
    public void render() { // 重寫渲染

        shieldAvailable = System.currentTimeMillis() - lastShieldTime < shieldcooldown; // 如果護盾冷卻時間已過，則可以使用護盾
        BladeAvailable = System.currentTimeMillis() - lastBladeTime < bladeCooldown; // 如果脈衝技能冷卻時間已過，則可以使用脈衝技能

         if (!alive && team == Team.friend) { // 玩家死亡
            if (!Exploded) {
                if (explosionframe == 0){
                    explosionSound.play();
                }
                //System.out.println(System.currentTimeMillis() );
                //System.out.println(lastExplosiontick);
                if (System.currentTimeMillis() - lastExplosiontick >  ExplosionDuration / PlayerExplosion.length && explosionframe < PlayerExplosion.length) {
                    lastExplosiontick = System.currentTimeMillis();
                    image = PlayerExplosion[explosionframe]; // 切換爆炸幀圖
                    explosionframe  ++ ;
                    lastExplosiontick = System.currentTimeMillis() ;
                }
                if (explosionframe >= PlayerExplosion.length) {
                    Exploded = true; // 爆炸動畫結束
                }
            }


        }
        GC.graphicsContext.setGlobalAlpha(1.0); // 每次繪圖前強制恢復完全不透明
        super.render();
    }


    @Override
    public Rectangle2D getContour() { // 返回碰撞框（微調可擊範圍）
        return new Rectangle2D(x+0.2*width, y +0.2*height, width, height);
    }

    public void move () { // 飛機移動
        if (team == Team.friend) {
            if (alive) {
                PlayerControl(); // 玩家操作
            } else {
                Ax = -(Vx * Resistance); // 慣性漸停
                Ay = -(Vy * Resistance);
                Vx += Ax;
                Vy += Ay;
            }
            BorderCheck(); // 邊界檢查
        }
        else if (team == Team.enemy) {
            //System.out.println("Moving vx: " + Vx + ", vy: " + Vy);
            x += Vx;
            y += Vy;
            BorderCheck(); // 邊界檢查
        }
    }

    private void PlayerControl() { // 玩家操控與物理運動
        Ax = 0;
        Ay = 0;
        //image = jetImage;

        long now = System.currentTimeMillis();

        // Dash 判斷與狀態更新
        boolean dashTriggered = MouseTracker.rightPressed && (now - lastDashTime > dashCooldown);
        if (dashTriggered) {
            isDashing = true;
            lastDashTime = now;
            dashEndTime = now + dashDuration;
        }

        // Dash 狀態自動結束
        if (now >= dashEndTime) {
            isDashing = false;
        }

        float acc = isDashing ? 3 * Acc : Acc; // 衝刺時加速度提升

        if (KeyProcessor.pressedKeys.contains(KeyCode.W)) Ay -= acc;
        if (KeyProcessor.pressedKeys.contains(KeyCode.S)) Ay += acc;
        if (KeyProcessor.pressedKeys.contains(KeyCode.A)) Ax -= acc;
        if (KeyProcessor.pressedKeys.contains(KeyCode.D)) Ax += acc;

        if (MouseTracker.leftPressed && (now - lastFireTime > fireCooldown)) { // 滑鼠左鍵可射擊
            Fire();
            lastFireTime = now;
        }

        if (KeyProcessor.pressedKeys.contains(KeyCode.C)) cheat();

        if (KeyProcessor.pressedKeys.contains(KeyCode.E)) shieldopen();

        if (KeyProcessor.pressedKeys.contains(KeyCode.Q)) fireblade();

        if (Ax == 0 && Vx != 0) Ax = -(Vx * Resistance);
        if (Ay == 0 && Vy != 0) Ay = -(Vy * Resistance);

        Vx += Ax;
        Vy += Ay;

        if (Vy > 3) {image = JetImageRight;}
        else if (Vy < -3) image = JetImageLeft;
        else image = jetImage;

        //System.out.println(Vy);

        if (!isDashing) {
            if (Vx > SpeedLimit) Vx = SpeedLimit;
            if (Vx < -SpeedLimit) Vx = -SpeedLimit;
            if (Vy > SpeedLimit) Vy = SpeedLimit;
            if (Vy < -SpeedLimit) Vy = -SpeedLimit;
        }
    }

    public void Fire() {
        double centerX = x + width / 2;
        double centerY = y + height / 2;
        double wingOffset = height * 0.25;// 机翼距中心偏移

        fireSound.setVolume(1.0);
        fireSound.play();

        // 上機翼（左翼，旋轉後）
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200), e -> {
            GC.bullets.add(new Bullet(centerX, centerY - 18 - wingOffset, 0, 8, GC, team));
        }));
        timeline.play();
        // 下機翼（右翼，旋轉後）
        GC.bullets.add(new Bullet(centerX, centerY - 18 + wingOffset,0,8, GC, team));
    }

    public void shieldopen(){
        long now = System.currentTimeMillis();
        if(now-lastShieldTime < shieldcooldown) return;
        lastShieldTime = now;
        //給無敵效果
        skill.shield.apply(getCenterX(), getCenterY(), GC);
        //無敵的動畫
        utils.ShieldVisual.playEMP(GC,this, 5000);
    }

    public void cheat(){
        skill.undeaftalways.apply(getCenterX(), getCenterY(), GC);
        utils.ShieldVisual.playEMP(GC,this, 1000000000);
    }

    private void BorderCheck() {
        if (x < -0.5 * width) x = -0.5 * width;
        if (x >= -0.5 * width && x <= Director.WIDTH - width + 0.5 * width) x += Vx;
        if (x > Director.WIDTH - width + 0.5 * width) x = Director.WIDTH - width + 0.5 * width;

        if (y < -0.5 * height) y = -0.5 * height;
        if (y >= -0.5 * height && y <= Director.HEIGHT - height + 0.5 * height) y += Vy;
        if (y > Director.HEIGHT - height + 0.5 * height) y = Director.HEIGHT - height + 0.5 * height;
    }
    public void fireblade(){
        long now = System.currentTimeMillis();
        if(now - lastBladeTime < bladeCooldown) return;
        lastBladeTime = now;
        bladeskill.activateBlade(this, GC);
    }
}


