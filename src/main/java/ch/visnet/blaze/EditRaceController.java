package ch.visnet.blaze;

import javafx.scene.control.Dialogs;
import javafx.stage.Stage;

public class EditRaceController {
  private Stage stage;
  private MainView view;
  private Race race;

  public EditRaceController(Stage stage, MainView view, Race race) {
    this.stage = stage;
    this.view = view;
    this.race = race;
  }

  public void addSetpoint(int position) {
    if (null == race) {
      Dialogs.showWarningDialog(stage, "Select a race first");
      return;
    }
    race.addCheckpoint(position);
    view.raceUpdated(race);
    Blaze.saveRaces();
  }

  public void changeCheckpointPosition(Race race, Checkpoint checkpoint, int newPosition) {
    race.removeCheckpoint(checkpoint.getPosition());
    race.addCheckpoint(newPosition);
    Blaze.saveRaces();
  }

  public void changeCheckpointSpeed(Race race, Checkpoint checkpoint, double newSpeed) {
    race.addCheckpoint(checkpoint.getPosition(), newSpeed);
    Blaze.saveRaces();
  }

  public void deleteCheckpoint(Race race, Checkpoint checkpoint) {
    race.removeCheckpoint(checkpoint.getPosition());
    Blaze.saveRaces();
  }
}
