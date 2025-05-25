package entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import scene.GameControl;
import utils.Team;

public class Bullet extends EntityRole{
    @Override
    public boolean ImpactCheck(Entity e){
        return false;
    }

    @Override
    public void render() {
        super.render();
        Bulletmove();
    }

    public static Image bulletimage = new Image("/Image/PlayerJet.png" );
    public static double BulletWidth = 40, BulletHeight = 30;
    private float V = 8;

    public Bullet( double x, double y,  GameControl GC, Team team) {
        super(bulletimage, x, y, BulletWidth, BulletHeight, GC, team);
    }
    public void Bulletmove(){
        y-=V;
    }

}
