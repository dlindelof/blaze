package ch.visnet.blaze;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CheckpointTest {
  private CheckpointBuilder cb;

  @Before
  public void setup() {
    cb = new CheckpointBuilder();
  }

  @Test
  public void builderCanCreateCheckpoint() {
    Checkpoint result;
    cb.setPosition(1200);
    result = cb.build();
    assertEquals(1200, result.getPosition());
    assertEquals(-1, result.getNewSpeed());
  }

  @Test
  public void builderCanCreateCheckpointWithNewPositionAndSpeed() {
    Checkpoint result;
    cb.setPosition(1000);
    cb.setNewSpeed(33);
    result = cb.build();
    assertEquals(1000, result.getPosition());
    assertEquals(33, result.getNewSpeed());
  }

  @Test
  public void knowsTimeOfPassage() {
    Checkpoint cp1, cp2;
    cp1 = new Checkpoint(0, 55);
    cp2 = new Checkpoint(1000);
    cp2.setPrevious(cp1);
    assertEquals(3600000. / 55, cp2.getTimeOfPassage(), 1);
  }

}
