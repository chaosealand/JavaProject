package entity;

import javafx.scene.image.Image;
import scene.GameControl;
import utils.Team;
import entity.BasicEnemy;
import java.util.Random;

/**
 * 敌人基类 - 扩展自Jet类，提供通用的敌人行为
 */
public abstract class EnemyJet extends Jet {
    protected Random random = new Random();
    protected long lastAttackTime = 0;
    protected long attackCooldown = 1500; // 默认攻击冷却时间(毫秒)
    protected int movementPattern = 0; // 移动模式
    protected double movementSpeed = 2.0; // 移动速度
    protected double health = 100; // 生命值
    protected int scoreValue = 100; // 击败后获得的分数
    protected boolean isDodging = false; // 是否在执行闪避动作
    protected long dodgeCooldown = 5000; // 闪避冷却时间
    protected long lastDodgeTime = 0; // 上次闪避时间
    protected int damage = 10; // 攻击伤害

    protected boolean scoreGiven = false;

    public boolean isScoreGiven() {
        return scoreGiven;
    }

    public void markScoreGiven() {
        scoreGiven = true;
    }

    private final float ExplosionDuration = 1000;
    private int explosionframe =0;
    private long lastExplosiontick = 0;
    private static final Image[] EnemyExplosion = {
            new Image("Image/jet1-1.png"),
            new Image("Image/jet1-2.png"),
            new Image("Image/jet1-3.png"),
            new Image("Image/jet1-4.png"),
            new Image("Image/jet1-5.png"),
            new Image("Image/jet1-6.png"),
            new Image("Image/jet1-7.png"),
            new Image("Image/jet1-8.png"),
            new Image("Image/jet1-9.png"),
            new Image("Image/jet1-10.png"),
            new Image("Image/jet1-11.png"),
            new Image("Image/jet1-12.png"),
            new Image("Image/jet1-13.png"),
            new Image("Image/jet1-14.png"),
            new Image("Image/jet1-15.png"),
            new Image("Image/jet1-16.png"),
            new Image("Image/jet1-17.png"),
            new Image("Image/jet1-18.png"),
            new Image("Image/jet1-19.png"),
            new Image("Image/jet1-20.png"),
    };
    private static final Image[] BossExplosion = {
            new Image("Image/jet2-1.png"),
            new Image("Image/jet2-2.png"),
            new Image("Image/jet2-3.png"),
            new Image("Image/jet2-4.png"),
            new Image("Image/jet2-5.png"),
            new Image("Image/jet2-6.png"),
            new Image("Image/jet2-7.png"),
            new Image("Image/jet2-8.png"),
            new Image("Image/jet2-9.png"),
            new Image("Image/jet2-10.png"),
            new Image("Image/jet2-11.png"),
            new Image("Image/jet2-12.png"),
            new Image("Image/jet2-13.png"),
            new Image("Image/jet2-14.png"),
    };

    public EnemyJet(double x, double y, GameControl gc) {
        super(Jet.EnemyImage, x, y, Jet.EnemyWidth, Jet.EnemyHeight, gc, Team.enemy);
    }

    public EnemyJet(Image image, double x, double y, double width, double height, GameControl gc) {
        super(image, x, y, width, height, gc, Team.enemy);
    }

    @Override
    public void render() {
        if (isAlive()) {
            // 检查敌人是否飞出屏幕边界
            if (!(this instanceof BOSS)) {
                if (x < -width || x > Director.Director.WIDTH + width ||
                        y < -height || y > Director.Director.HEIGHT + height) {
                    System.out.println("Enemy 出界被判死？ x=" + x + " y=" + y + " class=" + getClass().getSimpleName());
                    setAlive(false);
                    return;
                }
            }

            // 更新行为
            updateMovement();
            attack();

            // 闪避玩家子弹
            tryDodge();

            move();
            // 渲染飞机
            super.render();
        } else if (!Exploded) {
            long now = System.currentTimeMillis();
            if (this instanceof BOSS) {
                if (now - lastExplosiontick >= ExplosionDuration / BossExplosion.length && explosionframe < BossExplosion.length) {
                    image = BossExplosion[explosionframe]; // 換幀
                    explosionframe++;
                    lastExplosiontick = now;
                }

                GC.graphicsContext.setGlobalAlpha(1.0);
                super.render(); // 渲染爆炸幀

                // 若所有幀播放完畢
                if (explosionframe >= EnemyExplosion.length) {
                    Exploded = true;
                }
            } else {
                if (now - lastExplosiontick >= ExplosionDuration / EnemyExplosion.length && explosionframe < EnemyExplosion.length) {
                    image = EnemyExplosion[explosionframe]; // 換幀
                    explosionframe++;
                    lastExplosiontick = now;
                }

                GC.graphicsContext.setGlobalAlpha(1.0);
                super.render(); // 渲染爆炸幀

                // 若所有幀播放完畢
                if (explosionframe >= EnemyExplosion.length) {
                    Exploded = true;
                }
            }



        }

    }

    /**
     * 更新敌人移动行为
     */
    protected abstract void updateMovement();

    /**
     * 敌人攻击行为
     */
    protected abstract void attack();

    /**
     * 尝试闪避玩家子弹
     */
    protected void tryDodge() {
        long now = System.currentTimeMillis();
        if (now - lastDodgeTime < dodgeCooldown || isDodging) {
            return;
        }

        // 检查附近是否有子弹
        for (Bullet bullet : GC.bullets) {
            // 只对玩家的子弹进行闪避
            if (bullet.team == Team.friend) {
                // 计算与子弹的距离
                double dx = bullet.x - getCenterX();
                double dy = bullet.y - getCenterY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                // 如果子弹足够近且正在接近，尝试闪避
                if (distance < 100 && isApproaching(bullet)) {
                    // 随机决定是否闪避（概率增加难度）
                    if (random.nextInt(100) < 1) { // 30%的概率闪避
                        performDodge(dx, dy);
                        lastDodgeTime = now;
                        break;
                    }
                }
            }
        }
    }

    /**
     * 判断子弹是否正在接近敌人
     */
    private boolean isApproaching(Bullet bullet) {
        // 计算子弹与敌人之间的距离变化方向
        double futureX = bullet.x + bullet.V * Math.cos(bullet.AngleRadian);
        double futureY = bullet.y - bullet.V * Math.sin(bullet.AngleRadian);;

        double currentDistance = Math.sqrt(Math.pow(bullet.x - getCenterX(), 2) + Math.pow(bullet.y - getCenterY(), 2));
        double futureDistance = Math.sqrt(Math.pow(futureX - getCenterX(), 2) + Math.pow(futureY - getCenterY(), 2));

        // 如果未来距离小于当前距离，说明子弹正在接近
        return futureDistance < currentDistance;
    }

    /**
     * 执行闪避动作
     */
    private void performDodge(double bulletDx, double bulletDy) {
        // 向垂直于子弹方向的方向闪避
        double perpX = -bulletDy;
        double perpY = bulletDx;

        // 标准化方向向量
        double length = Math.sqrt(perpX * perpX + perpY * perpY);
        if (length > 0) {
            perpX /= length;
            perpY /= length;
        }

        // 随机选择闪避方向（左或右）
        if (random.nextBoolean()) {
            perpX = -perpX;
            perpY = -perpY;
        }

        // 设置闪避移动
        this.Vx = (float) (perpX * movementSpeed * 3); // 闪避速度是正常速度的3倍
        this.Vy = (float) (perpY * movementSpeed * 3);


        // 设置闪避状态
        isDodging = true;

        // 设置一个计时器，在一段时间后恢复正常移动
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    isDodging = false;
                }
            },
            200 // 500毫秒后恢复正常移动
        );
    }

    /**
     * 受到伤害
     */
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0 && isAlive()) {

            setAlive(false);
        }
    }

    /**
     * 获取敌人当前生命值
     */
    public double getHealth() {
        return health;
    }

    /**
     * 获取击败后获得的分数
     */
    public int getScoreValue() {
        return scoreValue;
    }
}
