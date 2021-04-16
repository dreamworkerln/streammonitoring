package ru.kvanttelecom.tv.streammonitoring.core.entities;

import lombok.Getter;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Point {

    @Getter
    private Double x;

    @Getter
    private Double y;

    protected Point() {}

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;
        Point point = (Point) o;
        return Objects.equals(x, point.x) &&
            Objects.equals(y, point.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Point{" +
            "x=" + x +
            ", y=" + y +
            '}';
    }
}
