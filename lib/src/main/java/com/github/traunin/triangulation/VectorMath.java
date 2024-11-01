package com.github.traunin.triangulation;

/**
 * A utility class for working with {@link Vector2f}
 */
final class VectorMath {
    /**
     * Prevents class instantiation.
     * @throws UnsupportedOperationException when called
     */
    private VectorMath() {
        throw new UnsupportedOperationException("Cannot be instantiated.");
    }

    /**
     * Calculates the cross product of vectors (BA) x (BC).
     * @param a A coordinates
     * @param b B coordinates
     * @param c C coordinates
     * @return cross product of vectors (BA) x (BC)
     */
    static float crossProduct(Vector2f a, Vector2f b, Vector2f c) {
        float dx1 = b.x() - a.x();
        float dy1 = b.y() - a.y();
        float dx2 = c.x() - a.x();
        float dy2 = c.y() - a.y();

        return dx1 * dy2 - dx2 * dy1;
    }

    /**
     * Checks whether point P is inside of triangle ABC.
     * <p>Uses the cross product of three vectors.
     * @param a A coordinates
     * @param b B coordinates
     * @param c C coordinates
     * @param p P coordinates
     * @return true if P is inside ABC
     */
    static boolean isPointInTriangle(Vector2f a, Vector2f b, Vector2f c, Vector2f p) {
        float check1 = crossProduct(a, b, p);
        float check2 = crossProduct(p, b, c);
        float check3 = crossProduct(p, c, a);

        return (check1 >= 0 && check2 >= 0 && check3 >= 0) || (check1 <= 0 && check2 <= 0 && check3 <= 0);
    }

    /**
     * Calculates the length of vector AB.
     * @param a A coordinates
     * @param b B coordinates
     * @return length of vector AB
     */
    static float edgeLength(Vector2f a, Vector2f b) {
        float dx = a.x() - b.x();
        float dy = a.y() - b.y();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
