package entity;

import javafx.scene.image.Image;
import scene.GameControl;
import utils.Team;

public class Enemy extends EntityRole{
    @Override
    public boolean ImpactCheck(Entity e){
        return false;
    }

    public Enemy(Image image, double x, double y, double width, double height, GameControl GC, Team team) {
        super(image, x, y, width, height, GC, team);
    }
    public static Image EnemyImage = new Image("/Image/PlayerJet.png");
    public static double EnemyWidth = 60 , EnemyHeight = 72 ;
}
