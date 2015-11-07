package fileManager.Models;

import javafx.collections.ObservableList;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;

import java.io.Serializable;


/**
 * Created by bider_000 on 06.11.2015.
 */
public class PolygonModel extends ShapeModel implements Serializable {

    private Double[] points = new Double[10];

    private String fill;
    private String stroke;
    private double opacity;
    private String id;

    public PolygonModel() {

    }

    public PolygonModel(Polygon polygon) {
        points = polygon.getPoints().toArray(points);
        fill = polygon.getFill().toString();
        stroke = polygon.getStroke().toString();
        opacity = polygon.getOpacity();
        id = polygon.getId();
    }

    public Polygon toViewModel() {
        Polygon shape = new Polygon();
        ObservableList<Double> shapePoints = shape.getPoints();
        for (Double point : points) {
            if (point != null) {
                shapePoints.add(point);
            }
        }
        shape.setFill(Paint.valueOf(fill));
        shape.setStroke(Paint.valueOf(stroke));
        shape.setOpacity(opacity);
        shape.setId(id);
        return shape;
    }

    public Double[] getPoints() {
        return points;
    }

    public void setPoints(Double[] points) {
        this.points = points;
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
