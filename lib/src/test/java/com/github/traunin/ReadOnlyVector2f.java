package com.github.traunin;

public class ReadOnlyVector2f implements Vector2f {
    private final float x;
    private final float y;

    public ReadOnlyVector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
