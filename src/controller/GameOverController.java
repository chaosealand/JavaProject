package controller;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import Director.Director;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;

public class GameOverController {


    private static int LableFPS = 20 ;
    @FXML
    public ImageView GameResult;

    @FXML
    private TextField TakeDownLabel;
    @FXML
    private TextField TimeLabel;
    @FXML
    private TextField killcount;
    @FXML
    private TextField timecount;

    private int gameOverIndex = 0;
    private static final Image[] GAME_OVER_IMAGE = new Image[27];
    public static final Image[] BackToMenu = new Image[5];

    // 静态初始化块用于预加载所有图像
    static {
        preloadImages();
    }

    // 预加载所有图像资源
    private static void preloadImages() {
        try {
            // 预加载 GameOver 图像
            for (int i = 0; i < GAME_OVER_IMAGE.length; i++) {
                final int index = i + 1;
                GAME_OVER_IMAGE[i] = new Image("/Image/GameOver/" + index + ".png", true); // 使用true开启后台加载
            }

            // 预加载 BackToMenu 图像
            for (int i = 0; i < BackToMenu.length; i++) {
                final int index = i + 1;
                BackToMenu[i] = new Image("/Image/BackToMenu/" + index + ".png", true); // 使用true开启后台加载
            }

            // 确保所有图像完全加载
            for (Image img : GAME_OVER_IMAGE) {
                img.progressProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal.doubleValue() < 1.0) {
                        // 图像仍在加载中
                    }
                });
            }

            for (Image img : BackToMenu) {
                img.progressProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal.doubleValue() < 1.0) {
                        // 图像仍在加载中
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("图像预加载失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private AnchorPane Panel;


    @FXML
    private ImageView ButtonToMenu;

    private int backToMenuIndex = 0;
    private Timeline backToMenuTimeline;
    private Timeline backToMenuReverseTimeline;
    private boolean FullMenuShown = false ;
    @FXML
    public void initialize() {
        // 點擊按鈕 → 回到主選單場景

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000.0 / LableFPS),e->{
            GameResult.setImage(GAME_OVER_IMAGE[gameOverIndex]);
            gameOverIndex = (gameOverIndex + 1) % GAME_OVER_IMAGE.length;
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();



        // ButtonToMenu 動畫
        ButtonToMenu.setImage(BackToMenu[0]);
        Panel.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.2), Panel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        Timeline faded = new Timeline(new KeyFrame(Duration.millis(2500), e -> {
            fadeIn.play();
        }));
        faded.setOnFinished(e -> {
            FullMenuShown = true;
            ShowStat();
        });
        faded.play();


    }

    @FXML
    void btmClick(MouseEvent event) {
        try {
            Director.getInstance().GC.clear();
            Director.getInstance().ToMenu();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btmEnter(MouseEvent event) {
        if (backToMenuReverseTimeline != null && backToMenuReverseTimeline.getStatus() == Timeline.Status.RUNNING) {
            backToMenuReverseTimeline.stop();
        }
        if (backToMenuTimeline == null) {
            backToMenuTimeline = new Timeline(new KeyFrame(Duration.millis(30), e -> {
                ButtonToMenu.setImage(BackToMenu[backToMenuIndex]);
                backToMenuIndex++;
                if (backToMenuIndex >= BackToMenu.length) {
                    backToMenuTimeline.stop();  // 播放完後停止
                }
            }));
            backToMenuTimeline.setCycleCount(BackToMenu.length);  // 播放次數 = 圖片數量
        }
        backToMenuIndex = 0;  // 從第一張開始
        backToMenuTimeline.playFromStart();
    }

    @FXML
    void btmExit(MouseEvent event) {
        if (backToMenuTimeline != null && backToMenuTimeline.getStatus() == Timeline.Status.RUNNING) {
            backToMenuTimeline.stop();
        }
        if (backToMenuReverseTimeline == null) {
            backToMenuReverseTimeline = new Timeline(new KeyFrame(Duration.millis(30), e -> {
                ButtonToMenu.setImage(BackToMenu[backToMenuIndex]);
                backToMenuIndex--;
                if (backToMenuIndex < 0) {
                    backToMenuReverseTimeline.stop();  // 播放完後停止
                }
            }));
            backToMenuReverseTimeline.setCycleCount(BackToMenu.length);  // 播放次數 = 圖片數量
        }
        backToMenuIndex = BackToMenu.length - 1;  // 從最後一張開始
        backToMenuReverseTimeline.playFromStart();
    }

    int wordcounter = 0;
    private void ShowStat () {
        String takedowns    = "Enemy Killed  :";
        String TimeSurvived = "Time Survived :";
        int tdlength = takedowns.length();
        int tsLength = TimeSurvived.length();

        if (wordcounter < tdlength-1){
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {
                if (wordcounter < tdlength - 1) {
                    TakeDownLabel.setText(takedowns.substring(0, wordcounter + 1));
                    TimeLabel.setText(TimeSurvived.substring(0, wordcounter +1));
                    wordcounter++;
                } else {
                    TakeDownLabel.setText(takedowns);
                    TimeLabel.setText(TimeSurvived);
                }
            }));
            timeline.setCycleCount(takedowns.length());
            timeline.setOnFinished(e -> {
                TakeDownLabel.setText("Enemy Killed  \n" + utils.StatBoard.getkills());
                TimeLabel.setText("Time Survived \n" + utils.StatBoard.getSurvivedTime());
            });
            timeline.setOnFinished(e-> {
                // 在这里可以添加其他操作，比如显示按钮等
                killcount.setText(String.valueOf(utils.StatBoard.getkills()));
                int survialminute = utils.StatBoard.getSurvivedTime() / 60;
                int survialsecond = utils.StatBoard.getSurvivedTime() % 60;
                timecount.setText( String.format("%02d:%02d", survialminute, survialsecond));
            });
            timeline.play();
        }




    }
}
