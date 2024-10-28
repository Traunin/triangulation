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
    private final static Color DEFAULT_VERTEX_COLOR = Color.RED;
    private final static float DEFAULT_POLYGON_SIZE = 100f;
    private final static int SIDE_COUNT = 6;
    private final static float VERTEX_SIZE = 10f;
    private final List<Vertex> vertices = new ArrayList<>();
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
        Vertex initialVertex = new Vertex(new Vector2f(
            (float) (DEFAULT_POLYGON_SIZE + canvas.getWidth() / 2),
            (float) (canvas.getHeight() / 2)
        ));
        vertices.add(initialVertex);

        // polygon has at least 3 vertices
        int sides = Math.max(SIDE_COUNT, 3);

        Vertex prevVertex = initialVertex;
        for (int i = 1; i < sides; i++) {
            Vertex currentVertex = new Vertex(new Vector2f(
                (float) (Math.cos(i * 2 * Math.PI / sides) * DEFAULT_POLYGON_SIZE + canvas.getWidth() / 2),
                (float) (Math.sin(i * 2 * Math.PI / sides) * DEFAULT_POLYGON_SIZE + canvas.getHeight() / 2)
            ));

            vertices.add(currentVertex);
            prevVertex.connect(currentVertex);
            prevVertex = currentVertex;
        }

        prevVertex.connect(initialVertex);
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

        for (Vertex vertex : vertices) {
            drawEdge(vertex, ctx);
        }

        for (Vertex vertex : vertices) {
            drawVertex(vertex, ctx);
        }
    }

    private void drawVertex(Vertex vertex, GraphicsContext ctx) {
        ctx.setFill(vertex.getColor());
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

    private void drawEdge(Vertex vertex, GraphicsContext ctx) {
        ctx.strokeLine(vertex.getX(), vertex.getY(), vertex.getConnected().getX(), vertex.getConnected().getY());
    }

    private static class Vertex {
        private Color color = DEFAULT_VERTEX_COLOR;
        private Vector2f position;
        private Vertex connected;

        public Vertex(Vector2f position, Vertex connected) {
            this(position);
            this.connected = connected;
        }

        public Vertex(Vector2f position) {
            this.position = position;
        }

        public Vector2f getPosition() {
            return position;
        }

        public void setPosition(Vector2f position) {
            this.position = position;
        }

        public Vertex getConnected() {
            return connected;
        }

        public void connect(Vertex connection) {
            this.connected = connection;
        }

        public float getX() {
            return position.getX();
        }

        public float getY() {
            return position.getY();
        }

        public Color getColor() {
            return color;
        }
    }
}
