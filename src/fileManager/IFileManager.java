package fileManager;

import javafx.collections.ObservableList;
import javafx.scene.shape.Shape;

/**
 * Created by bider_000 on 06.11.2015.
 */
public interface IFileManager {
    Response load(String path);

    Response save(Object object, String path);

    Response saveShapes(ObservableList<Shape> shapes, String path);

    Response loadShapes(String path);
}
