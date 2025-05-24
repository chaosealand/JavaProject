package utils;

import Director.Director;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;
import java.util.Set;

public class KeyProcesser implements EventHandler<KeyEvent> {

    public static Set<KeyCode> pressedKeys = new HashSet<>();

    @Override
    public void handle(KeyEvent keyEvent) {
        KeyCode Code = keyEvent.getCode();
        EventType<KeyEvent> T = keyEvent.getEventType();

        if (T ==KeyEvent.KEY_PRESSED) {
            pressedKeys.add(Code);
        }
        else if (T == KeyEvent.KEY_RELEASED){
            if (Code==KeyCode.SPACE) Director.getInstance().GC.ToPauseOrNotToPause();
            pressedKeys.remove(Code);
        }

    }
}
