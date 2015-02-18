package ch.visnet.blaze;

import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class RaceTest {
  private Race race;

  @Before
  public void setUp() {
    race = new Race("Mario Kart");
  }

  @Test
  public void raceAsString() {
    assertEquals("Mario Kart", race.toString());
  }

  @Test
  public void raceHasInitialCheckpoint() {
    assertEquals(1, race.numCheckpoints());
  }

  @Test
  public void canEnterCheckpoint() {
    race.addCheckpoint(1642);
    assertEquals(2, race.numCheckpoints());
  }

  @Test
  public void canEnterCheckpointWithNewSpeed() {
    race.addCheckpoint(1843, 55);
    assertEquals(2, race.numCheckpoints());
  }

  @Test
  public void canEnterCheckpointWithNonIntegralSpeed() {
    int someDistance = 42;
    double someNonIntegralSpeed = 40.5;
    race.addCheckpoint(someDistance, someNonIntegralSpeed);
    assertEquals(someNonIntegralSpeed, race.getCheckpoints().get(1).getNewSpeed(), 0.001);
  }

  @Test
  public void givesNextCheckpointAtTime() {
    race.addCheckpoint(0, 55);
    race.addCheckpoint(1200);
    assertEquals(1, race.nextCheckpointAtTime(40));
  }

  @Test
  public void canDeleteCheckpoint() {
    race.addCheckpoint(1300);
    race.addCheckpoint(1400);
    race.removeCheckpoint(1400);
    assertEquals(2, race.numCheckpoints());
  }

  @Test
  public void canGetCollectionOfCheckpoints() {
    List<Checkpoint> checkpoints;
    race.addCheckpoint(0, 55);
    race.addCheckpoint(1200);
    race.addCheckpoint(123);
    checkpoints = race.getCheckpoints();
    assertEquals(3, checkpoints.size());
    assertEquals(0, race.first().getPosition());
    assertEquals(1200, race.last().getPosition());
    assertEquals(55, race.last().getSpeed());
  }

  @Test
  public void computesTotalTime() {
    race.addCheckpoint(1000, 60);
    race.addCheckpoint(2000, 40);
    race.addCheckpoint(3000);
    assertEquals(3600000. / Race.DEFAULT_SPEED + 3600000. / 60 + 3600000. / 40,
            race.getTotalTime(), 1);
  }

  @Test
  public void aRace_canBeSerialized() throws IOException, ClassNotFoundException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);
    Race newRace;
    race.addCheckpoint(1000, 60);
    race.addCheckpoint(2000, 40);
    race.addCheckpoint(3000);
    oos.writeObject(race);
    newRace = (Race) new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray())).readObject();
    assertEquals(race.getTotalTime(), newRace.getTotalTime());
  }

  @Test
  public void twoRaces_canBeSerialized() throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);
    List<Race> races = new ArrayList<Race>();
    races.add(new Race("Foo"));
    races.add(new Race("Bar"));
    oos.writeObject(races);
    assertEquals(2, ((List) new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray())).readObject()).size());
  }
}
