package com.github.traunin.triangulation;

public class ReadOnlyVector2f implements Vector2f {
    private final float x;
    private final float y;

    public ReadOnlyVector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }
}
