package fileManager;

import fileManager.Models.EllipseModel;
import fileManager.Models.PolygonModel;
import fileManager.Models.RectangleModel;
import fileManager.Models.ShapeModel;
import javafx.collections.ObservableList;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by bider_000 on 06.11.2015.
 */
public class FileManager implements IFileManager {
    private static FileManager ourInstance = new FileManager();

    public static FileManager getInstance() {
        return ourInstance;
    }

    private FileManager() {
    }

    @Override
    public Response load(String path) {
        try {
            XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(path)));
            Object content = decoder.readObject();

            return new Response(true, "Successfully loaded from " + path, content);
        } catch (Exception e) {
            return new Response(false, "Failed loading file");
        }
    }

    @Override
    public Response saveShapes(ObservableList<Shape> shapes, String path) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);

            ArrayList<ShapeModel> models = new ArrayList<>(shapes.size());
//            Map<String, ShapeModel> shapesMap = new HashMap<String, ShapeModel>(shapes.size());

            for (Shape shape : shapes) {
                if (shape instanceof Ellipse)
                    models.add(new EllipseModel((Ellipse) shape));
                else if (shape instanceof Rectangle)
                    models.add(new RectangleModel((Rectangle) shape));
                else if (shape instanceof Polygon)
                    models.add(new PolygonModel((Polygon) shape));
            }

            mapper.writeValue(new File(path), models);
            return new Response(true, "Successfully saved to " + path);
        } catch (Exception e) {
            return new Response(false, "Failed saving file");
        }
    }

    @Override
    public Response loadShapes(String path) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);

            ArrayList<Object> test = new ArrayList<>();
            ArrayList<ShapeModel> models = mapper.readValue(new File(path), new TypeReference<ArrayList<ShapeModel>>() {});

            ArrayList<Shape> viewModels = new ArrayList<>(models.size());

            for (ShapeModel model : models) {
                if (model instanceof EllipseModel)
                    viewModels.add(model.toViewModel());
                else if (model instanceof RectangleModel)
                    viewModels.add(model.toViewModel());
                else if (model instanceof PolygonModel)
                    viewModels.add(model.toViewModel());
            }
            return new Response(true, "Successfully loaded from " + path, viewModels);

        } catch (Exception e) {
            return new Response(false, "Failed loading file");
        }
    }

    @Override
    public Response save(Object object, String path) {
        try {
            XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(path)));
            encoder.writeObject(object);
            encoder.flush();
            return new Response(true, "Successfully saved to " + path);
        } catch (Exception e) {
            return new Response(false, "Failed saving file");
        }
    }
}
