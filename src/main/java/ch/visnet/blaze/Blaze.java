package ch.visnet.blaze;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Dialogs;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Blaze {
  private static Stage stage;
  public static final String SAVE_PATH = Main.BLAZE_HOME + "races.dat";
  static private ObservableList<Race> races = FXCollections.observableArrayList();
  private MainView view;

  public Blaze(Stage stage) {
    Blaze.stage = stage;
    loadRaces();
    new MainController(stage, races);
  }

  private void loadRaces() {
    if (! new File(SAVE_PATH).exists()) return;
    try {
      tryLoadRaces();
    } catch (Exception e) {
      Dialogs.showErrorDialog(stage, "Unable to restore state.");
      new File(SAVE_PATH).delete();
    }
  }

  private void tryLoadRaces() throws Exception {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_PATH));
    List<Race> races = (List<Race>)ois.readObject();
    Blaze.races = FXCollections.observableList(races);
    ois.close();
  }

  static public void saveRaces() {
    try {
      trySaveRaces();
    } catch (Exception e) {
      Dialogs.showErrorDialog(stage, "Unable to save the races to disk.");
    }
  }

  static private void trySaveRaces() throws Exception {
    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(Blaze.SAVE_PATH));
    List<Race> races = new ArrayList<Race>(Blaze.races);
    oos.writeObject(races);
    oos.close();
  }

}
