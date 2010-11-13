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

function raceView(race: Race) {
    Scene {
        content: VBox {
            spacing: 2
            content: [
                checkpointsList = ListView {
                    items: bind race.checkpoints
                },
                HBox {
                    content: [
                        Label { text: "Speed [km/h]" },
                        raceSpeedInput = TextBox {
                            text: Float.toString(race.targetAverageSpeed)
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
                        Label { text: "New checkpoint [m]" },
                        checkpointInput = TextBox {
                            promptText: "new checkpoint"
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
                        Label { text: "Offset [m]" },
                        offsetInput = TextBox {
                            promptText: "offset"
                        }
                    ]
                },
                Button {
                    text: "Delete checkpoint",
                    action: function() {
                        delete checkpointsList.selectedItem as Integer from race.checkpoints;
                        save(races);
                    }
                },
                Button {
                    text: "RUN",
                    action: function() { println("UNIMPLEMENTED") };
                }
            ]
        }
    }
}

def racesView: Scene = Scene {
    width: 1024
    height: 600
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
                            cell.node = CheckBox {
                                text : "{cell.item}"
                            }

                        }
                    }
                }
            },//ListView
            Button {
                text: "SELECT"
                action: function() { stage.scene = raceView(races[racesList.selectedIndex]) }
            },
            Button {
                text: "DELETE"
                action: function() {
                    delete races[racesList.selectedIndex];
                }
            },
            Button {
                text: "NEW"
                action: function() { stage.scene = newRaceView; }
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
                        promptText: "Name"
                    },
                    raceSpeedInput = TextBox {
                        promptText: "Speed in km/h"
                    }
                ]//content
            },//VBox
            VBox {
                content: [
                    Button {
                        text: "Submit"
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
                        action: function() {
                            stage.scene = racesView;
                        }
                    }
                ]//content
            }//VBox
        ]//content
    }//HBox
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
    }
    pw.println();
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
//    fullScreen: true
    scene: racesView
}