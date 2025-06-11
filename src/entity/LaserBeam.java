package entity;


import Director.Director;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import scene.GameControl;
import utils.Team;


public class LaserBeam extends Entity{ // 雷射光束類，繼承 Entity

    private static final Color Alert = Color.YELLOW ; // 警告顏色（黃色）
    private static final Color Attack = Color.RED; // 攻擊顏色（紅色）
    private static final int LaserRemainFrame = 35; // 雷射持續幀數
    Entity e ;
    // 使用静态初始化块预加载图片，并添加错误处理
    private static Image reddot; // 紅點圖片
    static {
        try {
            reddot = new Image("Image/RedDot.png", true); // 使用后台加载
        } catch (Exception e) {
            System.err.println("无法加载LaserBeam红点图片: " + e.getMessage()); // 加載失敗輸出錯誤
            // 创建一个1x1像素的默认图片作为后备
        }
    }
    private Bullet bullet; // 內部生成的子彈
    private Team team ; // 隊伍
    public LaserBeam(double x, double y, GameControl GC , Team t , Entity e) { // 建構子
        super(x, y, GC); // 調用父類建構子
        team = t ; // 設定隊伍
        this.e = e ;
    }

    int AlertInterval = 120 ; // unit : frames // 警告階段持續幀數
    int counter = 0 ; // 幀計數
    double BeamDestinationX = 0 ; // 光束終點X
    double BeamDestinationY = 0 ; // 光束終點Y
    Line logicalLine ; // 邏輯線（判斷碰撞等用）
    Boolean Activated = false ; // 是否啟動

    @Override
    public void render() { // 渲染
        if (counter==0) {
            SetBorderIntersection(GC.Player.getCenterX(),GC.Player.getCenterY()); // 設定終點（延長至邊界）
            counter++;
        }
        if (counter<AlertInterval){
            GC.graphicsContext.setStroke(Alert); // 警告階段畫黃色

            GC.graphicsContext.setLineWidth(1); // 線寬1
            GC.graphicsContext.strokeLine(x,y,BeamDestinationX, BeamDestinationY); // 畫警告線

            counter++ ;
            x = e.x;
            y = e.y ;
        }
        else if (counter == AlertInterval){
            Activated = true  ; // 進入攻擊階段
            logicalLine = new Line(x,y,BeamDestinationX, BeamDestinationY); // 建立判斷用邏輯線
            logicalLine.setStrokeWidth(6); // 設定判斷寬度
            counter++;
            bullet = new Bullet(x,y,40,Math.atan2(BeamDestinationX-x,BeamDestinationY-y)-Math.PI/2,GC,team,reddot) ; // 生成雷射子彈（特殊角度/圖片）
        }
        else if (counter < AlertInterval+LaserRemainFrame) {
            GC.graphicsContext.setStroke(Attack); // 攻擊階段用紅色
            GC.graphicsContext.setLineWidth(4); // 線寬4

            int tmp = counter - AlertInterval; // 計算已經攻擊幾幀
            if (tmp <= LaserRemainFrame/2) {
                DrawByLinePercent(x,y,BeamDestinationX,BeamDestinationY,(double)2*tmp/LaserRemainFrame); // 前半段：逐步拉長
            }
            else {
                DrawByLinePercent(BeamDestinationX,BeamDestinationY,x,y,((double)2*(LaserRemainFrame-tmp))/LaserRemainFrame); // 後半段：逐步縮短
            }

            counter++;
        }
        else {
            Activated = false ; // 結束攻擊
            GC.LaserList.remove(this); // 從雷射列表移除
        }
    }

    private void DrawByLinePercent(double x1,double y1,double x2,double y2,double percent){ // 畫線段的部分百分比
        double dx =  x2-x1 ; // X差
        double dy =  y2-y1 ; // Y差
        GC.graphicsContext.strokeLine(x1,y1,x1+(percent*dx),y1+(percent*dy)); // 畫從起點到百分比位置
    }

    private void SetBorderIntersection (double TargetX,double  TargetY) { // 根據目標延長到畫面邊界
        double dx =  TargetX-x ; // X方向向量
        double dy =  TargetY-y ; // Y方向向量
        double LongestPossibleDiagonal = Math.sqrt(Director.HEIGHT*Director.HEIGHT+Director.WIDTH*Director.WIDTH); // 取得畫面最大對角長
        double TargetDistance = Math.sqrt(dx*dx+dy*dy); // 目標距離
        BeamDestinationX = x + dx * (LongestPossibleDiagonal /TargetDistance); // 計算終點X（超過螢幕到邊界）
        BeamDestinationY = y + dy * (LongestPossibleDiagonal /TargetDistance); // 終點Y
    }

    //碰撞檢測:LogicalLine

    @Override
    public Rectangle2D getContour() { // 取得碰撞框（覆寫自父類，可自訂）
        return super.getContour();
    }
}
