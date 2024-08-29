package ru.akvine.qraft.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class Point {
    private long x;
    private long y;

    public Point(Point point) {
        this.x = point.getX();
        this.y = point.getY();
    }
}
