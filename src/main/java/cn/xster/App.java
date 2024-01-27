package cn.xster;

import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * JavaFX App
 */
public class App extends Application {
  private static Scene scene;
  private static FXMLLoader fxmlLoader;
  private static List<String> args;
  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

  @Override
  public void init() {
    LOGGER.info("Application init");
  }

  @Override
  public void start(Stage stage) throws IOException {
    LOGGER.info("Application start...");

    scene = new Scene(loadFXML("primary"), 900, 600);
    stage.setTitle("TFT");

//        String imgPath = String.valueOf(ImageUtils.imgPathUri("logo.png"));
//        stage.getIcons().add(new Image(imgPath));
    stage.setScene(scene);


    try {
      stage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void stop() {
    LOGGER.info("Application stopped");
  }

  private static Parent loadFXML(String fxml) throws IOException {
    URL resource = App.class.getResource(fxml + ".fxml");
    App.class.getResource(fxml + ".fxml");
    fxmlLoader = new FXMLLoader(resource);

    return fxmlLoader.load();
  }

}