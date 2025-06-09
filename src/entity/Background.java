package entity;

import javafx.scene.image.Image;
import Director.Director;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import scene.GameControl;
import javafx.scene.media.Media;



//changes
public class Background {
    Media media ;
    public MediaPlayer mediaPlayer ;
    public MediaView mediaView ;

    public static Image black = new Image("/Image/Black.jpg");
    //public Background(Image image, GameControl gc) {
        //super (image,0,0,Director.WIDTH,Director.HEIGHT,gc);
    //}


    public void PlayMedia() {
        mediaPlayer.play();
    }
    public Background(GameControl GC) {
        media = new Media(getClass().getResource("/Video/background1.mp4").toExternalForm());
        mediaPlayer = new MediaPlayer(media);
        mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(Director.WIDTH);
        mediaView.setFitHeight(Director.HEIGHT);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaView.setX(0);
        mediaView.setY(0);
    }
}
