package entity;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import scene.GameControl;
import utils.Team;

import java.util.List;

public class Bullet extends EntityRole{ // 子彈類別，繼承 EntityRole

    public double AngleRadian = 0 ; // 子彈方向，弧度制

    public boolean ImpactCheck(Jet jet){ // 檢查與玩家/敵機碰撞
        if(jet != null && getContour().intersects(jet.getContour()) && this.team != jet.team && !jet.undefeatable){
            if (jet instanceof EnemyJet) {
                ((EnemyJet) jet).takeDamage(10); // 這裡 10 是子彈傷害，可以根據需要調整
            } else {
                jet.setAlive(false); // 例如玩家被敵人打到，直接死
            }
            alive = false; // 子彈消失
            return true;
        }
        return false;
    }


    public void ImpactCheck(List<Jet> enemys){ // 批次檢查多台飛機碰撞
        for(Jet E: enemys){
            ImpactCheck(E); // 檢查每台
        }
    }

    @Override
    public void render() { // 重寫繪製方法，支援旋轉
        // 重写渲染方法以应用角度旋转
        GraphicsContext gc = GC.graphicsContext; // 取得繪圖上下文

        // 保存当前图形上下文的状态
        gc.save(); // 保存狀態，方便恢復

        // 设置旋转中心点为子弹的中心位置
        double centerX = x + width / 2; // 中心X
        double centerY = y + height / 2; // 中心Y

        // 移动到旋转中心点
        gc.translate(centerX, centerY); // 原點平移到中心

        // 将弧度转换为角度，并旋转图像
        // AngleRadian是与x轴正方向的夹角，需要转换为度数
        double angleDegrees = Math.toDegrees(AngleRadian); // 角度制
        gc.rotate(90 - angleDegrees); // 旋轉（朝上為0度）

        // 绘制图像，需要调整位置，使其中心点与原中心点对齐
        gc.drawImage(image, -width / 2, -height / 2, width, height); // 圖片繪製

        // 恢复图形上下文的原始状态
        gc.restore(); // 恢復狀態

        // 移动子弹
        Bulletmove(); // 子彈移動

        // 检查子弹状态
        if(!alive){
            GC.bullets.remove(this); // 死亡即移除
        }
    }

    public static Image bulletimage = new Image("/Image/Bullet.png" ); // 預設子彈圖
    public static double BulletWidth = 15, BulletHeight = 42; // 子彈寬高
    public double V = 5; // 子彈速度

    public Bullet( double x, double y,  GameControl GC, Team team) { // 一般用建構子
        super(bulletimage, x, y, BulletWidth, BulletHeight, GC, team); // 呼叫父類
        AngleRadian = 0 ; // 預設角度
    }
    public Bullet( double x, double y, double V , double Angle ,GameControl GC, Team team,Image image) { // For Laser
        super(image, x, y, BulletWidth, BulletHeight, GC, team); // 指定圖片
        AngleRadian = Angle ; // 指定角度
        this.V = V ; // 指定速度
        GC.bullets.add(this) ; // 立即加入子彈池
    }
    public Bullet( double x, double y  , double Angle ,GameControl GC, Team team) { // For Elite
        super(bulletimage, x, y, BulletWidth, BulletHeight, GC, team); // 預設圖片
        AngleRadian = Angle ; // 指定角度
        GC.bullets.add(this) ; // 立即加入子彈池
    }
    public Bullet( double x, double y  , double Angle ,double V,GameControl GC, Team team) { // For Elite
        super(bulletimage, x, y, BulletWidth, BulletHeight, GC, team); // 預設圖片
        AngleRadian = Angle ; // 指定角度
        this.V = V ; // 指定速度
        GC.bullets.add(this) ; // 立即加入子彈池
    }

    public void Bulletmove(){ // 子彈移動公式
        y -= V * Math.sin(AngleRadian); // y 方向
        x += V * Math.cos(AngleRadian); // x 方向
    }

}