package utils;

import Director.Director;
import javafx.animation.AnimationTimer;

public class FrameUpdater extends AnimationTimer {
    Float current ;
    Float Last ;
    Float delta ;

    @Override
    public void handle(long l) {
        if (Director.getInstance().GC.GameRunning){
            Director.getInstance().GC.RenderAll();}
    }
}
