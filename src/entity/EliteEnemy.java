package entity;

import Director.Director;
import javafx.scene.paint.Color;
import scene.GameControl;
import utils.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * 精英敌人 - 复杂的移动模式和多种攻击手段
 */
public class EliteEnemy extends EnemyJet {

    private int attackPattern = 0; // 攻击模式
    private int phaseCounter = 0; // 阶段计数器
    private long lastPhaseChange = 0; // 上次阶段变化时间
    private long phaseChangeCooldown = 8000; // 阶段变化冷却时间
    private List<Bullet> orbitalBullets = new ArrayList<>(); // 环绕子弹列表

    public EliteEnemy(double x, double y, GameControl gc) {
        super(x, y, gc);
        // 精英敌人参数设置
        this.movementSpeed = 2.0;
        this.attackCooldown = 1200; // 攻击冷却时间更短
        this.health = 250; // 较高生命值
        this.scoreValue = 500; // 高分数奖励
        this.damage = 20;
        //initOrbitalBullets();
    }

    /**
     * 初始化环绕子弹
     */
    /*
    private void initOrbitalBullets() {
        // 创建4个环绕子弹
        for (int i = 0; i < 4; i++) {
            final double angle = i * (Math.PI / 2); // 均匀分布在圆周上
            final double orbitRadius = 50;

            Bullet orb = new Bullet(0, 0, GC, Team.enemy) {
                private double orbitAngle = angle;
                private double angularSpeed = 0.05;

                @Override
                public void render() {
                    if (!isAlive()) return;

                    // 更新环绕角度
                    orbitAngle += angularSpeed;

                    // 计算环绕位置
                    x = EliteEnemy.this.getCenterX() + Math.cos(orbitAngle) * orbitRadius;
                    y = EliteEnemy.this.getCenterY() + Math.sin(orbitAngle) * orbitRadius;

                    // 渲染为紫色能量球
                    GC.graphicsContext.setFill(Color.PURPLE);
                    GC.graphicsContext.fillOval(x - width/2, y - height/2, width, height);
                }
            };

            // 设置环绕子弹属性
            orb.width = 12;
            orb.height = 12;

            orbitalBullets.add(orb);
            GC.bullets.add(orb);
        }
    }*/

    @Override
    protected void updateMovement() {
        // 如果正在执行闪避动作，跳过常规移动
        if (isDodging) return;

        long now = System.currentTimeMillis();

        // 检查是否需要更改阶段
        if (now - lastPhaseChange > phaseChangeCooldown) {
            lastPhaseChange = now;
            phaseCounter = (phaseCounter + 1) % 3;
            attackPattern = (attackPattern + 1) % 3; // 同时也改变攻击模式
        }

        // 根据当前阶段执行不同的移动模式
        switch (phaseCounter) {
            case 0: // 下降阶段 - 快速向下移动到指定位置
                if (x > Director.WIDTH * 0.7) {
                    this.Vy = 0;
                    this.Vx = - (float) (movementSpeed * 1.5);
                } else {
                    this.Vy = 0;
                    // 向玩家所在方向移动一小段，然后停下
                    if (GC.Player != null) {
                        double targetX = GC.Player.getCenterX();
                        double dx = targetX - getCenterX();
                        // 缓慢向玩家方向移动
                        this.Vx = (float) (Math.signum(dx) * Math.min(Math.abs(dx) * 0.03, movementSpeed));
                        if (Math.abs(dx) < 20) this.Vx = 0;
                    }
                }
                break;

            case 1: // 攻击阶段 - 左右移动轨迹
                double t = (now % 5000) / 5000.0; // 0到1的周期值
                // 基于正弦函数进行左右移动
                this.Vx = (float) (Math.sin(t * 2 * Math.PI) * movementSpeed * 1.2);
                this.Vy = (float) (Math.sin(t * 4 * Math.PI) * movementSpeed * 0.5); // 轻微上下移动
                break;

            case 2: // 冲刺阶段 - 向玩家位置快速冲刺
                if (GC.Player != null && GC.Player.isAlive()) {
                    double targetX = GC.Player.getCenterX();
                    double targetY = GC.Player.getCenterY() - 100; // 目标位置在玩家上方

                    double dx = targetX - getCenterX();
                    double dy = targetY - getCenterY();

                    // 归一化方向向量
                    double length = Math.sqrt(dx*dx + dy*dy);
                    if (length > 0) {
                        dx /= length;
                        dy /= length;
                    }

                    // 设置速度
                    this.Vx = (float) (dx * movementSpeed * 2);
                    this.Vy = (float) (dy * movementSpeed * 2);

                    // 如果已经靠近目标，减速
                    if (length < 50) {
                        this.Vx *= 0.5;
                        this.Vy *= 0.5;
                    }
                }
                break;
        }

        // 防止飞出屏幕边界
        if (x < 0) Vx = Math.abs(Vx);
        if (x > Director.WIDTH - width) Vx = -Math.abs(Vx);
        if (y < 0) Vy = Math.abs(Vy);
        if (y > Director.HEIGHT * 0.7) Vy = -Math.abs(Vy);
    }

    @Override
    protected void attack() {
        // 检查敌人是否死亡，如果死亡则清除其环绕子弹
        if (!isAlive()) {
            for (Bullet orb : orbitalBullets) {
                orb.setAlive(false);
            }
            return;
        }

        long now = System.currentTimeMillis();

        // 检查攻击冷却时间
        if (now - lastAttackTime > attackCooldown) {
            double centerX = getCenterX();
            double centerY = getCenterY();

            // 执行当前攻击模式
            switch (attackPattern) {
                case 0: // 多方向散射
                    multiDirectionalShot(centerX, centerY);
                    break;

                case 1: // 激光攻击
                    laserAttack(centerX, centerY);
                    break;

                case 2: // 发射环绕子弹
                    //shootOrbitalBullets();
                    break;
            }

            // 更新最后攻击时间
            lastAttackTime = now;
        }
    }

    /**
     * 多方向散射攻击
     */
    private void multiDirectionalShot(double centerX, double centerY) {
        // 发射6个方向的子弹
        int bulletCount = 6;
        for (int i = 0; i < bulletCount; i++) {
            double angle = (2 * Math.PI * i / bulletCount);
            Bullet bullet = new Bullet(centerX, centerY,angle, GC, Team.enemy);
            GC.bullets.add(bullet);
        }
    }

    /**
     * 激光攻击
     */
    private void laserAttack(double centerX, double centerY) {
        // 检查玩家是否存在
        if (GC.Player != null && GC.Player.isAlive()) {
            // 瞄准玩家位置
            double targetX = GC.Player.getCenterX();
            double targetY = GC.Player.getCenterY();

            // 计算方向
            double dx = targetX - centerX;
            double dy = targetY - centerY;
            double length = Math.sqrt(dx * dx + dy * dy);

            if (length > 0) {
                dx /= length;
                dy /= length;

                // 创建一个主激光束和两个侧面的激光束
                //LaserBeam mainLaser = new LaserBeam(centerX, centerY,  GC, Team.enemy);
                //GC.LaserList.add(mainLaser);

                // 创建两个侧面激光束（左右各一个）
                /*
                double sideAngle = Math.PI / 12; // 15度角

                // 左侧激光
                double leftDx = Math.cos(Math.atan2(dy, dx) - sideAngle) * 0.95;
                double leftDy = Math.sin(Math.atan2(dy, dx) - sideAngle) * 0.95;
                LaserBeam leftLaser = new LaserBeam(centerX, centerY, leftDx, leftDy, GC, Team.enemy);

                GC.LaserList.add(leftLaser);

                // 右侧激光
                double rightDx = Math.cos(Math.atan2(dy, dx) + sideAngle) * 0.95;
                double rightDy = Math.sin(Math.atan2(dy, dx) + sideAngle) * 0.95;
                LaserBeam rightLaser = new LaserBeam(centerX, centerY, rightDx, rightDy, GC, Team.enemy);

                GC.LaserList.add(rightLaser);
                */
            }
        }
    }

    /**
     * 发射环绕子弹
     */
    /*
    private void shootOrbitalBullets() {
        for (Bullet orb : orbitalBullets) {
            if (orb.isAlive()) {
                // 获取环绕子弹的当前位置
                double orbX = orb.x;
                double orbY = orb.y;

                // 计算指向玩家的方向
                if (GC.Player != null && GC.Player.isAlive()) {
                    double targetX = GC.Player.getCenterX();
                    double targetY = GC.Player.getCenterY();

                    double dx = targetX - orbX;
                    double dy = targetY - orbY;
                    double length = Math.sqrt(dx * dx + dy * dy);

                    if (length > 0) {
                        dx /= length;
                        dy /= length;

                        // 从环绕子弹位置发射一个新子弹
                        Bullet projectile = new Bullet(orbX, orbY, GC, Team.enemy);
                        projectile.directionX = dx;
                        projectile.directionY = dy;
                        projectile.speed = 5;
                        GC.bullets.add(projectile);
                    }
                }
            }
        }
    }*/

    @Override
    public void setAlive(boolean alive) {
        super.setAlive(alive);

        // 如果死亡，释放环绕子弹
        if (!alive) {
            for (Bullet orb : orbitalBullets) {
                orb.setAlive(false);
            }
        }
    }
}
