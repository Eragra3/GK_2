package fileManager.Models;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;


/**
 * Created by bider_000 on 06.11.2015.
 */
public class RectangleModel extends ShapeModel implements Serializable {
    private double width;
    private double height;

    private double x;
    private double y;

    private String fill;
    private String stroke;
    private double opacity;
    private String id;

    public RectangleModel() {

    }

    public RectangleModel(Rectangle rec) {
        width = rec.getWidth();
        height = rec.getHeight();
        x = rec.getX();
        y = rec.getY();
        fill = rec.getFill().toString();
        stroke = rec.getStroke().toString();
        opacity = rec.getOpacity();
        id = rec.getId();
    }

    public Rectangle toViewModel() {
        Rectangle shape = new Rectangle();
        shape.setX(x);
        shape.setY(y);
        shape.setWidth(width);
        shape.setHeight(height);
        shape.setFill(Paint.valueOf(fill));
        shape.setStroke(Paint.valueOf(stroke));
        shape.setOpacity(opacity);
        shape.setId(id);
        return shape;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getFill() {
        return fill;
    }

    public void setFill(String fill) {
        this.fill = fill;
    }

    public String getStroke() {
        return stroke;
    }

    public void setStroke(String stroke) {
        this.stroke = stroke;
    }

    public double getOpacity() {
        return opacity;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
