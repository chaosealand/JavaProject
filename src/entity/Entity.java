package entity;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import scene.GameControl;

public abstract class Entity { //所有的畫面物件都是繼承於它，如:public class Tank extends Entity....
    Image image ; //實體的圖片
    double x , y , width , height ; //實體的參數
    GameControl GC ;


    public Entity(Image image, double x, double y, double width, double height, GameControl GC) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.GC = GC;
    }
    public void render(){
        GraphicsContext gc = GC.graphicsContext;
        gc.drawImage(image,x,y,width,height);
    }
    public Rectangle2D getContour(){    //碰撞檢測使用
        return new Rectangle2D(x, y, width, height);
    }


}
