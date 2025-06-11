package controller;

import Director.Director;
import entity.Background;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;

import static controller.GameOverController.*;

public class VictoryController {


    @FXML
    private AnchorPane MainPane;

    @FXML
    private ImageView ButtonToMenu;

    @FXML
    private TextField GameResult;

    @FXML
    private AnchorPane Panel;

    @FXML
    private TextField TakeDownLabel;

    @FXML
    private TextField TimeLabel;

    @FXML
    private TextField killcount;

    @FXML
    private TextField timecount;


    private boolean FullMenuShown = false ;
    Timeline ShowPanel ;
    Background background ;

    @FXML
    public void initialize() {
        // 初始化背景视频
        try {
            background = new Background("/Video/VictoryVideo.mp4");
            // 设置媒体错误处理
            background.mediaPlayer.setOnError(() -> {
                System.out.println("媒体播放错误：" + background.mediaPlayer.getError());
                // 媒体错误时创建一个静态背景作为替代
                createFallbackBackground();
            });

            // 设置mediaView的Z顺序，确保它位于最底层
            background.mediaView.setViewOrder(100.0); // 较大的viewOrder值表示更靠后
            MainPane.getChildren().add(0, background.mediaView); // 添加到子节点列表的最开始位置
        } catch (Exception ex) {
            System.out.println("视频初始化失败：" + ex.getMessage());
            ex.printStackTrace();
            // 如果视频初始化失败，创建静态背景
            createFallbackBackground();
        }

        // 初始化ButtonToMenu图像和面板的透明度
        ButtonToMenu.setImage(BackToMenu[0]);
        Panel.setOpacity(0);
        Panel.setViewOrder(1.0); // 确保面板在视频上方

        // 创建淡入动画
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.2), Panel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.2), Panel);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        // 创建面板显示时间线
        ShowPanel = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            fadeIn.play();
        }));
        ShowPanel.setOnFinished(e -> {
            FullMenuShown = true;
            ShowStat();
        });
        AnchorPane pane = new AnchorPane();
        pane.setPrefSize(Director.WIDTH, Director.HEIGHT);
        pane.setOpacity(0);
        pane.setStyle("-fx-background-color: #FFFFFF;"); // 半透明白色背景
        MainPane.getChildren().add(pane);
        FadeTransition paneFadeIn = new FadeTransition(Duration.seconds(1.2), pane);
        paneFadeIn.setFromValue(0);
        paneFadeIn.setToValue(1);
        pane.setViewOrder(50);

        ImageView victoryImage = new ImageView("/Image/GenshinStart.jpg");
        victoryImage.setFitWidth(Director.WIDTH);
        victoryImage.setFitHeight(Director.HEIGHT);
        victoryImage.setOpacity(0);
        MainPane.getChildren().add(victoryImage);
        FadeTransition Genshin = new FadeTransition(Duration.seconds(1.2),victoryImage);
        Genshin.setFromValue(0);
        Genshin.setToValue(1);
        victoryImage.setViewOrder(50);


        // 创建主时间线，管理视频播放和面板显示
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(100), e -> {
                // 尝试播放媒体
                if (background != null && background.mediaPlayer != null) {
                    try {
                        background.PlayMedia();
                    } catch (Exception ex) {
                        System.out.println("视频播放失败：" + ex.getMessage());
                        createFallbackBackground();
                    }
                }
            }),
            new KeyFrame(Duration.millis(6000), e -> {
                ShowPanel.play();
            }),
                new KeyFrame(Duration.seconds(18), e -> {
                    fadeOut.play();
                }),
                new KeyFrame(Duration.seconds(22.5), e -> {
                    paneFadeIn.play();
                }),
                new KeyFrame(Duration.seconds(26), e -> {
                    Genshin.play();
                }),
                new KeyFrame(Duration.seconds(30), e -> {
                    try {
                        Director.getInstance().GC.clear();
                        Director.getInstance().ToMenu();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                })
        );

        // 启动时间线动画
        timeline.play();
    }

    /**
     * 创建备用背景（当视频无法播放时使用）
     */
    private void createFallbackBackground() {
        // 移除旧媒体视图
        if (background != null && background.mediaView != null) {
            MainPane.getChildren().remove(background.mediaView);
        }

        // 创建静态背景图片（使用已有的Black.jpg）
        javafx.scene.image.Image fallbackImage = new javafx.scene.image.Image("/Image/Black.jpg");
        javafx.scene.image.ImageView fallbackView = new javafx.scene.image.ImageView(fallbackImage);
        fallbackView.setFitWidth(Director.WIDTH);
        fallbackView.setFitHeight(Director.HEIGHT);
        fallbackView.setViewOrder(100.0); // 确保在最底层

        // 添加到主面板
        MainPane.getChildren().add(0, fallbackView);

        // 添加文本显示"VICTORY!"
        javafx.scene.text.Text victoryText = new javafx.scene.text.Text("VICTORY!");
        victoryText.setFont(new javafx.scene.text.Font("Arial", 72));
        victoryText.setFill(javafx.scene.paint.Color.GOLD);

        // 设置文本居中
        victoryText.setLayoutX((Director.WIDTH - victoryText.getBoundsInLocal().getWidth()) / 2);
        victoryText.setLayoutY(Director.HEIGHT / 3);

        MainPane.getChildren().add(victoryText);

        // 创建文本淡入效果
        FadeTransition textFade = new FadeTransition(Duration.seconds(1.5), victoryText);
        textFade.setFromValue(0);
        textFade.setToValue(1);
        textFade.play();
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
    private int backToMenuIndex = 0;
    private Timeline backToMenuTimeline;
    private Timeline backToMenuReverseTimeline;

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
    private void ShowStat() {
        String levelclear = "Level Cleared ";
        String takedowns = "Enemy Killed  :";
        String TimeSurvived = "Time Survived :";
        int lclength = levelclear.length();
        int tdlength = takedowns.length();
        int tsLength = TimeSurvived.length();

        // 先显示Level Cleared文本，一个字符一个字符地出现
        Timeline levelTimeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            if (wordcounter < lclength) {
                GameResult.setText(levelclear.substring(0, wordcounter + 1));
                wordcounter++;
            } else {
                GameResult.setText(levelclear);
            }
        }));
        levelTimeline.setCycleCount(lclength);

        // 在Level Cleared完全显示后，显示其他统计信息
        levelTimeline.setOnFinished(e -> {
            // 重置字符计数器
            wordcounter = 0;

            // 开始显示击杀数和生存时间
            Timeline statsTimeline = new Timeline(new KeyFrame(Duration.millis(100), ev -> {
                if (wordcounter < tdlength) {
                    TakeDownLabel.setText(takedowns.substring(0, wordcounter + 1));
                    TimeLabel.setText(TimeSurvived.substring(0, wordcounter + 1));
                    wordcounter++;
                } else {
                    TakeDownLabel.setText(takedowns);
                    TimeLabel.setText(TimeSurvived);
                }
            }));
            statsTimeline.setCycleCount(tdlength);
            statsTimeline.setOnFinished(ev -> {
                // 显示完整的统计信息及数值
                TakeDownLabel.setText("Enemy Killed  " );
                TimeLabel.setText("Time Survived " );

                // 显示具体的数字
                killcount.setText(String.valueOf(utils.StatBoard.getkills()));
                timecount.setText(String.valueOf(utils.StatBoard.getSurvivedTime()));
            });

            // 启动统计动画
            statsTimeline.play();
        });

        // 启动关卡清除动画
        levelTimeline.play();
    }
}
