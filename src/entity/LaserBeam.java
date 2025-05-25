package entity;


import Director.Director;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import scene.GameControl;



public class LaserBeam extends Entity{

    private static final Color Alert = Color.YELLOW ;
    private static final Color Attack = Color.RED;
    private static final int LaserRemainFrame = 35;

    public LaserBeam(double x, double y, GameControl GC) {
        super(x, y, GC);
    }

    int AlertInterval = 45 ; // unit : frames
    int counter = 0 ;
    double BeamDestinationX = 0 ;
    double BeamDestinationY = 0 ;
    Line logicalLine ;
    Boolean Activated = false ;






    @Override
    public void render() {
        if (counter==0) {
            SetBorderIntersection(GC.Player.getCenterX(),GC.Player.getCenterY());
            counter++;
        }
        if (counter<AlertInterval){
            GC.graphicsContext.setStroke(Alert);

            GC.graphicsContext.setLineWidth(1);
            GC.graphicsContext.strokeLine(x,y,BeamDestinationX, BeamDestinationY);

            counter++ ;
        }
        else if (counter == AlertInterval){
            Activated = true  ;
            logicalLine = new Line(x,y,BeamDestinationX, BeamDestinationY);
            logicalLine.setStrokeWidth(6);
            counter++;
        }
        else if (counter < AlertInterval+LaserRemainFrame) {
            GC.graphicsContext.setStroke(Attack);
            GC.graphicsContext.setLineWidth(4);
            int tmp = counter - AlertInterval;
            if (tmp <= LaserRemainFrame/2) {
                DrawByLinePercent(x,y,BeamDestinationX,BeamDestinationY,(double)2*tmp/LaserRemainFrame);
            }
            else {

                DrawByLinePercent(BeamDestinationX,BeamDestinationY,x,y,((double)2*(LaserRemainFrame-tmp))/LaserRemainFrame);
            }

            counter++;
        }
        else {
            Activated = false ;
            GC.LaserList.remove(this);
        }
    }
    private void DrawByLinePercent(double x1,double y1,double x2,double y2,double percent){
        double dx =  x2-x1 ;
        double dy =  y2-y1 ;
        GC.graphicsContext.strokeLine(x1,y1,x1+(percent*dx),y1+(percent*dy));
    }

    private void SetBorderIntersection (double TargetX,double  TargetY) {
        double dx =  TargetX-x ;
        double dy =  TargetY-y ;
        double LongestPossibleDiagonal = Math.sqrt(Director.HEIGHT*Director.HEIGHT+Director.WIDTH*Director.WIDTH);
        double TargetDistance = Math.sqrt(dx*dx+dy*dy);
        BeamDestinationX = x + dx * (LongestPossibleDiagonal /TargetDistance);
        BeamDestinationY = y + dy * (LongestPossibleDiagonal /TargetDistance);

    }

    //碰撞檢測:LogicalLine
}

