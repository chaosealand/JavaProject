package entity;


import javafx.scene.image.Image;
import scene.GameControl;
import utils.Team;

import java.util.List;

public class Bullet extends EntityRole{

    private double AngleRadian = 0 ;
    public boolean ImpactCheck(Jet jet){
        if(jet!= null && getContour().intersects(jet.getContour())&&this.team!=jet.team){

            jet.setAlive(false);
            alive = false;
            return true;

        }
        return false;
    }
    public void ImpactCheck(List<Jet> enemies){
        for(Jet E: enemies){
            ImpactCheck(E);
        }
    }

    public Bullet( double x, double y,double AngleRad,double Velocity,  GameControl GC, Team team) {
        super(bulletimage, x, y, BulletWidth, BulletHeight, GC, team);
        this.AngleRadian = AngleRad ;
        this.Velocity = Velocity ;
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

    private double Velocity = 8;

    public Bullet( double x, double y,  GameControl GC, Team team) {
        super(bulletimage, x, y, BulletWidth, BulletHeight, GC, team);
        AngleRadian = 0 ;
        Velocity = 14 ;
    }
    public void Bulletmove(){
        y -= Velocity * Math.cos(AngleRadian);
        x += Velocity * Math.sin(AngleRadian);
    }
}
