package entity;

import javafx.scene.image.Image;
import scene.GameControl;

import java.util.Random;

public class Explosion extends Entity {


    private static int TotalExplosionTime = 500; //milliseconds
    private static Image[] ExplosionImages; // 爆炸圖片陣列
    static {
        try {
            ExplosionImages = new Image[]{
                new Image("Image/SmallExplosion/01.png"),
                new Image("Image/SmallExplosion/02.png"),
                new Image("Image/SmallExplosion/03.png"),
                new Image("Image/SmallExplosion/04.png"),
                new Image("Image/SmallExplosion/05.png"),
                new Image("Image/SmallExplosion/06.png"),
                new Image("Image/SmallExplosion/07.png"),
                new Image("Image/SmallExplosion/08.png"),
                new Image("Image/SmallExplosion/09.png"),
                new Image("Image/SmallExplosion/10.png"),
            };
        } catch (Exception e) {
            System.err.println("無法加載爆炸圖片: " + e.getMessage());
        }
    }

    private long startTime; // 爆炸效果开始时间
    private int currentFrameIndex = 0; // 当前显示的图片索引
    private boolean isAnimationFinished = false; // 动画是否已结束
    private Entity e ;
    Random rnd = new Random();
    double rndFactorX ;
    double rndFactorY ;

    /**
     * 爆炸效果构造函数
     * @param x X坐标
     * @param y Y坐标
     * @param GC 游戏控制器
     */
    public Explosion(double x, double y, GameControl GC,Entity e) {
        super(x, y, GC);
        this.width = 50; // 设置爆炸效果宽度
        this.height = 50; // 设置爆炸效果高度
        this.image = ExplosionImages[0]; // 设置初始图片
        this.startTime = System.currentTimeMillis(); // 记录开始时间
        this.e = e; // 保存触发爆炸的实体
        rndFactorX = rnd.nextInt(20)-10;
        rndFactorY = rnd.nextInt(20)-10;

        // 将爆炸效果添加到游戏控制器的爆炸列表中
    }

    /**
     * 更新爆炸动画状态
     */
    public void update() {
        x = e.getCenterX(); // 设置爆炸效果位置为实体中心
        y = e.getCenterY(); // 设置爆炸效果位置为实体中心

        if (isAnimationFinished) return;

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;

        // 根据经过的时间计算当前应该显示的帧
        int frameIndex = (int)((elapsedTime * ExplosionImages.length) / TotalExplosionTime);

        // 如果已经显示完所有帧，则标记动画结束
        if (frameIndex >= ExplosionImages.length) {
            isAnimationFinished = true;
            // 从爆炸列表中移除自身
            GC.ExplosionList.remove(this);
            return;
        }

        // 更新当前显示的图片
        if (frameIndex != currentFrameIndex) {
            currentFrameIndex = frameIndex;
            image = ExplosionImages[currentFrameIndex];
        }



    }

    /**
     * 渲染爆炸效果
     */
    public void render() {

        update(); // 更新动画状态
        if (!isAnimationFinished) {

            double realCenterX = e.getCenterX() - 20;
            double realCenterY = e.getCenterY() - 20;

            GC.graphicsContext.drawImage(image, realCenterX + rndFactorX,realCenterY+rndFactorY, width, height);
        }
    }

    /**
     * 检查爆炸动画是否已结束
     * @return 动画是否结束
     */
    public boolean isAnimationFinished() {
        return isAnimationFinished;
    }
}

