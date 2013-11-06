package ch.visnet.blaze;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MainView {
  private Stage stage;
  private Scene scene;
  private MainController controller;
  private ObservableList<Race> races;
  private EditRaceController editRaceController;
  private final ObjectProperty<Race> selectedRace = new SimpleObjectProperty<>();
  private Button runButton = new Button("RUN");
  private ListView<Race> racesList;
  private ListView<Checkpoint> checkpointList;
  private TextField newRaceField;
  private RunRaceController runRaceController;

  public MainView(Stage stage, MainController controller, ObservableList<Race> races) {
    this.stage = stage;
    this.controller = controller;
    this.races = races;
    racesList = new ListView<>(races);
    checkpointList = new ListView<>();
    scene = buildScene();
    stage.setScene(scene);
    stage.show();
  }

  public Scene buildScene() {
    BorderPane border = new BorderPane();
    Scene result = new Scene(border, 800, 500);
    border.setLeft(leftPane());
    border.setCenter(centerPane());
    border.setRight(rightPane(runButton));
    return result;
  }

  Node leftPane() {
    VBox leftPane = new VBox();
    newRaceField = new TextField();
    newRaceField.setPromptText("nouvelle course");
    newRaceField.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent actionEvent) {
        controller.newRace(newRaceField.getText());
      }
    });
    leftPane.getChildren().add(racesList);
    racesList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Race>() {
      @Override
      public void changed(ObservableValue<? extends Race> observableValue, Race race, Race race2) {
        controller.selectRace(race2);
      }
    }
    );
    selectedRace.bind(racesList.getSelectionModel().selectedItemProperty());
    leftPane.getChildren().add(newRaceField);
    return leftPane;
  }


  Node centerPane() {
    VBox centerPane = new VBox();
    checkpointList.setCellFactory(new Callback<ListView<Checkpoint>, ListCell<Checkpoint>>() {
      public ListCell<Checkpoint> call(ListView<Checkpoint> checkpointListView) {
        return new CheckpointCell();
      }
    });
    checkpointList.setEditable(true);
    final TextField positionField = new TextField();
    positionField.setPromptText("nouvelle position");
    positionField.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        controller.addSetpoint(selectedRace.get(), positionField.getText());
      }
    });
    centerPane.getChildren().addAll(checkpointList, positionField);
    return centerPane;
  }

  Node rightPane(Button runButton) {
    VBox rightPane = new VBox();
    rightPane.getChildren().add(runButton);
    runButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        controller.runRace(selectedRace.get(), scene);
      }
    });
    return rightPane;
  }

  public void setEditRaceController(EditRaceController editRaceController) {
    this.editRaceController = editRaceController;
  }

  public void showWarning(String message) {
    Dialogs.showWarningDialog(stage, message);
  }

  public void select(Race race) {
    racesList.getSelectionModel().select(race);
    checkpointList.setItems(race.getCheckpoints());
  }

  public void raceUpdated(Race race) {
    checkpointList.setItems(race.getCheckpoints());
  }

  public void clearNewRaceField() {
    newRaceField.clear();
  }

  public void setRunRaceController(RunRaceController runRaceController) {
    this.runRaceController = runRaceController;
  }

  class CheckpointCell extends ListCell<Checkpoint> {
    private HBox contents;

    @Override
    public void updateItem(final Checkpoint checkpoint, boolean empty) {
      super.updateItem(checkpoint, empty);
      if (empty) return;
      contents = new HBox();
      final Label idField = new Label(Integer.toString(checkpoint.getId()));
      final TextField positionField = new TextField(Integer.toString(checkpoint.getPosition()));
      positionField.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent actionEvent) {
          editRaceController.changeCheckpointPosition(selectedRace.get(), checkpoint, Integer.parseInt(positionField.getText()));
        }
      });
      final TextField newSpeedField = new TextField();
      if (-1 != checkpoint.getNewSpeed())
        newSpeedField.setText(Integer.toString(checkpoint.getNewSpeed()));
      newSpeedField.setPromptText("nouvelle vitesse");
      newSpeedField.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent actionEvent) {
          editRaceController.changeCheckpointSpeed(selectedRace.get(), checkpoint, Integer.parseInt(newSpeedField.getText()));
        }
      });
      Button deleteButton = new Button("DELETE");
      deleteButton.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent actionEvent) {
          editRaceController.deleteCheckpoint(selectedRace.get(), checkpoint);
        }
      });
      contents.getChildren().add(idField);
      contents.getChildren().add(positionField);
      contents.getChildren().add(newSpeedField);
      contents.getChildren().add(deleteButton);
      setGraphic(contents);
    }

  }
}
