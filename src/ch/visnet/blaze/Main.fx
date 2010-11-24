/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.visnet.blaze;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextBox;
import java.io.Serializable;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.util.Sequences;
import java.util.Arrays;
import javafx.io.Storage;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.io.OutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.lang.System;
import javax.naming.spi.DirectoryManager;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.layout.Flow;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.text.SimpleDateFormat;

class Race extends Serializable {
    var name: String;
    var targetAverageSpeed: Float;
    var checkpoints: Integer[];

    override function toString() { return name; }
}

var races: Race[];
load();

var raceNameInput: TextBox;
var raceSpeedInput: TextBox;
var checkpointInput: TextBox;
var offsetInput: TextBox;
var racesList: ListView;
var checkpointsList: ListView;
var thisRace: Race;

var nextCheckpointIndex = 0;
var raceStartMillis: Long;
var raceOffset: Integer;
var distanceRemaining: Integer;

def countDownFile = Media { source: "file:{System.getProperty("user.home")}/projects/blaze/src/ch/visnet/blaze/CountDownFrom10.mp3" }
def countDown = MediaPlayer { media: countDownFile }
def countDownDuration = 11000; // make 'o' of 'zero' coincide with countdown 0
function isLessThanCountDownDuration(duration: Integer) { return duration < countDownDuration; }


function raceView(race: Race) {
    Scene {
        content: VBox {
            spacing: 2
            content: [
                checkpointsList = ListView {
                    items: bind race.checkpoints
                    cellFactory: function() {
                        def cell: ListCell = ListCell {
                            onUpdate: function() {
                                if (cell.item == null)
                                    cell.node = null
                                else
                                    cell.node = Label {
                                        font: Font { size: 20 }
                                        text : "{cell.item}"
                                    }
                                }
                            }
                        }
                },
                HBox {
                    content: [
                        Label { 
                            text: "Speed [km/h]"
                            font: Font { size: 20 }
                        },
                        raceSpeedInput = TextBox {
                            text: Float.toString(race.targetAverageSpeed)
                            font: Font { size: 20 }
                            action: function() {
                                race.targetAverageSpeed = Float.valueOf(raceSpeedInput.text);
                                println("Im here");
                                save(races);
                            }
                        }
                    ]
                },
                HBox {
                    content: [
                        Label { text: "New checkpoint [m]"
                                font: Font { size: 20 }
                        },
                        checkpointInput = TextBox {
                            promptText: "new checkpoint"
                            font: Font { size: 20 }
                            action: function() {
                                insert Integer.parseInt(checkpointInput.text) into race.checkpoints;
                                race.checkpoints = Sequences.sort(race.checkpoints) as Integer[];
                                save(races);
                            }

                        }

                    ]
                },
                HBox {
                    content: [
                        Label { 
                            text: "Offset [m]"
                            font: Font { size: 20 }
                        },
                        offsetInput = TextBox {
                            promptText: "offset"
                            text: "0"
                            font: Font { size: 20 }
                        }
                    ]
                },
                Button {
                    text: "DELETE CHECKPOINT",
                    font: Font { size: 20 }
                    action: function() {
                        delete checkpointsList.selectedItem as Integer from race.checkpoints;
                        save(races);
                    }
                },
                Button {
                    text: "BACK",
                    font: Font { size: 20 }
                    action: function() { stage.scene = racesView; }
                },
                Button {
                    text: "RUN",
                    font: Font { size: 20 }
                    action: function() {
                        thisRace = race;
                        raceStartMillis = System.currentTimeMillis();
                        raceOffset = Integer.valueOf(offsetInput.text);
                        displayedDistanceText.visible = true;
                        clock.play();
                        stage.scene = runningView;
                    };
                }
            ]
        }
    }
}

def racesView: Scene = Scene {
    content: VBox {
        spacing: 2
        content: [racesList = ListView {
            items: bind races
            cellFactory: function() {
                def cell: ListCell = ListCell {
                    onUpdate: function() {
                        if (cell.item == null)
                            cell.node = null
                        else
                            cell.node = Label {
                                font: Font { size: 20 }
                                text : "{cell.item}"
                            }

                        }
                    }
                }
            },//ListView
            Button {
                text: "SELECT"
                font: Font { size: 20 }
                action: function() { stage.scene = raceView(races[racesList.selectedIndex]) }
            },
            Button {
                text: "DELETE"
                font: Font { size: 20 }
                action: function() { delete races[racesList.selectedIndex]; save(races); }
            },
            Button {
                text: "NEW"
                font: Font { size: 20 }
                action: function() {
                    stage.scene = newRaceView;
                }
            },
            Button {
                text: "QUIT"
                font: Font { size: 20 }
                action: function() { System.exit(0); }
            }

        ]//content
    }//VBox
}

def newRaceView = Scene {
    content: HBox {
        spacing: 5
        content: [
            VBox {
                content: [
                    raceNameInput = TextBox {
                        font: Font { size: 20 }
                        promptText: "Name"
                    },
                    raceSpeedInput = TextBox {
                        font: Font { size: 20 }
                        promptText: "Speed in km/h"
                    }
                ]//content
            },//VBox
            VBox {
                content: [
                    Button {
                        text: "Submit"
                        font: Font { size: 20 }
                        action: function() {
                            var newRace = Race {
                                name: raceNameInput.text
                                targetAverageSpeed: Float.valueOf(raceSpeedInput.text)
                            }
                            insert newRace into races;
                            println("Race created");
                            save(races);
                            stage.scene = racesView;
                        }
                    },
                    Button {
                        text: "Cancel"
                        font: Font { size: 20 }
                        action: function() {
                            stage.scene = racesView;
                        }
                    }
                ]//content
            }//VBox
        ]//content
    }//HBox
}

def images: Image[] = for (i in [0..11]) {
            Image {
                url: "{__DIR__}{i}.png"
                height: 200
                preserveRatio: true}
                }

var currImgs: Image[];
resetClock();

function resetClock() {
    currImgs[0] = images[8];
    currImgs[1] = images[8];
    currImgs[2] = images[8];
    currImgs[3] = images[8];
    currImgs[4] = images[8];
    currImgs[5] = images[8];
    currImgs[6] = images[10];
    currImgs[7] = images[11];
}

def sdf = new SimpleDateFormat("mmssSSS");
function updateClock(millis: Long) {
    def mmssSSS = sdf.format(millis);
    // Finally, map strings to images
    currImgs[0] = images[Integer.parseInt(mmssSSS.substring(0, 1))];
    currImgs[1] = images[Integer.parseInt(mmssSSS.substring(1, 2))];
    currImgs[2] = images[Integer.parseInt(mmssSSS.substring(2, 3))];
    currImgs[3] = images[Integer.parseInt(mmssSSS.substring(3, 4))];
    currImgs[4] = images[Integer.parseInt(mmssSSS.substring(4, 5))];
    currImgs[5] = images[Integer.parseInt(mmssSSS.substring(5, 6))];
}

def displayedDistanceText = Text {
    x: 100
    y: 450
    content: bind "Checkpoint {nextCheckpointIndex+1}: {distanceRemaining} m"
    fill: Color.GREEN
    font : Font {
        size: 50
        name: "Courier"
    }
    visible: true;
}


def runningView = Scene {
    content: [
        Rectangle {
            width: 1024
            height: 600
            fill: Color.BLACK
            onMouseReleased: function (e: MouseEvent) {
                nextCheckpointIndex = 0;
                clock.stop();
                countDown.stop();
                stage.scene = racesView;
            }
        },
        Flow {
            hgap: 8
            layoutX: 150
            layoutY: 150
            wrapLength: 1000
            content: [
                ImageView { image: bind currImgs[0] },
                ImageView { image: bind currImgs[1] },
                ImageView { image: bind currImgs[6] }, // LED dots
                ImageView { image: bind currImgs[2] },
                ImageView { image: bind currImgs[3] },
                ImageView { image: bind currImgs[7] }, // LED period
                ImageView { image: bind currImgs[4] },
                ImageView { image: bind currImgs[5] }
            ]
        },
        displayedDistanceText
    ]
}


var clock : Timeline = Timeline {
    repeatCount: Timeline.INDEFINITE
    keyFrames:
        KeyFrame {
            time: 47ms
            action: function () {
                distanceRemaining = computeDistanceToDisplay(thisRace);
                if (distanceRemaining >= 0) {
                    var timeToDisplay = distanceRemaining / (thisRace.targetAverageSpeed / 3.6 / 1000) as Long;
                    updateClock(timeToDisplay);
                    if (isLessThanCountDownDuration(timeToDisplay)) {
                        countDown.play();
                    } else
                        countDown.stop();
                } else {
                    distanceRemaining = 0;
                    displayedDistanceText.visible = false;
                    resetClock();
                }
                //updateDisplayedDistance(distanceToDisplay);
            }
        }
    }

function computeDistanceToDisplay(race: Race): Integer {
    var now = System.currentTimeMillis();
    var elapsed = now - raceStartMillis;
    var result = race.checkpoints[nextCheckpointIndex] - elapsed * race.targetAverageSpeed / 3.6 / 1000 - raceOffset;
    if (result < 0) {
        nextCheckpointIndex++;
        if (nextCheckpointIndex < race.checkpoints.size())
            return race.checkpoints[nextCheckpointIndex]
        else
            return -1;
    } else {
        return result;
    }
}


function save(races: Race[]) {
    var userHome = System.getProperty("user.home");
    var directory = new File("{userHome}{File.separator}.blaze");
    directory.mkdir();
    var racesFile = new File(directory, "races.data");
    racesFile.delete();
    racesFile.createNewFile();
    var pw = new PrintWriter(new BufferedWriter(new FileWriter(racesFile)));
    for (race in races){
        pw.print("{race.name}:{race.targetAverageSpeed}");
        for (checkpoint in race.checkpoints)
            pw.print(":{checkpoint}");
        pw.println();
    }
    pw.flush();
    pw.close();
}

function load() {
    var userHome = System.getProperty("user.home");
    var directory = new File("{userHome}{File.separator}.blaze");
    var racesFile = new File(directory, "races.data");
    if (not racesFile.exists()) return;
    var br = new BufferedReader(new FileReader(racesFile));
    while (br.ready()) {
        var string = br.readLine();
        var elements = string.split(":") as String[];
        var name = elements[0];
        var speed = Float.valueOf(elements[1]);
        var checkpoints: Integer[] = [];
        for (checkpoint in elements[2..])
            insert Float.valueOf(checkpoint) into checkpoints;
        insert Race{
            name : name
            targetAverageSpeed: speed
            checkpoints : checkpoints
        } into races;
    }
    br.close();
}



var stage = Stage {
    title: "Blaze"
    width: 500
    height: 600
//    fullScreen: true
    scene: racesView
}