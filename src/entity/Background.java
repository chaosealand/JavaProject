package entity;

import javafx.scene.image.Image;
import Director.Director;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import scene.GameControl;
import javafx.scene.media.Media;
import java.net.URL;


//changes
public class Background {
    Media media;
    public MediaPlayer mediaPlayer;
    public MediaView mediaView;

    public static Image black = new Image("/Image/Black.jpg");
    //public Background(Image image, GameControl gc) {
        //super (image,0,0,Director.WIDTH,Director.HEIGHT,gc);
    //}


    public void PlayMedia() {
        mediaPlayer.play();
    }

    public Background(GameControl GC) {
        try {
            // 使用相对路径加载视频资源
            URL resourceUrl = getClass().getResource("/Video/background1.mp4");
            if (resourceUrl == null) {
                System.err.println("无法找到视频资源: /Video/background1.mp4");
                // 如果找不到视频，可以考虑使用黑色背景作为后备方案
                return;
            }

            media = new Media(resourceUrl.toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            mediaView = new MediaView(mediaPlayer);
            mediaView.setFitWidth(Director.WIDTH);
            mediaView.setFitHeight(Director.HEIGHT);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaView.setX(0);
            mediaView.setY(0);
        } catch (Exception e) {
            System.err.println("加载视频背景时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
