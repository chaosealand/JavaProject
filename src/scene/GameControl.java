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
    public List<Bullet> bullets1 = new ArrayList<>();
    public List<Bullet> bullets2 = new ArrayList<>();


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
    }

    public void RenderAll (){
        Player.move();
        background.render();
        for(Bullet b: bullets2){
            b.render();
        }
        for(Bullet b: bullets1){
            b.render();
        }
        Player.render();
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


