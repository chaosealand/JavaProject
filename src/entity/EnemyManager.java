package entity;

import Director.Director;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import scene.GameControl;
import utils.Team;

import java.util.Random;

/**
 * 敌人管理器 - 负责敌人的生成和管理
 */
public class EnemyManager {
    private GameControl gameControl;
    private Random random = new Random();
    private Timeline spawnTimeline;
    private int wave = 1;
    private int maxEnemiesOnScreen = 4;

    // 敌人类型枚举
    public enum EnemyType {
        BASIC,      // 基本敌人，直线移动，常规子弹
        STRIKER,    // 突击型敌人，快速移动，短程激光
        ELITE       // 精英敌人，复杂移动模式，多种攻击方式
    }

    public EnemyManager(GameControl gameControl) {
        this.gameControl = gameControl;
    }

    /**
     * 启动敌人生成系统
     */
    public void startSpawning() {
        // 停止现有的生成时间线（如果有）
        if (spawnTimeline != null) {
            spawnTimeline.stop();
        }

        // 创建新的时间线来定期生成敌人
        spawnTimeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> spawnEnemy()));
        spawnTimeline.setCycleCount(Timeline.INDEFINITE);
        spawnTimeline.play();
    }

    /**
     * 停止敌人生成
     */
    public void stopSpawning() {
        if (spawnTimeline != null) {
            spawnTimeline.stop();
        }
    }

    /**
     * 生成敌人
     */
    private void spawnEnemy() {
        // 如果屏幕上敌人数量已达上限，则不生成
        if (gameControl.enemies.size() >= maxEnemiesOnScreen) {
            System.out.println( gameControl.enemies.size() + " enemies on screen, not spawning new enemy.");
            return;
        }

        // 根据波数增加难度
        updateDifficulty();

        // 随机选择敌人类型
        EnemyType type = getRandomEnemyType();

        // 随机位置（屏幕顶部）
        double x = Director.WIDTH + 20 ;
        double y = random.nextDouble() * (Director.HEIGHT - Jet.EnemyHeight);

        // 基于类型生成敌人
        EnemyJet enemy;
        switch (type) {
            case STRIKER:
                enemy = new Spiker(x, y, gameControl);
                break;
            case ELITE:
                enemy = new EliteEnemy(x, y, gameControl);
                break;
            case BASIC:
            default:
                enemy = new BasicEnemy(x, y, gameControl);

                //System.out.println("spawned a basic enemy at " + x + ", " + y);
                break;
        }

        // 添加到游戏控制器的敌人列表
        gameControl.enemies.add(enemy);
    }

    /**
     * 获取随机敌人类型（基于当前波数和概率）
     */
    private EnemyType getRandomEnemyType() {
        int roll = random.nextInt(100);

        // 随着波数增加，出现高级敌人的概率增加
        if (wave >= 10 && roll < 15) {
            return EnemyType.ELITE;
        } else if (wave >= 3 && roll < 50) {
            return EnemyType.STRIKER;
        } else {
            return EnemyType.BASIC;
        }
    }

    /**
     * 基于游戏进行时间更新难度
     */
    private void updateDifficulty() {

        int newWave = 1 + (int)(gameControl.getGameTime() / 15);

        if (newWave > wave) {
            wave = newWave;
            // 每波增加最大敌人数量，但限制在12个
            maxEnemiesOnScreen = Math.min(12, 6 + wave / 2);
        }
    }

    /**
     * 清理资源
     */
    public void clear() {
        if (spawnTimeline != null) {
            spawnTimeline.stop();
            spawnTimeline = null;
        }
    }
}
