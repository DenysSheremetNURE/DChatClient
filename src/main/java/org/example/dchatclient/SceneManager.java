package org.example.dchatclient;

import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;

public class SceneManager {
    public static void changeToScene(Scene scene, Node sourceNode){
        Stage stage = (Stage) sourceNode.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
