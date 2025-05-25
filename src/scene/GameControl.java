package scene;

import Director.Director;
import entity.Background;
import entity.Bullet;
import entity.Jet;
import entity.LaserBeam;
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
    Bullet bullet ;

    public Boolean GameRunning = false ;
    public List<LaserBeam> LaserList = new ArrayList<>();


    public void initialize (Stage stage) {
        this.stage = stage ;
        AnchorPane root = new AnchorPane(canvas);
        Player = new Jet(Jet.jetImage,460,480,60,72,this, Team.friend);
        Player.render();
        stage.getScene().setRoot(root);
        background = new Background(this);
        stage.getScene().setOnKeyPressed(keyProcessor);
        stage.getScene().setOnKeyReleased(keyProcessor);

        GameRunning = true ;
        frameUpdater.start();
    }

    public void RenderAll (){
        Player.move();
        background.render();
        Player.render();

        for (int i=0;i<LaserList.size();i++){
            LaserBeam L = LaserList.get(i);
            L.render();
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


