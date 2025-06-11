package entity;

import scene.GameControl;
import utils.Team;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import java.util.Random;
import javafx.scene.image.Image;

public class BOSS extends EnemyJet {
    private double rightX;
    private double centerY;
    private double moveAngle = 0;
    private double baseMoveSpeed = 0.008;    // 基礎擺動速度
    private double moveRange = 900;           // 擺動幅度
    private double slowZoneRange = 450;       // 中間緩速區域上下距離範圍

    private int currentPattern = 0;
    private static final int PATTERN_COUNT = 4;
    private long lastPatternChangeTime = 0;
    private long patternDuration = 5200;
    private boolean isPatternWarning = false;
    private long patternWarningStart = 0;
    private long patternWarningDuration = 1100;

    private long lastBulletTime = 0;
    private long bulletInterval = 300;

    private boolean laserActive = false;
    private long laserStartTime = 0;
    private long laserDuration = 1000;


    private Random random = new Random();

    private static Image BossImage = new Image("Image/jet2-1.png");

    public BOSS(double x, double y, GameControl gc) {
        super(BossImage, x, y, 240, 260, gc);
        this.rightX = x;
        this.centerY = y;

        this.health = 2500;
        this.scoreValue = 2000;
        this.lastPatternChangeTime = System.currentTimeMillis();
    }

    @Override
    protected void updateMovement() {
        // 固定X軸在右半邊
        x = rightX;
        long times = System.currentTimeMillis()/1000;

        // 計算Y軸與中心距離
        double distFromCenter = Math.abs(y - centerY);

        // 緩速因子，中心區域內較慢，遠離中心速度加快
        double speedFactor;
        if (distFromCenter > slowZoneRange) {
            speedFactor = 0.2 + 0.8 * (distFromCenter / slowZoneRange);
        } else {
            speedFactor = 1.0;
        }

        moveAngle += baseMoveSpeed * speedFactor;

        // 根據調整後的角度計算Y座標
        y = 540 + (moveRange/2) * Math.sin(moveAngle);

        Vx = 0;
        Vy = 0;
    }

    @Override
    protected void attack() {
        long now = System.currentTimeMillis();

        // 玩家指令反制 (8%概率)
        if (random.nextDouble() < 0.08) {
            KeyCode lastKey = utils.KeyProcessor.getLastKeyPressed();
            if (lastKey != null) {
                switch (lastKey) {
                    case A: Vx += 7f; break;
                    case D: Vx -= 7f; break;
                    case W: Vy += 7f; break;
                    case S: Vy -= 7f; break;
                    case SPACE:
                        if (currentPattern == 0) fireCircle(18, 5.2);
                        else if (currentPattern == 1 && !laserActive) {
                            laserActive = true;
                            laserStartTime = now;
                        }
                        break;
                    case E: this.health += 30; break;
                    default: break;
                }
            }
        }

        // 預警處理
        if (isPatternWarning) {
            if (now - patternWarningStart > patternWarningDuration) {
                isPatternWarning = false;
                currentPattern = (currentPattern + 1) % PATTERN_COUNT;
                lastPatternChangeTime = now;
                lastBulletTime = now;
                if (currentPattern == 1) {
                    laserActive = true;
                    laserStartTime = now;
                }
            }
            return;
        }

        // 模式切換預警
        if (now - lastPatternChangeTime > patternDuration) {
            isPatternWarning = true;
            patternWarningStart = now;
            return;
        }

        // 攻擊模式判斷
        switch (currentPattern) {
            case 0:
                if (now - lastBulletTime > bulletInterval) {
                    fireCircle(12, 4.5);
                    lastBulletTime = now;
                }
                break;
            case 1:
                if (laserActive) {
                    if (now - laserStartTime > laserDuration) {
                        laserActive = false;
                        lastPatternChangeTime = now;
                    } else {
                        double laserY = getCenterY();
                        double laserHeight = 64 * 1.5;
                        double laserWidth = 800 * 3;
                        double laserLeft = getCenterX() - laserWidth;
                        if (GC.Player != null && GC.Player.isAlive()) {
                            double px = GC.Player.getCenterX();
                            double py = GC.Player.getCenterY();
                            if (px >= laserLeft && px <= laserLeft + laserWidth &&
                                    py >= laserY - laserHeight / 2 && py <= laserY + laserHeight / 2) {

                                GC.Player.setAlive(GC.Player.undefeatable? false : true);
                            }
                        }
                    }
                }
                break;
            case 2:
                if (now - lastBulletTime > bulletInterval + 280) {
                    fireHomingBullets(4, 2.7);
                    lastBulletTime = now;
                }
                break;
            case 3:
                if (now - lastBulletTime > bulletInterval + 80) {
                    fireFan(7, 5.8, Math.PI, Math.PI / 1.4);
                    lastBulletTime = now;
                }
                break;
        }
    }

    private void fireCircle(int N, double speed) {
        double r = 2 * Math.PI;
        double cx = getCenterX(), cy = getCenterY() + height / 2;
        for (int i = 0; i < N; i++) {
            double angle = r * i / N;
            new Bullet(cx, cy, angle, speed, GC, Team.enemy);
        }
    }

    private void fireFan(int N, double speed, double centerAngle, double spreadAngle) {
        double cx = getCenterX(), cy = getCenterY() + height / 2;
        for (int i = 0; i < N; i++) {
            double angle = centerAngle - spreadAngle / 2 + i * spreadAngle / (N - 1);
            new Bullet(cx, cy, angle, speed, GC, Team.enemy);
        }
    }

    private void fireHomingBullets(int count, double speed) {
        double cx = getCenterX(), cy = getCenterY() + height / 2;
        double spread = Math.PI / 9;
        for (int i = 0; i < count; i++) {
            double angle = Math.PI / 2 + (i - (count - 1) / 2.0) * spread;
            Bullet b = new Bullet(cx, cy, angle, speed, GC, Team.enemy) {
                @Override
                public void Bulletmove() {
                    if (GC.Player != null && GC.Player.isAlive()) {
                        double dx = GC.Player.getCenterX() - x;
                        double dy = GC.Player.getCenterY() - y;
                        double targetAngle = Math.atan2(dy, dx);

                        double delta = targetAngle - AngleRadian;
                        // 將 delta 限制在 -π 到 π 之間
                        if (delta > Math.PI) delta -= 2 * Math.PI;
                        if (delta < -Math.PI) delta += 2 * Math.PI;

                        // 增加追蹤速度（例如 0.10，比原來 0.04 快約 2.5 倍）
                        double turnRate = 0.10;

                        // 限制最大轉向角度，避免跳太快
                        double maxTurn = 0.15; // 弧度，約8.6度
                        if (delta > maxTurn) delta = maxTurn;
                        else if (delta < -maxTurn) delta = -maxTurn;

                        AngleRadian += turnRate * delta;
                    }
                    super.Bulletmove();
                }
            };
        }
    }

    @Override
    public void render() {
        GraphicsContext gc = GC.graphicsContext;

        if (isPatternWarning) {
            gc.save();
            gc.setGlobalAlpha(0.55);
            gc.setStroke(Color.ORANGE);
            gc.setLineWidth(12);
            double r = (currentPattern == 1) ? 400 : 320;
            gc.strokeOval(getCenterX() - r, getCenterY() - r, r * 2, r * 2);
            gc.restore();
        }

        if (laserActive) {
            gc.save();
            gc.setGlobalAlpha(0.6);
            gc.setFill(Color.YELLOW);
            double laserY = getCenterY();
            double laserHeight = 64 * 1.5;
            double laserWidth = 800 * 3;
            double laserLeft = getCenterX() - laserWidth;
            gc.fillRect(laserLeft, laserY - laserHeight / 2, laserWidth, laserHeight);
            gc.setGlobalAlpha(0.85);
            gc.setStroke(Color.ORANGERED);
            gc.setLineWidth(6);
            gc.strokeRect(laserLeft, laserY - laserHeight / 2, laserWidth, laserHeight);
            gc.restore();
        }

        super.render();
    }
}
