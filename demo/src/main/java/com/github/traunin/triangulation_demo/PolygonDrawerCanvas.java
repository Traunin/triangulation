package com.github.traunin.triangulation_demo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public class PolygonDrawerCanvas {

    private final static float DEFAULT_POLYGON_SIZE = 50f;
    private final static int SIDE_COUNT = 6;
    private final static float VERTEX_SIZE = 5f;
    private final List<Vector2f> vertices = new ArrayList<>();
    private final Canvas canvas;

    /**
     * Attaches listeners to parent to update canvas size and redraw it upon parent size changes
     * @param parent canvas parent - a scene pane
     * @param canvas canvas
     */
    public PolygonDrawerCanvas(Pane parent, Canvas canvas) {
        this.canvas = canvas;

        addSizeListener(parent.widthProperty(), canvas::setWidth);
        addSizeListener(parent.heightProperty(), canvas::setHeight);

        // hacky solution
        // canvas has to be full size before finding the center point
        // javafx initializes height later than width
        // if the order changes... too bad
        ChangeListener<Number> listener = new ChangeListener<>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                initializePolygon();
                redraw();
                parent.heightProperty().removeListener(this);
            }
        };
        parent.heightProperty().addListener(listener);
    }

    public void initializePolygon() {
        for (int i = 0; i < SIDE_COUNT; i++) {
            vertices.add(new Vector2f(
                (float) (Math.cos(i * 2 * Math.PI / SIDE_COUNT) * DEFAULT_POLYGON_SIZE + canvas.getWidth() / 2),
                (float) (Math.sin(i * 2 * Math.PI / SIDE_COUNT) * DEFAULT_POLYGON_SIZE + canvas.getHeight() / 2)
            ));
        }
    }

    private void addSizeListener(ObservableValue<? extends Number> property, Consumer<Double> sizeSetter) {
        property.addListener((observableValue, oldValue, newValue) -> {
            sizeSetter.accept(newValue.doubleValue());
            redraw();
        });
    }

    private void redraw() {
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        ctx.clearRect(0, 0, width, height);

        ctx.setFill(Color.RED);
        for (Vector2f vertex : vertices) {
            drawVertex(vertex, ctx);
        }
    }

    private void drawVertex(Vector2f vertex, GraphicsContext ctx) {
        ctx.fillArc(
            vertex.getX() - VERTEX_SIZE / 2,
            vertex.getY() - VERTEX_SIZE / 2,
            VERTEX_SIZE,
            VERTEX_SIZE,
            0,
            360,
            ArcType.ROUND
        );
    }
}
