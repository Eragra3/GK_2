package logic;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;

import java.util.ArrayList;

/**
 * Created by bider_000 on 26.10.2015.
 */
public class PolygonDrawer implements Drawer {
    private static int idCounter = 1;
    private ObservableList addedShapes;

    private Pane canvas;
    private ArrayList<Shape> previewPoints = new ArrayList(20);
    private Polyline previewShape = getPreviewShape();


    public PolygonDrawer(Pane canvas, ObservableList addedShapes) {
        this.canvas = canvas;
        this.addedShapes = addedShapes;
    }

    public void startDrawing() {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, drawHandler);
    }

    public void stopDrawing() {
        canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, drawHandler);
        previewShape.getPoints().clear();
    }

    private EventHandler<MouseEvent> drawHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED && mouseEvent.getButton() != MouseButton.MIDDLE) {
                if (!previewShape.isVisible()) {
                    previewShape.setVisible(true);
                    canvas.getChildren().add(previewShape);
                }

                Shape previewPoint = getPreviewPoint(mouseEvent.getX(), mouseEvent.getY());
                canvas.getChildren().add(previewPoint);
                previewPoints.add(previewPoint);


                int size = previewShape.getPoints().size();
                if (size > 2) {
                    Point2D mouse = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());
                    Point2D first = canvas.localToScene(previewShape.getPoints().get(0),
                            previewShape.getPoints().get(1));
                    Point2D last = canvas.localToScene(previewShape.getPoints().get(size - 2),
                            previewShape.getPoints().get(size - 1));
                    if (first.distance(mouse) < 10 || last.distance(mouse) < 3) {
                        Polygon shape = getShape();
                        shape.getPoints().addAll(previewShape.getPoints());

                        previewShape.setVisible(false);
                        previewShape.getPoints().clear();

                        canvas.getChildren().removeAll(previewPoints);
                        previewPoints.clear();

                        addedShapes.add(shape);
                        canvas.getChildren().remove(previewShape);
                        return;
                    }
                }
                previewShape.getPoints().addAll(mouseEvent.getX(), mouseEvent.getY());

                mouseEvent.consume();
            } else if (mouseEvent.getButton() != MouseButton.MIDDLE) {
                mouseEvent.consume();
            }
        }
    };

    private Polygon getShape() {
        Polygon shape = new Polygon();
        shape.setFill(Configuration.getMainColor());
        shape.setId("Polygon " + String.valueOf(idCounter++));
        shape.setOpacity(Configuration.getShapeOpacity());
        shape.setStroke(Color.BLACK);
        return shape;
    }

    private Polyline getPreviewShape() {
        Polyline shape = new Polyline();
        shape.setStroke(Color.BLACK);
        shape.getStrokeDashArray().addAll(2.0, 2.0);
        shape.setVisible(false);
        return shape;
    }

    private Shape getPreviewPoint(double x, double y) {
        Ellipse preview = new Ellipse();
        preview.setCenterX(x);
        preview.setCenterY(y);
        preview.setRadiusX(4);
        preview.setRadiusY(4);
        preview.setFill(Color.WHITE);
        preview.setOpacity(1);
        preview.setStroke(Color.BLACK);
        preview.getStrokeDashArray().add(new Double(2));
        preview.setVisible(true);
        return preview;
    }

    public static void resetIdCounter() {
        idCounter = 1;
    }
}
