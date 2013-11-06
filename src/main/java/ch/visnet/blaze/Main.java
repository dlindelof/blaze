package ch.visnet.blaze;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.*;

public class Main extends Application {
  public static final String BLAZE_HOME = System.getProperty("user.home") + System.getProperty("file.separator") + ".blaze" + System.getProperty("file.separator");
  public static final String RES_HOME   = BLAZE_HOME + System.getProperty("file.separator") + "res" + System.getProperty("file.separator");

  public static void main(String[] args) {
    launch(args);
  }

  public void init() {
    try {
      createHomeDirectory();
      copyResourcesToHomeDirectory();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  private void createHomeDirectory() throws IOException {
    new File(BLAZE_HOME).mkdir();
    new File(RES_HOME).mkdir();
  }

  private void copyResourcesToHomeDirectory() throws IOException {
    byte[] buffer = new byte[1024];
    InputStream countdown = getClass().getResourceAsStream("/CountDownFrom10.mp3");
    OutputStream onDisk = new BufferedOutputStream(new FileOutputStream(RES_HOME + "CountDownFrom10.mp3"));
    int len = countdown.read(buffer);
    while (len != -1) {
      onDisk.write(buffer, 0, len);
      len = countdown.read(buffer);
    }
  }

  public void start(Stage stage) {
    stage.setTitle("Blaze");
    new Blaze(stage);
  }
}
