package com.github.traunin.triangulation;

class VectorMath {
    static float crossProduct(Vector2f a, Vector2f b, Vector2f c) {
        float dx1 = b.x() - a.x();
        float dy1 = b.y() - a.y();
        float dx2 = c.x() - a.x();
        float dy2 = c.y() - a.y();

        return dx1 * dy2 - dx2 * dy1;
    }

    static boolean isPointInTriangle(Vector2f a, Vector2f b, Vector2f c, Vector2f p) {
        float check1 = crossProduct(a, b, p);
        float check2 = crossProduct(p, b, c);
        float check3 = crossProduct(p, c, a);

        return (check1 >= 0 && check2 >= 0 && check3 >= 0) || (check1 <= 0 && check2 <= 0 && check3 <= 0);
    }

    static float edgeLength(Vector2f a, Vector2f b) {
        float dx = a.x() - b.x();
        float dy = a.y() - b.y();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
