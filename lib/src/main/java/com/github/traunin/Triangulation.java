package com.github.traunin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Triangulation {
    public static <T extends Vector2f> List<T[]> convexPolygonTriangulate(List<T> vertices, List<Integer> vertexIndices) {
        int vertexCount = vertexIndices.size();

        if (vertexCount < 3) {
            throw new TriangulationException("Not enough vertex indices for a polygon");
        }

        List<T> orderedVertices = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            orderedVertices.set(i, vertices.get(vertexIndices.get(i)));
        }


        return convexPolygonTriangulate(orderedVertices);
    }

    @SafeVarargs
    public static <T extends Vector2f> List<T[]> convexPolygonTriangulate(T... vertices) {
        List<T> verticesList = Arrays.stream(vertices).toList();

        return convexPolygonTriangulate(verticesList);
    }

    public static <T extends Vector2f> List<T[]> convexPolygonTriangulate(List<T> vertices) {
        int vertexCount = vertices.size();

        if (vertexCount < 3) {
            throw new TriangulationException("Not enough vertices for a polygon");
        }

        List<T[]> triangles = new ArrayList<>(vertexCount - 2);

        return triangles;
    }
}