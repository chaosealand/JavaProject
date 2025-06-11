package utils;

import Director.Director;
import entity.LaserBeam;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;
import java.util.Set;

public class KeyProcessor implements EventHandler<KeyEvent> {

    public static Set<KeyCode> pressedKeys = new HashSet<>();
    public static KeyCode lastKeyPressed = null; // 新增

    @Override
    public void handle(KeyEvent keyEvent) {
        KeyCode Code = keyEvent.getCode();
        EventType<KeyEvent> T = keyEvent.getEventType();

        if (T == KeyEvent.KEY_PRESSED) {
            lastKeyPressed = Code; // 新增
            if (Code == KeyCode.L) {
               ;
            }
            pressedKeys.add(Code);
        } else if (T == KeyEvent.KEY_RELEASED) {

            if (Code == KeyCode.SPACE) {
                Director.getInstance().GC.ToPauseOrNotToPause();
            }
            pressedKeys.remove(Code);
        }
    }

    // 讓 Boss 方便取得最後一次按下的按鍵
    public static KeyCode getLastKeyPressed() {
        return lastKeyPressed;
    }
}
