package com.github.traunin.triangulation_demo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public class PolygonDrawerCanvas {
    private final static Color DEFAULT_VERTEX_COLOR = Color.RED;
    private final static Color HIGHTLIGHT_VERTEX_COLOR = Color.YELLOW;
    private final static float DEFAULT_POLYGON_SIZE = 100f;
    private final static int SIDE_COUNT = 6;
    private final static float VERTEX_SIZE = 20f;
    private final List<Vertex> vertices = new ArrayList<>();
    private final Canvas canvas;
    private Vertex selectedVertex = null;
    private double offsetX;
    private double offsetY;

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


        canvas.setOnMousePressed(e -> {
            if (selectedVertex != null) {
                canvas.setCursor(Cursor.CLOSED_HAND);
                offsetX = e.getX() - selectedVertex.getX();
                offsetY = e.getY() - selectedVertex.getY();
            }
        });

        canvas.setOnMouseReleased(e -> {
            if (selectedVertex != null) {
                canvas.setCursor(Cursor.OPEN_HAND);
            }
        });

        canvas.setOnMouseMoved(e -> {
            highlightVertex(e.getX(), e.getY());
            redraw();
        });

        canvas.setOnMouseDragged(e -> {
            if (selectedVertex != null) {
                selectedVertex.setPosition(new Vector2f((float) (e.getX() - offsetX), (float) (e.getY() - offsetY)));
                canvas.setCursor(Cursor.CLOSED_HAND);
            }
            redraw();
        });
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
        ctx.setFill(vertex == selectedVertex ?  HIGHTLIGHT_VERTEX_COLOR : DEFAULT_VERTEX_COLOR);
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

    private void highlightVertex(double x, double y) {
        for (Vertex vertex : vertices) {
            if (vertex.getDistance(x, y) <= VERTEX_SIZE / 2) {
                selectedVertex = vertex;
                canvas.setCursor(Cursor.OPEN_HAND);
                return;
            }
        }
        canvas.setCursor(Cursor.DEFAULT);
        selectedVertex = null;
    }

    private static class Vertex {
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

        public double getDistance(double x, double y) {
            double dX = x - position.getX();
            double dY = y - position.getY();
            return Math.sqrt(dX * dX + dY * dY);
        }
    }
}
