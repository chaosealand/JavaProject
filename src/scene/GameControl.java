package scene;

import Animation.SceneTransition;
import Director.Director;
import entity.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.*;

import java.util.ArrayList;
import java.util.List;

public class GameControl { // 主遊戲畫面控制

    Stage stage = new Stage(); // 遊戲視窗
    Canvas canvas = new Canvas(Director.WIDTH,Director.HEIGHT); // 遊戲繪圖區域
    public GraphicsContext graphicsContext = canvas.getGraphicsContext2D(); // 2D繪圖上下文
    Background background; // 遊戲背景
    FrameUpdater frameUpdater = new FrameUpdater(); // 畫面更新器

    MouseTracker mouseTracker = new MouseTracker(); // 滑鼠事件追蹤
    KeyProcessor keyProcessor = new KeyProcessor(); // 鍵盤事件處理器
    public Jet Player = null; // 玩家物件

    public List<Bullet> bullets = new ArrayList<>(); // 子彈列表
    public List<Jet> enemies = new ArrayList<>(); // 敵人列表
    public List<LaserBeam> LaserList = new ArrayList<>(); // 雷射光束列表
    public List<Explosion> ExplosionList = new ArrayList<>(); // 爆炸效果列表

    public AnchorPane root; // 場景根節點

    public Boolean GameRunning = false; // 遊戲運行狀態
    private boolean gameWinTriggered = false;

    // 添加敌人管理
    private EnemyManager enemyManager; // 敵人生成���管理
    private boolean bossSpawned = false;// 宣告 boss 是否已經生成過

    private long gameStartTime; // 游戏开始时间
    private int playerScore = 0; // 玩家分數

    public int killcount = 0 ;
    public int outofboundkill = 0 ;
    // EnemyManager.java
    public void spawnBoss() {
        BOSS boss = new BOSS(1500, 100, this);
        this.enemies.add(boss);
    }
    public void initialize (Stage stage) { // 遊戲初始化
        this.stage = stage;
        this.background = new Background(this);  // 設定背景（傳入自身作為參照）
        this.root = new AnchorPane(); // 建立根節���

        this.root.getChildren().add(background.mediaView); // 加入背景到畫面
        this.root.getChildren().add(canvas); // 加入畫布

        Player = new Jet(460,480,this, Team.friend); // 生成玩家物件
        Player.render(); // 渲染玩家

        // 初始化敌人管理器
        this.enemyManager = new EnemyManager(this);
        // 记录游戏开始时间
        this.gameStartTime = System.currentTimeMillis();

        stage.getScene().setOnKeyPressed(keyProcessor); // 鍵盤按下事件
        stage.getScene().setOnKeyReleased(keyProcessor); // 鍵盤放開事件
        //讓 MouseTracker 正確地接收到 按下 和 放開 的事件，進而更新 leftPressed 狀態
        stage.getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, mouseTracker); // 滑鼠按下事件
        stage.getScene().addEventHandler(MouseEvent.MOUSE_RELEASED, mouseTracker); // 滑鼠放開事件

        GameRunning = true; // 遊戲開始
        frameUpdater.start(); // 啟動畫面刷新

        enemyManager.startSpawning(); // 啟動敵人產生
        SceneTransition.SceneTransition(stage.getScene(),root,1); // 場景轉換特效
        background.PlayMedia(); // 播放背景音樂/影片
    }

    public void RenderAll() { // 遊戲主渲染流程
        if (!GameRunning) return; // 非運行狀態不渲染

        // 清除整个画布，防止出现拖尾效果
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // 清空畫布
        Player.move(); // 移動玩家

        // 渲染玩家分数
        graphicsContext.setFont(new javafx.scene.text.Font("Consolas", 36));// 設定字體大小為36
        graphicsContext.setFill(javafx.scene.paint.Color.WHITE); // 設定字體顏色
        graphicsContext.fillText("分数: " + playerScore, 60, 90); // 顯示分數
        if (playerScore >=1000&& !bossSpawned) {
            spawnBoss();
            bossSpawned = true;
        }


        for(int i = 0; i < bullets.size(); i++) {   // 渲染子彈
            Bullet b = bullets.get(i);
            if (b.isAlive()) {
                b.render();
                b.ImpactCheck(enemies); // 檢查是否打中敵人
                b.ImpactCheck(Player); // 檢查是否打中玩家
            }
        }

        for(int i = 0; i < enemies.size(); i++) { // 渲染敵人
            Jet enemy = enemies.get(i);
            if (enemy.isAlive()) {
                enemy.render();
                // 檢查與玩家碰撞
                if (Player.isAlive() && !Player.undefeatable && Player.getContour().intersects(enemy.getContour())) {
                    Player.setAlive(false); // 玩家死亡
                    enemy.setAlive(false); // 敵人死亡

                }
            } else {

                enemy.render(); // 🔧 確保爆炸動畫能播放！！

                // ======= Boss 死亡即勝利判斷 =======
                if (enemy instanceof BOSS && !gameWinTriggered) {
                    // 如果是 BOSS 且未觸發勝利，則觸發勝利
                    gameWinTriggered = true;
                    GameRunning = false;
                    StatBoard.addSurvivedTime(getGameTime());
                    StatBoard.addEnemyTakeDown(killcount- outofboundkill); // 增加擊殺數
                    Director.getInstance().ToGameWin(stage);



                } // 使用 Platform.runLater 確保 JavaFX 主執行緒安全轉場
                // ==================================

                // 如果敌人死亡，增加分数并移除
                if (enemy instanceof EnemyJet) {
                    EnemyJet ej = (EnemyJet) enemy;
                    if (!ej.isScoreGiven()) {
                        playerScore += ej.getScoreValue();
                        killcount += 1;
                        ej.markScoreGiven(); // ✅ 標記分數已加過
                    }
                }
                if(enemy.Exploded){
                    enemies.remove(i); // 刪除敵人
                    i--; // 保證下標正確
                }



            }

        }


        Player.render(); // 再次渲染玩家（防止被其他物件遮蓋）

        for (int i=0;i<LaserList.size();i++){ // 渲染雷射光束
            LaserBeam L = LaserList.get(i);
            L.render();
        }
        for (int i=0;i<ExplosionList.size();i++){ // 渲染雷射光束
            Explosion E = ExplosionList.get(i);
            E.render();
        }


        // 渲染护盾效果（如果激活的话）
        utils.ShieldVisual.renderShield();

        if (!Player.isAlive() && Player.Exploded) { // 玩家死亡且爆炸動畫完成
            GameRunning = false; // 結束遊戲
            StatBoard.addSurvivedTime(getGameTime());
            StatBoard.addEnemyTakeDown(killcount- outofboundkill); // 增加擊殺數
            // 使用 Platform.runLater 確保 JavaFX 主執行緒安全轉場
            javafx.application.Platform.runLater(() -> {
                Director.getInstance().ToGameOver(stage); // 轉到遊戲結束畫面
            });
        }
    }

    // 获取游戏运行时间（秒）
    public int getGameTime() {
        return (int)((System.currentTimeMillis() - gameStartTime) / 1000); // 回傳經過秒數
    }

    // 获取玩家分数
    public int getPlayerScore() {
        return playerScore; // 回傳分數
    }


    public void clear() { // 遊戲重設與資源回收
        stage.removeEventHandler(KeyEvent.KEY_PRESSED, keyProcessor); // 移除鍵盤事件
        stage.removeEventHandler(KeyEvent.KEY_RELEASED, keyProcessor);
        KeyProcessor.pressedKeys.clear(); // 清除按鍵緩存
        frameUpdater.stop(); // 停止畫面刷新

        // 停止敌人生成
        if (enemyManager != null) {
            enemyManager.stopSpawning();
        }

        // 停止媒体播放器，释放资源
        if (background != null && background.mediaPlayer != null) {
            background.mediaPlayer.stop();
            background.mediaPlayer.dispose();
            System.out.println("cleared media player resources."); // 印出資源釋放訊息
        }

        Player = null; // 玩家設為空（釋放）
    }

    public void ToPauseOrNotToPause() { // 暫停與恢復遊戲
        if (!Player.isAlive()) {
            return; // 如果玩家已經死亡，則不允許暫停遊戲
        }
        GameRunning = !GameRunning; // 反轉運行狀態

        if (GameRunning) {
            background.PlayMedia(); // 播放背景
            enemyManager.startSpawning(); // 開始產生敵人
        } else {
            if (background.mediaPlayer != null) {
                background.mediaPlayer.pause(); // 暫停背景
            }
            enemyManager.stopSpawning(); // 停止敵人產生
        }
    }
}
