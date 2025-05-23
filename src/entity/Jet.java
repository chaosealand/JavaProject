package entity;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import scene.GameControl;
import utils.KeyProcesser;

public class Jet extends Entity{
    public static Image jetImage = new Image("/Image/PlayerJet.png");

    private float Vx = 4;
    private float Vy = 4;
    private float acceleration = 0;

    public Jet(Image image, double x, double y, double width, double height, GameControl GC) {
        super(image, x, y, width, height, GC);
    }

    public void move (){
        if (KeyProcesser.pressedKeys.contains(KeyCode.W)) y -= Vy;
        if (KeyProcesser.pressedKeys.contains(KeyCode.S)) y += Vy;
        if (KeyProcesser.pressedKeys.contains(KeyCode.A)) x -= Vx;
        if (KeyProcesser.pressedKeys.contains(KeyCode.D)) x += Vx;

    }

}
