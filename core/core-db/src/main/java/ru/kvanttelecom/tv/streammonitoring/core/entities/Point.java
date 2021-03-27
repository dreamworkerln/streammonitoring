package ru.kvanttelecom.tv.streammonitoring.core.entities;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
public class Point {

    @Getter
    private final double x;

    @Getter
    private final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
