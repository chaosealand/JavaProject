package utils;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class MouseTracker implements EventHandler<MouseEvent> {

    public static double CursorX ;
    public static double CursorY ;
    public static boolean leftPressed = false;

    @Override
    public void handle(MouseEvent E) {
        CursorX = E.getX();
        CursorY = E.getY();
    }
}
