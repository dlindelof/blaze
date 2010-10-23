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

class Race extends Serializable {
    var name: String;
    var targetAverageSpeed: Float;
    var checkpoints: Integer[];

    override function toString() { name }
}

var raceNameInput: TextBox;
var raceSpeedInput: TextBox;
var checkpointInput: TextBox;
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
                            text: bind Float.toString(race.targetAverageSpeed)
                            action: function() {
                                race.targetAverageSpeed = Float.parseFloat(raceSpeedInput.text);
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
                            }

                        }

                    ]
                },
                Button {
                    text: "Delete checkpoint",
                    action: function() { println("UNIMPLEMENTED") }
                },
                Button {
                    text: "RUN",
                    action: function() { println("UNIMPLEMENTED") };
                }




            ]
        }
    }
}

var races: Race[];// = [Race { name: "Naples-Bordeaux", targetAverageSpeed: 50, checkpoints: [200] }];


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
                                targetAverageSpeed: Float.parseFloat(raceSpeedInput.text)
                            }
                            insert newRace into races;
                            println("Race created");
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


var stage = Stage {
    title: "Blaze"
//    fullScreen: true
    scene: racesView
}