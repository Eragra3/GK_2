package presentation.Controllers;

import common.Helpers;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import logic.Drawer;
import logic.EllipsisDrawer;
import logic.PolygonDrawer;
import logic.RectangleDrawer;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;

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
    ScrollPane scrollPane;
    @FXML
    Pane canvasWrapper;

    Scene scene;

    //Drawers
    RectangleDrawer rectangleDrawer;
    EllipsisDrawer ellipsisDrawer;
    PolygonDrawer polygonDrawer;
    Drawer currentDrawer;

    //Binding properties
    private DoubleProperty imageScale = new SimpleDoubleProperty(100);
    private DoubleProperty imageWidth;
    private DoubleProperty imageHeight;

    public Controller() {
    }

    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        this.rectangleDrawer = new RectangleDrawer(canvas);
        this.ellipsisDrawer = new EllipsisDrawer(canvas);
        this.polygonDrawer = new PolygonDrawer(canvas);
        currentDrawer = null;

        Scale scale = new Scale();

        //binding properties
        MonadicBinding<Double> maxWidth = EasyBind.map(imageView.boundsInParentProperty(), Bounds::getWidth);
        MonadicBinding<Double> maxHeight = EasyBind.map(imageView.boundsInParentProperty(), Bounds::getHeight);
        MonadicBinding<Number> imageWidth = EasyBind.select(imageView.imageProperty()).selectObject(Image::widthProperty);
        MonadicBinding<Number> imageHeight = EasyBind.select(imageView.imageProperty()).selectObject(Image::heightProperty);

        scale.xProperty().bind(Helpers.divide(maxWidth, imageWidth));
        scale.yProperty().bind(Helpers.divide(maxHeight, imageHeight));
        canvas.getTransforms().add(scale);

        canvas.prefWidthProperty().bind(imageWidth);
        canvas.minWidthProperty().bind(imageWidth);
        canvas.maxWidthProperty().bind(imageWidth);

        canvas.prefHeightProperty().bind(imageHeight);
        canvas.minHeightProperty().bind(imageHeight);
        canvas.maxHeightProperty().bind(imageHeight);
        //
        center();
        scrollPane.addEventFilter(ScrollEvent.SCROLL, this::handleScroll);
        canvasWrapper.prefHeightProperty().bind(imageView.fitHeightProperty());
        canvasWrapper.prefWidthProperty().bind(imageView.fitWidthProperty());
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    @FXML
    public void loadImage() {
        File file = fileChooser.showOpenDialog(scene.getWindow());
        if (file != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                imageView.setImage(image);
                resetImageView(imageView);
//                Bounds imageViewBounds = imageView.getBoundsInParent();
//                imageView.setTranslateX(scrollPane.getWidth() / 2 - imageViewBounds.getWidth() / 2);
//                imageView.setTranslateY(scrollPane.getHeight() / 2 - imageViewBounds.getHeight() / 2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void center() {
        if (imageView.getImage() == null) return;

        Bounds boundsInParent = imageView.getBoundsInParent();
        double imageWidth = boundsInParent.getWidth();
        double imageHeight = boundsInParent.getHeight();

        double scrollPaneWidth = scrollPane.getWidth();
        double scrollPaneHeight = scrollPane.getHeight();
        if (imageWidth < scrollPaneWidth) {
            //center horizontally
            double dX = imageWidth / 2 - imageWidth / 2;
            canvasWrapper.setTranslateX(dX);
        } else {
            canvasWrapper.setTranslateX(0);
        }

        if (imageHeight < scrollPaneHeight) {
            //center vertically
            double dY = scrollPaneHeight / 2 - imageHeight / 2;
            canvasWrapper.setTranslateY(dY);
        } else {
            canvasWrapper.setTranslateY(0);
        }
    }

    private static final double MAX_SCALE = 10.0d;
    private static final double MIN_SCALE = .1d;

    public void handleScroll(ScrollEvent event) {
        if (event.isControlDown()) {

            double delta = 1.1;

            double scale = imageView.getScaleX(); // currently we only use X, same value is used for Y
//            double oldScale = scale;

            if (event.getDeltaY() < 0)
                scale /= delta;
            else
                scale *= delta;

            scale = clamp(scale, MIN_SCALE, MAX_SCALE);

            imageView.getTransforms().add(Transform.scale(scale, scale));
//        imageView.setScaleX(scale);
//        imageView.setScaleY(scale);

//        Bounds imageViewBounds = imageView.getBoundsInParent();
//        double verticalPadding;
//        double horizontalPadding ;
//
//        horizontalPadding = imageViewBounds.getWidth() / 2 - scrollPane.getWidth() / 2;
//        verticalPadding = imageViewBounds.getHeight() / 2 - scrollPane.getHeight() / 2;
            event.consume();
        }
    }

    public void startPanning(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.MIDDLE) {
            scrollPane.setPannable(true);
        }
    }

    public void stopPanning(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.MIDDLE) {
            scrollPane.setPannable(false);
        }
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

    @FXML
    public void startDrawingRectangle() {
        if (currentDrawer != null)
            currentDrawer.stopDrawing();

        scene.setCursor(Cursor.CROSSHAIR);
        rectangleDrawer.startDrawing();
        currentDrawer = rectangleDrawer;
    }

    @FXML
    public void startDrawingEllipsis() {
        if (currentDrawer != null)
            currentDrawer.stopDrawing();

        scene.setCursor(Cursor.CROSSHAIR);
        ellipsisDrawer.startDrawing();
        currentDrawer = ellipsisDrawer;
    }

    @FXML
    public void startDrawingPolygon() {
        if (currentDrawer != null)
            currentDrawer.stopDrawing();

        scene.setCursor(Cursor.CROSSHAIR);
        polygonDrawer.startDrawing();
        currentDrawer = polygonDrawer;
    }

    @FXML
    public void setHandActive() {
        if (currentDrawer != null)
            currentDrawer.stopDrawing();
        Scene scene = borderPane.getScene();
        scene.setCursor(Cursor.HAND);
        currentDrawer = null;
    }
}
