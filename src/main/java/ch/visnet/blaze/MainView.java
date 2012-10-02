package ch.visnet.blaze;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MainView {
  public static final String SAVE_PATH = Blaze.BLAZE_HOME + "races.dat";
  private final ObservableList<Race> races = FXCollections.observableArrayList();
  private final ObservableList<Checkpoint> checkpoints = FXCollections.observableArrayList();
  private ObjectProperty<Race> selectedRace = new SimpleObjectProperty<Race>();
  private Text messages = new Text();

  public Scene build(final Stage stage) {
    BorderPane border = new BorderPane();
    // Left
    border.setLeft(leftPane());
    // Center
    border.setCenter(centerPane());
    // Right
    Button runButton = new Button("RUN");
    VBox rightPane = new VBox();
    rightPane.getChildren().add(runButton);
    border.setRight(rightPane);
    // Bottom
    border.setBottom(messages);
    loadRaces();
    final Scene result = new Scene(border, 600, 400);
    runButton.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent actionEvent) {
        if (selectedRace == null || selectedRace.get() == null) return;
        stage.setScene(new RaceView(stage, result, selectedRace.get()).build());
      }
    });
    return result;
  }

  Node leftPane() {
    VBox leftPane = new VBox();
    final ListView<Race> racesList = new ListView<Race>(races);
    selectedRace.bind(racesList.getSelectionModel().selectedItemProperty());
    selectedRace.addListener(new ChangeListener<Race>() {
      public void changed(ObservableValue<? extends Race> observableValue, Race race, Race race1) {
        checkpoints.setAll(race1.getCheckpoints());
      }
    });
    final TextField newRaceField = new TextField();
    newRaceField.setPromptText("nouvelle course");
    newRaceField.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent actionEvent) {
        Race newRace = new Race(newRaceField.getText());
        races.add(newRace);
        saveRaces();
        racesList.getSelectionModel().select(newRace);
        checkpoints.setAll(newRace.getCheckpoints());
        newRaceField.setText("");
      }
    });
    leftPane.getChildren().add(racesList);
    leftPane.getChildren().add(newRaceField);
    return leftPane;
  }

  private void saveRaces() {
    try {
      trySaveRaces();
    } catch (Exception e) {
      messages.setText("Unable to save state.\n\n" + e.getMessage());
    }
  }

  private void trySaveRaces() throws Exception {
    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_PATH));
    List<Race> races = new ArrayList<Race>(this.races);
    oos.writeObject(races);
    oos.close();
  }

  private void loadRaces() {
    try {
      tryLoadRaces();
    } catch (Exception e) {
      messages.setText("Unable to restore state.\n\n" + e.getMessage());
      new File(SAVE_PATH).delete();
    }
  }

  private void tryLoadRaces() throws Exception {
    List<Race> races;
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_PATH));
    races = (List<Race>)ois.readObject();
    this.races.addAll(races);
    ois.close();
  }

  Node centerPane() {
    VBox centerPane = new VBox();
    ListView<Checkpoint> checkpointList = new ListView<Checkpoint>(checkpoints);
    checkpointList.setCellFactory(new Callback<ListView<Checkpoint>, ListCell<Checkpoint>>() {
      public ListCell<Checkpoint> call(ListView<Checkpoint> checkpointListView) {
        return new CheckpointCell();
      }
    });
    checkpointList.setEditable(true);
    final TextField positionField = new TextField();
    positionField.setPromptText("nouvelle position");
    positionField.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent actionEvent) {
        if (null == selectedRace) return;
        selectedRace.get().addCheckpoint(Integer.parseInt(positionField.getText()));
        saveRaces();
        checkpoints.setAll(selectedRace.get().getCheckpoints());
        positionField.setText("");
      }
    });
    centerPane.getChildren().addAll(checkpointList, positionField);
    return centerPane;
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
          selectedRace.get().removeCheckpoint(checkpoint.getPosition());
          selectedRace.get().addCheckpoint(Integer.parseInt(positionField.getText()));
          checkpoints.setAll(selectedRace.get().getCheckpoints());
        }
      });
      final TextField newSpeedField = new TextField();
      if (-1 != checkpoint.getNewSpeed())
        newSpeedField.setText(Integer.toString(checkpoint.getNewSpeed()));
      newSpeedField.setPromptText("nouvelle vitesse");
      newSpeedField.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent actionEvent) {
          selectedRace.get().addCheckpoint(checkpoint.getPosition(), Integer.parseInt(newSpeedField.getText()));
          checkpoints.setAll(selectedRace.get().getCheckpoints());
        }
      });
      Button deleteButton = new Button("DELETE");
      deleteButton.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent actionEvent) {
          selectedRace.get().removeCheckpoint(checkpoint.getPosition());
          checkpoints.setAll(selectedRace.get().getCheckpoints());
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
