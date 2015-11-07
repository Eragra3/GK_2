package presentation.Models;

import javafx.scene.shape.Shape;

/**
 * Created by bider_000 on 06.11.2015.
 */
public class ShapeWrapper extends Shape {

    private boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public com.sun.javafx.geom.Shape impl_configShape() {
        return null;
    }
}
