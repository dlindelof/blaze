package ch.visnet.blaze;

import javafx.animation.Animation;
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
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class RaceView {
  private Stage stage;
  private Scene parent;
  private Image[] digits = new Image[10];
  private ImageView[] clock = new ImageView[7];
  private ImageView[] odo   = new ImageView[6];
  private ImageView[] timer = new ImageView[7];
  private Race race;
  private Timeline raceTimeline = new Timeline();
  private Checkpoint nextCheckpoint;
  private LongProperty elapsedTime = new SimpleLongProperty();
  private SimpleDateFormat sdf = new SimpleDateFormat("mm:ss.S");
  private DecimalFormat df = new DecimalFormat("00,000");
  private SimpleDateFormat timerdf = new SimpleDateFormat("H:mm:ss");
  private MediaPlayer countdown = new MediaPlayer(new Media("file:" + System.getProperty("user.home") + "/.blaze/res/" + "CountDownFrom10.mp3"));
  private LongProperty elapsedDistance = new SimpleLongProperty();
  private Label checkpointId = new Label();
  private boolean raceOver = false;

  public RaceView(Stage stage, Scene parent, Race race) {
    this.race = race;
    this.parent = parent;
    this.stage = stage;
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
    for (int i=0;i<timer.length;i++)
      timer[i] = new ImageView(digits[8]);
    timerdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    timer[1].setImage(colon);
    timer[4].setImage(colon);
    raceTimeline.getKeyFrames().addAll(raceKeyFrame());
    raceTimeline.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        raceOver = true;
      }
    });
    elapsedTime.addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> observableValue, Number _, Number newElapsedTime) {
        long timeToNextCheckpoint = nextCheckpoint.getTimeOfPassageInMilliSeconds() - newElapsedTime.longValue();
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
    elapsedTime.addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> observableValue, Number _, Number newElapsedTime) {
        char[] time = timerdf.format(newElapsedTime.longValue()).toCharArray();
        timer[0].setImage(digits[time[0] - 48]);
        timer[2].setImage(digits[time[2] - 48]);
        timer[3].setImage(digits[time[3] - 48]);
        timer[5].setImage(digits[time[5] - 48]);
        timer[6].setImage(digits[time[6] - 48]);
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
    stage.setScene(build());
    stage.setFullScreen(true);
   }


  public Scene build() {
    HBox main = new HBox();
    //main.setAlignment(Pos.CENTER);
    main.getChildren().add(checkpointId);
    HBox.setMargin(checkpointId, new Insets(20));
    VBox digitsColumn = new VBox(20);
    main.getChildren().add(digitsColumn);
    digitsColumn.setAlignment(Pos.BASELINE_RIGHT);
    digitsColumn.getChildren().add(makeDigitsLabel("Temps restant [mm:ss.s]"));
    HBox clockBox = new HBox(8);
    clockBox.getChildren().addAll(clock);
    clockBox.setAlignment(Pos.BASELINE_RIGHT);
    digitsColumn.getChildren().add(clockBox);
    digitsColumn.getChildren().add(makeDigitsLabel("Distance totale [km]"));
    HBox odoBox = new HBox(8);
    odoBox.getChildren().addAll(odo);
    odoBox.setAlignment(Pos.BASELINE_RIGHT);
    digitsColumn.getChildren().add(odoBox);
    digitsColumn.getChildren().add(makeDigitsLabel("Temps total [h:mm:ss]"));
    HBox timerBox = new HBox(8);
    timerBox.getChildren().addAll(timer);
    timerBox.setAlignment(Pos.BASELINE_RIGHT);
    digitsColumn.getChildren().add(timerBox);
    Scene scene = new Scene(main, 600, 400);
    scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
      public void handle(KeyEvent keyEvent) {
        if (keyEvent.getCode() != KeyCode.SPACE) return;
        if (raceTimeline.getStatus() == Animation.Status.STOPPED && !raceOver) {
          nextCheckpoint = race.first();
          raceTimeline.play();
        } else {
          raceTimeline.stop();
          countdown.stop();
          stage.setFullScreen(false);
          stage.setScene(parent);
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
      long timeOfPassage = cp.getTimeOfPassageInMilliSeconds();
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
