package com.github.traunin.triangulation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class TriangulationTest {
    private final static List<ReadOnlyVector2f> TRIANGLE = Arrays.asList(
        new ReadOnlyVector2f(0, 0),
        new ReadOnlyVector2f(3, 0),
        new ReadOnlyVector2f(0, 4)
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
        } catch (TriangulationException exception) {
            String expectedError = "Not enough vertex indices for a polygon";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testVertexIndexOutsideOfVertices() {
        try {
            Triangulation.earClippingTriangulate(TRIANGLE, Arrays.asList(1, 2, 3));
            Assertions.fail();
        } catch (TriangulationException exception) {
            String expectedError = "Vertex index 3 is outside of vertex list of length 3";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }
}
