package presentation.Controllers;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import logic.EllipsisDrawer;
import logic.RectangleDrawer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Controller implements Initializable {

    final FileChooser fileChooser = new FileChooser();
    @FXML
    BorderPane borderPane;
    @FXML
    ImageView imageView;
    @FXML
    Pane canvas;
    @FXML
    Pane scrollPane;

    Scene scene;

    //Drawers
    RectangleDrawer rectangleDrawer;
    EllipsisDrawer ellipsisDrawer;

    public Controller() {
    }

    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        rectangleDrawer = new RectangleDrawer(canvas, scrollPane);
        ellipsisDrawer = new EllipsisDrawer(canvas, scrollPane);
    }

    @FXML
    public void loadImage() {
        File file = fileChooser.showOpenDialog(borderPane.getScene().getWindow());
        if (file != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                imageView.setImage(image);
                resetImageView(imageView);
                Bounds imageViewBounds = imageView.getBoundsInParent();
                imageView.setTranslateX((scrollPane.getWidth() - imageViewBounds.getWidth()) / 2);
                imageView.setTranslateY((scrollPane.getHeight() - imageViewBounds.getHeight()) / 2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static final double MAX_SCALE = 10.0d;
    private static final double MIN_SCALE = .1d;

    public void handleScroll(ScrollEvent event) {
        double delta = 1.1;

        double scale = imageView.getScaleX(); // currently we only use X, same value is used for Y
        double oldScale = scale;

        if (event.getDeltaY() < 0)
            scale /= delta;
        else
            scale *= delta;

        scale = clamp(scale, MIN_SCALE, MAX_SCALE);

        imageView.setScaleX(scale);
        imageView.setScaleY(scale);
//        scrollPane.setFitToHeight(true);
//        scrollPane.setFitToWidth(true);

        Bounds imageViewBounds = imageView.getBoundsInParent();
        if (imageViewBounds.getWidth() < scrollPane.getWidth() && imageViewBounds.getHeight() < scrollPane.getHeight()) {
//            double scaleDiff = scale - oldScale;
            imageView.setTranslateX((scrollPane.getWidth() - imageViewBounds.getWidth()) / 2);
            imageView.setTranslateY((scrollPane.getHeight() - imageViewBounds.getHeight()) / 2);
        }

        event.consume();
    }

    public static double clamp(double value, double min, double max) {
        if (Double.compare(value, min) < 0)
            return min;

        if (Double.compare(value, max) > 0)
            return max;

        return value;
    }

    private void resetImageView(ImageView view) {
        view.setTranslateX(0);
        view.setTranslateY(0);
        view.setScaleX(1);
        view.setScaleY(1);
    }

    private void stopDrawing() {
        this.scene = borderPane.getScene();
        rectangleDrawer.stopDrawing();
        ellipsisDrawer.stopDrawing();
    }

    @FXML
    public void startDrawingRectangle() {
        stopDrawing();
        scene.setCursor(Cursor.CROSSHAIR);
        rectangleDrawer.startDrawing();
    }

    @FXML
    public void startDrawingEllipsis() {
        stopDrawing();
        scene.setCursor(Cursor.CROSSHAIR);
        ellipsisDrawer.startDrawing();
    }

    @FXML
    public void setHandActive() {
        stopDrawing();
        Scene scene = borderPane.getScene();
        scene.setCursor(Cursor.HAND);
    }
}
