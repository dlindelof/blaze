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

class Race extends Serializable {
    var name: String;
    var targetAverageSpeed: Float;
    var checkpoints: Integer[];

    override function toString() { name }
}

function raceView(race: Race) { Scene {
    content: [ListView {
        items: bind race.checkpoints
    }

        ]
}
}

var races: Race[];

function goToRaceView() {
    stage.scene = raceView;
    return
}


def racesView: Scene = Scene {
    width: 1024
    height: 600
    content:
        [ListView {
            items: bind races
            cellFactory: function() {
                def cell: ListCell = ListCell {
                    node: CheckBox {
                        text: bind "{cell.item}"
                    }

                }

            }

        },
        Button {
            text: "SELECT"
            action: goToRaceView
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

def newRaceView = Scene {
    content: [TextBox {
        promptText: "Enter name for new race my lord"
        action: function() {
            println("Added new race");
            stage.scene = racesView;
            }
            }
        ]
}


var stage = Stage {
    title: "Blaze"
    fullScreen: true
    scene: racesView
}