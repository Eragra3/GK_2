package logic;

import javafx.scene.paint.Color;

/**
 * Created by bider_000 on 09.11.2015.
 */
public class Configuration {
    private static Color mainColor = Color.BLUE;
    private static double shapeOpacity = 0.5;

    public static double getShapeOpacity() {
        return shapeOpacity;
    }

    public static void setShapeOpacity(double shapeOpacity) {
        Configuration.shapeOpacity = shapeOpacity;
    }

    public static Color getMainColor() {
        return mainColor;
    }

    public static void setMainColor(Color mainColor) {
        Configuration.mainColor = mainColor;
    }
}
