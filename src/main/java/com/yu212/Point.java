package com.yu212;

import java.util.ArrayList;
import java.util.List;

public class Point implements Comparable<Point> {
    private static final int[] da = {-1, 0, 1, 0, -1, 1, 1, -1, -1};
    public final int x;
    public final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static List<Point> iterate(int width, int heght) {
        List<Point> list = new ArrayList<>();
        for (int i = 0; i < heght; i++) {
            for (int j = 0; j < width; j++) {
                list.add(new Point(j, i));
            }
        }
        return list;
    }

    public Point add(int dx, int dy) {
        return new Point(x + dx, y + dy);
    }

    public Point add(Point d) {
        return new Point(x + d.x, y + d.y);
    }

    public boolean isInner(int width, int height) {
        return 0 <= x && x < width && 0 <= y && y < height;
    }

    public Point moved(int d) {
        return new Point(x + da[d], y + da[d + 1]);
    }

    public List<Point> neighborPoints(int type) {
        List<Point> list = new ArrayList<>();
        for (int i = 0; i < type; i++) {
            int nx = x + da[i];
            int ny = y + da[i + 1];
            Point point = new Point(nx, ny);
            list.add(point);
        }
        return list;
    }

    @Override
    public int compareTo(Point o) {
        return x == o.x ? Integer.compare(y, o.y) : Integer.compare(x, o.x);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) {
            return false;
        }
        Point point = (Point)o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return x * 31 + y;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }
}
