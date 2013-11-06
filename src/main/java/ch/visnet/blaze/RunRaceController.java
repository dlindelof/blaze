package ch.visnet.blaze;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class RunRaceController {
  private Scene parent;
  private Stage stage;
  private Race race;

  public RunRaceController(Stage stage, Scene parent, Race race) {
    this.parent = parent;
    this.race  = race;
    this.stage = stage;
  }

  public void run() {
    new RaceView(stage, parent, race);
  }
}
