package entity;

import javafx.scene.Group;
import javafx.scene.image.Image;
import scene.GameControl;
import utils.Team;

import java.util.*;

import java.util.HashMap;

public abstract class EntityRole extends Entity{
    boolean alive = true;
    Team team;
    Map<String, Image> imageMap = new HashMap<>();

    public EntityRole(Image image, double x, double y, double width, double height, GameControl GC, Team team) {
        super(image, x, y, width, height, GC);
        this.team = team;
    }

    public abstract boolean ImpactCheck(Entity entity);
}
