package ch.visnet.blaze;

import java.io.Serializable;
import java.util.*;

public class Race implements Serializable {
  static final int DEFAULT_SPEED = 55;

  final String name;
  private SortedSet<Checkpoint> checkpoints = new TreeSet<Checkpoint>();
  private List<Leg> legs = new ArrayList<Leg>();

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
    for (Checkpoint cp : checkpoints) {
      if (cp.compareTo(checkpoint) >= 0) {
        if (cp.compareTo(checkpoint) == 0)
          checkpoints.remove(cp);
        break;
      }
    }
    checkpoints.add(checkpoint);
    updateCheckpoints();
    updateLegs();
  }

  private void updateCheckpoints() {
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

  public SortedSet<Checkpoint> getCheckpoints() {
    return Collections.unmodifiableSortedSet(checkpoints);
  }

  public void removeCheckpoint(int position) {
    Checkpoint cpToRemove = null;
    for (Checkpoint cp : checkpoints) {
      if (cp.getPosition() == position) {
        cpToRemove = cp;
        break;
      }
    }
    if (null != cpToRemove)
      checkpoints.remove(cpToRemove);
  }

  public long getTotalTime() {
    long result = 0;
    for (Leg leg : legs)
      result += leg.getDuration();
    return result;
  }

  private class Leg implements Serializable {
    private int distance;
    private int speed;
    private long duration;
    private Checkpoint end;

    Leg(Checkpoint start, Checkpoint end) {
      speed = start.getSpeed();
      distance = end.getPosition() - start.getPosition();
      duration = distance * 3600 / speed;
    }

    public long getDuration() {
      return duration;
    }

    public Checkpoint getEnd() {
      return end;
    }
  }

}
