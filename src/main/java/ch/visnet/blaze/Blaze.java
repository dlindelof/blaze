package ch.visnet.blaze;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Blaze extends Application {
  public static final String BLAZE_HOME = System.getProperty("user.home")+System.getProperty("file.separator")+".blaze"+System.getProperty("file.separator");

    public void start(Stage stage) {
        Scene scene = new MainView().build(stage);
        stage.setScene(scene);
        stage.setTitle("Blaze");
        stage.setFullScreen(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
