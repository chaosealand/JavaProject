package entity;

import javafx.scene.image.Image;
import scene.GameControl;
import utils.Team;
import java.util.*;
import java.util.HashMap;

public abstract class EntityRole extends Entity{

    boolean alive = true;
    public boolean undefeatable = false; //設定飛機無敵狀態
    public long undefeatableTimeLast = 0; //設定飛機剩餘無敵時間
    Team team;
    Map<String, Image> imageMap = new HashMap<>();

    public EntityRole(Image image, double x, double y, double width, double height, GameControl GC, Team team) {
        super(image, x, y, width, height, GC);
        this.team = team;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Team getTeam() {
        return team;
    }
}
