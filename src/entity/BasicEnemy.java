package entity;

import Director.Director;
import javafx.scene.image.Image;
import scene.GameControl;
import utils.Team;

/**
 * 基础敌人 - 简单的直线移动和单发攻击
 */
public class BasicEnemy extends EnemyJet { // 定義基礎敵人類別，繼承 EnemyJet

    private static final Image enemyImage = new Image("Image/Enemy1.png"); // 替換為實際圖片路徑

    public BasicEnemy(double x, double y, GameControl gc) { // 建構子，指定初始座標與遊戲控制器
        super(x, y, gc); // 調用父類別建構子
        // 基本敵人參數設定
        this.movementSpeed = 1.3; // 設定移動速度
        this.attackCooldown = 1000; // 2秒發射一次
        this.health = 50; // 設定血量
        this.scoreValue = 100; // 擊破得分
        this.damage = 10; // 傷害值
    }

    @Override
    protected void updateMovement() { // 移動更新邏輯
        // 如果正在執行閃避動作，跳過常規移動
        if (isDodging) return;

        // 基本敵人垂直向下移動
        this.Vy = 0; // 垂直速度設為0
        if (x < Director.WIDTH * 0.4) {
            this.Vx = - (float) (movementSpeed * 1.5); // 向右移動（速度較快，因為Vx是負值時往左但這邏輯可能你反了，可自行驗證）
        } else {
            this.Vx = - (float) (movementSpeed ); // 向左移動
        }

        // 如果飛出螢幕底部，設為死亡
        if (x < 0) { // 超出畫面左界
            GC.outofboundkill += 1; // 增加出界死亡計數

            setAlive(false); // 設為死亡
        }
    }

    @Override
    protected void attack() { // 攻擊邏輯
        long now = System.currentTimeMillis(); // 取得目前系統時間
        // 檢查攻擊冷卻時間
        if (now - lastAttackTime > attackCooldown) {
            // 發射單發子彈
            double centerX = getCenterX(); // 取得敵人中心點X座標
            double centerY = getCenterY() - 15; // 取得敵人中心點Y座標並略微往上
            double targetx = GC.Player.getCenterX(); // 取得玩家中心點X座標
            double targety = GC.Player.getCenterY(); // 取得玩家中心點Y座標
            double angle = Math.atan2(targety - centerY, targetx - centerX); // 算出指向玩家的角度

            double offsetangle =  angle + (Math.PI / 3 ) * random.nextDouble() - Math.PI/6; // 調整角度讓子彈有隨機散射

            if (x > GC.Player.getCenterX()) {
                GC.bullets.add(new Bullet(centerX, centerY, -offsetangle, 4, GC, Team.enemy)); // 新增敵方子彈到子彈列表
            }

            //GC.bullets.add(new SpikeBullet(centerX, centerY, -offsetangle ,0.5,GC, Team.enemy)); //（預留）可改成發射SpikeBullet

            // 更新最後攻擊時間
            lastAttackTime = now;
        }
    }
}