package entity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuButton extends JFrame {
    private JLabel imageLabel;
    private ImageIcon[] images;
    private int currentIndex = 0;
    private int Width = 400;
    private int Height = 300;

    public MenuButton() {
        setTitle("圖片輪播範例");
        setSize(Width, Height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 載入圖片（路徑請換成你自己的圖片路徑）
        images = new ImageIcon[6];
        for (int i = 0; i < 6; i++) {
            images[i] = new ImageIcon("/Image/start" + (i + 1) + ".png");
        }

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setIcon(images[currentIndex]);

        add(imageLabel, BorderLayout.CENTER);

        // 使用Timer每隔2秒切換圖片
        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentIndex = (currentIndex + 1) % images.length;
                imageLabel.setIcon(images[currentIndex]);
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuButton().setVisible(true);
        });
    }
}
