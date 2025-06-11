package utils;

public class StatBoard {

    private static int EnemyTakeDowns = 0 ;
    private static int SurvivedTime = 0 ;


    public static void reset() {

        EnemyTakeDowns = 0;
        SurvivedTime = 0;
    }


   public static int getkills() {
        return EnemyTakeDowns;
    }

    public static int getSurvivedTime() {
        return SurvivedTime;
    }

    public static void addEnemyTakeDown() {
        EnemyTakeDowns++;
    }
    public static void addEnemyTakeDown(int i) {
        EnemyTakeDowns+= i;
    }

    public static void addSurvivedTime(int time) {
        SurvivedTime += time;
    }
}
