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

public class Jet extends EntityRole {

    public static final Image jetImage = new Image("/Image/JetImage.png");
    public static final Image JetImageLeft = new Image("/Image/JetImageLeft.png");
    public static final Image JetImageRight = new Image("/Image/JetImageRight.png");
    public static final Image EnemyImage = new Image("/Image/PlayerJet.png");


    public static final double PlayerWidth = 120, PlayerHeight = 144;
    public static final double EnemyWidth = 60, EnemyHeight = 72;

    private static final float SpeedLimit = 5f;
    private static final float Acc = 0.6f;
    private static final float Resistance = 0.01f;

    private final long fireCooldown = 200;
    private final long dashCooldown = 1000;
    private final long dashDuration = 100;
    private final long shieldcooldown = 10000;

    private long lastFireTime = 0;
    private long lastDashTime = 0;
    private long dashEndTime = 0;
    private long lastShieldTime = 0;
    private boolean isDashing = false;

    private float Vx = 0, Vy = 0;
    private float Ax = 0, Ay = 0;

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
        super(image, x, y, width, height, GC, team);
    }
    public Jet(double x, double y, GameControl GC, Team team) {
        super(jetImage, x, y, PlayerWidth, PlayerHeight, GC, team);
        this.team = team;
        if (team == Team.enemy) {
            image = EnemyImage;
            width = EnemyWidth;
            height = EnemyHeight;
        }
    }

    @Override
    public void render() {
        if (!alive && team == Team.enemy) {
            GC.enemies.remove(this);
            return;
        } else if (!alive && team == Team.friend) {

            if (!Exploded) {
                //System.out.println(System.currentTimeMillis() );
                //System.out.println(lastExplosiontick);
                if (System.currentTimeMillis() - lastExplosiontick >  ExplosionDuration / PlayerExplosion.length && explosionframe < PlayerExplosion.length) {
                    lastExplosiontick = System.currentTimeMillis();
                    image = PlayerExplosion[explosionframe];
                    explosionframe  ++ ;
                    lastExplosiontick = System.currentTimeMillis() ;
                    System.out.println(explosionframe);
                }
                if (explosionframe >= PlayerExplosion.length) {
                    Exploded = true;
                }
            }



        }
        super.render();
    }


    @Override
    public Rectangle2D getContour() {
        return new Rectangle2D(x+0.2*width, y, width, height);
    }

    public void move () {

        if (team == Team.friend) {
            if (alive) {
                PlayerControl();
            } else {
                Ax = -(Vx * Resistance);
                Ay = -(Vy * Resistance);
                Vx += Ax;
                Vy += Ay;
            }
            BorderCheck();
        }

    }

    private void PlayerControl() {
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

        float acc = isDashing ? 3 * Acc : Acc;

        if (KeyProcessor.pressedKeys.contains(KeyCode.W)) Ay -= acc;
        if (KeyProcessor.pressedKeys.contains(KeyCode.S)) Ay += acc;
        if (KeyProcessor.pressedKeys.contains(KeyCode.A)) Ax -= acc;
        if (KeyProcessor.pressedKeys.contains(KeyCode.D)) Ax += acc;

        if (MouseTracker.leftPressed && (now - lastFireTime > fireCooldown)) {
            Fire();
            lastFireTime = now;
        }

        if (KeyProcessor.pressedKeys.contains(KeyCode.E)) shieldopen();



        if (Ax == 0 && Vx != 0) Ax = -(Vx * Resistance);
        if (Ay == 0 && Vy != 0) Ay = -(Vy * Resistance);

        Vx += Ax;
        Vy += Ay;

        if (Vy > 3) {System.out.println(Vy);image = JetImageRight;}
        else image = jetImage;

        if (Vy < -3) image = JetImageLeft;
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

        GC.bullets.add(new Bullet(centerX, centerY, GC, team));
        GC.bullets.add(new Bullet(centerX - 40, centerY, GC, team));
    }

    public void shieldopen(){
        long now = System.currentTimeMillis();
        if(now-lastShieldTime < shieldcooldown) return;
        lastShieldTime = now;
        //給無敵效果
        skill.shield.apply(getCenterX(), getCenterY(), GC);
        //無敵的動畫
        utils.ShieldVisual.playEMP(GC.root,this, 5000);
    }

    private void BorderCheck() {
        if (x < -0.5 * width) x = -0.5 * width;
        if (x >= -0.5 * width && x <= Director.WIDTH - width + 0.5 * width) x += Vx;
        if (x > Director.WIDTH - width + 0.5 * width) x = Director.WIDTH - width + 0.5 * width;

        if (y < -0.5 * height) y = -0.5 * height;
        if (y >= -0.5 * height && y <= Director.HEIGHT - height + 0.5 * height) y += Vy;
        if (y > Director.HEIGHT - height + 0.5 * height) y = Director.HEIGHT - height + 0.5 * height;
    }
}
