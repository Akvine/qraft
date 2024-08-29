package ru.akvine.qraft.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
@NoArgsConstructor
public class LineSegment {
    private boolean processed;
    private Cell cell;
    private Point point1;
    private Point point2;
    private double cornerBlockPathRadius ;

    public LineSegment(Point point1, Point point2) {
        this.point1 = point1;
        this.point2 = point2;
    }

    public LineSegment(Point point1, Point point2, double cornerBlockPathRadius) {
        Point newPoint1 = new Point(point1);
        Point newPoint2 = new Point(point2);
        this.point1 = newPoint1;
        this.point2 = newPoint2;
        this.cornerBlockPathRadius = cornerBlockPathRadius;
    }
}
