package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import Director.Director;
import javafx.fxml.FXML;
import javafx.util.Duration;

public class MenuController {

    @FXML
    private ImageView imageViewPlay;

    @FXML
    private ImageView imageViewExit;

    private Timeline forwardPlayTimeline;  // 控制 Play 按鈕的輪播動畫
    private Timeline backwardPlayTimeline;
    private int currentPlayIndex = 0;

    private Timeline forwardExitTimeline;  // 控制 Exit 按鈕的輪播動畫
    private Timeline backwardExitTimeline;
    private int currentExitIndex = 0;

    // Play 按鈕的圖片路徑陣列
    private final String[] playImages = {
            "/Image/start1.png",
            "/Image/start2.png",
            "/Image/start3.png",
            "/Image/start4.png",
            "/Image/start5.png",
            "/Image/start6.png"
    };

    // Exit 按鈕的圖片路徑陣列
    private final String[] exitImages = {
            "/Image/Exit1.png",
            "/Image/Exit2.png",
            "/Image/Exit3.png",
            "/Image/Exit4.png",
            "/Image/Exit5.png",
            "/Image/Exit6.png"
    };

    @FXML
    public void initialize() {
        imageViewPlay.toFront();
        imageViewExit.toFront();
        currentPlayIndex = 0;
        currentExitIndex = 0;
        imageViewPlay.setImage(new Image(getClass().getResource(playImages[currentPlayIndex]).toString()));
        imageViewExit.setImage(new Image(getClass().getResource(exitImages[currentExitIndex]).toString()));
    }

    @FXML
    void Exit(MouseEvent event) {
        Director.getInstance().ExitGame();
    }

    @FXML
    void Play(MouseEvent event) {
        Director.getInstance().StartGame();

    }

    @FXML
    void StartButtonMouseEnter(MouseEvent event) {
        // 停止反向動畫
        if (backwardPlayTimeline != null && backwardPlayTimeline.getStatus() == Animation.Status.RUNNING) {
            backwardPlayTimeline.stop();
        }
        if (forwardPlayTimeline == null) {
            forwardPlayTimeline = new Timeline(
                    new KeyFrame(Duration.millis(35), e -> {
                        imageViewPlay.setImage(new Image(getClass().getResource(playImages[currentPlayIndex]).toString()));
                        currentPlayIndex++;
                        if (currentPlayIndex >= playImages.length) {
                            forwardPlayTimeline.stop();  // 播放完後停止
                        }
                    })
            );
            forwardPlayTimeline.setCycleCount(playImages.length);  // 播放次數 = 圖片數量
        }
        currentPlayIndex = 0;  // 從第一張開始
        forwardPlayTimeline.playFromStart();
    }

    @FXML
    void StartButtonMouseExit(MouseEvent event) {
        // 停止正向動畫
        if (forwardPlayTimeline != null && forwardPlayTimeline.getStatus() == Animation.Status.RUNNING) {
            forwardPlayTimeline.stop();
        }
        if (backwardPlayTimeline == null) {
            backwardPlayTimeline = new Timeline(
                    new KeyFrame(Duration.millis(35), e -> {
                        imageViewPlay.setImage(new Image(getClass().getResource(playImages[currentPlayIndex]).toString()));
                        currentPlayIndex--;
                        if (currentPlayIndex < 0) {
                            backwardPlayTimeline.stop();  // 播放完後停止
                        }
                    })
            );
            backwardPlayTimeline.setCycleCount(playImages.length);  // 播放次數 = 圖片數量
        }
        currentPlayIndex = playImages.length - 1;  // 從最後一張開始倒退
        backwardPlayTimeline.playFromStart();
    }

    @FXML
    void ExitButtonMouseEnter(MouseEvent event) {
        // 停止反向動畫
        if (backwardExitTimeline != null && backwardExitTimeline.getStatus() == Animation.Status.RUNNING) {
            backwardExitTimeline.stop();
        }

        if (forwardExitTimeline == null) {
            forwardExitTimeline = new Timeline(
                    new KeyFrame(Duration.millis(35), e -> {
                        imageViewExit.setImage(new Image(getClass().getResource(exitImages[currentExitIndex]).toString()));
                        currentExitIndex++;
                        if (currentExitIndex >= exitImages.length) {
                            forwardExitTimeline.stop();  // 播放完後停止
                        }
                    })
            );
            forwardExitTimeline.setCycleCount(exitImages.length);  // 播放次數 = 圖片數量
        }
        currentExitIndex = 0;  // 從第一張開始
        forwardExitTimeline.playFromStart();
    }

    @FXML
    void ExitButtonMouseExit(MouseEvent event) {
        // 停止正向動畫
        if (forwardExitTimeline != null && forwardExitTimeline.getStatus() == Animation.Status.RUNNING) {
            forwardExitTimeline.stop();
        }
        if (backwardExitTimeline == null) {
            backwardExitTimeline = new Timeline(
                    new KeyFrame(Duration.millis(35), e -> {
                        imageViewExit.setImage(new Image(getClass().getResource(exitImages[currentExitIndex]).toString()));
                        currentExitIndex--;
                        if (currentExitIndex < 0) {
                            backwardExitTimeline.stop();  // 播放完後停止
                        }
                    })
            );
            backwardExitTimeline.setCycleCount(exitImages.length);  // 播放次數 = 圖片數量
        }
        currentExitIndex = exitImages.length - 1;  // 從最後一張開始倒退
        backwardExitTimeline.playFromStart();
    }
}