package presentation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import presentation.Controllers.Controller;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Views/main_window.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1200, 800);

        Controller controller = loader.getController();
        controller.setScene(scene);

        primaryStage.setTitle("Grafika komputerowa 2");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
