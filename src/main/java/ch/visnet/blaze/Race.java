package ch.visnet.blaze;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Race implements Serializable {
  static final int DEFAULT_SPEED = 55;

  private String name;
  private ObservableList<Checkpoint> checkpoints = FXCollections.observableArrayList();
  private List<Leg> legs = new ArrayList<>();

  public Race(String name) {
    this.name = name;
    addCheckpoint(0, DEFAULT_SPEED);
  }

  @Override
  public String toString() {
    return name;
  }

  public void addCheckpoint(int distance) {
    addCheckpoint(new Checkpoint(distance));
  }

  public void addCheckpoint(int distance, int speed) {
    addCheckpoint(new Checkpoint(distance, speed));
  }

  private void addCheckpoint(Checkpoint checkpoint) {
    if (checkpoints.contains(checkpoint))
      checkpoints.remove(checkpoint);
    checkpoints.add(checkpoint);
    updateCheckpoints();
    updateLegs();
  }

  public Checkpoint first() {
    return checkpoints.get(0);
  }

  public Checkpoint last() {
    return checkpoints.get(checkpoints.size() - 1);
  }

  private void updateCheckpoints() {
    FXCollections.sort(checkpoints);
    Iterator<Checkpoint> it = checkpoints.iterator();
    Checkpoint previousCP = null;
    int id = 1;
    while (it.hasNext()) {
      Checkpoint cp = it.next();
      if (null != previousCP) {
        previousCP.setNext(cp);
        cp.setPrevious(previousCP);
      }
      previousCP = cp;
      cp.setId(id++);
    }
  }

  private void updateLegs() {
    legs.clear();
    for (Checkpoint checkpoint: checkpoints) {
      if (checkpoint.hasNext())
        legs.add(new Leg(checkpoint, checkpoint.getNext()));
    }
  }

  public int numCheckpoints() {
    return checkpoints.size();
  }

  public int nextCheckpointAtTime(long time) {
    long totalTime = 0;
    int checkpoint = 0;
    for (Leg leg : legs) {
      totalTime += leg.getDuration();
      checkpoint += 1;
      if (totalTime > time)
        return checkpoint;
    }
    return -1; // cannot happen
  }

  public ObservableList<Checkpoint> getCheckpoints() {
    return FXCollections.unmodifiableObservableList(checkpoints);
  }

  public void removeCheckpoint(int position) {
    checkpoints.remove(new Checkpoint(position));
  }

  public long getTotalTime() {
    long result = 0;
    for (Leg leg : legs)
      result += leg.getDuration();
    return result;
  }

  private void writeObject(ObjectOutputStream oos) throws IOException {
    List serializableCheckpoints = new ArrayList(checkpoints);
    oos.writeObject(name);
    oos.writeObject(serializableCheckpoints);
  }

  private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
    List deserializedCheckpoints;
    name = (String)ois.readObject();
    deserializedCheckpoints = (ArrayList)ois.readObject();
    checkpoints = FXCollections.observableList(deserializedCheckpoints);
    legs = new ArrayList<>();
    updateCheckpoints();
    updateLegs();
  }

  private class Leg implements Serializable {
    private int distance;
    private int speed;
    private long duration;

    Leg(Checkpoint start, Checkpoint end) {
      speed = start.getSpeed();
      distance = end.getPosition() - start.getPosition();
      duration = distance * 3600 / speed;
    }

    public long getDuration() {
      return duration;
    }

  }

}
