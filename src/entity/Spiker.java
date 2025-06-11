package entity;

import Director.Director;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import scene.GameControl;
import utils.Team;

import java.time.Duration;
import java.time.Instant;

/**
 * Spiker敌人 - 具有三种状态的高级敌人
 * 状态1: 悬停并发射普通子弹，持续10秒
 * 状态2: 悬停并发射尖刺子弹，持续15秒
 * 状态3: 发出警示后向玩家冲刺，执行完毕后死亡
 */
public class Spiker extends EnemyJet {
    // 状态常量
    private static final int STATE_NORMAL_SHOOT = 0;    // 普通射击状态
    private static final int STATE_SPIKE_SHOOT = 1;     // 尖刺射击状态
    private static final int STATE_RUSH = 2;            // 冲刺状态

    // Spiker特有的属性
    private int currentState = STATE_NORMAL_SHOOT; // 初始状态
    private Instant stateStartTime; // 当前状态开始时间

    // 状态持续时间（毫秒）
    private static final long STATE_1_DURATION = 10000; // 10秒
    private static final long STATE_2_DURATION = 15000; // 15秒
    private static final long WARNING_DURATION = 2000;  // 2秒警示时间

    // 移动相关属性
    private double targetX;     // 目标X位置
    private double targetY;     // 目标Y位置
    private boolean isWarning = false; // 是否处于警示阶段
    private Instant warningStartTime;  // 警示开始时间

    // 攻击相关属性
    private int consecutiveShots = 0;  // 连续射击计数
    private static final int MAX_CONSECUTIVE_SHOTS = 2; // 每次攻击发射子弹数
    private static final double RUSH_SPEED = 8.0;       // 冲刺速度

    // 警示线属性
    private boolean drawWarningLine = false;
    private double targetPlayerX;
    private double targetPlayerY;

    // 构造函数
    public Spiker(double x, double y, GameControl gc) {
        super(x, y, gc);

        // 设置基本属性
        this.health = 250;                // 更高的生命值
        this.scoreValue = 300;            // 击败获得更多分数
        this.movementSpeed = 1.5;         // 基本移动速度
        this.attackCooldown = 6000;       // 攻击冷却与基本敌人相同

        // 初始化状态和时间
        stateStartTime = Instant.now();

        // 初始化悬停位置
        generateHoverPosition();
    }

    /**
     * 生成初始悬停位置
     */
    private void generateHoverPosition() {
        targetX = 0.8 * Director.WIDTH - random.nextDouble() * 100; // 在屏幕右侧区域随机选择位置
        targetY = random.nextDouble() * (Director.HEIGHT - height); // 随机Y位置
    }

    @Override
    protected void updateMovement() {
        // 检查状态是否需要转换
        checkStateTransition();

        switch (currentState) {
            case STATE_NORMAL_SHOOT:
            case STATE_SPIKE_SHOOT:
                // 状态1和2: 移动到目标悬停位置
                updateHoverMovement();
                break;

            case STATE_RUSH:
                if (!isWarning) {
                    // 进入警示阶段
                    if (GC.Player != null && GC.Player.isAlive()) {
                        // 设置警示目标为玩家位置
                        targetPlayerX = GC.Player.getCenterX();
                        targetPlayerY = GC.Player.getCenterY();
                        drawWarningLine = true;
                        isWarning = true;
                        warningStartTime = Instant.now();
                    } else {
                        // 如果玩家死亡，跳过警示阶段
                        startRush();
                    }
                } else {
                    // 检查警示是否结束
                    if (Duration.between(warningStartTime, Instant.now()).toMillis() >= WARNING_DURATION) {
                        // 警示结束，开始冲刺
                        startRush();
                    }
                }
                break;
        }
    }

    /**
     * 检查状态是否应该切换
     */
    private void checkStateTransition() {
        long stateDuration = Duration.between(stateStartTime, Instant.now()).toMillis();

        switch (currentState) {
            case STATE_NORMAL_SHOOT:
                if (stateDuration >= STATE_1_DURATION) {
                    changeState(STATE_SPIKE_SHOOT);
                }
                break;
            case STATE_SPIKE_SHOOT:
                if (stateDuration >= STATE_2_DURATION) {
                    changeState(STATE_RUSH);
                }
                break;
        }
    }

    /**
     * 更新悬停移动
     */
    private void updateHoverMovement() {
        // 向目标位置平滑移动
        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 5) {
            // 还没到达目标位置，继续移动
            Vx = (float) (dx / distance * movementSpeed);
            Vy = (float) (dy / distance * movementSpeed);
        } else {
            // 到达目标位置，可能生成一个新的目标位置
            if (random.nextInt(100) < 5) { // 5%几率改变目标位置
                generateHoverPosition();
            } else {
                Vx = 0;
                Vy = 0;
            }
        }
    }

    /**
     * 开始冲刺阶段
     */
    private void startRush() {
        // 清除警示状态
        drawWarningLine = false;
        isWarning = false;

        // 如果有设定目标，就朝目标方向冲刺
        double dx = targetPlayerX - getCenterX();
        double dy = targetPlayerY - getCenterY();
        double length = Math.sqrt(dx * dx + dy * dy);

        // 标准化并设置冲刺速度
        if (length > 0) {
            Vx = (float) (dx / length * RUSH_SPEED);
            Vy = (float) (dy / length * RUSH_SPEED);
        } else {
            // 如果没有目标，随机方向冲刺
            double angle = random.nextDouble() * 2 * Math.PI;
            Vx = (float) (Math.cos(angle) * RUSH_SPEED);
            Vy = (float) (Math.sin(angle) * RUSH_SPEED);
        }
    }

    /**
     * 改变Spiker的状态
     */
    private void changeState(int newState) {
        currentState = newState;
        stateStartTime = Instant.now();

        // 特殊状态处理
        if (currentState == STATE_RUSH) {
            isWarning = false;
        } else if (currentState == STATE_NORMAL_SHOOT || currentState == STATE_SPIKE_SHOOT) {
            // 重新生成悬停位置
            generateHoverPosition();
        }
    }

    @Override
    protected void attack() {
        // 只有在悬停状态且冷却完成时才攻击
        if ((currentState == STATE_NORMAL_SHOOT || currentState == STATE_SPIKE_SHOOT) &&
            System.currentTimeMillis() - lastAttackTime >= attackCooldown) {

            // 检查是否有玩家存活
            if (GC.Player != null && GC.Player.isAlive()) {
                // 计算到玩家的方向
                double dx = GC.Player.getCenterX() - getCenterX();
                double dy = GC.Player.getCenterY() - getCenterY();
                double angle = Math.atan2(-dy, dx); // 注意y轴方向与常规坐标系相反

                // 根据当前状态选择子弹类型
                if (currentState == STATE_NORMAL_SHOOT) {
                    // 发射普通子弹
                    Timeline timeline = new Timeline(
                        new KeyFrame(javafx.util.Duration.millis(200), e -> {
                                double randomangle = angle + (Math.PI / 3 ) * random.nextDouble() - Math.PI/6;
                                new Bullet(getCenterX(), getCenterY(), randomangle, GC, team);
                        })
                    );
                    timeline.setCycleCount(2);
                    timeline.play();

                } else {
                    // 发射尖刺子弹
                    double randomangle = angle + (Math.PI / 3 ) * random.nextDouble() - Math.PI/6;
                    new SpikeBullet(getCenterX(), getCenterY(), randomangle, 1.0, GC, team);

                    if (random.nextBoolean()) GC.LaserList.add(new LaserBeam(getCenterX()+10, getCenterY()+10,  GC, team,this));

                }
                lastAttackTime = System.currentTimeMillis(); // 更新上次攻击时间
                // 更新连续射击计数

            }
        }
    }

    @Override
    public void render() {
        // 检查Spiker是否在冲刺状态下到达边界
        if (currentState == STATE_RUSH ) {
            // 检查是否超出屏幕边界
            if (x <= 50  || x > Director.WIDTH || y < -height || y > Director.HEIGHT) {
                // 冲刺状态下达到边界，直接自毁
                setAlive(false);
                GC.outofboundkill += 1 ;

                drawWarningLine = false;
                return;
            }
        }

        // 渲染警示线
        if (drawWarningLine && isAlive()) {
            // 绘制警示线，从Spiker指向目标位置
            GC.graphicsContext.setStroke(Color.YELLOW);
            GC.graphicsContext.setLineWidth(3);
            double dx = targetPlayerX - getCenterX();
            double dy = targetPlayerY - getCenterY();
            double targetx =  getCenterX() + 100 * dx ;
            double targetY =  getCenterY() + 100 * dy ;
            GC.graphicsContext.strokeLine(getCenterX(), getCenterY(), targetx, targetY);
        }

        // 渲染飞机
        super.render();
    }
}
