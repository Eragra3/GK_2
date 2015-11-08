package presentation.Controllers;

import common.Helpers;
import fileManager.FileManager;
import fileManager.IFileManager;
import fileManager.Response;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import logic.Drawer;
import logic.EllipseDrawer;
import logic.PolygonDrawer;
import logic.RectangleDrawer;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;

import javax.imageio.ImageIO;
import javax.xml.bind.annotation.XmlRootElement;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@XmlRootElement(namespace = "presentation.Controllers")
public class Controller {

    final FileChooser fileChooser = new FileChooser();
    final IFileManager xmlFileManager = FileManager.getInstance();

    ObservableList<Shape> addedShapes = FXCollections.observableArrayList();

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
    @FXML
    ListView listView;

    Scene scene;

    //Drawers
    private RectangleDrawer rectangleDrawer;
    private EllipseDrawer ellipseDrawer;
    private PolygonDrawer polygonDrawer;
    private Drawer currentDrawer;

    //Binding properties
    private DoubleProperty imageScale = new SimpleDoubleProperty(100);
    private DoubleProperty imageWidth;
    private DoubleProperty imageHeight;

    public Controller() {
    }

    public void initialize() {
        rectangleDrawer = new RectangleDrawer(canvas, addedShapes);
        ellipseDrawer = new EllipseDrawer(canvas, addedShapes);
        polygonDrawer = new PolygonDrawer(canvas, addedShapes);
        currentDrawer = null;

        addedShapes.addListener((ListChangeListener<Shape>) c -> {
            while (c.next()) {
                if (c.wasRemoved()) {
                    for (Shape item : c.getRemoved())
                        canvas.getChildren().remove(item);
                } else if (c.wasAdded()) {
                    for (Shape item : c.getAddedSubList())
                        canvas.getChildren().add(item);
                }
            }
        });

        listView.setItems(addedShapes);
        listView.setCellFactory(list -> {
            final ListCell<Shape> cell = new ShapeCell();
            cell.setOnMouseEntered(event -> {
                final Object selectedItem = listView.getSelectionModel().getSelectedItem();
                final Shape shape = cell.getItem();
                if (shape != null && cell.isHover() && selectedItem != shape) {
                    shape.setFill(Color.ORANGE);
                    shape.setOpacity(1);
                }
            });
            cell.setOnMouseExited(event -> {
                final Shape shape = cell.getItem();
                if (shape != null && listView.getSelectionModel().getSelectedItem() != shape) {
                    shape.setFill(Drawer.mainColor);
                    shape.setOpacity(Drawer.shapeOpacity);
                }
            });
            cell.setOnMousePressed(event -> {
                final Object selectedItem = listView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    addedShapes.forEach(shape -> {
                        shape.setFill(Drawer.mainColor);
                        shape.setOpacity(Drawer.shapeOpacity);
                    });
                    ((Shape) selectedItem).setFill(Color.RED);
                    ((Shape) selectedItem).setOpacity(1);
                }
            });
            listView.setOnKeyReleased(event -> {
                final Object selectedItem = listView.getSelectionModel().getSelectedItem();
                if (event.getCode() == KeyCode.DELETE) {
                    addedShapes.remove(selectedItem);
                } else if ((event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) && selectedItem != null) {
                    addedShapes.forEach(shape -> {
                        shape.setFill(Drawer.mainColor);
                        shape.setOpacity(Drawer.shapeOpacity);
                    });
                    ((Shape) selectedItem).setFill(Color.RED);
                    ((Shape) selectedItem).setOpacity(1);
                }
            });
            return cell;
        });

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
//        center();
        scrollPane.addEventFilter(ScrollEvent.SCROLL, this::handleScroll);
        canvasWrapper.prefHeightProperty().bind(imageView.fitHeightProperty());
        canvasWrapper.prefWidthProperty().bind(imageView.fitWidthProperty());

        borderPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                if (currentDrawer != null)
                    currentDrawer.stopDrawing();
                scene.setCursor(Cursor.DEFAULT);
            }
        });
    }

    static class ShapeCell extends ListCell<Shape> {

        @Override
        public void updateItem(Shape item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(item.getId());
            } else {
                setText("");
            }
        }
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

    @FXML
    public void importShapes() {
        Response response = xmlFileManager.loadShapes("shapes.dat");
        if (response.success) {
//            addedShapes.clear();
            addedShapes.addAll((ArrayList<Shape>) response.content);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Import");
            alert.setHeaderText("Success");
            alert.setContentText(response.message);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Import");
            alert.setHeaderText("Error");
            alert.setContentText(response.message);
            alert.showAndWait();
        }
    }

    @FXML
    public void exportShapes() {
        Response response = xmlFileManager.saveShapes(addedShapes, "shapes.dat");
        if (response.success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export");
            alert.setContentText(response.message);
            alert.setHeaderText("Success");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Export");
            alert.setContentText(response.message);
            alert.setHeaderText("Error");
            alert.showAndWait();
        }
    }

    @FXML
    public void deleteAllShapes() {
        addedShapes.clear();
        canvas.getChildren().clear();
        EllipseDrawer.resetIdCounter();
        PolygonDrawer.resetIdCounter();
        RectangleDrawer.resetIdCounter();
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
        ellipseDrawer.startDrawing();
        currentDrawer = ellipseDrawer;
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
