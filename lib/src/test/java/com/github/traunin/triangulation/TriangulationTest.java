package com.github.traunin.triangulation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TriangulationTest {
    private final static Random RANDOM = new Random();

    private final static List<ReadOnlyVector2f> TRIANGLE = Arrays.asList(
        new ReadOnlyVector2f(0, 0),
        new ReadOnlyVector2f(3, 0),
        new ReadOnlyVector2f(0, 4)
    );

    private final static List<ReadOnlyVector2f> SELF_INTERSECTING_POLYGON = Arrays.asList(
        new ReadOnlyVector2f(0, 0),
        new ReadOnlyVector2f(0, 2),
        new ReadOnlyVector2f(-1, 1),
        new ReadOnlyVector2f(2, 0)
    );

    @Test
    public void testConvexTriangulation() {
        List<int[]> triangleList = Triangulation.convexPolygonTriangulate(Arrays.asList(0, 1, 2, 3, 4));
        Assertions.assertArrayEquals(triangleList.get(0), new int[]{0, 2, 1});
        Assertions.assertArrayEquals(triangleList.get(1), new int[]{0, 3, 2});
        Assertions.assertArrayEquals(triangleList.get(2), new int[]{0, 4, 3});
    }

    @Test
    public void testIncorrectVertexIndicesCount() {
        try {
            Triangulation.convexPolygonTriangulate(2);
            Assertions.fail();
        } catch (IllegalArgumentException exception) {
            String expectedError = "Not enough vertex indices for a polygon";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testVertexIndexOutsideOfVertices() {
        try {
            Triangulation.earClippingTriangulate(TRIANGLE, Arrays.asList(1, 2, 3));
            Assertions.fail();
        } catch (IllegalArgumentException exception) {
            String expectedError = "Vertex index 3 is outside of vertex list of length 3";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testSelfIntersectingPolygon() {
        try {
            Triangulation.earClippingTriangulate(SELF_INTERSECTING_POLYGON);
            Assertions.fail();
        } catch (TriangulationException exception) {
            String expectedError = "Polygon has self-intersections";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @RepeatedTest(10)
    public void testRandomPolygon() {
        int verticesCount = 10;
        float size = 10;
        List<ReadOnlyVector2f> randomPolygon = new ArrayList<>(10);

        for (int i = 0; i < verticesCount; i++) {
            float randomSize = RANDOM.nextFloat(size, size + size);
            randomPolygon.add(new ReadOnlyVector2f(
                (float) (Math.cos(i * 2 * Math.PI / verticesCount) * randomSize),
                (float) (Math.sin(i * 2 * Math.PI / verticesCount) * randomSize)
            ));
        }

        List<int[]> triangles = Triangulation.earClippingTriangulate(randomPolygon);
        Assertions.assertEquals(triangles.size(), verticesCount - 2);
    }
}
