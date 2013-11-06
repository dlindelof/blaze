package ch.visnet.blaze;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Dialogs;
import javafx.stage.Stage;

public class MainController {
  private Stage stage;
  private ObservableList<Race> races;
  private MainView view;

  public MainController(Stage stage, ObservableList<Race> races) {
    this.stage = stage;
    this.races = races;
    view = new MainView(stage, this, races);
  }

  public void newRace(String name) {
    Race race = new Race(name);
    races.add(race);
    Blaze.saveRaces();
    selectRace(race);
    view.clearNewRaceField();
  }

  public void selectRace(Race race) {
    view.select(race);
    view.setEditRaceController(new EditRaceController(stage, view, race));
  }

  public void addSetpoint(Race selectedRace, String positionAsText) {
    if (null == selectedRace) {
      Dialogs.showWarningDialog(stage, "Select a race first");
      return;
    }
    try {
      new EditRaceController(stage, view, selectedRace).addSetpoint(Integer.parseInt(positionAsText));
    } catch (NumberFormatException e) {
      Dialogs.showErrorDialog(stage, "Invalid position");
    }
  }

  public void runRace(Race selectedRace, Scene parent) {
    if (null == selectedRace) {
      Dialogs.showWarningDialog(stage, "Select a race first");
      return;
    }
    new RunRaceController(stage, parent, selectedRace).run();
  }
}
