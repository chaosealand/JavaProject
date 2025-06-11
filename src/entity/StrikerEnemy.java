package entity;

import Director.Director;
import scene.GameControl;
import utils.Team;

/**
 * 突击型敌人 - 快速移动并使用激光攻击
 */
public class StrikerEnemy extends EnemyJet {

    private int movementState = 0; // 0: 向下, 1: 横向移动, 2: 攻击模式
    private long lastStateChange = 0;
    private long movementStateTime = 5000; // 每个移动状态持续时间
    private double targetY; // 横向移动的目标位置
    private double HoverX = Director.WIDTH * (0.8 + (random.nextInt(20)-10) * 0.01 ); // 横向移动的目标位置偏移量

    public StrikerEnemy(double x, double y, GameControl gc) {
        super(x, y, gc);
        // 突击型敌人参数设置
        this.movementSpeed = 2.5; // 更快的移动速度
        this.attackCooldown = 3000; // 3秒发射一次激光
        this.health = 75;
        this.scoreValue = 150;
        this.damage = 15;

        // 初始化目标位置
        updateTargetPosition();
    }

    private void updateTargetPosition() {
        // 设置一个随机的横向目标位置
        targetY = random.nextDouble() * (Director.HEIGHT - Jet.EnemyHeight);
    }

    @Override
    protected void updateMovement() {
        // 如果正在执行闪避动作，跳过常规移动
        if (isDodging) return;

        long now = System.currentTimeMillis();

        // 检查是否需要更改移动状态
        if (now - lastStateChange > movementStateTime) {
            lastStateChange = now;
            movementState = (movementState + 1) % 3;

            if (movementState == 1) { // 进入横向移动状态时更新目标位置
                updateTargetPosition();
            }
            // 进入攻击模式时初始化随机移动方向，而不是每帧都随机
            if (movementState == 2) {
                this.Vy = (float) ((random.nextDouble() - 0.5) * movementSpeed * 0.8);
                this.Vx = (float) ((random.nextDouble() * 0.5) * movementSpeed * 0.5);
            }
        }

        // 根据当前状态执行不同的移动模式
        switch (movementState) {
            case 0:
                if (x > HoverX) {
                    // 如果在屏幕右侧，向左移动
                    this.Vx = - (float) movementSpeed;
                } else {
                    // 向下移动
                    this.Vx = 0;
                }
                break;

            case 1: // 横向移动到指定位置
                if (x > HoverX) {
                    // 如果在屏幕右侧，向左移动
                    this.Vx = - (float) movementSpeed;
                } else {
                    // 向下移动
                    this.Vx = 0;
                }

                double dy = targetY - this.y;
                // 如果接近目标，停止移动
                if (Math.abs(dy) < 5) {
                    this.Vy = 0;
                } else if (Math.abs(dy) < 30) {
                    // 接近目标时平滑减速
                    this.Vy = (float) (dy * 0.05);
                } else {
                    // 正常移动
                    this.Vy = (float) (dy > 0 ? movementSpeed : -movementSpeed);
                }
                break;

            case 2: // 攻击模式 - 保持当前移动方向，不在每帧随机改变
                // 每隔一段时间才改变方向，而不是每帧都改变
                if (now % 1000 < 20) { // 约每秒改变一次方向
                    this.Vy = (float) ((random.nextDouble() - 0.5) * movementSpeed * 0.8);
                    this.Vx = (float) ((random.nextDouble() * 0.5) * movementSpeed * 0.5);
                }
                break;
        }

        // 防止飞出屏幕
        if (x < 0) Vx = Math.abs(Vx);
        if (x > Director.WIDTH + 10) Vx = -Math.abs(Vx);
        if (y < 0) Vy = Math.abs(Vy);
        if (y > Director.HEIGHT - height) Vy = -Math.abs(Vy); // 修正了这里的边界检测
    }

    @Override
    protected void attack() {
        long now = System.currentTimeMillis();

        // 在攻击模式下攻击频率更高
        long currentAttackCooldown = (movementState == 2) ? attackCooldown / 2 : attackCooldown;

        // 检查攻击冷却时间
        if (now - lastAttackTime > currentAttackCooldown) {
            double centerX = getCenterX();
            double centerY = getCenterY();

            // 查找玩家位置
            Jet player = GC.Player;
            if (player != null && player.isAlive()) {
                // 创建一个指向玩家的激光
                    // 创建激光
                    LaserBeam laser = new LaserBeam(centerX, centerY,  GC, Team.enemy, this);

                    GC.LaserList.add(laser);
                }
            }

            // 更新最后攻击时间
            lastAttackTime = now;
        }
    }

