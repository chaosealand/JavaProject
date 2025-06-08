package utils;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class MouseTracker implements EventHandler<MouseEvent> {

    public static double CursorX ;
    public static double CursorY ;
    public static boolean leftPressed = false;
    public static boolean rightPressed = false;

    @Override
    public void handle(MouseEvent E) {
        CursorX = E.getX();
        CursorY = E.getY();

        if (E.getEventType() == MouseEvent.MOUSE_PRESSED && E.getButton() == MouseButton.PRIMARY) {

            leftPressed = true;
        } else if (E.getEventType() == MouseEvent.MOUSE_RELEASED&& E.getButton() == MouseButton.PRIMARY) {
            leftPressed = false;
        }
        if (E.getEventType() == MouseEvent.MOUSE_PRESSED&& E.getButton() == MouseButton.SECONDARY){
            rightPressed = true;
        } else if (E.getEventType() == MouseEvent.MOUSE_RELEASED&& E.getButton() == MouseButton.SECONDARY) {
            rightPressed = false;
        }
    }
}
