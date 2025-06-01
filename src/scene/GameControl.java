package scene;

import Director.Director;
import entity.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import utils.FrameUpdater;
import utils.KeyProcessor;
import utils.Team;

import java.util.ArrayList;
import java.util.List;

public class GameControl { //主遊戲畫面

    Stage stage = new Stage();
    Canvas canvas = new Canvas(Director.WIDTH,Director.HEIGHT);
    public GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
    Background background ;
    FrameUpdater frameUpdater = new FrameUpdater();

    KeyProcessor keyProcessor = new KeyProcessor();
    public Jet Player = null;
    public List<Bullet> bullets = new ArrayList<>();//子彈
    public List<Enemy> enemys = new ArrayList<>();//建立複數敵人
     public List<LaserBeam> LaserList = new ArrayList<>();



    public Boolean GameRunning = false ;

    public void initialize (Stage stage) {
        this.stage = stage ;
        AnchorPane root = new AnchorPane(canvas);
        Player = new Jet(Jet.jetImage,460,480,120,144,this, Team.friend);
        Player.render();
        stage.getScene().setRoot(root);
        background = new Background(this);
        stage.getScene().setOnKeyPressed(keyProcessor);
        stage.getScene().setOnKeyReleased(keyProcessor);

        GameRunning = true ;
        frameUpdater.start();
        initEnemy();
    }

    public void RenderAll () {
        if (!GameRunning) return;
        Player.move();
        background.render();
        // 渲染子彈 + 碰撞
        for (int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            b.render();
            b.ImpactCheck(enemys);
        }
        // 渲染敵人 + 撞擊玩家
        for (int i = 0; i < enemys.size(); i++) {
            Enemy enemy = enemys.get(i);
            enemy.render();
            // 玩家與敵人碰撞
            if (Player.isAlive() && enemy.isAlive() &&
                    Player.getContour().intersects(enemy.getContour())) {
                Player.setAlive(false);
                enemy.setAlive(false);
                System.out.println("Player collided with enemy!");
            }
        }
        // 渲染玩家
        Player.render();
        // 渲染雷射
        for (int i = 0; i < LaserList.size(); i++) {
            LaserBeam L = LaserList.get(i);
            L.render();
        }
        // 玩家死亡轉場
        if (!Player.isAlive()) {
            GameRunning = false;
            // 使用 Platform.runLater 確保 JavaFX 主執行緒安全轉場
            javafx.application.Platform.runLater(() -> {
                Director.getInstance().ToGameOver(stage);
            });
            return;
        }
    }


    public void initEnemy(){//初始化敵人的位置
        for(int i = 0; i<6; i++){
            Enemy enemy = new Enemy(500+i*200, 400, this);
            enemys.add(enemy);
        }
    }

    public void clear () {
        stage.removeEventHandler(KeyEvent.KEY_PRESSED, keyProcessor);
        stage.removeEventHandler(KeyEvent.KEY_RELEASED, keyProcessor);
        frameUpdater.stop();
        Player = null ;
    }

    public void ToPauseOrNotToPause() {
        GameRunning = (GameRunning)?false:true;
    }

}


