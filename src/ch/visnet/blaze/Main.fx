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

class Race extends Serializable {
    var name: String;
    var targetAverageSpeed: Float;
    var checkpoints: Integer[];

    override function toString() { name }
}

var raceName: TextBox;
var speedInput: TextBox;
var racesList: ListView;

function raceView(race: Race) {
    Scene {
        content: VBox {
            spacing: 2
            content: [
                ListView {
                    items: bind race.checkpoints
                },
                speedInput = TextBox {
                    text: bind "{race.targetAverageSpeed} km/h"
                    action: function() {
                        race.targetAverageSpeed = Float.parseFloat(speedInput.text);
                        speedInput.text = "{race.targetAverageSpeed} km/h";
                    }
                }
            ]
        }
    }
}

var races: Race[] = [Race { name: "Naples-Bordeaux" }];


def racesView: Scene = Scene {
//    width: 1024
//    height: 600
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
        },
        Button {
            text: "SELECT"
            action: function() { stage.scene = raceView(races[racesList.selectedIndex]) }
        },
        Button {
            text: "DELETE"
        },
        Button {
            text: "NEW"
            action: function() { stage.scene = newRaceView; }
        }

        ]
    }

        
}

def newRaceView = Scene {
    content: raceName = TextBox {
        promptText: "Enter name for new race my lord"
        action: function() {
            races = [races, Race { name: raceName.text; }];
            stage.scene = racesView;
            }
        }
}


var stage = Stage {
    title: "Blaze"
//    fullScreen: true
    scene: racesView
}