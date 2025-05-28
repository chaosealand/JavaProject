package entity;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import scene.GameControl;
import utils.Team;

import java.util.List;

public class Bullet extends EntityRole{

    public boolean ImpactCheck(Jet jet){
        if(jet!= null && getContour().intersects(jet.getContour())&&this.team!=jet.team){

            jet.setAlive(false);
            alive = false;
            return true;

        }
        return false;
    }
    public void ImpactCheck(List<Jet> enemys){
        for(Jet E: enemys){
            ImpactCheck(E);
        }
    }


    @Override
    public void render() {
        super.render();
        Bulletmove();
        if(!alive){
            GC.bullets.remove(this);
            return;
        }
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
