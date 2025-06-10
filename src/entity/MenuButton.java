package entity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuButton extends JFrame { // 主選單按鈕類別，繼承JFrame視窗
    private JLabel imageLabel; // 用於顯示圖片的標籤
    private ImageIcon[] images; // 圖片陣列
    private int currentIndex = 0; // 目前顯示的圖片索引
    private int Width = 400; // 視窗寬度
    private int Height = 300; // 視窗高度

    public MenuButton() { // 建構子
        setTitle("圖片輪播範例"); // 設定視窗標題
        setSize(Width, Height); // 設定視窗大小
        setDefaultCloseOperation(EXIT_ON_CLOSE); // 關閉視窗時結束程式
        setLocationRelativeTo(null); // 視窗置中

        // 載入圖片（路徑請換成你自己的圖片路徑）
        images = new ImageIcon[6]; // 建立6張圖片的陣列
        for (int i = 0; i < 6; i++) {
            images[i] = new ImageIcon("/Image/start" + (i + 1) + ".png"); // 載入對應路徑的圖片
        }

        imageLabel = new JLabel(); // 建立圖片標籤
        imageLabel.setHorizontalAlignment(JLabel.CENTER); // 水平置中顯示
        imageLabel.setIcon(images[currentIndex]); // 設定初始顯示的圖片

        add(imageLabel, BorderLayout.CENTER); // 把圖片標籤加到視窗中央

        // 使用Timer每隔2秒切換圖片
        Timer timer = new Timer(500, new ActionListener() { // 建立每500毫秒執行一次的計時器
            @Override
            public void actionPerformed(ActionEvent e) { // 每次觸發執行
                currentIndex = (currentIndex + 1) % images.length; // 依序切換索引
                imageLabel.setIcon(images[currentIndex]); // 顯示新圖片
            }
        });
        timer.start(); // 啟動計時器
    }

    public static void main(String[] args) { // 主程式進入點
        SwingUtilities.invokeLater(() -> { // 在Swing UI執行緒啟動
            new MenuButton().setVisible(true); // 建立並顯示MenuButton視窗
        });
    }
}

