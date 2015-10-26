package logic;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

/**
 * Created by bider_000 on 26.10.2015.
 */
public class EllipsisDrawer implements Drawer {
    private static int idCounter = 1;

    private Pane canvas;
    private Pane interactionPane;
    private Ellipse previewShape = getPreviewShape();

    private double initX;
    private double initY;

    public EllipsisDrawer(Pane canvas, Pane interactionPane) {
        this.canvas = canvas;
        this.interactionPane = interactionPane;
    }

    public void startDrawing() {
        interactionPane.addEventHandler(MouseEvent.MOUSE_PRESSED, drawHandler);
        interactionPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, drawHandler);
        interactionPane.addEventHandler(MouseEvent.MOUSE_RELEASED, drawHandler);
    }

    public void stopDrawing() {
        interactionPane.removeEventHandler(MouseEvent.MOUSE_PRESSED, drawHandler);
        interactionPane.removeEventHandler(MouseEvent.MOUSE_DRAGGED, drawHandler);
        interactionPane.removeEventHandler(MouseEvent.MOUSE_RELEASED, drawHandler);
    }

    private EventHandler<MouseEvent> drawHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                previewShape.setCenterX(mouseEvent.getX());
                previewShape.setCenterY(mouseEvent.getY());
                canvas.getChildren().add(previewShape);
                initX = mouseEvent.getX();
                initY = mouseEvent.getY();
            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                double mouseX = mouseEvent.getX();
                double mouseY = mouseEvent.getY();

//                if (mouseX < initX) {
//                    previewShape.setX(mouseX);
//                }
                previewShape.setRadiusX(Math.abs(initX - mouseX));
//                if (mouseY < initY) {
//                    previewShape.setY(mouseY);
//                }
                previewShape.setRadiusY(Math.abs(initY - mouseY));

            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                // Clone the rectangle
                Ellipse r = getShape();
                r.setCenterX(previewShape.getCenterX());
                r.setCenterY(previewShape.getCenterY());
                r.setRadiusX(previewShape.getRadiusX());
                r.setRadiusY(previewShape.getRadiusY());
                canvas.getChildren().add(r);
                canvas.getChildren().remove(previewShape);
            }
            mouseEvent.consume();
        }
    };

    private Ellipse getShape() {
        Ellipse shape = new Ellipse();
        shape.setFill(Color.BLUE);
        shape.setId(String.valueOf(idCounter++));
        shape.setOpacity(0.2);
        shape.setStroke(Color.BLACK);
        return shape;
    }

    private Ellipse getPreviewShape() {
        Ellipse shape = new Ellipse();
        shape.setFill(Color.BLACK);
        shape.setOpacity(0.8);
        return shape;
    }
}
