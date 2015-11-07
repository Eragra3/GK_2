package logic;


import javafx.scene.paint.Color;

/**
 * Created by bider_000 on 26.10.2015.
 */
public interface Drawer {
    public static Color mainColor = Color.BLUE;
    public static double shapeOpacity = 0.5;

    void startDrawing();

    void stopDrawing();
}
