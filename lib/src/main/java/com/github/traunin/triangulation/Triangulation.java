package com.github.traunin.triangulation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public final class Triangulation {
    private Triangulation() {
        throw new UnsupportedOperationException("Cannot be instantiated.");
    }

    private static void checkVertexIndicesCount(int n) {
        if (n < 3) {
            throw new IllegalArgumentException("Not enough vertex indices for a polygon");
        }
    }

    public static List<int[]> convexPolygonTriangulate(List<Integer> vertexIndices) {
        int vertexIndicesCount = vertexIndices.size();
        checkVertexIndicesCount(vertexIndicesCount);

        List<int[]> triangles = new ArrayList<>(vertexIndicesCount - 2);

        for (int i = 2; i < vertexIndicesCount; i++) {
            triangles.add(new int[]{0, vertexIndices.get(i), vertexIndices.get(i - 1)});
        }

        return triangles;
    }

    public static List<int[]> convexPolygonTriangulate(int n) {
        List<Integer> vertexIndices = IntStream.rangeClosed(0, n - 1).boxed().toList();

        return convexPolygonTriangulate(vertexIndices);
    }


    public static <T extends Vector2f> List<int[]> earClippingTriangulate(List<T> vertices) {
        List<Integer> vertexIndices = IntStream.rangeClosed(0, vertices.size() - 1).boxed().toList();

        return earClippingTriangulate(vertices, vertexIndices);
    }

    public static <T extends Vector2f> List<int[]> earClippingTriangulate(List<T> vertices, List<Integer> vertexIndices) {
        int vertexIndicesCount = vertexIndices.size();
        checkVertexIndicesCount(vertexIndicesCount);

        int vertexCount = vertices.size();
        for (Integer vertexIndex : vertexIndices) {
            if (vertexIndex >= vertexCount) {
                throw new IllegalArgumentException(
                    String.format("Vertex index %d is outside of vertex list of length %d", vertexIndex, vertexCount)
                );
            }
        }

        List<int[]> triangles = new ArrayList<>(vertexIndicesCount - 2);
        // copy vertexIndices to avoid side effects on input data
        List<Integer> potentialEars = new ArrayList<>(vertexIndices);
        int potentialEarsCount = vertexIndicesCount;

        boolean isCCW = isCounterClockwise(vertices, vertexIndices);

        for (boolean hasClippedEars = true; hasClippedEars;) {
            hasClippedEars = false;
            for (int i = 1; i < potentialEarsCount - 1; i++) {
                int prevVertexIndex = potentialEars.get(i - 1);
                int curVertexIndex = potentialEars.get(i);
                int nextVertexIndex = potentialEars.get(i + 1);

                Vector2f prevVertex = vertices.get(prevVertexIndex);
                Vector2f curVertex = vertices.get(curVertexIndex);
                Vector2f nextVertex = vertices.get(nextVertexIndex);

                float crossProduct = VectorMath.crossProduct(prevVertex, curVertex, nextVertex);
                // check if convex
                // TODO handle zero? points on one line or ends match
                if ((isCCW ? crossProduct : -crossProduct) < 0) {
                    continue;
                }
                boolean isEar = true;

                // check if no other points in triangle
                for (int j = 0; j < vertexIndicesCount; j++) {
                    int checkedVertexIndex = vertexIndices.get(j);
                    if (
                        checkedVertexIndex == prevVertexIndex ||
                        checkedVertexIndex == curVertexIndex ||
                        checkedVertexIndex == nextVertexIndex
                    ) {
                        continue;
                    }

                    Vector2f checkedVertex = vertices.get(checkedVertexIndex);
                    if (VectorMath.isPointInTriangle(prevVertex, curVertex, nextVertex, checkedVertex)) {
                        isEar = false;
                        break;
                    }
                }

                if (isEar) {
                    triangles.add(new int[] {prevVertexIndex, curVertexIndex, nextVertexIndex});
                    potentialEars.remove(i);
                    potentialEarsCount--;
                    i--;
                    hasClippedEars = true;
                }
            }
        }

        if (triangles.size() != vertexIndicesCount - 2) {
            throw new TriangulationException("Polygon has self-intersections");
        }

        return triangles;
    }

    /**
     * Calculates the polygon area using the shoelace formula. The direction is determined by the sign of the area.
     *
     * @param vertices List of vertices implementing {@link Vector2f}
     * @param vertexIndices List of {@link Integer} determining vertices order
     * @return true if counter-clockwise
     */
    private static <T extends Vector2f> boolean isCounterClockwise(List<T> vertices, List<Integer> vertexIndices) {
        float area = 0;
        int vertexIndicesCount = vertexIndices.size();

        Vector2f prevVertex = vertices.get(vertexIndices.get(0));
        for (int i = 1; i <= vertexIndicesCount; i++) {
            Vector2f currentVertex = vertices.get(vertexIndices.get(i % vertexIndicesCount));

            area += (currentVertex.x() - prevVertex.x()) * (currentVertex.y() + prevVertex.y());
            prevVertex = currentVertex;
        }

        return area < 0;
    }
}