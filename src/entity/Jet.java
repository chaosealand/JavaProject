package entity;

import Director.Director;


import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import scene.GameControl;
import utils.KeyProcessor;
import utils.MouseTracker;
import utils.Team;



public class Jet extends EntityRole{


    public static Image EnemyImage = new Image("/Image/PlayerJet.png");
    public static Image jetImage = new Image("/Image/JetImage.png");
    public static Image JetImageLeft = new Image("/Image/JetImageLeft.png");
    public static Image JetImageRight = new Image("/Image/JetImageRight.png");

    public static double PlayerWidth = 120 , PlayerHeight = 144 ;
    public static double EnemyWidth = 60 , EnemyHeight = 72 ;

    private final static float SpeedLimit = 5 ;
    private final static float Acc = 0.6F ; //飛機加速度，影響操控性
    private final static float Resistance = 0.01F; //阻力係數，影響飛機靜止到停止時間

    private long lastFireTime = 0;
    private final long fireCooldown = 200;  //單位為毫秒

    private float Vx = 0;
    private float Vy = 0;
    private float Ax = 0.1F;
    private float Ay = 0.1F;

    public Jet(Image image, double x, double y, double width, double height, GameControl GC, Team team) {
        super(image, x, y, width, height, GC, team);
    }

    @Override
    public void render() {
        if(!alive && team == Team.enemy){
            GC.enemies.remove(this);
            return;
        }
        else if ( !alive && team == Team.friend) {
            //遊戲結束，玩家死亡
            System.out.println("Player Died");
        }
        super.render();
    }

    @Override
    public Rectangle2D getContour() {
        return new Rectangle2D(x+0.2*width, y, width, height);
    }

    public void move () {
        if (team == Team.friend) {
            PlayerControl();
            BorderCheck();
        }


    }

    public void Fire(){
        double Bullety = y+height/2;
        double Bulletx = x+width/2;
        Bullet bullet1 = new Bullet(Bulletx, Bullety, GC, team);
        GC.bullets.add(bullet1);
        Bullet bullet2 = new Bullet(Bulletx-40, Bullety, GC, team);
        GC.bullets.add(bullet2);
    }

    public void ToCursor (){
        x = MouseTracker.CursorX;
        y = MouseTracker.CursorY;
    }

    private void PlayerControl () {
        Ax = 0 ;
        Ay = 0 ;

        image = new Image("/Image/JetImage.png");
        if (KeyProcessor.pressedKeys.contains(KeyCode.W)) Ay -= Jet.Acc;
        if (KeyProcessor.pressedKeys.contains(KeyCode.S)) Ay += Jet.Acc;
        if (KeyProcessor.pressedKeys.contains(KeyCode.A)) Ax -= Jet.Acc;
        if (KeyProcessor.pressedKeys.contains(KeyCode.D)) Ax += Jet.Acc;
        if (MouseTracker.leftPressed) {
            long now = System.currentTimeMillis();
            if (now - lastFireTime > fireCooldown) {
                Fire();
                lastFireTime = now;
            }
        }

        if (Vx>3) image = JetImageRight;
        if (Vx<-3) image = JetImageLeft;

        if (Ax==0 && Vx!=0) Ax = - (Vx*Jet.Resistance);
        if (Ay==0 && Vy!=0) Ay = - (Vy*Jet.Resistance);

        if ((Vx+Ax)>=Jet.SpeedLimit) Vx = Jet.SpeedLimit;
        else if ((Vx+Ax)<=-Jet.SpeedLimit) Vx = -Jet.SpeedLimit;
        else Vx += Ax ;

        if ((Vy+Ay)>=Jet.SpeedLimit) Vy = Jet.SpeedLimit;
        else if ((Vy+Ay)<=-Jet.SpeedLimit) Vy = -Jet.SpeedLimit;
        else Vy += Ay ;
    }

    private void BorderCheck (){ //邊界檢測
        if(x<-0.5*width) x = -0.5*width; //左邊界
        if(x>=-0.5*width && x<=Director.WIDTH-width+0.5*width) x+=Vx;
        if(y<-0.5*height) y = -0.5*height;//上邊界
        if(y>=-0.5*height && y<=Director.HEIGHT-height+0.5*height) y+=Vy;
        if(x>Director.WIDTH-width+0.5*width) x = Director.WIDTH-width+0.5*width;//右邊界
        if(y>Director.HEIGHT-height+0.5*height) y = Director.HEIGHT-height+0.5*height;//下邊界
    }


}
