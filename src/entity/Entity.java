package entity;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import scene.GameControl;

import java.util.ArrayList;
import java.util.List;

public abstract class Entity { //所有的畫面物件都是繼承於它，如:public class Tank extends Entity....
    Image image ; //實體的圖片
    double x , y , width , height ; //實體的參數
    GameControl GC ;
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    //給shield找飛機的中心要用


    public Entity(Image image, double x, double y, double width, double height, GameControl GC) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.GC = GC;
    }

    public Entity(double x, double y, GameControl GC) {
        this.x = x;
        this.y = y;
        this.GC = GC;
    }
    public void render (boolean b) { //背景
        if (b) {
            GraphicsContext gc = GC.graphicsContext;
            gc.drawImage(image,x,y,width,height);
        }
    }
    public void render(){

        GraphicsContext gc = GC.graphicsContext;

        // 保存当前图形上下文的状态
        gc.save();

        // 设置旋转中心点为实体的中心位置
        double centerX = x + width / 2;
        double centerY = y + height / 2;

        // 移动到旋转中心点
        gc.translate(centerX, centerY);

        // 顺时针旋转90度（JavaFX 中角度是按顺时针方向的，90度就是顺时针90度）
        gc.rotate(90);

        // 绘制图像，需要调整位置，使其中心点与原中心点对齐
        gc.drawImage(image, -width / 2, -height / 2, width, height);

        // 恢复图形上下文的原始状态
        gc.restore();
    }
    public Rectangle2D getContour(){    //碰撞檢測使用
        return new Rectangle2D(x, y, width, height);
    }


    public double getCenterX (){
        return (x+width/2);
    }

    public double getCenterY (){
        return (y+height/2);
    }

}
