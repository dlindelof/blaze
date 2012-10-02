package ch.visnet.blaze;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RaceView {
  private Stage stage;
  private Scene parent;
  private Image[] digits = new Image[10];
  private ImageView[] clock = new ImageView[7];
  private ImageView[] odo = new ImageView[6];
  private Race race;
  private Timeline raceTimeline = new Timeline();
  private Checkpoint nextCheckpoint;
  private LongProperty elapsedTime = new SimpleLongProperty();
  private SimpleDateFormat sdf = new SimpleDateFormat("mm:ss.S");
  private DecimalFormat df = new DecimalFormat("00,000");
  private MediaPlayer countdown = new MediaPlayer(new Media("file:" + System.getProperty("user.home") + "/.blaze/res/" + "CountDownFrom10.mp3"));
  private LongProperty elapsedDistance = new SimpleLongProperty();
  private Label checkpointId = new Label();

  public RaceView(Stage stage, Scene parent, Race race) {
    this.race = race;
    this.stage = stage;
    this.parent = parent;
    for (int i=0;i<digits.length;i++) {
      digits[i] = new Image(i + ".png");
    }
    Image colon = new Image(10 + ".png");
    Image dot = new Image(11 + ".png");
    for (int i=0;i<clock.length;i++)
      clock[i] = new ImageView(digits[8]);
    clock[2].setImage(colon);
    clock[5].setImage(dot);
    for (int i=0;i<odo.length;i++)
      odo[i] = new ImageView(digits[8]);
    odo[2].setImage(dot);
    raceTimeline.getKeyFrames().addAll(raceKeyFrame());
    elapsedTime.addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> observableValue, Number _, Number newElapsedTime) {
        long timeToNextCheckpoint = nextCheckpoint.getTimeOfPassage() - newElapsedTime.longValue();
        if (timeToNextCheckpoint <= 0) {
          timeToNextCheckpoint = 0;
        }
        if (timeToNextCheckpoint <= 11000)
          countdown.play();
        else
          countdown.stop();
        char[] time = sdf.format(timeToNextCheckpoint).toCharArray();
        clock[0].setImage(digits[time[0] - 48]);
        clock[1].setImage(digits[time[1] - 48]);
        clock[3].setImage(digits[time[3] - 48]);
        clock[4].setImage(digits[time[4] - 48]);
        clock[6].setImage(digits[time[6] - 48]);
      }
    });
    elapsedDistance.addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> observableValue, Number _, Number newElapsedDistance) {
        char distance[] = df.format(newElapsedDistance).toCharArray();
        odo[0].setImage(digits[distance[0]-48]);
        odo[1].setImage(digits[distance[1]-48]);
        odo[3].setImage(digits[distance[3]-48]);
        odo[4].setImage(digits[distance[4]-48]);
        odo[5].setImage(digits[distance[5]-48]);
      }
    });
    checkpointId.setText("1");
    checkpointId.setFont(new Font(200));
    checkpointId.setAlignment(Pos.CENTER_RIGHT);
   }


  public Scene build() {
    HBox main = new HBox();
    //main.setAlignment(Pos.CENTER);
    main.getChildren().add(checkpointId);
    HBox.setMargin(checkpointId, new Insets(20));
    VBox digitsColumn = new VBox(10);
    main.getChildren().add(digitsColumn);
    digitsColumn.setAlignment(Pos.BASELINE_RIGHT);
    digitsColumn.getChildren().add(makeDigitsLabel("Temps restant"));
    HBox clockBox = new HBox();
    clockBox.getChildren().addAll(clock);
    clockBox.setAlignment(Pos.BASELINE_RIGHT);
    digitsColumn.getChildren().add(clockBox);
    digitsColumn.getChildren().add(makeDigitsLabel("Distance totale"));
    HBox odoBox = new HBox();
    odoBox.getChildren().addAll(odo);
    odoBox.setAlignment(Pos.BASELINE_RIGHT);
    digitsColumn.getChildren().add(odoBox);
    Scene scene = new Scene(main, 600, 400);
    scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
      public void handle(KeyEvent keyEvent) {
        if (keyEvent.getCode() != KeyCode.SPACE) return;
        switch (raceTimeline.getStatus()) {
          case STOPPED:
            nextCheckpoint = race.getCheckpoints().first();
            raceTimeline.play();
            break;
          case RUNNING:
            countdown.stop();
            stage.setScene(parent);
            break;
        }
      }
    });
    return scene;
  }

  Label makeDigitsLabel(String text) {
    Label result = new Label(text);
    result.setFont(new Font(60));
    return result;
  }

  private List<KeyFrame> raceKeyFrame() {
    List<KeyFrame> result = new ArrayList<KeyFrame>();
    for (Checkpoint cp : race.getCheckpoints()) {
      long timeOfPassage = cp.getTimeOfPassage();
      KeyFrame keyFrame = new KeyFrame(new Duration(timeOfPassage),
              new EventHandler<ActionEvent>() {
                public void handle(ActionEvent actionEvent) {
                  nextCheckpoint = nextCheckpoint.getNext();
                  if (null != nextCheckpoint)
                    checkpointId.setText(Integer.toString(nextCheckpoint.getId()));
                }
              },
              new KeyValue(elapsedTime, timeOfPassage),
              new KeyValue(elapsedDistance, cp.getPosition()));

      result.add(keyFrame);
    }
    return result;
  }
}
