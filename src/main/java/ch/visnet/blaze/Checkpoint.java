package ch.visnet.blaze;

import java.io.Serializable;

class Checkpoint implements Comparable<Checkpoint>, Serializable {
  private int position;
  private Checkpoint next, previous;
  private int speed;
  private int id;

  Checkpoint(int position, int speed) {
    this.position = position;
    this.speed = speed;
  }

  public Checkpoint(int position) {
    this.position = position;
    speed = -1;
  }

  public int compareTo(Checkpoint checkpoint) {
    return this.position < checkpoint.position ? -1 : this.position > checkpoint.position ? 1 : 0;
  }

  @Override
  public boolean equals(Object that) {
    if (that == null) return false;
    return this.getClass() == that.getClass() && this.position == ((Checkpoint) that).position;
  }

  public int getSpeed() {
    return speed < 0 ? previous.getSpeed() : speed;
  }

  public int getNewSpeed() {
    return speed;
  }

  public int getPosition() {
    return position;
  }

  public Checkpoint getNext() {
    return next;
  }

  public boolean hasNext() {
    return next != null;
  }

  public void setNext(Checkpoint next) {
    this.next = next;
  }

  public void setPrevious(Checkpoint previous) {
    this.previous = previous;
  }

  public long getTimeOfPassageInSeconds() {
    if (null == previous)
      return 0;
    return Math.round((position - previous.position) * 3600 / previous.getSpeed()) + previous.getTimeOfPassageInSeconds();
  }

  public void setId(int newId) {
    id = newId;
  }

  public int getId() {
    return id;
  }
}
