package ch.visnet.blaze;

public class CheckpointBuilder {
  private int position;
  private int speed = -1;

  public void setPosition(int position) {
    this.position = position;
  }

  public Checkpoint build() {
    if (speed == -1)
      return new Checkpoint(position);
    else
      return new Checkpoint(position, speed);
  }

  public void setNewSpeed(int newSpeed) {
    speed = newSpeed;
  }
}
