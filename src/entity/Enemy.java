package entity;

import javafx.scene.image.Image;
import scene.GameControl;
import utils.Team;

public class Enemy extends EntityRole{

    public static Image EnemyImage = new Image("/Image/PlayerJet.png");
    public static double EnemyWidth = 60 , EnemyHeight = 72 ;

    public Enemy( double x, double y, GameControl GC) {
        super(EnemyImage, x, y, EnemyWidth, EnemyHeight, GC, Team.enemy);
    }

    @Override
    public void render() {
        if(!alive){
            GC.enemys.remove(this);
            return;
        }
        super.render();
    }
}
