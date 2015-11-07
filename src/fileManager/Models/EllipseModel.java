package fileManager.Models;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;

import java.io.Serializable;

/**
 * Created by bider_000 on 06.11.2015.
 */
public class EllipseModel extends ShapeModel implements Serializable {

    private double radiusX;
    private double radiusY;

    private double centerX;
    private double centerY;

    private String fill;
    private String stroke;
    private double opacity;
    private String id;

    public EllipseModel() {

    }

    public EllipseModel(Ellipse ellipse) {
        radiusX = ellipse.getRadiusX();
        radiusY = ellipse.getRadiusY();
        centerX = ellipse.getCenterX();
        centerY = ellipse.getCenterY();
        fill = ellipse.getFill().toString();
        stroke = ellipse.getStroke().toString();
        opacity = ellipse.getOpacity();
        id = ellipse.getId();
    }

    public Ellipse toViewModel() {
        Ellipse shape = new Ellipse();
        shape.setCenterX(centerX);
        shape.setCenterY(centerY);
        shape.setRadiusX(radiusX);
        shape.setRadiusY(radiusY);
        shape.setFill(Paint.valueOf(fill));
        shape.setStroke(Paint.valueOf(stroke));
        shape.setOpacity(opacity);
        shape.setId(id);
        return shape;
    }

    public double getRadiusX() {
        return radiusX;
    }

    public void setRadiusX(double radiusX) {
        this.radiusX = radiusX;
    }

    public double getRadiusY() {
        return radiusY;
    }

    public void setRadiusY(double radiusY) {
        this.radiusY = radiusY;
    }

    public double getCenterX() {
        return centerX;
    }

    public void setCenterX(double centerX) {
        this.centerX = centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public void setCenterY(double centerY) {
        this.centerY = centerY;
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
