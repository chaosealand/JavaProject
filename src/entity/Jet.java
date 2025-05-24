package entity;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import scene.GameControl;
import utils.KeyProcessor;
import utils.MouseTracker;

public class Jet extends Entity{

    public static Image jetImage = new Image("/Image/PlayerJet.png");
    public static double PlayerWidth = 60 , PlayerHeight = 72 ;

    private final static float SpeedLimit = 5 ;
    private final static float Acc = 0.6F ; //飛機加速度，影響操控性
    private final static float Resistance = 0.01F; //阻力係數，影響飛機靜止到停止時間



    private float Vx = 0;
    private float Vy = 0;
    private float Ax = 0.1F;
    private float Ay = 0.1F;

    public Jet(Image image, double x, double y, double width, double height, GameControl GC) {
        super(image, x, y, width, height, GC);
    }

    public void move (){
        Ax = 0 ;
        Ay = 0 ;
        if (KeyProcessor.pressedKeys.contains(KeyCode.W)) Ay -= Jet.Acc;
        if (KeyProcessor.pressedKeys.contains(KeyCode.S)) Ay += Jet.Acc;
        if (KeyProcessor.pressedKeys.contains(KeyCode.A)) Ax -= Jet.Acc;
        if (KeyProcessor.pressedKeys.contains(KeyCode.D)) Ax += Jet.Acc;

        if (Ax==0 && Vx!=0) Ax = - (Vx*Jet.Resistance);
        if (Ay==0 && Vy!=0) Ay = - (Vy*Jet.Resistance);

        if ((Vx+Ax)>=Jet.SpeedLimit) Vx = Jet.SpeedLimit;
        else if ((Vx+Ax)<=-Jet.SpeedLimit) Vx = -Jet.SpeedLimit;
        else Vx += Ax ;

        if ((Vy+Ay)>=Jet.SpeedLimit) Vy = Jet.SpeedLimit;
        else if ((Vy+Ay)<=-Jet.SpeedLimit) Vy = -Jet.SpeedLimit;
        else Vy += Ay ;

        x += Vx ;
        y += Vy ;

    }
    public void ToCursor (){
        x = MouseTracker.CursorX;
        y = MouseTracker.CursorY;
    }



}
