package entity;


import javafx.scene.canvas.GraphicsContext;
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
        }
    }

    public static Image bulletimage = new Image("/Image/Bullet.png" );
    public static double BulletWidth = 40, BulletHeight = 30;
    private double V = 8;

    public Bullet( double x, double y,  GameControl GC, Team team) {
        super(bulletimage, x, y, BulletWidth, BulletHeight, GC, team);
        AngleRadian = Math.PI/2 ;
    }
    public Bullet( double x, double y, double V , double Angle ,GameControl GC, Team team,Image image) {
        super(image, x, y, BulletWidth, BulletHeight, GC, team);
        AngleRadian = Angle ;
        this.V = V ;
        GC.bullets.add(this) ;
    }

    public void Bulletmove(){
        y -= V * Math.sin(AngleRadian);
        x += V * Math.cos(AngleRadian);
    }



}
