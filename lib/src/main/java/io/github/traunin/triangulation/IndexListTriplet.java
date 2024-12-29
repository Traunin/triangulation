package io.github.traunin.triangulation;

import java.util.List;

class IndexListTriplet <T> {
    private final int prev;
    private final int cur;
    private final int next;
    private final T prevElement;
    private final T curElement;
    private final T nextElement;

    public IndexListTriplet(int prev, int cur, int next, List<T> list) {
        this.prev = prev;
        this.cur = cur;
        this.next = next;
        prevElement = list.get(prev);
        curElement = list.get(cur);
        nextElement = list.get(next);
    }

    public static <T> IndexListTriplet<T> fromCurInList(int current, List<Integer> indices, List<T> list) {
        int prev = indices.get(current - 1);
        int cur = indices.get(current);
        int next = indices.get(current + 1);
        return new IndexListTriplet<>(prev, cur, next, list);
    }

    public int prev() {
        return prev;
    }

    public int cur() {
        return cur;
    }

    public int next() {
        return next;
    }

    public T prevElement() {
        return prevElement;
    }

    public T curElement() {
        return curElement;
    }

    public T nextElement() {
        return nextElement;
    }

    public boolean containsIndex(int index) {
        return index == prev || index == cur || index == next;
    }

    public int[] indicesAsArray() {
        return new int[] {prev, cur, next};
    }
}
