package com.github.traunin.triangulation_demo;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.VBox;

public class AppController implements Initializable {
    @FXML
    Canvas polygonCanvas;
    @FXML
    VBox rootVBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new PolygonDrawerCanvas(rootVBox, polygonCanvas);
    }
}
