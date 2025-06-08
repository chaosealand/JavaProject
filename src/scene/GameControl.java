package scene;

import Animation.SceneTransition;
import Director.Director;
import entity.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import utils.FrameUpdater;
import utils.KeyProcessor;
import utils.MouseTracker;
import utils.Team;

import java.util.ArrayList;
import java.util.List;

public class GameControl { //主遊戲畫面

    Stage stage = new Stage();
    Canvas canvas = new Canvas(Director.WIDTH,Director.HEIGHT);
    public GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
    Background background ;
    FrameUpdater frameUpdater = new FrameUpdater();

    MouseTracker mouseTracker = new MouseTracker();
    KeyProcessor keyProcessor = new KeyProcessor();
    public Jet Player = null;

    public List<Bullet> bullets = new ArrayList<>();//子彈
    public List<Jet> enemies = new ArrayList<>();//建立複數敵人
    public List<LaserBeam> LaserList = new ArrayList<>();



    public Boolean GameRunning = false ;

    public void initialize (Stage stage) {
        this.stage = stage ;
        AnchorPane root = new AnchorPane(canvas);
        Player = new Jet(460,480,this, Team.friend);
        Player.render();
        //stage.getScene().setRoot(root);
        background = new Background(this);

        stage.getScene().setOnKeyPressed(keyProcessor);
        stage.getScene().setOnKeyReleased(keyProcessor);
        //讓 MouseTracker 正確地接收到 按下 和 放開 的事件，進而更新 leftPressed 狀態
        stage.getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, mouseTracker);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_RELEASED, mouseTracker);

        GameRunning = true ;
        frameUpdater.start();
        initEnemy();
        SceneTransition.SceneTransition(stage.getScene(),root,1);

    }

    public void RenderAll (){

        Player.move();
        background.render();

        for(int i = 0; i < bullets.size(); i++){   //渲染子彈
            Bullet b = bullets.get(i);
            b.render();
            b.ImpactCheck(enemies);
            b.ImpactCheck(Player);
        }
        for(int i = 0; i < enemies.size(); i++){
            Jet enemy = enemies.get(i);
            enemy.render();
            if (Player.isAlive() && enemy.isAlive() &&
                    Player.getContour().intersects(enemy.getContour())) {
                Player.setAlive(false);
                enemy.setAlive(false);
                System.out.println("Player collided with enemy!");
            }
        }

        Player.render();

        for (int i=0;i<LaserList.size();i++){
            LaserBeam L = LaserList.get(i);
            L.render();
        }
        if (!Player.isAlive() && Player.Exploded) {
            GameRunning = false;
            clear();
            // 使用 Platform.runLater 確保 JavaFX 主執行緒安全轉場
            javafx.application.Platform.runLater(() -> {
                Director.getInstance().ToGameOver(stage);
            });
            return;
        }
    }

    public void initEnemy(){//初始化敵人的位置
        for(int i = 0; i<6; i++){
            Jet enemy = new Jet(Jet.EnemyImage,500+i*200, 400, Jet.EnemyWidth,Jet.EnemyHeight,this,Team.enemy);
            enemies.add(enemy);
        }
    }

    public void clear () {
        stage.removeEventHandler(KeyEvent.KEY_PRESSED, keyProcessor);
        stage.removeEventHandler(KeyEvent.KEY_RELEASED, keyProcessor);
        KeyProcessor.pressedKeys.clear();
        frameUpdater.stop();
        Player = null ;
    }

    public void ToPauseOrNotToPause() {
        GameRunning = (GameRunning)?false:true;
    }

}


