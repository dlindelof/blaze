package ch.visnet.blaze;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CheckpointTest {

  @Test
  public void knowsTimeOfPassage() {
    Checkpoint cp1, cp2;
    cp1 = new Checkpoint(0, 55);
    cp2 = new Checkpoint(1000);
    cp2.setPrevious(cp1);
    assertEquals(3600000. / 55, cp2.getTimeOfPassageInMilliSeconds(), 1);
  }

}
