package entity;

import javafx.scene.image.Image;
import Director.Director;
import scene.GameControl;


//changes
public class Background extends Entity{
    public static Image black = new Image("/Image/Black.jpg");
    public Background(Image image, GameControl gc) {
        super (image,0,0,Director.WIDTH,Director.HEIGHT,gc);
    }

    public Background(GameControl gc) {
        super (black,0,0,Director.WIDTH,Director.HEIGHT,gc);
    }
}
