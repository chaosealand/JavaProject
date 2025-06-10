package entity;

import Director.Director;



import javafx.geometry.Rectangle2D;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import scene.GameControl;
import utils.KeyProcessor;
import utils.MouseTracker;
import utils.Team;

import java.security.Key;

public class Jet extends EntityRole { // 飛機類（玩家/敵人），繼承 EntityRole

    public static final Image jetImage = new Image("/Image/JetImage.png"); // 玩家主機圖片
    public static final Image JetImageLeft = new Image("/Image/JetImageLeft.png"); // 玩家左偏圖片
    public static final Image JetImageRight = new Image("/Image/JetImageRight.png"); // 玩家右偏圖片
    public static final Image EnemyImage = new Image("/Image/Enemy1.png"); // 敵人機圖片

    public static final double PlayerWidth = 100, PlayerHeight = 120; // 玩家機尺寸
    public static final double EnemyWidth = 90, EnemyHeight = 108; // 敵人機尺寸

    private static final float SpeedLimit = 5f; // 最大速度
    private static final float Acc = 0.6f; // 加速度
    private static final float Resistance = 0.01f; // 阻力係數

    private final long fireCooldown = 200; // 發射間隔（毫秒）
    private final long dashCooldown = 1000; // 衝刺冷卻
    private final long dashDuration = 100; // 衝刺持續
    private final long shieldcooldown = 10000; // 護盾冷卻

    private long lastFireTime = 0; // 上次射擊時間
    private long lastDashTime = 0; // 上次衝刺時間
    private long dashEndTime = 0; // 衝刺結束時間
    private long lastShieldTime = 0; // 上次護盾時間
    private boolean isDashing = false; // 是否正在衝刺

    float Vx = 0; // X 方向速度
    float Vy = 0; // Y 方向速度
    private float Ax = 0, Ay = 0; // X, Y 方向加速度

    private final float ExplosionDuration = 1000; // 爆炸動畫總時長
    public boolean Exploded = false; // 爆炸是否結束
    private int explosionframe =0; // 當前爆炸幀
    private long lastExplosiontick = 0; // 上次爆炸幀刷新時間
    private static final Image[] PlayerExplosion = { // 爆炸動畫幀圖
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
        if (!alive && team == Team.enemy) { // 敵人死亡：從列表移除
            GC.enemies.remove(this);
            return;
        } else if (!alive && team == Team.friend) { // 玩家死亡
            if (!Exploded) {
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

        super.render(); // 呼叫父類別渲染
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

        if (KeyProcessor.pressedKeys.contains(KeyCode.E)) shieldopen(); // 按E可開盾

        if (Ax == 0 && Vx != 0) Ax = -(Vx * Resistance); // 阻力
        if (Ay == 0 && Vy != 0) Ay = -(Vy * Resistance);

        Vx += Ax;
        Vy += Ay;

        if (Vy > 3) {image = JetImageRight;} // 右偏圖
        else if (Vy < -3) image = JetImageLeft; // 左偏圖
        else image = jetImage; // 中立圖

        //System.out.println(Vy);

        if (!isDashing) { // 非衝刺狀態限制最大速度
            if (Vx > SpeedLimit) Vx = SpeedLimit;
            if (Vx < -SpeedLimit) Vx = -SpeedLimit;
            if (Vy > SpeedLimit) Vy = SpeedLimit;
            if (Vy < -SpeedLimit) Vy = -SpeedLimit;
        }
    }

    public void Fire() { // 玩家射擊
        double centerX = x + width / 2;
        double centerY = y + height / 2;
        double wingOffset = height * 0.25; // 机翼距中心偏移

        // 上機翼（左翼，旋轉後）
        GC.bullets.add(new Bullet(centerX, centerY - 15 - wingOffset,0,8 ,GC, team));
        // 下機翼（右翼，旋轉後）
        GC.bullets.add(new Bullet(centerX, centerY - 15 + wingOffset,0,8, GC, team));
    }

    public void shieldopen(){
        long now = System.currentTimeMillis();
        if(now-lastShieldTime < shieldcooldown) return; // 冷卻未到不觸發
        lastShieldTime = now;
        //給無敵效果
        skill.shield.apply(getCenterX(), getCenterY(), GC);
        //無敵的動畫
        utils.ShieldVisual.playEMP(GC,this, 5000);
    }

    private void BorderCheck() { // 邊界檢查（不出螢幕）
        if (x < -0.5 * width) x = -0.5 * width;
        if (x >= -0.5 * width && x <= Director.WIDTH - width + 0.5 * width) x += Vx;
        if (x > Director.WIDTH - width + 0.5 * width) x = Director.WIDTH - width + 0.5 * width;
        if (y < -0.5 * height) y = -0.5 * height;
        if (y >= -0.5 * height && y <= Director.HEIGHT - height + 0.5 * height) y += Vy;
        if (y > Director.HEIGHT - height + 0.5 * height) y = Director.HEIGHT - height + 0.5 * height;
    }
}
