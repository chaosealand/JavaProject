package scene;

import Director.Director;
import entity.Background;
import entity.Bullet;
import entity.Enemy;
import entity.Jet;
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
    KeyProcessor keyProcesser = new KeyProcessor();
    Jet Player = null;
    public List<Bullet> bullets = new ArrayList<>();//子彈
    public List<Enemy> enemys = new ArrayList<>();//建立複數敵人


    public Boolean GameRunning = false ;

    public void initialize (Stage stage) {
        this.stage = stage ;
        AnchorPane root = new AnchorPane(canvas);
        Player = new Jet(Jet.jetImage,460,480,120,144,this, Team.friend);
        Player.render();
        stage.getScene().setRoot(root);
        background = new Background(this);
        stage.getScene().setOnKeyPressed(keyProcesser);
        stage.getScene().setOnKeyReleased(keyProcesser);
        GameRunning = true ;
        frameUpdater.start();
        initEnemy();
    }

    public void RenderAll (){
        Player.move();
        background.render();
        for(int i = 0; i < bullets.size(); i++){   //渲染子彈
            Bullet b = bullets.get(i);
            b.render();
            b.ImpactCheck(enemys);
        }
        for(int i = 0; i < enemys.size(); i++){
            Enemy enemy = enemys.get(i);
            enemy.render();
        }
        Player.render();
    }

    public void initEnemy(){//初始化敵人的位置
        for(int i = 0; i<6; i++){
            Enemy enemy = new Enemy(500+i*200, 400, this);
            enemys.add(enemy);
        }
    }

    public void clear () {
        stage.removeEventHandler(KeyEvent.KEY_PRESSED,keyProcesser);
        stage.removeEventHandler(KeyEvent.KEY_RELEASED,keyProcesser);
        frameUpdater.stop();
        Player = null ;
    }

    public void ToPauseOrNotToPause() {
        GameRunning = (GameRunning)?false:true;
    }

}


