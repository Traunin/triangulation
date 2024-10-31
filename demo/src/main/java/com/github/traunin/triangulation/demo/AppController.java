package com.github.traunin.triangulation.demo;

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
    PolygonDrawerCanvas polygonDrawerCanvas;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        polygonDrawerCanvas = new PolygonDrawerCanvas(rootVBox, polygonCanvas);
    }

    @FXML
    public void handleToggleVertices() {
        polygonDrawerCanvas.toggleShowVertices();
    }

    @FXML
    public void handleToggleEdges() {
        polygonDrawerCanvas.toggleShowEdges();
    }

    @FXML
    public void handleToggleTriangles() {
        polygonDrawerCanvas.toggleShowTriangles();
    }
}
