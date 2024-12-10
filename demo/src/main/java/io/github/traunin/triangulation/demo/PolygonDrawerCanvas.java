package io.github.traunin.triangulation.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.github.traunin.triangulation.Triangulation;
import io.github.traunin.triangulation.TriangulationException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public class PolygonDrawerCanvas {
    private final static Color DEFAULT_VERTEX_COLOR = Color.RED;
    private final static Color HIGHTLIGHT_VERTEX_COLOR = Color.YELLOW;
    private final static Color GHOST_VERTEX_COLOR = new Color(1, 0, 0, 0.1);
    private final static float DEFAULT_POLYGON_SIZE = 100f;
    private final static int SIDE_COUNT = 6;
    private final static float VERTEX_SIZE = 20f;

    private boolean enableTriangulation = false;
    private boolean showVertices = true;
    private boolean showEdges = true;
    private boolean showTriangles = true;
    private final List<Vertex> vertices = new ArrayList<>();
    private final Canvas canvas;
    private Vertex selectedVertex = null;
    private Vertex ghostVertexEdge = null;
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
                enableTriangulation = true;
                redraw();
                parent.heightProperty().removeListener(this);
            }
        };
        parent.heightProperty().addListener(listener);


        canvas.setOnMousePressed(e -> {
            if (!showVertices) {
                return;
            }

            if (selectedVertex != null) {
                if (e.getButton() == MouseButton.SECONDARY && vertices.size() > 3) {
                    Vertex previous = null;
                    for (Vertex vertex : vertices) {
                        if (vertex.connected() == selectedVertex) {
                            previous = vertex;
                            break;
                        }
                    }
                    if (previous == null) {
                        return;
                    }
                    previous.connect(selectedVertex.connected());
                    vertices.remove(selectedVertex);
                    redraw();
                    return;
                }
                canvas.setCursor(Cursor.CLOSED_HAND);
                offsetX = e.getX() - selectedVertex.x();
                offsetY = e.getY() - selectedVertex.y();
                return;
            }

            if (ghostVertexEdge != null && e.getButton() == MouseButton.PRIMARY) {
                Vertex newVertex = new Vertex(
                    pointOnEdge(ghostVertexEdge, e.getX(), e.getY()),
                    HIGHTLIGHT_VERTEX_COLOR
                );
                newVertex.connect(ghostVertexEdge.connected());
                ghostVertexEdge.connect(newVertex);
                vertices.add(newVertex);
                selectedVertex = newVertex;
            }
            redraw();
        });

        canvas.setOnMouseReleased(e -> {
            if (!showVertices) {
                return;
            }

            if (selectedVertex != null) {
                canvas.setCursor(Cursor.OPEN_HAND);
            }
        });

        canvas.setOnMouseMoved(e -> {
            if (!showVertices) {
                return;
            }

            highlightVertex(e.getX(), e.getY());
            redraw();

            if (selectedVertex == null) {
                drawGhostVertex(e.getX(), e.getY(), canvas.getGraphicsContext2D());
            }
        });

        canvas.setOnMouseDragged(e -> {
            if (!showVertices) {
                return;
            }

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
        if (enableTriangulation && showTriangles) {
            List<Integer> vertexIndices = new ArrayList<>(vertices.size());
            List<Vector2f> verticesPositions = new ArrayList<>(vertices.size());
            for (Vertex vertex : vertices) {
                verticesPositions.add(vertex.position());
            }
            Vertex startVertex = vertices.get(0);
            vertexIndices.add(0);
            for (
                Vertex currentVertex = startVertex.connected();
                currentVertex != startVertex;
                currentVertex = currentVertex.connected()
            ) {
                vertexIndices.add(vertices.indexOf(currentVertex));
            }

            try {
                List<int[]> triangles = Triangulation.earClippingTriangulate(verticesPositions, vertexIndices);
                int i = 0;
                for (int[] triangle : triangles) {
                    ctx.setFill(Color.hsb(360f * i / triangles.size(), 1, 1));
                    drawTriangle(triangle, ctx);
                    i++;
                }
                if (selectedVertex != null) {
                    selectedVertex.setColor(HIGHTLIGHT_VERTEX_COLOR);
                }
            } catch (TriangulationException ignored) {
                if (selectedVertex != null) {
                    selectedVertex.setColor(Color.GREEN);
                }
            }
        }

        if (showEdges) {
            for (Vertex vertex : vertices) {
                drawEdge(vertex, ctx);
            }
        }

        if (showVertices) {
            for (Vertex vertex : vertices) {
                drawVertex(vertex, ctx);
            }
        }
    }

    private void drawTriangle(int[] vertexIndices, GraphicsContext ctx) {
        double[] x = new double[] {
            vertices.get(vertexIndices[0]).x(),
            vertices.get(vertexIndices[1]).x(),
            vertices.get(vertexIndices[2]).x(),
        };

        double[] y = new double[] {
            vertices.get(vertexIndices[0]).y(),
            vertices.get(vertexIndices[1]).y(),
            vertices.get(vertexIndices[2]).y(),
        };
        ctx.fillPolygon(x, y, 3);
    }

    private void drawVertex(Vertex vertex, GraphicsContext ctx) {
        ctx.setFill(vertex.color());
        ctx.fillArc(
            vertex.x() - VERTEX_SIZE / 2,
            vertex.y() - VERTEX_SIZE / 2,
            VERTEX_SIZE,
            VERTEX_SIZE,
            0,
            360,
            ArcType.ROUND
        );
    }

    private void drawGhostVertex(double mouseX, double mouseY, GraphicsContext ctx) {
        for (Vertex vertex : vertices) {
            if (isCursorOnEdge(
                vertex.x(), vertex.y(), vertex.connected().x(), vertex.connected().y(), mouseX, mouseY
            )) {
                canvas.setCursor(Cursor.CROSSHAIR);
                ghostVertexEdge = vertex;
                drawVertex(new Vertex(pointOnEdge(vertex, mouseX, mouseY), GHOST_VERTEX_COLOR), ctx);
                return;
            }
        }

        ghostVertexEdge = null;
    }

    private boolean isCursorOnEdge(
        double edgePoint1X, double edgePoint1Y, double edgePoint2X, double edgePoint2Y, double mouseX, double mouseY
    ) {
        double k = (float) (edgePoint2X - edgePoint1X) / (edgePoint2Y - edgePoint1Y);
        double pointYOnLine1 = k * (edgePoint1X - mouseX) + edgePoint1Y;
        double pointYOnLine2 = k * (edgePoint2X - mouseX) + edgePoint2Y;

        boolean cursorIsInVerticesRect = mouseY >= pointYOnLine1 != mouseY >= pointYOnLine2;
        if (!cursorIsInVerticesRect) {
            return false;
        }

        double mouseToEdgeDistance = distanceFromPointToLine(
            mouseX, mouseY, edgePoint1X, edgePoint1Y, edgePoint2X, edgePoint2Y
        );

        return  mouseToEdgeDistance <= VERTEX_SIZE / 2;
    }

    private double distanceFromPointToLine(double x0, double y0, double x1, double y1, double x2, double y2) {
        return Math.abs((y2 - y1) * x0 - (x2 - x1) * y0 + x2 * y1 - y2 * x1) /
                Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

    private Vector2f pointOnEdge(Vertex vertex, double mouseX, double mouseY) {
        Vector2f edgePoint1 = vertex.position();
        Vector2f edgePoint2 = vertex.connected().position();

        double edgeXLen = edgePoint2.x() - edgePoint1.x();
        double edgeYLen = edgePoint2.y() - edgePoint1.y();

        double edgeLenSquare = edgeXLen * edgeXLen + edgeYLen * edgeYLen;
        double t = ((mouseX - edgePoint1.x()) * (edgeXLen) + (mouseY - edgePoint1.y()) * edgeYLen) / edgeLenSquare;

        return new Vector2f(
            (float) (edgePoint1.x() + edgeXLen * t),
            (float) (edgePoint1.y() + edgeYLen * t)
        );
    }

    private void drawEdge(Vertex vertex, GraphicsContext ctx) {
        if (vertex.connected() != null) {
            ctx.strokeLine(vertex.x(), vertex.y(), vertex.connected().x(), vertex.connected().y());
        }
    }

    private void highlightVertex(double x, double y) {
        for (Vertex vertex : vertices) {
            if (vertex.distance(x, y) <= VERTEX_SIZE / 2) {
                selectedVertex = vertex;
                vertex.setColor(HIGHTLIGHT_VERTEX_COLOR);
                canvas.setCursor(Cursor.OPEN_HAND);
                return;
            }
        }
        if (selectedVertex != null) {
            selectedVertex.setColor(DEFAULT_VERTEX_COLOR);
        }
        canvas.setCursor(Cursor.DEFAULT);
        selectedVertex = null;
    }

    public void toggleShowVertices() {
        this.showVertices = !this.showVertices;
        redraw();
    }

    public void toggleShowEdges() {
        this.showEdges = !this.showEdges;
        redraw();
    }

    public void toggleShowTriangles() {
        this.showTriangles = !this.showTriangles;
        redraw();
    }

    private static class Vertex {
        private Color color;
        private Vector2f position;
        private Vertex connected;

        public Vertex(Vector2f position, Vertex connected) {
            this(position);
            this.connected = connected;
        }

        public Vertex(Vector2f position, Color color) {
            this(position);
            this.color = color;
        }

        public Vertex(Vector2f position) {
            this.position = position;
            this.color = DEFAULT_VERTEX_COLOR;
        }

        public Vector2f position() {
            return position;
        }

        public void setPosition(Vector2f position) {
            this.position = position;
        }

        public Vertex connected() {
            return connected;
        }

        public void connect(Vertex connection) {
            this.connected = connection;
        }

        public float x() {
            return position.x();
        }

        public float y() {
            return position.y();
        }

        public Color color() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public double distance(double x, double y) {
            double dX = x - position.x();
            double dY = y - position.y();
            return Math.sqrt(dX * dX + dY * dY);
        }
    }
}
